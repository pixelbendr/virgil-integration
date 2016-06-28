package com.psyphertxt.android.cyfa.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.Country;
import com.psyphertxt.android.cyfa.model.PhoneNumber;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.greenrobot.event.EventBus;

public class ContactUtils {

    /**
     * Loop through users phonebook, retrieve contacts and try to normalize phone numbers
     * based on the users calling code
     *
     * @param callingCode the users calling code
     * @return returns a task containing a list of phone numbers or null
     */
    public static Task<List<PhoneNumber>> searchPhoneBookAsync(final Context context, final String callingCode) {

        final Task<List<PhoneNumber>>.TaskCompletionSource task = Task.create();

        Task.callInBackground(new Callable<List<PhoneNumber>>() {

            @Override
            public List<PhoneNumber> call() {

                //create a list to hold the phone numbers
                List<PhoneNumber> contacts = new ArrayList<>();

                //set a projection for the query
                final String[] PROJECTION = new String[]{
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                };

                //set the sort order for the query
                final String SORT_ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

                //create a new cursor object
                Cursor phones = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
                        null, null, SORT_ORDER
                );

                if (phones != null) {
                    try {

                        CountryUtils.init(context);

                        //set the same values we need in the projection
                        //anything else will crash the application
                        //  final int contactIdIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                        final int displayNameIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        final int phoneNumberIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        // final int photoIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);

                        //loop through phone contacts database and retrieve contacts
                        while (phones.moveToNext()) {

                            //   int id = phones.getInt(contactIdIndex);
                            //   int photo = phones.getInt(photoIndex);
                            String name = phones.getString(displayNameIndex);
                            String number = ContactUtils.normalizeNumber(phones.getString(phoneNumberIndex));
                            String[] formatted = ContactUtils.splitPhoneNumber(number, callingCode);

                            PhoneNumber phoneNumber = new PhoneNumber(name, formatted[0] + formatted[1]);
                            // phoneNumber.setImage(photo);
                            contacts.add(phoneNumber);
                        }
                    } finally {
                        phones.close();
                    }
                    if (contacts.size() >= Config.NUMBER_ONE) {

                        task.setResult(contacts);

                    } else {

                        task.setError(new NullPointerException("no contacts found"));
                    }
                }
                return null;
            }
        });

        return task.getTask();
    }

    /**
     * takes a list of phone numbers, sends it to the server to find related users
     *
     * @return returns a hash map of the results and a result code
     */
    public static void findContactsAsync(final Context context, String callingCode, final CallbackListener.callbackForResults callbackForResults) {

        searchPhoneBookAsync(context, callingCode)
                .continueWith(new Continuation<List<PhoneNumber>, Object>() {

                    @Override
                    public Object then(Task<List<PhoneNumber>> task) throws Exception {

                        Map<String, Object> payload = new HashMap<>();
                        List<String> contacts = new ArrayList<>();

                        List<PhoneNumber> phoneNumbers = task.getResult();
                        for (PhoneNumber phoneNumber : phoneNumbers) {
                            contacts.add(phoneNumber.getNumber());
                        }

                        payload.put(Config.KEY_CONTACT, contacts);
                        payload.put(Config.KEY_USERNAME, User.getDeviceUser().getUsername());

                        getServerContacts(payload, callbackForResults);

                        return null;
                    }
                });
    }

    private static void getServerContacts(Map<String, Object> payload, final CallbackListener.callbackForResults callbackForResults) {
        ParseCloud.callFunctionInBackground(Config.DEFINE_GET_CONTACTS, payload, new FunctionCallback<HashMap<String, Object>>() {

            @Override
            public void done(HashMap<String, Object> hashMap, ParseException e) {
                if (e == null) {
                    EventBus.getDefault().postSticky(hashMap);
                    callbackForResults.success(hashMap);

                } else {

                    callbackForResults.error(e.getMessage());
                }
            }
        });
    }

    public static String getGoogleUsername(Context context) {

        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public static String[] splitPhoneNumber(String number, String defaultCallingCode) {
        if (number.startsWith("0")) {
            number = Long.valueOf(number).toString();
        }
        if (!number.startsWith("+")) {
            number = "+" + number;
        }

        for (Country c : CountryUtils.getCountriesSortedByCallingCode()) {
            if (number.startsWith(c.getCallingCode()) && (number.length() - c.getCallingCode().length()) >= 8) {
                return new String[]{c.getCallingCode(), number.substring(c.getCallingCode().length())};
            }
        }
        return new String[]{defaultCallingCode, number.substring(1)};
    }

    public static String normalizeNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return Config.EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            // Character.digit() supports ASCII and Unicode digits (fullwidth, Arabic-Indic, etc.)
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (i == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return normalizeNumber(PhoneNumberUtils.convertKeypadLettersToDigits(phoneNumber));
            }
        }
        return sb.toString();
    }
}

package com.psyphertxt.android.cyfa.util;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.scottyab.aescrypt.AESCrypt;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.Base64;

import android.app.Activity;
import android.content.Context;

import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage basic random string generation
 */
public class SecurityUtils {

    public static String PASSWORD = "punchy-tooth-3u3vievnpj3x11a32d3-18a9f52-1218f96-de30b9";

    public static String hash() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(25, random).toString(16);
    }

    public static String createHash() {
        return String.format("%s-%s-%s-%s", hash(), hash(), hash(), hash());
    }

  /*  public static String createSha(String string, String salt) {
        return new String(Hex.encodeHex(DigestUtils.sha(string + salt)));
    }

    public static String createUsername(String phoneNumber) {
        return createSha(phoneNumber, Config.CYFA_IO) + "@" + Config.CYFA_IO_URL;
    }*/

    public static String createConversationId() {

        NameUtils nameUtils = new NameBuilder()
                .setDelimiter("-")
                .setTokenLength(12)
                .setTokenHex(true)
                .build();

        return nameUtils.generate() + createHash();
    }

    public static String createMediaId() {

        NameUtils nameUtils = new NameBuilder()
                .setDelimiter("-")
                .setTokenLength(6)
                .setTokenHex(true)
                .build();

        return nameUtils.generate() + hash();
    }

    /**
     * connect to cloud code and bring back a randomly generated hash or token
     *
     * @param cloudCodeName      the cloud code function name
     * @param type               the type of result e.g "hash" || "token"
     * @param string             the string to generate the hash from
     * @param callbackForResults the results from the server
     */
    private static void generator(final String cloudCodeName, final String type, String string, final CallbackListener.callbackForResults callbackForResults) {
        HashMap<String, String> params = new HashMap<>();

        switch (type) {
            case Config.TOKEN:
                params.put(Config.KEY_USER_ID, User.getDeviceUserId());
                params.put(Config.KEY_USERNAME, User.getDeviceUser().getUsername());
                break;

            case Config.HASH:
                params.put(Config.STRING, string);
                break;
        }

        ParseCloud.callFunctionInBackground(cloudCodeName, params, new FunctionCallback<HashMap<String, Object>>() {
            @Override
            public void done(HashMap<String, Object> hashMap, ParseException e) {
                if (e == null) {
                    try {

                        int responseCode = (Integer) hashMap.get(NetworkUtils.KEY_RESPONSE_CODE);

                        if (responseCode == NetworkUtils.RESPONSE_OK) {

                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> data = (HashMap<String, Object>) hashMap.get(Config.DATA);

                            switch (type) {
                                case Config.TOKEN:
                                    if (data.get(Config.TOKEN) != null) {
                                        callbackForResults.success(data.get(Config.TOKEN));
                                    }
                                    break;

                                case Config.HASH:
                                    if (data.get(Config.HASH) != null) {
                                        callbackForResults.success(data.get(Config.HASH));
                                    }
                                    break;
                            }
                        } else {
                            callbackForResults.error(responseCode + Config.EMPTY_STRING);
                        }
                    } catch (Exception e1) {
                        callbackForResults.error(NetworkUtils.UNKNOWN_ERROR + Config.EMPTY_STRING);
                    }
                } else {
                    callbackForResults.error(e.getMessage());
                }
            }
        });
    }

    public static void generateHash(String string, CallbackListener.callbackForResults callbackForResults) {
        generator(Config.DEFINE_GENERATE_HASH, Config.HASH, string, callbackForResults);
    }

    public static void generateToken(CallbackListener.callbackForResults callbackForResults) {
        generator(Config.DEFINE_GENERATE_TOKEN, Config.TOKEN, Config.EMPTY_STRING, callbackForResults);
    }

    public static String encrypt(String Password, String plainText) throws GeneralSecurityException {
        //TODO read about WeChat security implementation
        //parts of conversation id plus device user id and context user id
        return AESCrypt.encrypt(Password, plainText);
    }

    public static Map<String, PublicKey> search(ClientFactory clientFactory) {

        SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder()
                .setValue(User.getDeviceUser().getUsername());
        List<VirgilCard> recipientCards = clientFactory.getPublicKeyClient()
                .search(criteriaBuilder.build());

        Map<String, PublicKey> recipients = new HashMap<>();
        for (VirgilCard recipientCard : recipientCards) {
            recipients.put(recipientCard.getId(), new PublicKey(Base64.decode(recipientCard.getPublicKey().getKey())));
        }

        return recipients;
    }

    //virgil-integration
    public static Map<String, Object> encrypt(String username,KeyPair keyPair, String message) throws Exception {

        Map<String, Object> encryption = new HashMap<>();
        String encryptedMessage = CryptoHelper.encrypt(message, username,keyPair.getPublic());
        String signature = CryptoHelper.sign(encryptedMessage, keyPair.getPrivate());
        encryption.put("text",encryptedMessage);
        encryption.put("signature",signature);
        return encryption;

    }

    //virgil-integration
    public static KeyPair generateKeyPair() {

        return KeyPairGenerator.generate();
    }

    //virgil-integration
    public static VirgilCard validate(ClientFactory clientFactory,KeyPair keyPair) {

        Identity identity = new ValidatedIdentity(IdentityType.EMAIL, User.getDeviceUser().getUsername());

        VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder()
                .setIdentity(identity)
                .setPublicKey(keyPair.getPublic());
        return clientFactory.getPublicKeyClient()
                .createCard(vcBuilder.build(), keyPair.getPrivate());
    }

    public static String decrypt(String Password, String cypherText) throws GeneralSecurityException {
        if (checkForEncode(cypherText)) {
            return AESCrypt.decrypt(Password, cypherText);
        }
        return cypherText;// + " <= Please update to the latest version to read this message.";
    }

    public static Boolean checkForEncode(String string) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);
        if (m.find()) {
            return true;
        }
        return false;
    }

    //takes message id
    //takes device user id
    //takes context user id
    public static String messagePassword(String conservationId, String deviceUserId, String contextUserId) {
        String[] strings = conservationId.split("-");
        return strings[2] + deviceUserId + contextUserId;
    }

    public static void setScreenCaptureAllowed(Activity activity, boolean allowed) {
        if (!allowed) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}

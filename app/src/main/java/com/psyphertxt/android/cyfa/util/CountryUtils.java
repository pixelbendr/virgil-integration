package com.psyphertxt.android.cyfa.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.Country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountryUtils {

    private static List<Country> allCountriesList;
    private static List<Country> countriesSortedByCallingCode;

    public static void init(Context context) {

        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<>();

                // Read from local file
                String allCountriesString = readFileAsString(context);
                JSONArray countries = new JSONArray(allCountriesString);

                for (int i = 0; i < countries.length(); i++) {
                    JSONObject index = countries.getJSONObject(i);
                    Country country = new Country();
                    country.setCode(index.getString("code"));
                    country.setName(index.getString("country"));
                    country.setCallingCode(index.getString("callingCode"));
                    allCountriesList.add(country);
                }

                Collections.sort(allCountriesList, new Comparator<Country>() {
                    @Override
                    public int compare(Country lhs, Country rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

                countriesSortedByCallingCode = new ArrayList<>(allCountriesList);
                Collections.sort(countriesSortedByCallingCode, new Comparator<Country>() {
                    @Override
                    public int compare(Country lhs, Country rhs) {
                        int a = lhs.getCallingCode().length();
                        int b = rhs.getCallingCode().length();
                        if (a < b) {
                            return -1;
                        } else if (a > b) {
                            return 1;
                        } else {
                            return lhs.getCallingCode().compareTo(rhs.getCallingCode());
                        }
                    }
                });
                Collections.reverse(countriesSortedByCallingCode);
                countriesSortedByCallingCode = Collections.unmodifiableList(countriesSortedByCallingCode);

            //    Logger.d("Countries Length: %s", countriesSortedByCallingCode.size());
//                Logger.d("Countries: %s", Arrays.toString(countriesSortedByCallingCode.toArray()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFileAsString(Context context)
            throws java.io.IOException {
        //String base64 = context.getResources().getString(R.string.countries);
        String base64 = context.getResources().getString(R.string.countries);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    public static List<Country> getAllCountries() {
        return new ArrayList<>(allCountriesList);
    }

    public static Country searchOne(Context context) {

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //String countryIso = manager.getSimCountryIso();
        String countryIso = manager.getNetworkCountryIso();
        Country selectedCountry = new Country();
        for (Country country : allCountriesList) {
            if (country.getCode().toLowerCase(Locale.ENGLISH)
                    .contains(countryIso.toLowerCase())) {
                selectedCountry = country;
                break;
            }
        }
        return selectedCountry;
    }

    public static List<Country> getCountriesSortedByCallingCode() {
        return countriesSortedByCallingCode;
    }
}

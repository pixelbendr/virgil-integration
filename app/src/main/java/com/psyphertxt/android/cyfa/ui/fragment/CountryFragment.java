package com.psyphertxt.android.cyfa.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.Country;
import com.psyphertxt.android.cyfa.ui.adapters.CountryListAdapter;
import com.psyphertxt.android.cyfa.util.CountryUtils;
import com.psyphertxt.android.cyfa.util.Fonts;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CountryFragment extends DialogFragment {

    @InjectView(R.id.text_country_search)
    EditText textCountrySearch;

    @InjectView(R.id.country_listView)
    ListView mListView;

    private CountryListAdapter mAdapter;
    private CountryPickerListener mListener;
    private List<Country> selectedCountries;

    /**
     * Set mListener
     *
     * @param listener
     */
    public void setListener(CountryPickerListener listener) {
        this.mListener = listener;
    }

    public EditText getSearchEditText() {
        return textCountrySearch;
    }

    public ListView getCountryListView() {
        return mListView;
    }

    /**
     * Convenient function to get currency code from country code currency code
     * is in English locale
     *
     * @param countryCode
     * @return
     */
    public static Currency getCurrencyCode(String countryCode) {
        try {
            return Currency.getInstance(new Locale("en", countryCode));
        } catch (Exception e) {

        }
        return null;
    }

    public static CountryFragment newInstance() {
        CountryFragment picker = new CountryFragment();
        Bundle bundle = new Bundle();
        picker.setArguments(bundle);
        return picker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.country_picker, null);

        ButterKnife.inject(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        CountryUtils.init(view.getContext());
        selectedCountries = CountryUtils.getAllCountries();

        //load application font for consistency in UI
        Fonts.regularFont(textCountrySearch, getActivity());

        //set focus and show the keyboard
        textCountrySearch.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        mAdapter = new CountryListAdapter(getActivity(), selectedCountries);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mListener != null) {
                    Country country = selectedCountries.get(position);
                    mListener.onSelectCountry(country.getName(),
                            country.getCode(), country.getCallingCode());
                }
            }
        });

        // Search for which countries matched user query
        textCountrySearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    /**
     * Search allCountriesList contains text and put result into
     * selectedCountriesList
     *
     * @param text
     */
    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountries.clear();
        for (Country country : CountryUtils.getAllCountries()) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase())) {
                selectedCountries.add(country);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public interface CountryPickerListener {
        void onSelectCountry(String name, String code, String callingCode);
    }
}

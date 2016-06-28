package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.model.Country;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.model.Themes;
import com.psyphertxt.android.cyfa.ui.fragment.CountryFragment;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.ui.widget.StartUpActionBarActivity;
import com.psyphertxt.android.cyfa.util.CountryUtils;
import com.psyphertxt.android.cyfa.util.Fonts;
import com.psyphertxt.android.cyfa.util.SignUpUtils;
import com.psyphertxt.android.cyfa.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class PhoneNumberActivity extends StartUpActionBarActivity {

    @InjectView(R.id.text_calling_code)
    TextView txtCallingCode;

    @InjectView(R.id.text_privacy_policy_caption)
    TextView txtPrivacyPolicyCaption;

    @InjectView(R.id.text_phone_number_title)
    TextView txtPhoneNumberTitle;

    @InjectView(R.id.text_phone_number_message)
    TextView txtPhoneNumberMessage;

    @InjectView(R.id.text_view_privacy_policy)
    TextView txtPrivacyPolicy;

    @InjectView(R.id.text_phone_number)
    EditText txtEditPhoneNumber;

    @InjectView(R.id.button_continue)
    Button btnContinue;

    protected CountryFragment picker;
    protected String callingCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        ButterKnife.inject(this);

        //TODO optimise this, current implementation is a waste of resources
        //http://www.tristanwaddington.com/2012/09/android-textview-with-custom-font-support/

        //load application font for consistency in UI
        Fonts.lightFont(txtCallingCode, this);
        // Fonts.lightFont(txtPhoneNumberTitle, this);
        Fonts.lightFont(txtPhoneNumberMessage, this);
        Fonts.lightFont(txtEditPhoneNumber, this);
        Fonts.lightFont(txtPrivacyPolicy, this);
        Fonts.lightFont(txtPrivacyPolicyCaption, this);
        Fonts.regularFont(btnContinue, this);

        //lets create a new country instance
        CountryUtils.init(this);

        //retrieve and set the users country and zip code
        Country country = CountryUtils.searchOne(this);
        txtCallingCode.setText(String.format("%s ( %s )", country.getName(), country.getCallingCode()));
        callingCode = country.getCallingCode();

        txtCallingCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picker = CountryFragment.newInstance();
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryFragment.CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code, String callingCodeString) {

                        txtCallingCode.setText(String.format("%s ( %s )", name, callingCodeString));
                        callingCode = callingCodeString;
                        picker.dismiss();

                    }
                });
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String phoneNumber = TextUtils.trimPhoneNumber(txtEditPhoneNumber.getText().toString());
                String dialogContent = callingCode + phoneNumber + getString(R.string.number_validation_message);

                if (phoneNumber.length() >= Config.NUMBER_LENGTH) {

                    final Context context = PhoneNumberActivity.this;

                    Settings settings = new Settings(context);
                    int theme = Themes.getThemeByColor(settings.getTheme()).get(Config.KEY_COLOR_PRIMARY);

                    new MaterialDialog.Builder(context)
                            //.titleColorRes(R.color.danube)
                            .titleColor(ContextCompat.getColor(context, theme))
                            .title(R.string.number_validation_subtitle)
                            .content(dialogContent)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {

                                    if (Config.IS_PROTO) {

                                        Intent intent = new Intent(PhoneNumberActivity.this, VerificationCodeActivity.class);
                                        startActivity(intent);

                                    } else {

                                        if (!phoneNumber.isEmpty()) {

                                            SignUp signUp = new SignUp();
                                            signUp.setCallingCode(callingCode);
                                            signUp.setNumber(phoneNumber);

                                            //use event bus to transfer user data
                                            EventBus.getDefault().postSticky(signUp);

                                            //lets check if the users phone number exist on the Cyfa platform
                                            SignUpUtils.validateUser(PhoneNumberActivity.this);

                                        } else {

                                            Alerts.show(PhoneNumberActivity.this,
                                                    getString(R.string.valid_phone_number_error_title),
                                                    getString(R.string.valid_phone_number_error_message));
                                        }
                                    }
                                }
                            })
                            .show();

                } else {

                    Alerts.show(PhoneNumberActivity.this,
                            getString(R.string.valid_phone_number_error_title),
                            getString(R.string.valid_phone_number_error_message));

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextUtils.showKeyboard(this, txtEditPhoneNumber);

        //set focus and show the keyboard
        // txtEditPhoneNumber.requestFocus();
        //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}

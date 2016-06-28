package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.widget.StartUpActionBarActivity;
import com.psyphertxt.android.cyfa.util.Fonts;
import com.psyphertxt.android.cyfa.util.SignUpUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class VerificationCodeActivity extends StartUpActionBarActivity {

    BroadcastReceiver smsReceiver;

    @InjectView(R.id.button_get_started)
    Button btnValidationNext;

    @InjectView(R.id.button_resend)
    Button btnResend;

    @InjectView(R.id.text_validation_code)
    EditText txtEditVerifyCode;

    @InjectView(R.id.text_regular_message)
    TextView txtVerifyCodeMessage;

    @InjectView(R.id.text_change_number)
    TextView txtChangeNumber;

    @InjectView(R.id.text_phone_number_title)
    TextView txtVerifyCodeTitle;

    @InjectView(R.id.linearLayout_change_number)
    LinearLayout linearLayoutChangeNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        ButterKnife.inject(this);

        //load application font for consistency in UI
        Fonts.lightFont(txtVerifyCodeTitle, this);
        Fonts.lightFont(txtVerifyCodeMessage, this);
        Fonts.regularFont(txtChangeNumber, this);
        Fonts.regularFont(txtEditVerifyCode, this);
        Fonts.regularFont(btnValidationNext, this);
        Fonts.lightFont(btnResend, this);


        btnValidationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Config.IS_PROTO) {

                    Intent intent = new Intent(VerificationCodeActivity.this, ProfileNameActivity.class);
                    intent.putExtra(Config.KEY_CALLING_CODE, "+233");
                    startActivity(intent);

                } else {

                    SignUpUtils.validateCode(VerificationCodeActivity.this, txtEditVerifyCode.getText().toString());

                }
            }
        });

        //TODO auto-enable button after characters have been entered
        // btnValidationNext.setEnabled(false);
   /*     txtEditVerifyCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                btnValidationNext.setEnabled(v.getText().length() == 4);
                return false;
            }
        });*/

        //click event to go back to the previous activity
        //which allows the user to update or change their number
        linearLayoutChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    private String getSMSMessage(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            if (messages.length > -1) {
                return messages[0].getMessageBody();
            }
        }
        return Config.EMPTY_STRING;
    }

    @Override
    protected void onResume() {
        super.onResume();
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //lets extract the code sent from the server
                String smsBody = getSMSMessage(intent);

                //lets extract just numbers from the sms message
                final String verifyCode = smsBody.replaceAll("[^0-9]", "");

                //lets check if the message contains this exact string
                if (smsBody.toLowerCase().contains("Your Cyfa validation code is ".toLowerCase())) {

                    final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);

                    //lets check the length of the code and also compare the code
                    //if the sms code is exactly equal to the server code
                    if (verifyCode.length() == Config.CODE_LENGTH &&
                            Integer.parseInt(verifyCode) == signUp.getCode()) {

                        SignUpUtils.validateCode(VerificationCodeActivity.this, verifyCode);

                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

    }

    @Override
    protected void onPause() {
        unregisterReceiver(smsReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

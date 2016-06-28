package com.psyphertxt.android.cyfa.ui.activity.startup;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.ui.widget.StartUpActionBarActivity;
import com.psyphertxt.android.cyfa.util.ContactUtils;
import com.psyphertxt.android.cyfa.util.Fonts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class ProfileNameActivity extends StartUpActionBarActivity {

    @InjectView(R.id.text_profile_name_title)
    TextView txtTitle;

    @InjectView(R.id.text_profile_name_message)
    TextView txtMessage;

    @InjectView(R.id.text_edit_profile_name)
    EditText txtEditProfileName;

    @InjectView(R.id.button_next)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_name);

        ButterKnife.inject(this);

        Fonts.lightFont(txtTitle, this);
        Fonts.lightFont(txtMessage, this);
        Fonts.lightFont(txtEditProfileName, this);
        Fonts.regularFont(btnNext, this);

        txtEditProfileName.setText(ContactUtils.getGoogleUsername(this));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtEditProfileName.length() >= Config.DISPLAY_NAME_MIN_LENGTH &&
                        txtEditProfileName.length() <= Config.DISPLAY_NAME_MAX_LENGTH &&
                        !txtEditProfileName.getText().toString().equals(Config.EMPTY_STRING)) {

                    Intent intent = new Intent(ProfileNameActivity.this, ThemeColorActivity.class);

                    final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);
                    signUp.setProfileName(txtEditProfileName.getText().toString());
                    EventBus.getDefault().postSticky(signUp);

                    startActivity(intent);

                } else {

                    Alerts.show(ProfileNameActivity.this,
                            getString(R.string.profile_name_error_title),
                            getString(R.string.profile_name_error_message));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set focus and show the keyboard
        txtEditProfileName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ready, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

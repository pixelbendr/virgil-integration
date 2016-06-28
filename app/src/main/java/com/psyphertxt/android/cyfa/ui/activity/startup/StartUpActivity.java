package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.ui.widget.StartUpActionBarActivity;
import com.psyphertxt.android.cyfa.util.Fonts;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StartUpActivity extends StartUpActionBarActivity {

    @InjectView(R.id.text_welcome_message)
    TextView txtMessage;

    @InjectView(R.id.button_lets_go)
    Button btnLetsGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.inject(this);

        //load application font for consistency in UI
        Fonts.lightFont(txtMessage, this);
        Fonts.regularFont(btnLetsGo, this);

        btnLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartUpActivity.this, PhoneNumberActivity.class);
                startActivity(intent);

            }
        });
    }

}

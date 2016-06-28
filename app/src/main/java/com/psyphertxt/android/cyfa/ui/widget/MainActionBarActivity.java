package com.psyphertxt.android.cyfa.ui.widget;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.model.Themes;
import com.psyphertxt.android.cyfa.util.TextUtils;
import com.virgilsecurity.sdk.client.ClientFactory;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * this activity is responsible for setting the users chosen theme
 * on all activities
 */
public class MainActionBarActivity extends AppCompatActivity {

    public Settings settings;
    private String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        settings = new Settings(this);
        theme = TextUtils.getBundleString(bundle, Config.KEY_THEME, settings.getTheme());
        setTheme(Themes.getThemeByName(theme));



        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int color = Themes.getThemeByColor(settings.getTheme()).get(Config.KEY_COLOR_ACCENT);
            int colorPrimary = ContextCompat.getColor(this,color);
            window.setStatusBarColor(colorPrimary);
        }
    }

    //when using this method call getResource().getColor(getColorPrimary())
    public int getColorPrimary() {
        return Themes.getThemeByColor(theme).get(Config.KEY_COLOR_PRIMARY);
    }

    public int getColorAccent() {
        return Themes.getThemeByColor(theme).get(Config.KEY_COLOR_ACCENT);
    }
}

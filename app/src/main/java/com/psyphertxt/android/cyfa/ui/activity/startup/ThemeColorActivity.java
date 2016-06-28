package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.adapters.ThemeAdapter;
import com.psyphertxt.android.cyfa.ui.widget.Shape;
import com.psyphertxt.android.cyfa.util.Fonts;
import com.psyphertxt.android.cyfa.model.Themes;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class ThemeColorActivity extends AppCompatActivity {

    @InjectView(R.id.color_grid)
    GridView gridViewColor;

    @InjectView(R.id.button_skip)
    Button btnSkip;

    @InjectView(R.id.button_theme_finish)
    Button btnThemeFinish;

    @InjectView(R.id.view_theme_color)
    View viewThemeColor;

    @InjectView(R.id.text_color_name)
    TextView txtColorName;

    @InjectView(R.id.text_theme_title)
    TextView txtThemeTitle;

    @InjectView(R.id.text_theme_message)
    TextView txtThemeMessage;

    private List<Themes> listThemes;
    private Settings settings;
    private String selectedTheme;

    private View.OnClickListener finish = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Config.IS_PROTO) {

                //when the user clicks handle the click here
                Intent intent = new Intent(ThemeColorActivity.this, LoaderActivity.class);
                intent.putExtra(Config.KEY_THEME, selectedTheme);
                startActivity(intent);

            } else {

                //when the user clicks handle the click here
                Intent intent = new Intent(ThemeColorActivity.this, LoaderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);
                signUp.setTheme(selectedTheme);
                EventBus.getDefault().postSticky(signUp);

                startActivity(intent);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_color);

        ButterKnife.inject(this);

        //load application font for consistency in UI
        Fonts.lightFont(txtThemeTitle, this);
        Fonts.lightFont(txtThemeMessage, this);
        Fonts.lightFont(txtColorName, this);
        Fonts.lightFont(btnSkip, this);
        Fonts.regularFont(btnThemeFinish, this);

        //init static themes
        initThemes(this);

        //set default theme
        selectedTheme = Config.THEME_NAME_INDIGO;

        // create a new drawable with a white stroke
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(3, ContextCompat.getColor(this,android.R.color.white));

        //lets create one instance of our grid for
        //better memory management
        if (gridViewColor.getAdapter() == null) {
            ThemeAdapter adapter = new ThemeAdapter(this, listThemes);
            gridViewColor.setAdapter(adapter);
        } else {
            ((ThemeAdapter) gridViewColor.getAdapter()).refill(listThemes);
        }

        gridViewColor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //get two objects in the current view and hide them
                //this can be optimized by getting only the current and previous item
                //without using the loop
                for (int i = 0; i < listThemes.size(); i++) {
                    FrameLayout frameLayout = (FrameLayout) parent.getChildAt(i).findViewById(R.id.themeSelectedColor);
                    ImageView imageView = (ImageView) parent.getChildAt(i).findViewById(R.id.icon_check_image);
                    if (frameLayout.getVisibility() == View.VISIBLE) {
                        frameLayout.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }

                //select all
                view.findViewById(R.id.themeSelectedColor).setVisibility(View.VISIBLE);
                view.findViewById(R.id.icon_check_image).setVisibility(View.VISIBLE);

                int primaryColor = listThemes.get(position).getColorPrimary();
                int accentColor = listThemes.get(position).getColorAccent();

                ColorDrawable colorDrawable = (ColorDrawable) btnThemeFinish.getBackground();

                Integer colorFrom = colorDrawable.getColor();
                Integer colorTo = primaryColor;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        btnThemeFinish.setBackgroundColor((Integer) animator.getAnimatedValue());
                    }

                });

                colorAnimation.setDuration(Config.DEFAULT_ANIMATION_DURATION);
                colorAnimation.start();

                Shape.background(viewThemeColor, Shape.oval(1, primaryColor, accentColor));

                txtColorName.setText(listThemes.get(position).getColorName());
                txtColorName.setTextColor(primaryColor);
                txtThemeTitle.setTextColor(accentColor);
                txtThemeMessage.setTextColor(primaryColor);
                btnSkip.setTextColor(accentColor);

                //set application theme
                settings = new Settings(ThemeColorActivity.this);
                settings.setTheme(listThemes.get(position).getColorName());
                selectedTheme = listThemes.get(position).getColorName();

                // Check if we're running on Android 5.0 or higher
                //set the application status bar to the users accent color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(accentColor);
                }
            }
        });

        btnThemeFinish.setOnClickListener(finish);
        btnSkip.setOnClickListener(finish);

    }


    void initThemes(Context context) {

        listThemes = new ArrayList<>();

        Themes themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.pomegranate));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.cinnabar));
        themes.setColorName(Config.THEME_NAME_RED);
        themes.setThemeName(R.style.AppTheme_Red);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.amber));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.selective_yellow));
        themes.setColorName(Config.THEME_NAME_YELLOW);
        themes.setThemeName(R.style.AppTheme_Yellow);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.california));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.gold_drop));
        themes.setColorName(Config.THEME_NAME_ORANGE);
        themes.setThemeName(R.style.AppTheme_Orange);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.wild_strawberry));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.razzmatazz));
        themes.setColorName(Config.THEME_NAME_PINK);
        themes.setThemeName(R.style.AppTheme_Pink);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.danube));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.mariner));
        themes.setColorName(Config.THEME_NAME_INDIGO);
        themes.setThemeName(R.style.AppTheme_Base);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.robins_egg_blue));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.pacific_blue));
        themes.setColorName(Config.THEME_NAME_BLUE);
        themes.setThemeName(R.style.AppTheme_Blue);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.lynch));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.fiord));
        themes.setColorName(Config.THEME_NAME_GRAY);
        themes.setThemeName(R.style.AppTheme_Gray);
        listThemes.add(themes);

        themes = new Themes();
        themes.setColorPrimary(ContextCompat.getColor(context, R.color.green_haze));
        themes.setColorAccent(ContextCompat.getColor(context, R.color.green_leaf));
        themes.setColorName(Config.THEME_NAME_GREEN);
        themes.setThemeName(R.style.AppTheme_Green);
        listThemes.add(themes);
    }


}

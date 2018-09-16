package net.ucoz.softoad.cryptowidget_2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Пендальф Синий on 12.04.2018.
 */

public class ConfigActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    public static final String WIDGET_PREF = "widget_pref";
    public static final String PREF_NAME = "name";
    public static final String PREF_COLOR = "color";
    public static final String PREF_TIME = "time";

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;

    private final String LOG_TAG = "myLogs";

    private SeekBar seekR;
    private SeekBar seekG;
    private SeekBar seekB;
    private SeekBar seekA;
    private SeekBar seekTime;

    private TextView valR;
    private TextView valG;
    private TextView valB;
    private TextView valA;
    private TextView valTime;

    private int valueR = 0;
    private int valueG = 0;
    private int valueB = 0;
    private int valueA = 250;
    private int valueTime = 6;

    private LinearLayout rl1;
    private LinearLayout rl2;
    private LinearLayout rl3;
    private LinearLayout rl4;

    private  FrameLayout frameLayout;

    private Switch aSwitch;

    private int backgroundColor = 0;


    private ViewGroup.LayoutParams lpFrame;
    private ViewGroup.LayoutParams lpRelative;

    private AutoCompleteTextView input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate config");

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
        resultValue = new Intent();

        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.config);

        seekR = findViewById(R.id.seekBar_R);
        seekG = findViewById(R.id.seekBar_G);
        seekB = findViewById(R.id.seekBar_B);
        seekA = findViewById(R.id.seekBar_Alpha);
        seekTime = findViewById(R.id.seekBar_time);

        SeekListener seekListener = new SeekListener();

        seekR.setOnSeekBarChangeListener(seekListener);
        seekG.setOnSeekBarChangeListener(seekListener);
        seekB.setOnSeekBarChangeListener(seekListener);
        seekA.setOnSeekBarChangeListener(seekListener);
        seekTime.setOnSeekBarChangeListener(seekListener);

        frameLayout = findViewById(R.id.frame);

        valR = findViewById(R.id.valR);
        valG = findViewById(R.id.valG);
        valB = findViewById(R.id.valB);
        valA = findViewById(R.id.valAlpha);
        valTime = findViewById(R.id.valTime);

        rl1 = findViewById(R.id.relative1);
        rl2 = findViewById(R.id.relative2);
        rl3 = findViewById(R.id.relative3);
        rl4 = findViewById(R.id.relative4);

        lpFrame = frameLayout.getLayoutParams();
        lpRelative = rl1.getLayoutParams();

        aSwitch = findViewById(R.id.background_on);
        aSwitch.setOnCheckedChangeListener(this);
        aSwitch.setChecked(false);

        input = findViewById(R.id.input);
        input.setAdapter(new CurrenciesAdapter(this));
        input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });

        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        valueTime = sp.getInt(ConfigActivity.PREF_TIME, 5);
        valTime.setText(String.valueOf(valueTime));

    }


    public void onClick(View v) {

        String nameOfCrypt = input.getText().toString();
        if (nameOfCrypt.equals("")) {
            Toast.makeText(this,R.string.notice_name, Toast.LENGTH_LONG).show();
            return;
        }

        // Записываем значения с экрана в Preferences
        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_NAME + widgetID, nameOfCrypt);
        editor.putInt(PREF_COLOR + widgetID, backgroundColor);
        editor.putInt(PREF_TIME, valueTime);
        editor.apply();

        System.out.println("valueTime" + valueTime);

        // положительный ответ
        setResult(RESULT_OK, resultValue);

        Log.d(LOG_TAG, "finish config " + widgetID);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewGroup.LayoutParams lp1 = null;
        ViewGroup.LayoutParams lp2 = null;
        if (!isChecked) {
            lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            System.out.println("Сужение");
        }else {
            lp1 = lpFrame;
            lp2 = lpRelative;
            System.out.println("Расширение");
        }

        frameLayout.setLayoutParams(lp1);
        rl1.setLayoutParams(lp2);
        rl2.setLayoutParams(lp2);
        rl3.setLayoutParams(lp2);
        rl4.setLayoutParams(lp2);

    }

    public class SeekListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.seekBar_R:
                    valueR = progress;
                    valR.setText(String.valueOf(progress));
                    break;
                case R.id.seekBar_G:
                    valueG = progress;
                    valG.setText(String.valueOf(progress));
                    break;
                case R.id.seekBar_B:
                    valueB = progress;
                    valB.setText(String.valueOf(progress));
                    break;
                case R.id.seekBar_Alpha:
                    valueA = progress;
                    valA.setText(String.valueOf(progress));
                    break;
                case R.id.seekBar_time:
                    if (progress < 3) progress = 3;
                    valueTime = progress;
                    valTime.setText(String.valueOf(progress));
                    break;
            }
            if (aSwitch.isChecked()) backgroundColor = Color.argb(valueA, valueR, valueG, valueB);

            frameLayout.setBackgroundColor(backgroundColor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}

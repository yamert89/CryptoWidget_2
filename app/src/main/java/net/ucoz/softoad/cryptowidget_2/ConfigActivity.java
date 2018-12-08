package net.ucoz.softoad.cryptowidget_2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Пендальф Синий on 12.04.2018.
 */

public class ConfigActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    public static final String WIDGET_PREF = "widget_pref";
    public static final String PREF_NAME = "name";
    public static final String PREF_COLOR = "color";
    public static final String PREF_TIME = "time";
    public static String STRATEGY = Utils.STRATEGY_COINGECKO;
    private Properties properties;

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

    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinnerStrategy;

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

        properties = new Properties();

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
        setNamesOfCryptoCurrencies();


        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        valueTime = sp.getInt(ConfigActivity.PREF_TIME, 5);
        valTime.setText(String.valueOf(valueTime));



        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinnerStrategy = findViewById(R.id.spinnerStrategy);

        //String[] dataAdapter = getListPrices();
        STRATEGY = sp.getString("strategy" + widgetID, Utils.STRATEGY_COINGECKO);



        spinnerStrategy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ON CLICK ITEM");
                System.out.println("POSITION " + position);

                switch (position){
                    case 0:
                        STRATEGY = Utils.STRATEGY_COINGECKO;
                        break;
                    case 1:
                        STRATEGY = Utils.STRATEGY_COINMARKETCAP;
                        break;
                }
                SharedPreferences.Editor editor = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE).edit();
                editor.putString("strategy" + widgetID, STRATEGY);
                editor.apply();
                setNamesOfCurrencies();
                setNamesOfCryptoCurrencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setNamesOfCurrencies();


    }

    private void setNamesOfCurrencies(){
        int prices_list = 0;
        int defaultValue_1 = 0;
        int defaultValue_2 = 0;
        switch (STRATEGY){
            case Utils.STRATEGY_COINGECKO:
                prices_list = R.array.coingecko_prices_list;
                defaultValue_1 = 43;
                defaultValue_2 = 8;
                break;
            case Utils.STRATEGY_COINMARKETCAP:
                prices_list = R.array.coinmarketcap_prices_list;
                defaultValue_1 = 0;
                defaultValue_2 = 25;
                break;
        }
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(), prices_list, R.layout.custom_drop_down);
        //arrayAdapter.setDropDownViewResource(R.layout.custom_drop_down);
        spinner1.setAdapter(arrayAdapter);
        spinner1.setSelection(defaultValue_1);
        spinner2.setAdapter(arrayAdapter);
        spinner2.setSelection(defaultValue_2);
    }

    private void setNamesOfCryptoCurrencies(){
        input.setAdapter(new CurrenciesAdapter(this, STRATEGY, properties));
        input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Context context = getApplicationContext();
        if (!Utils.hasConnection(context)) {
            Toast.makeText(context,R.string.notice_internet, Toast.LENGTH_LONG).show();
        }
    }

    private String[] getListPrices(){
        List<String> list = new ArrayList<>(49);
        Properties prop = new Properties();
        try {
            InputStream inputStream = getApplicationContext().getResources().getAssets().open("coingecko_prices");
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry entry :
                prop.entrySet()) {
            list.add(entry.getKey().toString());
        }
        return list.toArray(new String[0]);


    }



    public void onClick(View v) {

        try {
            String nameOfCrypt = null;
            String val = input.getText().toString();

            switch (STRATEGY){
                case Utils.STRATEGY_COINGECKO :
                    nameOfCrypt = val;
                    break;
                case Utils.STRATEGY_COINMARKETCAP:
                    nameOfCrypt = properties.getProperty(val);
                    break;
            }



            if (nameOfCrypt.equals("")) {
                Toast.makeText(this, R.string.notice_name, Toast.LENGTH_LONG).show();
                return;
            }

            // Записываем значения с экрана в Preferences
            SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_NAME + widgetID, nameOfCrypt);
            editor.putInt(PREF_COLOR + widgetID, backgroundColor);
            editor.putInt(PREF_TIME, valueTime);

            editor.putString("cur1" + widgetID, spinner1.getSelectedItem().toString());
            editor.putString("cur2" + widgetID, spinner2.getSelectedItem().toString());



            editor.apply();

            System.out.println("valueTime" + valueTime);

            // положительный ответ
            setResult(RESULT_OK, resultValue);

            Log.d(LOG_TAG, "finish config " + widgetID);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewGroup.LayoutParams lp1 = null;
        ViewGroup.LayoutParams lp2 = null;
        if (!isChecked) {
            lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }else {
            lp1 = lpFrame;
            lp2 = lpRelative;
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

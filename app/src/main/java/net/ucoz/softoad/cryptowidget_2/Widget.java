package net.ucoz.softoad.cryptowidget_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ALARM_SERVICE;
import static net.ucoz.softoad.cryptowidget_2.ConfigActivity.PREF_NAME;
import static net.ucoz.softoad.cryptowidget_2.ConfigActivity.PREF_TIME;

public class Widget extends AppWidgetProvider {
    static final String ALL_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.All_WIDGET_UPDATE";
    static final String DYNAMIC_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.DYNAMIC_WIDGET_UPDATE";
    static final String SOME_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.SOME_WIDGET_UPDATE";
    static final String SOME_WIDGET_RESULT = "net.ucoz.softoad.cryptowidget.SOME_WIDGET_RESULT";
    static final String SOME_WIDGET_ERROR = "net.ucoz.softoad.cryptowidget.ERROR";
    PendingIntent pendingIntent;
    AlarmManager am;
    Object[] data;



    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("____________ON RECEIVE!!!");
        try {
            String action = intent.getAction();
            System.out.println("ACTION______________!!!" + action);
            //System.out.println("THIS = " + this.hashCode());
            //System.out.println("CONTEXT = " + context.hashCode());


            if (action == null || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                    action.equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED) ||
                    action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED) ||
                    action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) return;

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            if (action.equals(SOME_WIDGET_RESULT)){
                Object[] id_fullUpd = getData(intent);
                updateWidget((int) id_fullUpd[0], context, (boolean) id_fullUpd[1], appWidgetManager);
                return;
            }

            if(action.equals(SOME_WIDGET_ERROR)){
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.errorStatus, "Server status: " + String.valueOf(intent.getIntExtra("err_status", 403)));
                appWidgetManager.updateAppWidget(intent.getIntExtra("err_id", 0), views);
                return;
            }




            SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

            String nameCurrency = "";
            String cur1 = "";
            String cur2 = "";
            Object[] remoteObjects = null;
            boolean fullUpdate = false;

            if (action.equals(ALL_WIDGET_UPDATE)) {
                ComponentName thisWidget = new ComponentName(context, Widget.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                for (int id :
                        appWidgetIds) {
                    enableProgress(id, context, appWidgetManager);
                    nameCurrency = sp.getString(PREF_NAME + id, "undefined");
                    if (nameCurrency.equals("undefined")) return;
                    cur1 = sp.getString("cur1" + id, "usd");
                    cur2 = sp.getString("cur2" + id, "btc");
                    fullUpdate = true;
                    remoteObjects = new Object[]{id, context, appWidgetManager, fullUpdate};

                    if (!startAsync(nameCurrency, cur1, cur2, id, context, remoteObjects)) {
                        disableProgress(id, context, appWidgetManager);
                        //saveIdDataForReboot(id, nameCurrency, cur1, cur2, context);
                        return;
                    }
                    saveIdDataForReboot(id, nameCurrency, cur1, cur2, context);
                    //updateWidget(id, context, true, appWidgetManager);

                }
                return;

            }

            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            enableProgress(mAppWidgetId, context, appWidgetManager);


            nameCurrency = sp.getString(PREF_NAME + mAppWidgetId, "undefined");
            if (nameCurrency.equals("undefined")) return;

            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {

                int time = sp.getInt(PREF_TIME, 5) * 60000;
                startAlarm(context, time);
                sp.edit().putBoolean("executed", true).apply();

            }

            if (action.equals(DYNAMIC_WIDGET_UPDATE)) {
                updateWidget(mAppWidgetId, context, false, appWidgetManager);
                return;
            }

            cur1 = sp.getString("cur1" + mAppWidgetId, "usd");
            cur2 = sp.getString("cur2" + mAppWidgetId, "btc");
            fullUpdate = true;
            remoteObjects = new Object[]{mAppWidgetId, context, appWidgetManager, fullUpdate};

            if (!startAsync(nameCurrency, cur1, cur2, mAppWidgetId, context, remoteObjects)) {
                disableProgress(mAppWidgetId, context, appWidgetManager);
                //saveIdDataForReboot(mAppWidgetId, nameCurrency, cur1, cur2, context);
                return;
            }
            saveIdDataForReboot(mAppWidgetId, nameCurrency, cur1, cur2, context);
            //updateWidget(mAppWidgetId, context, true, appWidgetManager);
        }catch (Exception e){
            e.printStackTrace();
        }







    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("__________________ON UPDATE!!!");
       // Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        boolean reload = sp.getBoolean("reload", false);
        if (!reload) return;
        sp.edit().putBoolean("reload", false).apply();
        for (int id :
                appWidgetIds) {
            String[] oldData = getIdDataForReboot(id, context);
            if (oldData == null) return;
            Object[] remoteObjects = new Object[]{id, context, appWidgetManager};
            if (!startAsync(oldData[0], oldData[1], oldData[2], id, context, remoteObjects)) {
                disableProgress(id, context, appWidgetManager);
                return;
            }
            boolean res = updateWidget(id, context, true, appWidgetManager);
            if (!res) return;
        }



    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if (am != null) am.cancel(pendingIntent);
        super.onDeleted(context, appWidgetIds);
        //Toast.makeText(context, "onDeleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        System.out.println("________________________________________ON ENABLED ___________________");

       /* Properties prop = new Properties();
        try {
            InputStream inputStream = context.getResources().getAssets().open("sert.jks");
            inputStream.read()
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("javax.net.ssl.trustStore", "/path/to/web2.uconn.edu.jks");*/


        //Toast.makeText(context, "onEnabled", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        boolean executed = sp.getBoolean("executed", false);
        if (!executed) return;
        sp.edit().putBoolean("reload", true).apply();







    }

    @Override
    public void onDisabled(Context context) {
        System.out.println("________ON DISABLED");
        if (am != null) am.cancel(pendingIntent);
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        //Toast.makeText(context, "onRestored", Toast.LENGTH_SHORT).show();

    }



    private boolean updateWidget(int id, Context context, boolean full,  AppWidgetManager appWidgetManager){
        try {
        //Toast.makeText(context, "UPDATE WIDGET", Toast.LENGTH_SHORT).show();

            System.out.println("UPDATE WIDGET - " + id);

            SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);


            if (full) {
                if (data == null) return false;
                if (data[0] != null) views.setImageViewBitmap(R.id.ico, (Bitmap) data[1]);
                views.setTextViewText(R.id.tv_name, (String) data[0]);
                views.setTextViewText(R.id.tv_priceDol, (String) data[2]);
                views.setTextViewText(R.id.tv_priceBTC, (String) data[3]);
                views.setTextViewText(R.id.tv_dyn_Dol, (String) data[4]);
                views.setTextViewText(R.id.tv_dyn_BTC, (String) data[5]);
                views.setTextViewText(R.id.tv_change, "24h");

                int color = sp.getInt(ConfigActivity.PREF_COLOR + id, 0);
                if (color != 0) views.setInt(R.id.general, "setBackgroundColor", color);
                // //TODO background drawable
                //views.setInt(R.id.general, "setBackground", )
                System.out.println("DATA SAVE" + data.length);

                saveChangeData(data, id, sp);


            } else {
                Object[] data2 = getChangeData(id, sp);
                int idx = (int) data2[14];
                System.out.println("IDX_________________! : " + idx);
                String header = "";
                /*for (Object ob :
                        data2) {
                    //System.out.println(ob);
                }*/
                switch (idx) {
                    case 0:
                        header = "24h";
                        break;
                    case 2:
                        header = "7d";
                        break;
                    case 4:
                        header = "14d";
                        break;
                    case 6:
                        header = "30d";
                        break;
                    case 8:
                        header = "60d";
                        break;
                    case 10:
                        header = "200d";
                        break;
                    case 12:
                        header = "1y";
                        break;
                    default:
                        header = "?";
                        break;
                }


                System.out.println("DATA GET  = " + data2.length);

                views.setTextViewText(R.id.tv_dyn_Dol, (String) data2[idx]);
                views.setTextViewText(R.id.tv_dyn_BTC, (String) data2[++idx]);


                views.setTextViewText(R.id.tv_change, header);
                if (idx < 13) sp.edit().putInt(id + "change_counter", ++idx).apply();
                else sp.edit().putInt(id + "change_counter", 0).apply();


            }

            //onClick
            Intent updateIntent = new Intent(context, Widget.class);
            updateIntent.setAction(SOME_WIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, id, updateIntent, 0);
            views.setOnClickPendingIntent(R.id.button, pIntent);

            updateIntent = new Intent(context, Widget.class);
            updateIntent.setAction(DYNAMIC_WIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            pIntent = PendingIntent.getBroadcast(context, id, updateIntent, 0);
            views.setOnClickPendingIntent(R.id.tv_change, pIntent);
            views.setOnClickPendingIntent(R.id.tv_dyn_Dol, pIntent);
            views.setOnClickPendingIntent(R.id.tv_dyn_BTC, pIntent);

            views.setViewVisibility(R.id.progressBar, View.INVISIBLE);
            appWidgetManager.updateAppWidget(id, views);
            System.out.println("Update Done");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;


    }

    private void saveChangeData(Object[] data, int id, SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 4; i < 18; i++) {
            editor.putString(id + "change" + i, (String) data[i]);
        }
        editor.putInt(id + "change_counter", (int) data[18]);
        editor.apply();
    }

    private Object[] getChangeData(int id, SharedPreferences sp){
        try {
            String change1_24h = sp.getString(id +"change4", null);
            String change2_24h = sp.getString(id +"change5", null);
            String change1_7d = sp.getString(id +"change6", null);
            String change2_7d = sp.getString(id +"change7", null);
            String change1_14d = sp.getString(id +"change8", null);
            String change2_14d = sp.getString(id +"change9", null);
            String change1_30d = sp.getString(id +"change10", null);
            String change2_30d = sp.getString(id +"change11", null);
            String change1_60d = sp.getString(id +"change12", null);
            String change2_60d = sp.getString(id +"change13", null);
            String change1_200d = sp.getString(id +"change14", null);
            String change2_200d = sp.getString(id +"change15", null);
            String change1_1y = sp.getString(id +"change16", null);
            String change2_1y = sp.getString(id +"change17", null);
            int counter = sp.getInt(id + "change_counter", 0);

            return new Object[]{change1_24h, change2_24h, change1_7d, change2_7d, change1_14d, change2_14d,
                    change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Object[] getData(Intent intent){

        Object[] id_fullUpd = null;

        try {
            System.out.println("BEFORE GET");
            String[] strings = intent.getStringArrayExtra("res_strings");
            Bitmap icon = intent.getParcelableExtra("res_icon");
            int counter = intent.getIntExtra("res_counter", 2);
            int id = intent.getIntExtra("res_id", 999);
            boolean full_Upd = intent.getBooleanExtra("res_fullUpd", false);

            id_fullUpd = new Object[]{id, full_Upd};

            data = new Object[]{strings[0], icon, strings[1], strings[2], strings[3], strings[4]
            , strings[5], strings[6], strings[7], strings[8], strings[9], strings[10], strings[11]
            , strings[12], strings[13], strings[14], strings[15], strings[16], counter};
            System.out.println("AFTER GET");
            /*if (data.length == 1) Toast.makeText(context, R.string.notice_fail_get + String.valueOf(data[0]), Toast.LENGTH_SHORT).show();*/
            if (data == null) return new Object[0];
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0];

        }
        return id_fullUpd;
    }

    private boolean startAsync(String s, String cur1, String cur2, int wId, Context context, Object[] remoteObjects){
        if (!Utils.hasConnection(context)) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

        DataProvider provider = new DataProvider();
        String st = sp.getString("strategy" + wId, null);
        if (st == null) {
            Toast.makeText(context, R.string.notice_fail_load_sp, Toast.LENGTH_LONG).show();
            st = Utils.STRATEGY_COINGECKO;
        }

        provider.execute(s, cur1, cur2, st, this, remoteObjects);
        return true;
    }

    private void scheduleGetData(final Context context, final String s, final String cur1, final String cur2){
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("отложенная Проверка сети");
                if (Utils.hasConnection(context)) {
                   // getData(s, cur1, cur2, context);
                    timer.cancel();

                }
            }
        }, 2000, 2000);
    }

    private void saveIdDataForReboot(int id, String nameC, String cur1, String cur2, Context context){
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(id + "dataRebootNameC", nameC);
        editor.putString(id + "dataRebootCur1", cur1);
        editor.putString(id + "dataRebootCur2", cur2);
        editor.apply();
    }

    private String[] getIdDataForReboot(int id, Context context){
        String[] data = null;
        try {
            SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            data = new String[]{sp.getString(id + "dataRebootNameC", ""), sp.getString(id + "dataRebootCur1", ""),
                    sp.getString(id + "dataRebootCur2", "")};
            editor.remove(id + "dataRebootNameC");
            editor.remove(id + "dataRebootCur1");
            editor.remove(id + "dataRebootCur2");
            editor.apply();
            int time = sp.getInt(PREF_TIME, 5) * 60000;
            startAlarm(context, time);
        }catch (Exception e){
            return null;
        }
        return data;
    }

    private void enableProgress(int id, Context context, AppWidgetManager manager) throws Exception{
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setViewVisibility(R.id.progressBar, View.VISIBLE);
        manager.updateAppWidget(id, views);
    }

    private void disableProgress(int id, Context context, AppWidgetManager manager){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setViewVisibility(R.id.progressBar, View.INVISIBLE);
        manager.updateAppWidget(id, views);
    }

    public void updateProgress(Integer val, Object[] remoteObjects){
        try {
            int id = (int) remoteObjects[0];
            Context context = (Context) remoteObjects[1];
            AppWidgetManager manager = (AppWidgetManager) remoteObjects[2];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            String text = val == 100 ? "" : String.valueOf(val) + "%";
            views.setTextViewText(R.id.progressTV, text);
            manager.updateAppWidget(id, views);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void startAlarm(Context context, int xTime) throws Exception{
        System.out.println("Start Alarm");

        Intent intent = new Intent(context,Widget.class);
        intent.setAction(ALL_WIDGET_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (am != null) am.cancel(pendingIntent);
        else am = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        System.out.println("time " + xTime);


        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 60000, xTime, pendingIntent );
    }




}

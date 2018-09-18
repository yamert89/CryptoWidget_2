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
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import java.util.concurrent.ExecutionException;

import static android.content.Context.ALARM_SERVICE;
import static net.ucoz.softoad.cryptowidget_2.ConfigActivity.PREF_NAME;
import static net.ucoz.softoad.cryptowidget_2.ConfigActivity.PREF_TIME;

public class Widget extends AppWidgetProvider {
    static final String ALL_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.All_WIDGET_UPDATE";
    static final String DYNAMIC_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.DYNAMIC_WIDGET_UPDATE";
    PendingIntent pendingIntent;
    AlarmManager am;
    SparseArray<Object[]> dataMap = new SparseArray<>();


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("ACTION______________!!!" + intent.getAction());
        System.out.println("THIS = " + this.hashCode());
        System.out.println("CONTEXT = " + context.hashCode());
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && am == null) return;
        if (intent.getAction() == null || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED) ||
                        intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) return;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        String nameCurrency = "";
        String cur1 = sp.getString("cur1","usd");
        String cur2 = sp.getString("cur2","btc");

        if (intent.getAction().equals(ALL_WIDGET_UPDATE)){
            ComponentName thisWidget = new ComponentName(context, Widget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id :
                    appWidgetIds) {
                nameCurrency = sp.getString(PREF_NAME + id, "undefined");
                getData(id, nameCurrency, cur1, cur2);
                updateWidget(id, context, true, appWidgetManager);
            }
            return;

        }

        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        }

        nameCurrency = sp.getString(PREF_NAME + mAppWidgetId, "undefined");

        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)){

            int time = sp.getInt(PREF_TIME, 5) * 60000;
            startAlarm(context, time);
        }

        if (intent.getAction().equals(DYNAMIC_WIDGET_UPDATE)){
            updateWidget(mAppWidgetId, context, false, appWidgetManager);
            return;
        }
        SparseArray dataMap = getResultExtras(true).getSparseParcelableArray("datamap");
        if (dataMap != null) dataMap.remove(mAppWidgetId);

        getData(mAppWidgetId, nameCurrency, cur1, cur2);
        updateWidget(mAppWidgetId, context, true, appWidgetManager);







    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if (am != null) am.cancel(pendingIntent);
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);






    }

    @Override
    public void onDisabled(Context context) {
        if (am != null) am.cancel(pendingIntent);
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    private void updateWidget(int id, Context context, boolean full,  AppWidgetManager appWidgetManager){

        System.out.println("UPDATE WIDGET - " + id);

        System.out.println("DATAMAP:  " + dataMap.hashCode());
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);



        if (full) {
            Object[] data = dataMap.get(id);

            if (data[0] != null) views.setImageViewBitmap(R.id.ico, (Bitmap) data[1]);
            views.setTextViewText(R.id.tv_name, (String) data[0]);
            views.setTextViewText(R.id.tv_priceDol,(String) data[2]);
            views.setTextViewText(R.id.tv_priceBTC,(String) data[3]);
            views.setTextViewText(R.id.tv_dyn_Dol,(String) data[4]);
            views.setTextViewText(R.id.tv_dyn_BTC,(String) data[5]);

            int color = sp.getInt(ConfigActivity.PREF_COLOR + id, 0);
            views.setInt(R.id.general, "setBackgroundColor", color);
            System.out.println("DATA SAVE" + data.length);

           saveChangeData(data, sp);

            views.setViewVisibility(R.id.progressBar, View.INVISIBLE);
        }else {
            Object[] data2 = getChangeData(sp);
            int idx = (int) data2[14];
            System.out.println("IDX_________________! : " + idx);
            String header = "";
            for (Object ob :
                    data2) {
                System.out.println(ob);
            }
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

            views.setTextViewText(R.id.tv_dyn_Dol,(String) data2[idx]);
            views.setTextViewText(R.id.tv_dyn_BTC,(String) data2[++idx]);


            views.setTextViewText(R.id.tv_change, header);
            if (idx < 13) sp.edit().putInt("change_counter", ++idx).apply();
            else sp.edit().putInt("change_counter", 0).apply();



        }

        //onClick
        Intent updateIntent = new Intent(context, Widget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
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



        appWidgetManager.updateAppWidget(id, views);


    }

    private void saveChangeData(Object[] data, SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 4; i < 18; i++) {
            editor.putString("change" + i, (String) data[i]);
        }
        editor.putInt("change_counter", (int) data[18]);
        editor.apply();
    }

    private Object[] getChangeData(SharedPreferences sp){
        try {
            String change1_24h = sp.getString("change4", null);
            String change2_24h = sp.getString("change5", null);
            String change1_7d = sp.getString("change6", null);
            String change2_7d = sp.getString("change7", null);
            String change1_14d = sp.getString("change8", null);
            String change2_14d = sp.getString("change9", null);
            String change1_30d = sp.getString("change10", null);
            String change2_30d = sp.getString("change11", null);
            String change1_60d = sp.getString("change12", null);
            String change2_60d = sp.getString("change13", null);
            String change1_200d = sp.getString("change14", null);
            String change2_200d = sp.getString("change15", null);
            String change1_1y = sp.getString("change16", null);
            String change2_1y = sp.getString("change17", null);
            int counter = sp.getInt("change_counter", 0);

            return new Object[]{change1_24h, change2_24h, change1_7d, change2_7d, change1_14d, change2_14d,
                    change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void getData(int id, String s, String cur1, String cur2){
        DataProvider provider = new DataProvider();
        provider.execute(s, cur1, cur2);
        try {

            Object[] d = provider.get();
            dataMap.put(id, d);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    void startAlarm(Context context, int xTime){
        System.out.println("Start Alarm");

        Intent intent = new Intent(context,Widget.class);
        intent.setAction(ALL_WIDGET_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (am != null) am.cancel(pendingIntent);
        else am = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        System.out.println("time " + xTime);


        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 60000, xTime, pendingIntent ); //TODO
    }
}

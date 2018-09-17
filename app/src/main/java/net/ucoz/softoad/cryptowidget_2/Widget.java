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
        if (intent.getAction() == null || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED) ||
                        intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DELETED) ||
                                intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) return;
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
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        }
        dataMap.remove(mAppWidgetId);
        nameCurrency = sp.getString(PREF_NAME + mAppWidgetId, "undefined");

        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)){

            int time = sp.getInt(PREF_TIME, 5) * 60000;
            startAlarm(context, time);
        }

        if (intent.getAction().equals(DYNAMIC_WIDGET_UPDATE)){
            updateWidget(mAppWidgetId, context, false, appWidgetManager);
        }
        getData(mAppWidgetId, nameCurrency, cur1, cur2);
        updateWidget(mAppWidgetId, context, true, appWidgetManager);




    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);


    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    private void updateWidget(int id, Context context, boolean full,  AppWidgetManager appWidgetManager){
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Object[] data = dataMap.get(id);
        if (full) {
            if (data[0] != null) views.setImageViewBitmap(R.id.ico, (Bitmap) data[1]);
            views.setTextViewText(R.id.tv_name, (String) data[0]);
            views.setTextViewText(R.id.tv_priceDol,(String) data[2]);
            views.setTextViewText(R.id.tv_priceBTC,(String) data[3]);
            views.setTextViewText(R.id.tv_dyn_Dol,(String) data[4]);
            views.setTextViewText(R.id.tv_dyn_BTC,(String) data[5]);

            int color = sp.getInt(ConfigActivity.PREF_COLOR + id, 0);
            views.setInt(R.id.general, "setBackgroundColor", color);

            if (((String) data[0]).length() > 20)
                views.setViewVisibility(R.id.tv_change, View.INVISIBLE);
            if (((String) data[0]).length() > 14)
                views.setViewVisibility(R.id.tv_price, View.INVISIBLE);

            views.setViewVisibility(R.id.progressBar, View.INVISIBLE);
        }else {
            int idx = (int) data[20];
            views.setTextViewText(R.id.tv_dyn_Dol,(String) data[idx]);
            views.setTextViewText(R.id.tv_dyn_BTC,(String) data[++idx]);
            String header = "";
            switch (idx){
                case 4:
                    header = "24h";
                    break;
                case 6:
                    header = "1d";
                    break;
                case 8:
                    header = "7d";
                    break;
                case 10:
                    header = "14d";
                    break;
                case 12:
                    header = "30d";
                    break;
                case 14:
                    header = "60d";
                    break;
                case 16:
                    header = "200d";
                    break;
                case 18:
                    header = "1y";
                    break;
            }
            views.setTextViewText(R.id.tv_change, header);
            data[20] = idx++;
        }
        appWidgetManager.updateAppWidget(id, views);


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

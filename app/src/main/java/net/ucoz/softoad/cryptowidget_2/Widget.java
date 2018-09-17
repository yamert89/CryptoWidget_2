package net.ucoz.softoad.cryptowidget_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class Widget extends AppWidgetProvider {
    static final String FORCE_WIDGET_UPDATE = "net.ucoz.softoad.cryptowidget.FORCE_WIDGET_UPDATE";
    PendingIntent pendingIntent;
    AlarmManager am;
    SparseArray<Object[]> dataMap = new SparseArray<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == null) return;
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)){
                startAlarm(context);
        }


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

    private void updateWidget(int id, Context context, boolean full){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Object[] data = dataMap.get(id);
        if (full) {
            if (data[0] != null) views.setImageViewBitmap(R.id.ico, (Bitmap) data[1]);
            views.setTextViewText(R.id.tv_name, (String) data[0]);
            views.setTextViewText(R.id.tv_priceDol,(String) data[2]);
            views.setTextViewText(R.id.tv_priceBTC,(String) data[3]);
            views.setTextViewText(R.id.tv_dyn_Dol,(String) data[4]);
            views.setTextViewText(R.id.tv_dyn_BTC,(String) data[5]);

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
            data[20] = idx++;
        }
        appWidgetManager.updateAppWidget(id, views);


    }

    private void getData(){

    }

    void startAlarm(Context context, int ...xTime){
        System.out.println("Start Alarm");

        Intent intent = new Intent(context,Widget.class);
        intent.setAction(FORCE_WIDGET_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (am != null) am.cancel(pendingIntent);
        else am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        int time = 0;

        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

        time = sp.getInt(ConfigActivity.PREF_TIME, 5) * 60000;
        if (xTime.length > 0){
            time = xTime[0];
        }
        System.out.println("time " + time);


        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 60000, time, pendingIntent ); //TODO
    }
}

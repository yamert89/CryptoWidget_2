package net.ucoz.softoad.cryptowidget_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.ucoz.softoad.cryptowidget_2.strategies.CoinGeckoStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.CoinMarketCapStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.Strategy;

import org.jsoup.Connection;

import static net.ucoz.softoad.cryptowidget_2.Widget.SOME_WIDGET_ERROR;
import static net.ucoz.softoad.cryptowidget_2.Widget.SOME_WIDGET_RESULT;

public class DataProvider extends AsyncTask<Object, Integer, Integer> {

    private Object[] respResult1;
    private Object[] respResult2;
    private int st;
    private Widget widget;
    private Object[] remoteObjects;


    @Override
    protected Integer doInBackground(Object... inputs) {
        String name = (String) inputs[0];
        String cur1 = (String) inputs[1];
        String cur2 = (String) inputs[2];
        String strat = (String) inputs[3];
        widget = (Widget) inputs[4];
        remoteObjects = (Object[]) inputs[5];
        //String strat= "coinmarketcap";
        Strategy strategy = null;
        JsonElement element1 = null;
        JsonElement element2 = null;

        if(strat == null) strat = Utils.STRATEGY_COINGECKO;

        publishProgress(10);

        switch (strat){
            case Utils.STRATEGY_COINGECKO:
                strategy = new CoinGeckoStrategy(name, cur1, cur2);
                respResult1 = strategy.connection();

                if ((st = checkStatus(respResult1)) != 0){
                    sendErrorMessage(st);
                    return st;
                };
                Connection.Response response = (Connection.Response) respResult1[0];
                String json = response.body();

                element1 = new JsonParser().parse(json);
                break;

            case Utils.STRATEGY_COINMARKETCAP:
                strategy = new CoinMarketCapStrategy(name, cur1, cur2);
                respResult1 = strategy.connection();
                publishProgress(40);
                respResult2 = strategy.connection();

                if ((st = checkStatus(respResult1)) != 0){
                    sendErrorMessage(st);

                };
                if ((st = checkStatus(respResult2)) != 0){
                    sendErrorMessage(st);
                    return st;
                };

                Connection.Response response1 = (Connection.Response) respResult1[0];
                Connection.Response response2 = (Connection.Response) respResult2[0];

                String json_cur1 = response1.body();
                String json_cur2 = response2.body();

                element1 = new com.google.gson.JsonParser().parse(json_cur1);
                element2 = new com.google.gson.JsonParser().parse(json_cur2);


                break;
        }

        publishProgress(80);
        strategy.getCurrencyData(element1, element2);
        String[] strings = strategy.getStrings();
        int counter = strategy.getCounter();
        Bitmap icon = strategy.getIcon();
        publishProgress(100);
        successIntent(strings, icon, counter);

        return 0;
    }

    private void successIntent(String[] strings, Bitmap icon, int counter){
        Context context = (Context) remoteObjects[1];
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(SOME_WIDGET_RESULT);

        intent.putExtra("res_strings", strings);
        intent.putExtra("res_counter", counter);
        intent.putExtra("res_icon", icon);
        intent.putExtra("res_id", (int) remoteObjects[0]);
        intent.putExtra("res_fullUpd", (boolean) remoteObjects[3]);
        context.sendBroadcast(intent);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        try {
            widget.updateProgress(values[0], remoteObjects);
            System.out.println("Update " + values[0]);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private int checkStatus(Object[] respResult){
        Connection.Response response = (Connection.Response) respResult[0];
        int status = (int) respResult[1];
        if (respResult.length < 2) {
            return (int) respResult[0];
        }
        return 0; //success
    }

    private void sendErrorMessage(int statusCode){
        Context context = (Context) remoteObjects[1];
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(SOME_WIDGET_ERROR);
        intent.putExtra("err_status", statusCode);
        intent.putExtra("err_id", (int) remoteObjects[0]);
        context.sendBroadcast(intent);
    }

    private void printJson(String json){
        System.out.println("RESPONSE : ");
        try {
            for (int i = 0; i < json.length(); i += 172) {
                System.out.println(json.substring(i, i + 172));
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("out of range");
        }
    }



}

package net.ucoz.softoad.cryptowidget_2;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.ucoz.softoad.cryptowidget_2.strategies.CoinGeckoStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.CoinMarketCapStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.Strategy;

import org.jsoup.Connection;

public class DataProvider extends AsyncTask<String, Void, Object[]> {

    private Object[] respResult1;
    private Object[] respResult2;
    private int st;


    @Override
    protected Object[] doInBackground(String... strings) {
        String name = strings[0];
        String cur1 = strings[1];
        String cur2 = strings[2];
        String strat = strings[3];
        //String strat= "coinmarketcap";
        Strategy strategy = null;
        JsonElement element1 = null;
        JsonElement element2 = null;

        switch (strat){
            case Utils.STRATEGY_COINGECKO:
                strategy = new CoinGeckoStrategy(name, cur1, cur2);
                respResult1 = strategy.connection();

                if ((st = checkStatus(respResult1)) != 0){
                    Object[] fail = new Object[1];
                    fail[0] = st;
                    return fail;
                };
                Connection.Response response = (Connection.Response) respResult1[0];
                String json = response.body();

                element1 = new JsonParser().parse(json);
                break;

            case Utils.STRATEGY_COINMARKETCAP:
                strategy = new CoinMarketCapStrategy(name, cur1, cur2);
                respResult1 = strategy.connection();
                respResult2 = strategy.connection();

                if ((st = checkStatus(respResult1)) != 0){
                    Object[] fail = new Object[1];
                    fail[0] = st;
                    return fail;
                };
                if ((st = checkStatus(respResult2)) != 0){
                    Object[] fail = new Object[1];
                    fail[0] = st;
                    return fail;
                };

                Connection.Response response1 = (Connection.Response) respResult1[0];
                Connection.Response response2 = (Connection.Response) respResult2[0];

                String json_cur1 = response1.body();
                String json_cur2 = response2.body();

                element1 = new com.google.gson.JsonParser().parse(json_cur1);
                element2 = new com.google.gson.JsonParser().parse(json_cur2);


                break;
        }


        return strategy.getCurrencyData(element1, element2);
    }

    private int checkStatus(Object[] respResult){
        Connection.Response response = (Connection.Response) respResult[0];
        int status = (int) respResult[1];
        if (response == null || status == 403) {
            return status;
        }
        return 0; //success
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

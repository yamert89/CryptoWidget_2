package net.ucoz.softoad.cryptowidget_2;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.ucoz.softoad.cryptowidget_2.strategies.CoinGeckoStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.CoinMarketCapStrategy;
import net.ucoz.softoad.cryptowidget_2.strategies.Strategy;

import org.jsoup.Connection;

public class DataProvider extends AsyncTask<String, Void, Object[]> {


    @Override
    protected Object[] doInBackground(String... strings) {
        String name = strings[0];
        String cur1 = strings[1];
        String cur2 = strings[2];
        //String strat= strings[3];
        String strat= "coinmarketcap";
        Strategy strategy = null;

        switch (strat){
            case "coingecko":
                strategy = new CoinGeckoStrategy(name, cur1, cur2);
                break;
            case "coinmarketcap":
                strategy = new CoinMarketCapStrategy(name, cur1, cur2);
                break;
        }

        Object[] respResult = strategy.connection();

        Connection.Response response = (Connection.Response) respResult[0];
        int status = (int) respResult[1];
        if (response == null || status == 403) {
            Object[] fail = new Object[18];
            fail[17] = status;
            return fail;
        }

        String json = response.body();

        System.out.println("RESPONSE : ");
        try {
            for (int i = 0; i < json.length(); i += 172) {
                System.out.println(json.substring(i, i + 172));
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("out of range");
        }


        JsonElement element = new JsonParser().parse(json);

        return strategy.getCurrencyData(element);
    }



}

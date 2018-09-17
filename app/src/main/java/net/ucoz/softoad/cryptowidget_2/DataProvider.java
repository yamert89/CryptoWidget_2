package net.ucoz.softoad.cryptowidget_2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;

public class DataProvider extends AsyncTask<String, Void, Object[]> {


    @Override
    protected Object[] doInBackground(String... strings) {
        return getCurrencyData(strings[0]);
    }

    private Object[] getCurrencyData(String name){
        String price1 = null;
        String price2 = null;
        String change1_24h = null;
        String change1_7d = null;
        String change1_1d = null;
        String change1_14d = null;
        String change1_30d = null;
        String change1_60d = null;
        String change1_200d = null;
        String change1_1y = null;

        String change2_24h = null;
        String change2_7d = null;
        String change2_1d = null;
        String change2_14d = null;
        String change2_30d = null;
        String change2_60d = null;
        String change2_200d = null;
        String change2_1y = null;

        Connection.Response response = null;
        try {
            response = Jsoup.connect("https://api.coingecko.com/api/v3/coins/" + name + "?localization=ru&sparkline=false").ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = response.body();
        JsonElement element = new com.google.gson.JsonParser().parse(json);

        JsonObject market_data = element.getAsJsonObject().get("market_data").getAsJsonObject();
        JsonObject current_price = market_data.get("current_price").getAsJsonObject();

        price1 = current_price.get("btc").getAsString();
        price2 = current_price.get("usd").getAsString();
        change1_24h = market_data.get("price_change_percentage_24h_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_24h = market_data.get("price_change_percentage_24h_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_1d = market_data.get("price_change_percentage_1d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_1d = market_data.get("price_change_percentage_1d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_7d = market_data.get("price_change_percentage_7d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_7d = market_data.get("price_change_percentage_7d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_14d = market_data.get("price_change_percentage_14d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_14d = market_data.get("price_change_percentage_14d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_30d = market_data.get("price_change_percentage_30d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_30d = market_data.get("price_change_percentage_30d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_60d = market_data.get("price_change_percentage_60d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_60d = market_data.get("price_change_percentage_60d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_200d = market_data.get("price_change_percentage_200d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_200d = market_data.get("price_change_percentage_200d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_1y = market_data.get("price_change_percentage_1y_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_1y = market_data.get("price_change_percentage_1y_in_currency").getAsJsonObject().get("usd").getAsString();
        String ico_url = element.getAsJsonObject().get("image").getAsJsonObject().get("small").getAsString();
        Bitmap image = loadBitmap(ico_url);
        int counter = 4;

        return new Object[]{name, image, price1, price2, change1_24h, change2_24h, change1_1d, change2_1d, change1_7d, change2_7d,
        change1_14d, change2_14d, change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};

    }

    private Bitmap loadBitmap(String url){
        URL newurl = null;
        Bitmap icon = null;
        try {
            newurl = new URL(url);
            icon = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка получения иконки");
            //FirebaseCrash.log("Ошибка получения иконки");
        }

        return icon;
    }

}

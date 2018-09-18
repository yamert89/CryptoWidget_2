package net.ucoz.softoad.cryptowidget_2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;

public class DataProvider extends AsyncTask<String, Void, Object[]> {
    private String name;
    private String cur1;
    private String cur2;
    private JsonObject market_data;


    @Override
    protected Object[] doInBackground(String... strings) {
        name = strings[0];
        cur1 = strings[1];
        cur2 = strings[2];

        return getCurrencyData(name, cur1, cur2);
    }

    private Object[] getCurrencyData(String name, String cur1, String cur2){
        String price1 = null;
        String price2 = null;
        String change1_24h = null;
        String change1_7d = null;
        String change1_14d = null;
        String change1_30d = null;
        String change1_60d = null;
        String change1_200d = null;
        String change1_1y = null;

        String change2_24h = null;
        String change2_7d = null;
        String change2_14d = null;
        String change2_30d = null;
        String change2_60d = null;
        String change2_200d = null;
        String change2_1y = null;

        try {
            System.out.println("name = [" + name + "], cur1 = [" + cur1 + "], cur2 = [" + cur2 + "]");

            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://api.coingecko.com/api/v3/coins/" + name + "?localization=ru&sparkline=false").ignoreContentType(true).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String json = response.body();
            JsonElement element = new JsonParser().parse(json);

            market_data = element.getAsJsonObject().get("market_data").getAsJsonObject();
            JsonObject current_price = market_data.get("current_price").getAsJsonObject();

            price1 = current_price.get(cur1).getAsString() + " " + cur1;
            price2 = current_price.get(cur2).getAsString() + " " + cur2;
            change1_24h = getChangePrepared("price_change_percentage_24h_in_currency", cur1);
            change2_24h = getChangePrepared("price_change_percentage_24h_in_currency", cur2);
            change1_7d =  getChangePrepared("price_change_percentage_7d_in_currency", cur1);
            change2_7d =  getChangePrepared("price_change_percentage_7d_in_currency", cur2);
            change1_14d = getChangePrepared("price_change_percentage_14d_in_currency", cur1);
            change2_14d = getChangePrepared("price_change_percentage_14d_in_currency", cur2);
            change1_30d = getChangePrepared("price_change_percentage_30d_in_currency", cur1);
            change2_30d = getChangePrepared("price_change_percentage_30d_in_currency", cur2);
            change1_60d = getChangePrepared("price_change_percentage_60d_in_currency", cur1);
            change2_60d = getChangePrepared("price_change_percentage_60d_in_currency", cur2);
            change1_200d = getChangePrepared("price_change_percentage_200d_in_currency", cur1);
            change2_200d = getChangePrepared("price_change_percentage_200d_in_currency", cur2);
            change1_1y = getChangePrepared("price_change_percentage_1y_in_currency", cur1);
            change2_1y = getChangePrepared("price_change_percentage_1y_in_currency", cur2);
            String ico_url = element.getAsJsonObject().get("image").getAsJsonObject().get("small").getAsString();
            Bitmap image = loadBitmap(ico_url);
            int counter = 4;



            return new Object[]{name, image, price1, price2, change1_24h, change2_24h, change1_7d, change2_7d,
                    change1_14d, change2_14d, change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private String getChangePrepared(String param, String cur){
        String s = market_data.get(param).getAsJsonObject().get(cur).getAsString();
        if (s.length() > 5)s = s.substring(0,5);
        return s + "%";
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

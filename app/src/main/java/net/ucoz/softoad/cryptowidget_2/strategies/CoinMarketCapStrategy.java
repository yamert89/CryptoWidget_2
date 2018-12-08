package net.ucoz.softoad.cryptowidget_2.strategies;

import android.graphics.Bitmap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class CoinMarketCapStrategy extends Strategy {
    private String url;

    private final String API_KEY = "9453bb8b-d186-4d67-99ea-bcd4de0cbf32";
    private final String API_REQUIRED_HEADER = "X-CMC_PRO_API_KEY";

    public CoinMarketCapStrategy(String name, String cur1, String cur2) {
        super(name, cur1, cur2);
    }

    @Override
    public Object[] connection() {
        System.out.println("name = [" + name + "], cur1 = [" + cur1 + "], cur2 = [" + cur2 + "]");
        Connection.Response response = null;

        url = url == null ? "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + name + "&convert=" + cur1
                : "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + name + "&convert=" + cur2;
        try {
            response = Jsoup.connect(url)
                    .header(API_REQUIRED_HEADER, API_KEY)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

        }catch (IOException e) {

            e.printStackTrace();
            System.out.println("Повторное подключение");
            try {
                response = Jsoup.connect(url)
                        .header(API_REQUIRED_HEADER, API_KEY)
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();

            } catch (IOException e1) {
                e1.printStackTrace();
                return new Object[]{null, response.statusCode()};
            }
        }

        return new Object[]{response, response.statusCode()};
    }

    @Override
    public Object[] getCurrencyData(JsonElement ... element) {
        try {

            JsonObject data = element[0].getAsJsonObject().get("data").getAsJsonObject();
            JsonObject name_obj1 = data.get(name).getAsJsonObject();
            JsonObject cur1_data = name_obj1.get("quote").getAsJsonObject().get(cur1).getAsJsonObject();

            JsonObject data2 = element[1].getAsJsonObject().get("data").getAsJsonObject();
            JsonObject name_obj2 = data2.get(name).getAsJsonObject();
            JsonObject cur2_data = name_obj2.get("quote").getAsJsonObject().get(cur2).getAsJsonObject();

            price1 = cur1_data.get("price").getAsString();
            price2 = cur2_data.get("price").getAsString();

            if (price1.length() > 16) price1 = price1.substring(0,16);
            if (price2.length() > 16) price2 = price2.substring(0,16);

            price1 += "  " + cur1;
            price2 += "  " + cur2;

            change1_24h = cur1_data.get("percent_change_24h").getAsString();
            change2_24h = cur2_data.get("percent_change_24h").getAsString();;
            change1_7d = cur1_data.get("percent_change_7d").getAsString();
            change2_7d = cur2_data.get("percent_change_7d").getAsString();
            change1_14d = "?";
            change2_14d = "?";
            change1_30d = "?";
            change2_30d = "?";
            change1_60d = "?";
            change2_60d = "?";
            change1_200d = "?";
            change2_200d = "?";
            change1_1y = "?";
            change2_1y = "?";

            checkNulls(change1_24h, change2_24h, change1_7d, change2_7d);

            Connection.Response response = Jsoup.connect("https://pro-api.coinmarketcap.com/v1/cryptocurrency/info?symbol=" + name)
                    .header(API_REQUIRED_HEADER, API_KEY)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JsonElement ico_element = new com.google.gson.JsonParser().parse(response.body());


            String ico_url = ico_element.getAsJsonObject().get("data").getAsJsonObject().get(name)
                    .getAsJsonObject().get("logo").getAsString();
            Bitmap image = loadBitmap(ico_url);
            int counter = 2;



            return new Object[]{name, image, price1, price2, change1_24h, change2_24h, change1_7d, change2_7d,
                    change1_14d, change2_14d, change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return new Object[0];
    }

    @Override
    public String getChangePrepared(String param, String cur) {
        return null;
    }

    private void checkNulls(String ... params){
        for (String par :
                params) {
            if (par == null) par = "?";
        }
    }


}

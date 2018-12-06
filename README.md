CryptoWidget#2
published on https://play.google.com/store/apps/details?id=net.ucoz.softoad.cryptowidget

https://pro.coinmarketcap.com/api/v1#section/Quick-Start-Guide


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileWriter;

public class Jsoup1 {
    private static final String API_KEY = "9453bb8b-d186-4d67-99ea-bcd4de0cbf32";
    private static final String API_REQUIRED_HEADER = "X-CMC_PRO_API_KEY";

    private static JsonObject market_data;

    static String name;
    static String cur1;
    static String cur2;

    static String price1 = null;
    static String price2 = null;
    static String change1_24h = null;
    static String change1_7d = null;
    static String change1_14d = null;
    static String change1_30d = null;
    static String change1_60d = null;
    static String change1_200d = null;
    static String change1_1y = null;

    static String change2_24h = null;
    static String change2_7d = null;
    static String change2_14d = null;
    static String change2_30d = null;
    static String change2_60d = null;
    static String change2_200d = null;
    static String change2_1y = null;

    public static void main(String[] args) {
        try{
            connect();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void connect() throws Exception{
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        Connection.Response response = Jsoup.connect(url) //TODO
                .header(API_REQUIRED_HEADER, API_KEY)
                .data("symbol", "BTC")
                .data("convert", "USD" + "," + "RUB")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        System.out.println(response);

        String json = response.body();

        JsonElement element = new com.google.gson.JsonParser().parse(json);

        JsonObject market_data = element.getAsJsonObject().get("market_data").getAsJsonObject();
        JsonObject current_price = market_data.get("current_price").getAsJsonObject();

        price1 = current_price.get("btc").getAsString();
        price2 = current_price.get("usd").getAsString();
        change1_24h = market_data.get("price_change_percentage_24h_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_24h = market_data.get("price_change_percentage_24h_in_currency").getAsJsonObject().get("usd").getAsString();
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

        String ico = element.getAsJsonObject().get("image").getAsJsonObject().get("small").getAsString();
    }
}


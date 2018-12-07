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

    private static JsonObject data;

    static String name = "BTC";
    static String cur1 = "USD";
    static String cur2 = "RUB";

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
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + name + "&convert=" + cur1;
        String url2 = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + name + "&convert=" + cur2;
        Connection.Response response = Jsoup.connect(url) //TODO
                .header(API_REQUIRED_HEADER, API_KEY)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        Connection.Response response2 = Jsoup.connect(url2) //TODO
                .header(API_REQUIRED_HEADER, API_KEY)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();


        String json_cur1 = response.body();
        String json_cur2 = response2.body();

        JsonElement element = new com.google.gson.JsonParser().parse(json_cur1);
        JsonObject data = element.getAsJsonObject().get("data").getAsJsonObject();
        JsonObject name_obj1 = data.get(name).getAsJsonObject();
        JsonObject cur1_data = name_obj1.get("quote").getAsJsonObject().get(cur1).getAsJsonObject();

        JsonElement element2 = new com.google.gson.JsonParser().parse(json_cur2);
        JsonObject data2 = element.getAsJsonObject().get("data").getAsJsonObject();
        JsonObject name_obj2 = data.get(name).getAsJsonObject();
        JsonObject cur2_data = name_obj1.get("quote").getAsJsonObject().get(cur1).getAsJsonObject();



        price1 = cur1_data.get("price").getAsString();
        price2 = cur2_data.get("price").getAsString();
        change1_24h = cur1_data.get("volume_24h").getAsString();
        change2_24h = cur2_data.get("volume_24h").getAsString();;
        change1_7d = data.get("price_change_percentage_7d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_7d = data.get("price_change_percentage_7d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_14d = data.get("price_change_percentage_14d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_14d = data.get("price_change_percentage_14d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_30d = data.get("price_change_percentage_30d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_30d = data.get("price_change_percentage_30d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_60d = data.get("price_change_percentage_60d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_60d = data.get("price_change_percentage_60d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_200d = data.get("price_change_percentage_200d_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_200d = data.get("price_change_percentage_200d_in_currency").getAsJsonObject().get("usd").getAsString();
        change1_1y = data.get("price_change_percentage_1y_in_currency").getAsJsonObject().get("btc").getAsString();
        change2_1y = data.get("price_change_percentage_1y_in_currency").getAsJsonObject().get("usd").getAsString();

        String ico = element.getAsJsonObject().get("image").getAsJsonObject().get("small").getAsString();
    }
}

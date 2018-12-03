package net.ucoz.softoad.cryptowidget_2.strategies;

import android.graphics.Bitmap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class CoinGeckoStrategy extends Strategy {
    private JsonObject market_data;

    public CoinGeckoStrategy(String name, String cur1, String cur2) {
        super(name, cur1, cur2);
    }

    @Override
    public Object[] connection() {
        System.out.println("name = [" + name + "], cur1 = [" + cur1 + "], cur2 = [" + cur2 + "]");
        Connection.Response response = null;
        String url = "https://api.coingecko.com/api/v3/coins/" + name + "?localization=ru";
        try {

            System.out.println("URL : " + url);
            response = Jsoup.connect(url)
                    .header(":authority","api.coingecko.com")
                    .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    //.header("accept-encoding","zip, deflate, br")
                    //.header("accept-language","ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("cache-control","max-age=0")
                    .header("upgrade-insecure-requests","1")
                    .referrer("https://api.coingecko.com")
                    .timeout(15000)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.99")
                    .execute();
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Повторное подключение");
            try {
                response = Jsoup.connect(url).ignoreContentType(true).execute();
            } catch (IOException e1) {
                e1.printStackTrace();
                return new Object[]{null, response.statusCode()};
            }
        }

        return new Object[]{response, response.statusCode()};
    }

    @Override
    public Object[] getCurrencyData(JsonElement element){

        try {


            market_data = element.getAsJsonObject().get("market_data").getAsJsonObject();
            JsonObject current_price = market_data.get("current_price").getAsJsonObject();

            price1 = current_price.get(cur1).getAsString();
            price2 = current_price.get(cur2).getAsString();

            if (price1.length() > 16) price1 = price1.substring(0,16);
            if (price2.length() > 16) price2 = price2.substring(0,16);

            price1 += "  " + cur1;
            price2 += "  " + cur2;

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
            int counter = 2;



            return new Object[]{name, image, price1, price2, change1_24h, change2_24h, change1_7d, change2_7d,
                    change1_14d, change2_14d, change1_30d, change2_30d, change1_60d, change2_60d, change1_200d, change2_200d, change1_1y, change2_1y, counter};

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    @Override
    public String getChangePrepared(String param, String cur){
        String s = null;
        try {
            s = market_data.get(param).getAsJsonObject().get(cur).getAsString();
            if (s.length() > 5) s = s.substring(0, 5);
        }catch (NullPointerException e){
            //System.out.println("!!!!!!!!!!!!String = " + s);
            return "?";
        }
        return s + "%";
    }
}
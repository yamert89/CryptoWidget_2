package net.ucoz.softoad.cryptowidget_2.strategies;

import com.google.gson.JsonElement;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class CoinMarketCapStrategy extends Strategy {

    private String apiKey = "9453bb8b-d186-4d67-99ea-bcd4de0cbf32";

    public CoinMarketCapStrategy(String name, String cur1, String cur2) {
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
    public Object[] getCurrencyData(JsonElement element) {
        return new Object[0];
    }

    @Override
    public String getChangePrepared(String param, String cur) {
        return null;
    }
}

package net.ucoz.softoad.cryptowidget_2.strategies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonElement;

import org.jsoup.Connection;

import java.io.IOException;
import java.net.URL;

public abstract class Strategy {
    Connection.Response response;
    String name;
    String cur1;
    String cur2;

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

    public Strategy(String name, String cur1, String cur2) {
        this.name = name;
        this.cur1 = cur1;
        this.cur2 = cur2;
    }

    public abstract Object[] connection(); //2nd val for return = status code
                                            // 1st val - response

    public abstract Object[] getCurrencyData(JsonElement element);

    public abstract String getChangePrepared(String param, String cur);

    public Bitmap loadBitmap(String url){
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

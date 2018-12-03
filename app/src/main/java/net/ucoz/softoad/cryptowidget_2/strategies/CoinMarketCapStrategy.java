package net.ucoz.softoad.cryptowidget_2.strategies;

import com.google.gson.JsonElement;

public class CoinMarketCapStrategy extends Strategy {

    public CoinMarketCapStrategy(String name, String cur1, String cur2) {
        super(name, cur1, cur2);
    }

    @Override
    public Object[] connection() {
        return null;
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

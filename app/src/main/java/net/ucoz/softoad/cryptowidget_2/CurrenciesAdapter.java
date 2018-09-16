package net.ucoz.softoad.cryptowidget_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Пендальф Синий on 14.04.2018.
 */

public class CurrenciesAdapter extends BaseAdapter implements Filterable {

   // private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<String> mResults;


    public CurrenciesAdapter(Context mContext) {
        this.mContext = mContext;
        this.mResults = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public String getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.simple_dropdown_item, parent, false);
        }
        String itemName = getItem(position);
        ((TextView)convertView.findViewById(R.id.list_item)).setText(itemName);
        //System.out.println("getView");

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> items = findCurrencies(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = items;
                    filterResults.count = items.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        System.out.println("getFilter");
        return filter;
    }

    private List<String> findCurrencies(String s) {
        List<String> list = null;
        try {
            list = new ArrayList<>();
            for (String name :
                    Widget.listOfCrypt.keySet()) {
                if (name.toLowerCase().startsWith(s.toLowerCase())) list.add(name);
            }
            System.out.println("findCurrencies");
        }catch (NullPointerException e){
            Widget.parserLinksExec();
            return findCurrencies(s);
        }

        return list;
    }


}

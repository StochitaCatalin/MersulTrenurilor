package com.stochitacatalin.mersultrenurilor.Adaptoare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.stochitacatalin.mersultrenurilor.DataBaseHelper;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Tren;

public class CautaTrenuriAdapter extends BaseAdapter implements Filterable {
        private Tren[] list;
        private Context context;
        private ListFilter listFilter = new ListFilter();
        private DataBaseHelper dataBaseHelper;
        private Tren selected;
        public CautaTrenuriAdapter(Context context,DataBaseHelper dataBaseHelper) {
            this.context = context;
            list = new Tren[0];
            this.dataBaseHelper = dataBaseHelper;
        }

        public Tren getSelected() {
            return selected;
        }

        public void save(int pos){
            selected = list[pos];
        }

        public boolean save(){
            if(list == null || list.length == 0)
                return false;
            selected = list[0];
            return true;
        }
        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Tren getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.item_statie,parent,false);

            TextView name = listItem.findViewById(R.id.text1);
            name.setText(list[position].getNumar());
            return listItem;
        }

        @Override
        public Filter getFilter() {
            return listFilter;
        }

        public class ListFilter extends Filter {
            private final Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();

                if (prefix == null || prefix.length() == 0) {
                    synchronized (lock) {
                        results.values = new Tren[0];
                        results.count = 0;
                    }
                } else {
                    Tren[] matchValues =
                            dataBaseHelper.getTrenuri(prefix.toString(),10);

                    results.values = matchValues;
                    results.count = matchValues.length;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    list = (Tren[]) results.values;
                } else {
                    list = null;
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        }
    }
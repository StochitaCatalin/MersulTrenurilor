package com.stochitacatalin.mersultrenurilor.Adaptoare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stochitacatalin.mersultrenurilor.ElementTrasa;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.TrenTrasa;
import com.stochitacatalin.mersultrenurilor.Utils;


public class StatieTrenAdapter extends ArrayAdapter<ElementTrasa> {
    private int ch;
    private String trenschimb,asteptare;
    public StatieTrenAdapter(TrenTrasa tren, Context context,int ch,String trenschimb,String asteptare) {
        super(context,R.layout.item_statie_tren,tren.getEtrasa());
        this.ch = ch;
        this.trenschimb  = trenschimb;
        this.asteptare = asteptare;
    }

    public int getViewTypeCount()
    {
        //return 2;
        return 3;
    }
    public int getItemViewType(int position)
    {
        if(position == getCount() - 1)
            return 2;
        if (position != ch)
        {
            return 0;
        }
        return 1;
    }

    @Override
    public int getCount() {
        if(ch != -1)
            return super.getCount() + 2;
        else
            return super.getCount() + 1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            if(ch!=-1 && position > ch)
                position--;
            ElementTrasa item = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_statie_tren, parent, false);
                viewHolder.statie = convertView.findViewById(R.id.tren);
                viewHolder.sosire = convertView.findViewById(R.id.sosire);
                viewHolder.plecare = convertView.findViewById(R.id.plecare);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            assert item != null;
            viewHolder.statie.setText(item.getStatie().getName());
            viewHolder.plecare.setText(Utils.toTime(item.getOraPlecare(), false));
            viewHolder.sosire.setText(Utils.toTime(item.getOraSosire(), false));

            return convertView;
        }
        else if(viewType == 1){
            SchimbareHolder viewHolder;
            if(convertView == null){
                viewHolder = new SchimbareHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_schimbare,parent,false);
                viewHolder.tren = convertView.findViewById(R.id.tren);
                viewHolder.asteptare = convertView.findViewById(R.id.timpAsteptare);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (SchimbareHolder) convertView.getTag();
            }
            viewHolder.tren.setText(trenschimb);
            viewHolder.asteptare.setText(asteptare);
            return convertView;
        }else{
            MissingHolder missingHolder;
            if(convertView == null){
                missingHolder = new MissingHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_train_add,parent,false);
                missingHolder.text = convertView.findViewById(R.id.text);
                convertView.setTag(missingHolder);
            }else{
                missingHolder = (MissingHolder) convertView.getTag();
            }
            missingHolder.text.setText("Statie lipsa?");
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView statie,sosire,plecare;
    }
    private static class SchimbareHolder{
        TextView tren,asteptare;
    }
    private static class MissingHolder{
        TextView text;
    }
}
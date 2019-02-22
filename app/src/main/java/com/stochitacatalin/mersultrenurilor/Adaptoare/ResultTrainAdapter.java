package com.stochitacatalin.mersultrenurilor.Adaptoare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Ruta;
import com.stochitacatalin.mersultrenurilor.TrenRuta;
import com.stochitacatalin.mersultrenurilor.Utils;

import java.util.ArrayList;
import java.util.Objects;


public class ResultTrainAdapter extends ArrayAdapter<Ruta> {

    private boolean missing;
    public ResultTrainAdapter(ArrayList<Ruta> trenuri, Context context,boolean missing) {
        super(context, R.layout.item_train, trenuri);
        this.missing = missing;
    }

    public int getViewTypeCount()
    {
        return missing?3:2;
    }

    @Override
    public int getCount() {
        int c = super.getCount();
        if(missing && c!= 0)
            return c+1;
        else
            return c;
    }

    public int getItemViewType(int position)
    {
        if(missing && position == getCount() - 1)
            return 2;
        if (Objects.requireNonNull(getItem(position)).getTrenuri().size() == 1)
        {
            return 0;
        }


        return 1;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        if(viewType == 0) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_train, parent, false);
                viewHolder.fromtime = convertView.findViewById(R.id.oraplecare);
                viewHolder.totime = convertView.findViewById(R.id.orasosire);
                viewHolder.numar = convertView.findViewById(R.id.text);
                viewHolder.serviciilist = convertView.findViewById(R.id.servicii);
                viewHolder.linearLayoutZi = convertView.findViewById(R.id.linzi);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }
            TrenRuta item = getItem(position).getTrenuri().get(0);
            assert item != null;
            String tnume = item.getCategorie() + " " + item.getTren();
            viewHolder.numar.setText(tnume);
            viewHolder.fromtime.setText(Utils.toTime(item.getForaPlecare(), false));
            viewHolder.totime.setText(Utils.toTime(item.getToraSosire(), false));
            int zp = item.getForaPlecare() / 86400;
            int zs = item.getToraSosire() / 86400;
            String zd = "+" + String.valueOf(zs - zp);
            if (!zd.equals("+0")) {
                ((TextView) viewHolder.linearLayoutZi.getChildAt(1)).setText(zd);
                viewHolder.linearLayoutZi.setVisibility(View.VISIBLE);
            } else
                viewHolder.linearLayoutZi.setVisibility(View.GONE);
            for (int i = 0; i < item.getServicii().length; i++)
                viewHolder.serviciilist.getChildAt(item.getServicii()[i]).setVisibility(View.VISIBLE);
            return convertView;
        }
        else if(viewType == 1){//1 , cu doua trenuri
            ViewHolder2 viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder2();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_train_2, parent, false);
                viewHolder.fromtime = convertView.findViewById(R.id.oraplecare);
                viewHolder.totime = convertView.findViewById(R.id.orasosire);
                viewHolder.numar = convertView.findViewById(R.id.text);
                viewHolder.serviciilist = convertView.findViewById(R.id.servicii);
                viewHolder.linearLayoutZi = convertView.findViewById(R.id.linzi);

                viewHolder.statie = convertView.findViewById(R.id.tren);
                viewHolder.asteptare = convertView.findViewById(R.id.timpAsteptare);
                viewHolder.fromtime2 = convertView.findViewById(R.id.oraplecare2);
                viewHolder.totime2 = convertView.findViewById(R.id.orasosire2);
                viewHolder.numar2 = convertView.findViewById(R.id.trainnumber2);
                viewHolder.serviciilist2 = convertView.findViewById(R.id.servicii2);
                viewHolder.linearLayoutZi2 = convertView.findViewById(R.id.linzi2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder2) convertView.getTag();
            }
            Ruta item = getItem(position);
            assert item != null;
            String tnume = item.getTrenuri().get(0).getCategorie() + " " + item.getTrenuri().get(0).getTren();
            viewHolder.numar.setText(tnume);
            viewHolder.fromtime.setText(Utils.toTime(item.getTrenuri().get(0).getForaPlecare(), false));
            viewHolder.totime.setText(Utils.toTime(item.getTrenuri().get(0).getToraSosire(), false));
            int zp = item.getTrenuri().get(0).getForaPlecare() / 86400;
            int zs = item.getTrenuri().get(0).getToraSosire() / 86400;
            String zd = "+" + String.valueOf(zs - zp);
            if (!zd.equals("+0")) {
                ((TextView) viewHolder.linearLayoutZi.getChildAt(1)).setText(zd);
                viewHolder.linearLayoutZi.setVisibility(View.VISIBLE);
            } else
                viewHolder.linearLayoutZi.setVisibility(View.GONE);
            for (int i = 0; i < item.getTrenuri().get(0).getServicii().length; i++)
                viewHolder.serviciilist.getChildAt(item.getTrenuri().get(0).getServicii()[i]).setVisibility(View.VISIBLE);

            viewHolder.statie.setText(item.getTrenuri().get(0).getTstatie().getName());
            viewHolder.asteptare.setText("Timp Așteptare : " + Utils.toTime(item.getAsteptare().get(0),false));
            String tnume2 = item.getTrenuri().get(1).getCategorie() + " " + item.getTrenuri().get(1).getTren();
            viewHolder.numar2.setText(tnume2);
            viewHolder.fromtime2.setText(Utils.toTime(item.getTrenuri().get(1).getForaPlecare(), false));
            viewHolder.totime2.setText(Utils.toTime(item.getTrenuri().get(1).getToraSosire(), false));
            int zp2 = item.getTrenuri().get(1).getForaPlecare() / 86400;
            int zs2 = item.getTrenuri().get(1).getToraSosire() / 86400;
            String zd2 = "+" + String.valueOf(zs2 - zp2);
            if (!zd2.equals("+0")) {
                ((TextView) viewHolder.linearLayoutZi2.getChildAt(1)).setText(zd2);
                viewHolder.linearLayoutZi2.setVisibility(View.VISIBLE);
            } else
                viewHolder.linearLayoutZi2.setVisibility(View.GONE);
            for (int i = 0; i < item.getTrenuri().get(1).getServicii().length; i++)
                viewHolder.serviciilist2.getChildAt(item.getTrenuri().get(1).getServicii()[i]).setVisibility(View.VISIBLE);
            return convertView;
        }else//2
        {
            ViewHolderMissing viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolderMissing();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_train_add, parent, false);
                viewHolder.text = convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderMissing) convertView.getTag();
            }
            viewHolder.text.setText("Tren lipsa?");
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView fromtime;
        TextView totime;
        TextView numar;
        LinearLayout serviciilist;
        LinearLayout linearLayoutZi;
    }
    private static class ViewHolder2 {
        TextView fromtime,totime,numar;
        LinearLayout serviciilist,linearLayoutZi;
        TextView statie,asteptare;
        TextView fromtime2,totime2,numar2;
        LinearLayout serviciilist2,linearLayoutZi2;
    }
    private static class ViewHolderMissing{
        TextView text;
    }
}

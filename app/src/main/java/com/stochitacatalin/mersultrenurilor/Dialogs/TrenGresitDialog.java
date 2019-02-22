package com.stochitacatalin.mersultrenurilor.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Retrofit.ResponseData;
import com.stochitacatalin.mersultrenurilor.Retrofit.RetrofitClient;
import com.stochitacatalin.mersultrenurilor.Retrofit.UpdateTrenGresitApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrenGresitDialog {
    String numar;
    String origine;
    String destinatie;
    Context context;
    public TrenGresitDialog(Context context,String numar,String origine,String destinatie) {
        this.numar = numar;
        this.origine = origine;
        this.destinatie = destinatie;
        this.context = context;
        View alertDialogView = LayoutInflater.from(context).inflate(R.layout.item_text, null);
        View titleView = LayoutInflater.from(context).inflate(R.layout.item_train_add, null);
        ((TextView)titleView.findViewById(R.id.text)).setText("Tren gresit?");
        ((TextView)alertDialogView.findViewById(R.id.text)).setText("Trenul "+numar+" nu are ruta de la "+origine+" la "+destinatie+" sau nu circula?Apasa pe trimite pentru a ne raporta eroarea");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(alertDialogView)
                .setCustomTitle(titleView)
                .setPositiveButton("Trimite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendReport();
                    }})
                .setCancelable(true)
                .create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
    void sendReport(){
        RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateTrenGresitApi.class)
                .update(numar,origine,destinatie)
                .enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        Toast.makeText(context,String.valueOf(response.body().isStatus()),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) { }
                });
    }
}

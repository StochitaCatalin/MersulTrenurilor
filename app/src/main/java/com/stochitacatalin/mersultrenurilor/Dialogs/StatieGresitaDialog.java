package com.stochitacatalin.mersultrenurilor.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stochitacatalin.mersultrenurilor.ElementTrasa;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Retrofit.ResponseData;
import com.stochitacatalin.mersultrenurilor.Retrofit.RetrofitClient;
import com.stochitacatalin.mersultrenurilor.Retrofit.UpdateStatieGresitaApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatieGresitaDialog {
    String numar;
    String statie;
    Context context;
    public StatieGresitaDialog(Context context, String numar, ElementTrasa et) {
        this.numar = numar;
        this.statie = et.getStatie().getName();
        this.context = context;
        View alertDialogView = LayoutInflater.from(context).inflate(R.layout.item_text, null);
        View titleView = LayoutInflater.from(context).inflate(R.layout.item_train_add, null);
        ((TextView)titleView.findViewById(R.id.text)).setText("Statie gresita?");
        ((TextView)alertDialogView.findViewById(R.id.text)).setText("Trenul "+numar+" nu opreste in statia "+et.getStatie().getName()+" sau ora de sosire/plecare este gresita?Apasa pe trimite pentru a ne raporta eroarea");
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
        RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateStatieGresitaApi.class)
                .update(numar,statie)
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

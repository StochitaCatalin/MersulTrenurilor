package com.stochitacatalin.mersultrenurilor.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.Adaptoare.CautaTrenuriAdapter;
import com.stochitacatalin.mersultrenurilor.Adaptoare.SearchStatiiAdapter;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.Retrofit.ResponseData;
import com.stochitacatalin.mersultrenurilor.Retrofit.RetrofitClient;
import com.stochitacatalin.mersultrenurilor.Retrofit.UpdateStatieLipsaApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatieLipsaDialog {
    AutoCompleteTextView autoTextStatie,autoTextNumar;
    MainActivity activity;
    Context context;
    public StatieLipsaDialog(Context context){
        activity = MainActivity.getActivity();
        this.context = context;
        View alertDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_statielipsa, null);
        View titleView = LayoutInflater.from(context).inflate(R.layout.item_train_add, null);
        ((TextView)titleView.findViewById(R.id.text)).setText("Statie lipsa?");
        autoTextNumar = alertDialogView.findViewById(R.id.autoTextNumar);
        autoTextStatie = alertDialogView.findViewById(R.id.autoTextStatie);

        CautaTrenuriAdapter adapterNumar = new CautaTrenuriAdapter(context,activity.getmDBHelper());
        autoTextNumar.setAdapter(adapterNumar);
        autoTextNumar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CautaTrenuriAdapter)autoTextNumar.getAdapter()).save(position);
                autoTextStatie.requestFocus();
            }
        });
        autoTextNumar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(((CautaTrenuriAdapter)autoTextNumar.getAdapter()).save()) {
                        autoTextNumar.setText(((CautaTrenuriAdapter)autoTextNumar.getAdapter()).getSelected().getNumar());
                        autoTextStatie.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        SearchStatiiAdapter adapterOrigine = new SearchStatiiAdapter(context,activity.getmDBHelper());
        autoTextStatie.setAdapter(adapterOrigine);
        autoTextStatie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchStatiiAdapter)autoTextStatie.getAdapter()).save(position);
                autoTextStatie.clearFocus();
                InputMethodManager imm = (InputMethodManager) StatieLipsaDialog.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoTextStatie.getWindowToken(), 0);
            }
        });
        autoTextStatie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(((SearchStatiiAdapter)autoTextStatie.getAdapter()).save()) {
                        autoTextStatie.setText(((SearchStatiiAdapter) autoTextStatie.getAdapter()).getSelected().getName());
                        autoTextStatie.clearFocus();
                        InputMethodManager imm = (InputMethodManager) StatieLipsaDialog.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoTextStatie.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
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
    void sendReport() {
        RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateStatieLipsaApi.class)
                .update(autoTextNumar.getText().toString(), autoTextStatie.getText().toString())
                .enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        Toast.makeText(context, String.valueOf(response.body().isStatus()), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                    }
                });
    }
}

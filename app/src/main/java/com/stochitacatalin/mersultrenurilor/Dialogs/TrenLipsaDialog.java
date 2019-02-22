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
import com.stochitacatalin.mersultrenurilor.Retrofit.UpdateTrenLipsaApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrenLipsaDialog {

    AutoCompleteTextView autoTextOrigine,autoTextDestinatie,autoTextNumar;
    MainActivity activity;
    Context context;
    public TrenLipsaDialog(Context context){
        activity = MainActivity.getActivity();
        this.context = context;
        View alertDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_trenlipsa, null);
        View titleView = LayoutInflater.from(context).inflate(R.layout.item_train_add, null);
        ((TextView)titleView.findViewById(R.id.text)).setText("Tren lipsa?");
        autoTextNumar = alertDialogView.findViewById(R.id.autoTextNumar);
        autoTextOrigine = alertDialogView.findViewById(R.id.autoTextOrigine);
        autoTextDestinatie = alertDialogView.findViewById(R.id.autoTextDestinatie);

        CautaTrenuriAdapter adapterNumar = new CautaTrenuriAdapter(context,activity.getmDBHelper());
        autoTextNumar.setAdapter(adapterNumar);
        autoTextNumar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CautaTrenuriAdapter)autoTextNumar.getAdapter()).save(position);
                autoTextOrigine.requestFocus();
            }
        });
        autoTextNumar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(((CautaTrenuriAdapter)autoTextNumar.getAdapter()).save()) {
                        autoTextNumar.setText(((CautaTrenuriAdapter)autoTextNumar.getAdapter()).getSelected().getNumar());
                        autoTextOrigine.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        SearchStatiiAdapter adapterOrigine = new SearchStatiiAdapter(context,activity.getmDBHelper());
        autoTextOrigine.setAdapter(adapterOrigine);
        autoTextOrigine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchStatiiAdapter)autoTextOrigine.getAdapter()).save(position);
                autoTextDestinatie.requestFocus();
            }
        });
        autoTextOrigine.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(((SearchStatiiAdapter)autoTextOrigine.getAdapter()).save()) {
                        autoTextOrigine.setText(((SearchStatiiAdapter)autoTextOrigine.getAdapter()).getSelected().getName());
                        autoTextDestinatie.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        SearchStatiiAdapter adapterDestinatie = new SearchStatiiAdapter(context,activity.getmDBHelper());
        autoTextDestinatie.setAdapter(adapterDestinatie);
        autoTextDestinatie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchStatiiAdapter)autoTextDestinatie.getAdapter()).save(position);
                autoTextDestinatie.clearFocus();
                InputMethodManager imm = (InputMethodManager) TrenLipsaDialog.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoTextDestinatie.getWindowToken(), 0);
            }
        });
        autoTextDestinatie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(((SearchStatiiAdapter)autoTextDestinatie.getAdapter()).save()) {
                        autoTextDestinatie.setText(((SearchStatiiAdapter) autoTextDestinatie.getAdapter()).getSelected().getName());
                        autoTextDestinatie.clearFocus();
                        InputMethodManager imm = (InputMethodManager) TrenLipsaDialog.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoTextDestinatie.getWindowToken(), 0);
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

    void sendReport(){
        RetrofitClient.getClient("https://stochitacatalin.com/mersultrenurilor/").create(UpdateTrenLipsaApi.class)
                .update(autoTextNumar.getText().toString(),autoTextOrigine.getText().toString(),autoTextDestinatie.getText().toString())
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

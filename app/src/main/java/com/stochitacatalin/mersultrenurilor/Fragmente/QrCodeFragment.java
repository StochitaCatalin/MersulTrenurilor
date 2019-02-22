package com.stochitacatalin.mersultrenurilor.Fragmente;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.stochitacatalin.mersultrenurilor.Activitati.MainActivity;
import com.stochitacatalin.mersultrenurilor.R;
import com.stochitacatalin.mersultrenurilor.TrenRuta;

public class QrCodeFragment extends Fragment {
    private CodeScanner mCodeScanner;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_qrcode, container, false);
        CodeScannerView scannerView = v.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                        /**
                         * TODO Decode and send to activity
                         */
                        String text = result.getText();
                        if(text.length() == 46) {
                            String from = text.substring(18, 23).replace('Q', '0');
                            String to = text.substring(23, 28).replace('Q', '0');
                            if (text.contains("Q")) {
                                text = text.substring(text.lastIndexOf("Q") + 1);
                                text = text.substring(0, text.length() - 1);
                            } else
                                text = "";

                            TrenRuta tr = activity.getmDBHelper().getTrenRuta(text,from,to);
                            Fragment f = activity.getActiveFragment();
                            if(f instanceof TrenFragment)
                                ((TrenFragment)f).setTrenRuta(tr);
                        }
                        else {
                            Fragment f = activity.getActiveFragment();
                            if(f instanceof TrenFragment)
                                ((TrenFragment)f).setTrenRuta(null);
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}

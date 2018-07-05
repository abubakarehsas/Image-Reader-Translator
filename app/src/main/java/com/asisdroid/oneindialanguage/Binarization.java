package com.asisdroid.oneindialanguage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;

import java.util.ArrayList;
import java.util.Arrays;


public class Binarization extends AppCompatActivity implements View.OnClickListener, AppCompatSeekBar.OnSeekBarChangeListener {
    private ImageView img;
    private Toolbar toolbar;
    private AppCompatSeekBar seekBar;
    private Pix pix;
    private FloatingActionButton fab;
    public static Bitmap umbralization;
    private Spinner spinner;
    public static int language;
    String[] languageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binarization);
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar,10);*/
        ViewCompat.setElevation((LinearLayout) findViewById(R.id.extension),10);
        spinner = (Spinner) findViewById(R.id.language);

        img = (ImageView) findViewById(R.id.croppedImage);
        fab = (FloatingActionButton) findViewById(R.id.nextStep);
        fab.setOnClickListener(this);
        pix = com.googlecode.leptonica.android.ReadFile.readBitmap(CropAndRotate.croppedImage);

        languageList = getBaseContext().getResources().getStringArray(R.array.languages_array);
        /*languageList.add("English");
        languageList.add("Gujrati");
        languageList.add("Hindi");
        languageList.add("Kannada");
        languageList.add("Malayali");
        languageList.add("Odia");
        languageList.add("Punjabi");
        languageList.add("Tamil");
        languageList.add("Telugu");*/

        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,languageList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view!=null) {
                    ((TextView) view).setTextColor(Color.WHITE);
                }
                language = i;
                OneIndiaLanguagePreferences.getInstance(getBaseContext()).setFromLanguage(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setSelection(OneIndiaLanguagePreferences.getInstance(getBaseContext()).getFromLanguage());
        try {
            OtsuThresholder otsuThresholder = new OtsuThresholder();
            int threshold = otsuThresholder.doThreshold(pix.getData());
                /* increase threshold because is better*/
            threshold += 20;
            umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap(GrayQuant.pixThresholdToBinary(pix, threshold));
            img.setImageBitmap(umbralization);
            seekBar = (AppCompatSeekBar) findViewById(R.id.umbralization);
            seekBar.setProgress(Integer.valueOf((50 * threshold) / 254));
            seekBar.setOnSeekBarChangeListener(this);
        }
        catch(Exception e){
            Toast.makeText(this, "Please, select correct language of the image!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap(
                GrayQuant.pixThresholdToBinary(pix, Integer.valueOf(((254 * seekBar.getProgress())/50)))
        );
        img.setImageBitmap(umbralization);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.nextStep) {
            if(checkInternetConenction()) {
                Intent intent = new Intent(Binarization.this, Recognizer.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Cannot proceed without internet connection!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        NetworkInfo info = connec.getActiveNetworkInfo();

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // Only update if WiFi or 3G is connected and not roaming

            return true;
        }
        return false;
    }
}

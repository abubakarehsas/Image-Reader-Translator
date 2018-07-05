package com.asisdroid.oneindialanguage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
/*
import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // todo API_KEY should not be stored in plain sight
    private static final String API_KEY = "AIzaSyA7KqEUjfE-SNnB83dGD5xe4g5o9DpRLls";
    final Handler textViewHandler = new Handler();

    Spinner spn_fromLang, spn_toLang;
    EditText edt_fromLang, edt_toLang;
    Button btn_chooseImg, btn_convert;
    String[] languageList, languageTranslationKeyList;
    int languageFromIndex, languageToIndex;
    String fromMessage;
    ProgressDialog progressD;
    static ProgressDialog progress;
    private SharedPreferences permissionStatus;

    private Uri outputFileUri;
    public final static String EXTRA_MESSAGE = "com.ltapps.textscanner.message";

    public static InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initEvents();
    }


    public void closeKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void initUI(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admobInterstetialunitID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        progress = new ProgressDialog(this);
        progress.setCancelable(false);

        spn_fromLang = (Spinner) findViewById(R.id.spn_fromLang);
        spn_toLang = (Spinner) findViewById(R.id.spn_toLang);

        edt_fromLang = (EditText) findViewById(R.id.edt_fromLang);
        edt_toLang = (EditText) findViewById(R.id.edt_toLang);

        btn_chooseImg = (Button) findViewById(R.id.btn_fromLangImage);
        btn_convert = (Button) findViewById(R.id.btn_convert);

        progressD = new ProgressDialog(this);
        progressD.setCancelable(false);
        progressD.setMessage("Converting to your language...");

        languageList = getResources().getStringArray(R.array.languages_array);
        languageTranslationKeyList = getResources().getStringArray(R.array.languages_keys_for_translation);
        /*languageList.add("English");
        languageList.add("Gujrati");
        languageList.add("Hindi");
        languageList.add("Kannada");
        languageList.add("Malayali");
        languageList.add("Odia");
        languageList.add("Punjabi");
        languageList.add("Tamil");
        languageList.add("Telugu");*/

        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languageList);
        spn_fromLang.setAdapter(adapter);
        spn_toLang.setAdapter(adapter);

        languageFromIndex = OneIndiaLanguagePreferences.getInstance(MainActivity.this).getFromLanguage();
        languageToIndex = OneIndiaLanguagePreferences.getInstance(MainActivity.this).getToLanguage();

        spn_fromLang.setSelection( languageFromIndex);
        spn_toLang.setSelection( languageToIndex);

        Intent intent = getIntent();
        if(intent.hasExtra("gotmessage")) {
            fromMessage = intent.getStringExtra("gotmessage");
            edt_fromLang.setText(fromMessage);
        }

        permissionStatus = getSharedPreferences("imagereaderpermissionStatuse2",MODE_PRIVATE);
    }

    public void initEvents(){
        spn_fromLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view!=null) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                }
                languageFromIndex = i;
                OneIndiaLanguagePreferences.getInstance(MainActivity.this).setFromLanguage(languageFromIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spn_toLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(view!=null) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
            }
                languageToIndex = i;
                OneIndiaLanguagePreferences.getInstance(MainActivity.this).setToLanguage(languageToIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                showProgressDialog("Converting...");
                if(checkInternetConenction()) { //Internet connected
                    if(mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when when the interstitial ad is closed.
                                closeProgressDialog();
                                if(checkInternetConenction()) {
                                    closeKeyboard();
                                    convertLang();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Cannot proceed without internet connection!", Toast.LENGTH_LONG).show();
                                }
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        });
                    }
                    else{
                        closeProgressDialog();
                        if(checkInternetConenction()) {
                            closeKeyboard();
                            convertLang();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Cannot proceed without internet connection!", Toast.LENGTH_LONG).show();
                        }
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }
                else{ //No internet
                    closeProgressDialog();
                    if(checkInternetConenction()) {
                        closeKeyboard();
                        convertLang();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Cannot proceed without internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btn_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                showProgressDialog("Loading...");
                if(checkInternetConenction()) { //Internet connected
                    if(mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when when the interstitial ad is closed.
                                closeProgressDialog();
                                askPermissions();
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        });
                    }
                    else{
                        closeProgressDialog();
                        askPermissions();
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }
                else{ //No internet
                    closeProgressDialog();
                    askPermissions();
                }
            }
        });

        edt_fromLang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt_fromLang.getRight() - edt_fromLang.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        String tempTxt = edt_fromLang.getText().toString().trim();
                        if (!tempTxt.equalsIgnoreCase("")){
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("From data", tempTxt);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Nothing to copy!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        edt_toLang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt_toLang.getRight() - edt_toLang.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        String tempTxt = edt_toLang.getText().toString().trim();
                        if (!tempTxt.equalsIgnoreCase("")){
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("To data", tempTxt);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Nothing to copy!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0] == 0){
            selectImage();
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

    public void askPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Image Reader");
                    builder.setMessage("Need permissions to select image to read.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Monyger");
                    builder.setMessage("Need permissions to add, save and show image's for your transactions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 22);
                            Toast.makeText(MainActivity.this, "Go to Permissions to Grant Location", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                editor.commit();


            } else {
                //You already have the permission, just go ahead.
                selectImage();
                //Intent mapIntent = new Intent(MainActivity.this,MapActivity.class);
                //startActivityForRe;sult(mapIntent,0);
            }
        }
        else{
            //Not needed for asking permissions below MarshMallow
            selectImage();
        }
    }

    private void convertLang(){
        if(!edt_fromLang.getText().toString().trim().equalsIgnoreCase("")) {
            if(spn_fromLang.getSelectedItemPosition()!=spn_toLang.getSelectedItemPosition()){
                if (progressD != null) {
                    progressD.show();
                }
                new ConvertLanguageOperation().execute();
            }
            else {
                edt_toLang.setText(edt_fromLang.getText().toString());
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Enter text or select an image to convert!", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConvertLanguageOperation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            TranslateOptions options = TranslateOptions.newBuilder()
                    .setApiKey(API_KEY)
                    .build();
            Translate translate = options.getService();
            final Translation translation =
                    translate.translate(edt_fromLang.getText().toString(),
                            Translate.TranslateOption.targetLanguage(languageTranslationKeyList[languageToIndex]));
            textViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (edt_toLang != null) {
                        Log.d("asisip","Convert-"+translation.getTranslatedText());
                        edt_toLang.setText(translation.getTranslatedText());
                        if(progressD!=null){
                            progressD.dismiss();
                        }
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void selectImage() {
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source Image");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    if(selectedImageUri!=null) {
                        nextStep(selectedImageUri.toString());
                    }
                    else{
                        Toast.makeText(this, "Image can't processed properly. Align properly while clicking picture.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    nextStep(selectedImageUri.toString());
                }
            }
        }
    }

    private void nextStep(String file) { //After selecting the image
        Intent intent = new Intent(this, CropAndRotate.class);
        intent.putExtra(EXTRA_MESSAGE, file);
        startActivity(intent);
    }
    public void showProgressDialog(String msg){
        progress.setMessage(msg);
        progress.show();
    }

    public void closeProgressDialog(){
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
}


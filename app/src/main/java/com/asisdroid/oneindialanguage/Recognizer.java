package com.asisdroid.oneindialanguage;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.util.Utils;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Recognizer extends AppCompatActivity {
    Spinner spn_fromLang, spn_toLang;
    EditText edt_fromLang, edt_toLang;
    Button btn_chooseImg, btn_convert;
    ArrayList<String> languageList = new ArrayList<>();
    int languageFromIndex, languageToIndex;
    private String textScanned;
    public ProgressDialog progressCopy, progressOcr;
    TessBaseAPI baseApi;
    AsyncTask<Void, Integer, Void> copy = new copyTask();
    AsyncTask<Void, Void, Void> ocr = new ocrTask();
    String[] languageNames = {"afr", "sqi", "amh", "ara", "aze", "eus", "bel", "ben", "bos", "bul", "cat", "khm", "ces", "chi_sim", "chi_tra",
            "dan", "eng", "epo", "est", "fas", "fin", "fra", "glg", "kat", "deu", "ell", "guj", "hat", "heb", "hin", "hun", "ind",
            "isl", "ita", "jav", "jpn", "kan", "kaz", "kir", "kor", "kur", "lao", "lat", "lav", "lit", "mal", "mar",
            "mkd", "mlt", "msa", "nep", "nor", "pan", "pol", "por", "pus", "ron", "rus", "sin", "slk", "slv", "spa", "srp",
            "swa", "swe", "tam", "tel", "tgk", "tgl", "tha", "tur", "ukr", "urd", "uzb", "vie", "cym", "yid"};
    int[] languageSizeArrayinKB;
    String[] languageNamesFull;

    ArrayList<String> languageUrls = new ArrayList<>();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.ltapps.textscanner/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognizer);
       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        ViewCompat.setElevation(toolbar,10);*/
//        ViewCompat.setElevation((LinearLayout) findViewById(R.id.extension),10);

        languageNamesFull =  getResources().getStringArray(R.array.languages_array);
        languageSizeArrayinKB = getResources().getIntArray(R.array.language_files_sizes);

        //Adding the urls for downloading languages
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=17_AyxLP_EjWyIjLM8aaQmU3xgy914sCJ&export=download"); //Afrikaans
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1PdiHByASONkZbFN5mskN5GhyuDNx-uLM&export=download"); //Albanian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1QMUPej3ljvmrHBcWhMysa3Y0Yhce9XMX&export=download"); //Amharic
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1H6dnbWZ39vXdxUQfbqLh7lReF03OWc2Q&export=download"); //Arabic
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1qUuKP3COJpPFuXMRUnjCEk-peVczFCNl&export=download"); //Azerbaijani
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1AgQaI967cJMLzKTKS2F96lqj-oq2oJro&export=download"); //Basque
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=17mMafO-pu8XB9LG3xqnUlzpl7NPsm_8D&export=download"); //Belarusian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=15jpGa-uMlUj_eN6U2MOVfmAryZ-cJe9C&export=download"); //Bengali
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1LBPUaDcGBwiLAib0mT6NXKP4NbNh-UtW&export=download"); //Bosnian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1TIRjHfTFr0pNRrqYWpRMNHg8T95y2B8y&export=download"); //Bulgarian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1--CFHaO420Bbp245B80JfwvOO-tLpH4p&export=download"); //Catalan; Valencian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1J-1z8LlSTJt0-ZwqnuqRy-njeMbYXLD1&export=download"); //Khmer
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1f9nmXOcZIds2UzAnTdQlNIsx7fJ0n0o7&export=download"); //Czech
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1pa8DmCLJOlXFuTAGnk6tySdlh3YsHvz2&export=download"); //Chinese - Simplified
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1FZduYQcGp0KfhjX3lDBlPTJDjsezZsnB&export=download"); //Chinese - Traditional
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1RcmMrUqZ7hwSHHCkPjWFn66UtSkL-Tsb&export=download"); //Danish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1H75pkRdEXqUrQCAhjaJ6ikobR-NJoT7v&export=download"); //English
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1rm1ozrTAbvtht3jRb9Sw0Mz8tSA-QD3k&export=download"); //Esperanto
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1Df_U7MXDd_v7SYJzoFD646WsuELEoXV7&export=download"); //Estonian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1mWH--00T1bTctJJPxWUQHFNP_VGTqG3S&export=download"); //Persian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=14m3x_Aubc0_NXfVbHCeOduDm1L9CQdkR&export=download"); //Finnish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1OxzrXeNbXF_nDMwRFazN8WYVgvoMRiEA&export=download"); //French
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=13NOxo9rFberPcNVgkTdfoHVQz0nAibcy&export=download"); //Galician
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=107s1no5DDFejRZQ03ysIUVFqRjB4XeT5&export=download"); //Georgian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1D9_cOYD6wPkXNN6ZcQbN4ch3vOuUhH3D&export=download"); //German
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1A1QULF7XKRCp4XxbsSOKOfLZnW5rXZSy&export=download"); //Greek, Modern
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1bHio7vX41yNwXvuWVpHSggdxR93sOSAy&export=download"); //Gujarati
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=162bJUoj-FPoDlnDoWtA1SQw-5xSQjX-P&export=download"); //Haitian; Haitian Creole
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1HpwKNitfM3h0lrJAKjGcj_sGzBtVZu-M&export=download"); //Hebrew
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=11GFsWGQYaTdsc8hR9f5MfXYjDSZv1gvg&export=download"); //Hindi
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1PpEvCxSNsUIh71ki536UMSmTxKjM2c7-&export=download"); //Hungarian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1o31tQ8wazR_XGcll1FCrzdB12SRRD2Q5&export=download"); //Indonesian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1n6-UZh6VhIejrv0avt2boP8mtxCW65Yj&export=download"); //Icelandic
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=198e9PmwMCNQmHLH9vjDTQCYCjpQVFukJ&export=download"); //Italian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1xTgMQLMxNSQh7iuC0JYlpMAoM9MlZnRf&export=download"); //Javanese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=16FQu6u5aMD19QMX9kcopr9Z2yPeBTBAJ&export=download"); //Japanese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=155dZN0vdmU-1GDwxI6K5cC9AZPbNwiB9&export=download"); //Kannada
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1a6S2C8xIhgyTuyPcSeIb-d3WViWQ3DGK&export=download"); //Kazakh
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1esTdTT2TUItBDcXpQy8Dqy2xQYgdzYJf&export=download"); //Kirghiz; Kyrgyz
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1sMU8cwLlFLY0n3bqq-RWM73PhGAIjPre&export=download"); //Korean
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1IfJAb04PsGaOjDOsCgeglIpV57H2e9p3&export=download"); //Kurdish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1KJDIpBkap3rw-u1vYFjcXsX0xuHxUGFF&export=download"); //Lao
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1FX-ag5KVpAoppKP_sHZv_wAu6Nw_KwYt&export=download"); //Latin
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1TyB75u9xbSKUN4TcAA7OUoUaKdEis2UL&export=download"); //Latvian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1SBlF22Jh2uz7UMR9GR7aelKvtRz5025e&export=download"); //Lithuanian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1qqWnnfxEJfLFgUwyOiKZ0K6EqV9NeyAH&export=download"); //Malayalam
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1hpIbfByeD1uLNIAPf0ciystykYELBEZ5&export=download"); //Marathi
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1djYva1JaxV_NvIOTAPUMbCaYusT4IWqd&export=download"); //Macedonian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=18VamdZ46htoFY_Cj9KwKOEBVxB9irtKT&export=download"); //Maltese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1OvEuEz3b3nvoobot3wTCmp-CZUykU-_g&export=download"); //Malay
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1kmh9wgF90pZpvRyiTMiMs2-UNyE17tyn&export=download"); //Nepali
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1Gu4Zk9qH1o2eC8JGL3Y5OLvATgVlVd9K&export=download"); //Norwegian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1p55tTc0aJuiaPb2bFU8cDjiTC1wjUCHi&export=download"); //Punjabi
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1q2HvwyFvhETKhUrQJSXoHWbAUO0-R4iY&export=download"); //Polish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1BVh3lh1Vpz_mWtfNVJAk0AKFrR09Fl9H&export=download"); //Portuguese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1oudE7BhvDvE-wDZnZFA3CUf_HGAuqPJT&export=download"); //Pushto
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1JIvdxljXGn4ZfZZ14CKdMHp7LjA9P0Qa&export=download"); //Romanian; Moldavian; Moldovan
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=17CNgBtyv3p2mKwA5NJPI1ZIF-5-yvnp2&export=download"); //Russian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1xMK1eiyqCZBV8WLorE5wAenQYsIYHbtO&export=download"); //Sinhala; Sinhalese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1HwFGvuR0nNqqV8JfAUUPuGZ5GvIY21Qg&export=download"); //Slovak
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=13ACOlfHQauBmfuxbSBMMP7WNz7XKGo7x&export=download"); //Slovenian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1UaB8iIzauRGHcKIusJcwpWnk2eDzCT2E&export=download"); //Spanish; Castilian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=12trWL--Oa2wpdCONCcHXwZllKSRytNpf&export=download"); //Serbian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1O0iw54m52RRpVq1tHiZ_6zqcOuophkxX&export=download"); //Swahili
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1ZahNjLBy76lN8Mk5WeCKm0bCDHg11Ofh&export=download"); //Swedish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1CYwQeoNXiLFfaZ_97iEb4udBuy7feLKd&export=download"); //Tamil
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1Cng-DiNOVxVe7npbjZdZp0hbOoNZTiA5&export=download"); //Telugu
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=13cLAx33R7SN_F3P2aqo3cbhG08kmNNbN&export=download"); //Tajik
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1vunaeEsvaQMpYw-5Z7tMDNdlF98zysj3&export=download"); //Tagalog
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1eet5jBmZr5txopJA5vhzs6WXbJA69qDa&export=download"); //Thai
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1DfE11ZaBGAi0YxG_nDWGs5bbces8qaOl&export=download"); //Turkish
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1MFKDMAcW6kcPhXAvkWvlJn3LKHJcYZCn&export=download"); //Ukrainian
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1HfSeCcYyG0AXdD0TQvptE-fyKPku-Vgn&export=download"); //Urdu
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=19Ee80x8P3LCpnw3tDdQ3zRx6aPjkPnHe&export=download"); //Uzbek
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1KPRyJL1KsFznKKpNCIU4t9ainSJ17yAu&export=download"); //Vietnamese
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1QqjogcLqhFFFYWDfRwQkLkUbGagOXaTT&export=download"); //Welsh
        languageUrls.add("https://drive.google.com/uc?authuser=0&id=1sKqVmCgOwom-8WJzqfgPkTsDY_Hz1tZD&export=download"); //Yiddish

        // Setting progress dialog for copy job.
        progressCopy = new ProgressDialog(Recognizer.this);
        progressCopy.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressCopy.setIndeterminate(false);
        progressCopy.setCancelable(false);
        progressCopy.setMessage("Downloading dictionary files for "+languageNamesFull[Binarization.language]+" language. please wait..\n(note: will be downloaded only once)");
        progressCopy.setMax(100);
        // Setting progress dialog for ocr job.
        progressOcr = new ProgressDialog(this);
        progressOcr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressOcr.setIndeterminate(true);
        progressOcr.setCancelable(false);
        progressOcr.setMessage("Extracting text, please wait..");
        textScanned = "";
        copy.execute();
    }

    private void recognizeText(){
        String language = "";
       /* if (Binarization.language == 0)
            language = "eng";
        else if (Binarization.language == 1)
            language= "guj";
        else if (Binarization.language == 2)
            language= "hin";
        else if (Binarization.language == 3)
            language= "kan";
        else if (Binarization.language == 4)
            language= "mal";
        else if (Binarization.language == 5)
            language= "pan";
        else if (Binarization.language == 6)
            language= "tam";
        else if (Binarization.language == 7)
            language= "tel";*/

       language = languageNames[Binarization.language];

       // Log.d("asisio","Lang = "+Binarization.language+"--"+language);
        baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, language, TessBaseAPI.OEM_TESSERACT_ONLY);
        baseApi.setImage(Binarization.umbralization);
        textScanned = baseApi.getUTF8Text();

    }

/*
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainneddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            Log.i("files",filename);
            InputStream in = null;
            OutputStream out = null;
            String dirout= DATA_PATH + "tessdata/";
            File outFile = new File(dirout, filename);
            Log.d("asisii",outFile.getAbsolutePath());
            if(!outFile.exists()) {
                try {
                    in = assetManager.open("trainneddata/"+filename);
                    Log.d("asisii","trainneddata/"+filename);

                    (new File(dirout)).mkdirs();
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    Log.e("tag", "Error creating files", e);
                }
            }
        }
    }*/

    private class copyTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressCopy.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressCopy.cancel();
            progressOcr.show();
            ocr.execute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("CopyTask","copying..");
            try {
                URL url = new URL(languageUrls.get(Binarization.language));//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                int fileLength = languageSizeArrayinKB[Binarization.language]*1000;

                Log.d("asisid", "Started downloading ");
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("asisid", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }

                InputStream in = null;
                OutputStream out = null;
                String dirout= DATA_PATH + "tessdata/";
                File outFile = new File(dirout, languageNames[Binarization.language]+".traineddata");
                if(!outFile.exists()) {
                    try {
                        (new File(dirout)).mkdirs();
                        out = new FileOutputStream(outFile);//Get OutputStream for NewFile Location
                        in = c.getInputStream();//Get InputStream for connection
                        // Detect the file lenghth

                        byte data[] = new byte[1024];
                        long total = 0;
                        int count;
                        while ((count = in.read(data)) != -1) {
                            total += count;
                            // Publish the progress
                            out.write(data, 0, count);
                            publishProgress((int) ((total * 100) / fileLength));
                        }

                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    } catch (IOException e) {
                        Log.e("tag", "Error creating files", e);
                    }
                }
                else {
                    int file_size = Integer.parseInt(String.valueOf(outFile.length()/1024));
                    if(file_size<languageSizeArrayinKB[Binarization.language]){
                        try {
                            (new File(dirout)).mkdirs();
                            out = new FileOutputStream(outFile);//Get OutputStream for NewFile Location
                            in = c.getInputStream();//Get InputStream for connection
                            // Detect the file lenghth
                            byte data[] = new byte[1024];
                            long total = 0;
                            int count;
                            while ((count = in.read(data)) != -1) {
                                total += count;
                                // Publish the progress
                                out.write(data, 0, count);
                                publishProgress((int) ((total * 100) / fileLength));
                            }
                            in.close();
                            in = null;
                            out.flush();
                            out.close();
                            out = null;
                        } catch (IOException e) {
                            Log.e("tag", "Error creating files", e);
                        }
                    }
                }
            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]<100) {
                 progressCopy.setProgress(values[0]);
            }
            Log.d("asisidown", ""+values[0]);
        }
    }

    private class ocrTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressOcr.cancel();
            //GO back to home page
            Intent intent = new Intent(Recognizer.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("gotmessage", textScanned);
            startActivity(intent);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask","extracting..");
            recognizeText();
            return null;
        }
    }

}

package com.asisdroid.oneindialanguage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashishkumarpolai on 7/14/2017.
 */

public class OneIndiaLanguagePreferences {
    private static OneIndiaLanguagePreferences sCommonPref;
    public SharedPreferences mPreference;
    private Context mContext;

    private String FROM_LANGUAGE = "fromLang";
    private String TO_LANGUAGE = "toLang";

    public static OneIndiaLanguagePreferences getInstance(Context context) {
        if (sCommonPref == null) {
            sCommonPref = new OneIndiaLanguagePreferences(context);
        }
        return sCommonPref;
    }

    public OneIndiaLanguagePreferences(Context context) {
        mContext = context;
        mPreference = mContext.getSharedPreferences("OneIndiaLang_Preferences",
                Context.MODE_PRIVATE);
    }

    public void clearPreference() {
        mPreference.edit().clear().commit();
    }

    public int getFromLanguage() {
        return mPreference.getInt(FROM_LANGUAGE, 0);
    }

    public void setFromLanguage(int fromlanguageindex) {

        mPreference.edit().putInt(FROM_LANGUAGE, fromlanguageindex)
                .commit();
    }

    public int getToLanguage() {
        return mPreference.getInt(TO_LANGUAGE, 0);
    }

    public void setToLanguage(int tolanguageindex) {

        mPreference.edit().putInt(TO_LANGUAGE, tolanguageindex)
                .commit();
    }
}

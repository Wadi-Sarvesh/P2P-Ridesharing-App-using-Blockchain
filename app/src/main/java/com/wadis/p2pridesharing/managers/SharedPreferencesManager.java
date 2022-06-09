package com.wadis.p2pridesharing.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREFERENCES_DATA = "com.wadis.p2pridesharing";
    private static final String ENCRYPTION_STATUS = "encryption_status";
    private static final String DARK_THEME = "dark_theme";
    private static final String PROOF_OF_WORK = "proof_of_work";
    private static final int DEFAULT_PROOF_OF_WORK = 2;

    public SharedPreferencesManager(Context context) {
         sharedPreferences = context.getSharedPreferences(PREFERENCES_DATA,Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
         editor.apply();
    }
    public void setPowValue(int powValue)
    {
        editor.putInt(PROOF_OF_WORK,powValue);
        editor.commit();
    }

    public  int getPowValue() {
        return sharedPreferences.getInt(PROOF_OF_WORK,DEFAULT_PROOF_OF_WORK);
    }
    public void setEncryptionStatus(boolean isActivated)
    {
        editor.putBoolean(ENCRYPTION_STATUS,isActivated);
        editor.commit();
    }
    public boolean getEncryptionStatus()
    {
        return sharedPreferences.getBoolean(ENCRYPTION_STATUS,false);
    }
    public void setDarkTheme(boolean isActivated)
    {
        editor.putBoolean(DARK_THEME,isActivated);
        editor.commit();
    }
    public boolean isDarkTheme()
    {
        return sharedPreferences.getBoolean(DARK_THEME,false);
    }


}

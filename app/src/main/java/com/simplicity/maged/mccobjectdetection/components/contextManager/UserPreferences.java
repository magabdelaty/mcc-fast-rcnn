package com.simplicity.maged.mccobjectdetection.components.contextManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

    public static final String FORCE_OFFLOADING = "force_offloading";
    public static final String FORCE_LOCAL = "force_local";
    public static final String DROPBOX_TOKEN = "drobox_token";
    public static final String AUTOMATICOFFLOADING = "automatic_offloading";
    public static final String AVAILABLESERVICES = "available_services";
    public static final String SIMPLEDECISION = "simple_decision";

    public static Boolean getAvailable_services(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(AVAILABLESERVICES, false);
    }

    public static void setgetAvailable_services(Boolean available_services,
                                                Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor = sharedPref.edit();
        editor.putBoolean(AVAILABLESERVICES, available_services);
        editor.commit();
    }

    public static Boolean getSimple_Decision(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(SIMPLEDECISION, false);
    }

    public static void setSimpleDecision(Boolean simple_decision,
                                         Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor = sharedPref.edit();
        editor.putBoolean(SIMPLEDECISION, simple_decision);
        editor.commit();
    }

    public static Boolean getAutomatic_offloading(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(AUTOMATICOFFLOADING, false);
    }

    public static void setAutomatic_offloading(Boolean automatic_offloading,
                                               Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor = sharedPref.edit();
        editor.putBoolean(AUTOMATICOFFLOADING, automatic_offloading);
        editor.commit();
    }

    public static Boolean getForce_offloading(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(FORCE_OFFLOADING, false);
    }

    public static void setForce_offloading(Boolean force_offloading,
                                           Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor = sharedPref.edit();
        editor.putBoolean(FORCE_OFFLOADING, force_offloading);
        // editor.putBoolean(FORCE_LOCAL, !force_offloading); // Done in UI
        // logic
        editor.commit();
    }

    public static Boolean getForce_local(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(FORCE_LOCAL, false);
    }

    public static void setForce_local(Boolean force_local, Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor = sharedPref.edit();
        editor.putBoolean(FORCE_LOCAL, force_local);
        // editor.putBoolean(FORCE_OFFLOADING, !force_local); // Done in UI
        // logic
        editor.commit();
    }

    public static String getDrobox_token(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getString(DROPBOX_TOKEN, "");
    }

    public static void setDrobox_token(String drobox_token, Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(DROPBOX_TOKEN, drobox_token);
        editor.commit();
    }

}

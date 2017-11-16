package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.json.JSONArray;
import org.json.JSONObject;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.RESTClient;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Providers {

    static ArrayList<Provider> providers;

    public static ArrayList<Provider> getProviders() {
        return providers;
    }

    @Nonnull
    public static Providers getInstance() {
        return ProvidersHolder.instance;
    }

    public synchronized Boolean addCloudProvider(Provider provider) {
        try {
            if (providers == null) {
                providers = new ArrayList<Provider>();
            }

            providers.add(provider);
            return true;
        } catch (Exception e) {
            Log.e("simplicity", "addCloudProvider: " + e.toString());
            return false;
        }
    }

    public synchronized Boolean addTempProvider(Context context,
                                                String serviceUrl, long sysBootTime, double upBW4, double downBW4,
                                                double upBW5, double downBW5) {
        try {
            if (providers == null) {
                providers = new ArrayList<Provider>();
            }

            JSONObject json = RESTClient.GetJsonRequest(context, serviceUrl
                    + "service_discovery", 100);
            Log.i("simplicity", json.toString());
            JSONArray services = json.getJSONArray("avail_services");
            String provider_uuid = json.getString("provider_uuid");
            int avail_memory = json.getInt("memoryAvail");
            int avail_energy = json.getInt("energyAvail");
            int computeSpeed = json.getInt("computeSpeed");
            Log.i("simplicity", "addTempProvider: upBW4: " + upBW4);
            Log.i("simplicity", "addTempProvider: provider_uuid: "
                    + provider_uuid);
            if (services.length() > 0) {
                context.getContentResolver()
                        .delete(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
                                ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID
                                        + " = ?",
                                new String[]{provider_uuid});
                // delete * from table where provider_uuid=xyz
            }
            Log.i("simplicity", "services.length: " + services.length());
            for (int i = 0; i < services.length(); i++) {
                JSONObject s = services.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID,
                        provider_uuid);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED,
                        computeSpeed);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILENERGY,
                        avail_energy);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY,
                        avail_memory);

                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW4,
                        upBW4);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW4,
                        downBW4);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW5,
                        upBW5);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW5,
                        downBW5);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_LAST_BOOT,
                        sysBootTime);
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_ANDROID_SERVICE,
                        s.getString("android_service"));
                values.put(
                        ContextManagerContentProvider.SERVICE_DISCOVERY_CLOUD_SERVICE,
                        s.getString("cloud_service"));
                Uri result = context
                        .getApplicationContext()
                        .getContentResolver()
                        .insert(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
                                values);
                Log.i("simplicity",
                        "addTempProvider: SERVICE_DISCOVERY_CONTENT_URI Inserted: "
                                + result.getLastPathSegment());
            }
            Provider provider = new Provider();
            String s2 = provider_uuid.replace("-", "");
            UUID uuid = new UUID(
                    new BigInteger(s2.substring(0, 16), 16).longValue(),
                    new BigInteger(s2.substring(16), 16).longValue());
            provider.id = uuid;
            provider.permanentFlag = false;
            provider.url = serviceUrl;
            if (services.length() > 0) {
                providers.add(provider);
                return true;
            }
            return false;

        } catch (Exception e) {
            Log.e("simplicity", "addTempProvider: " + e.toString());
            return false;
        }

    }

    private static class ProvidersHolder {
        public static final Providers instance = new Providers();
    }
}

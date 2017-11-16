package com.simplicity.maged.mccobjectdetection.components.contextManager;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.MyLifecycleHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private Intent mConEngineService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MyLifecycleHandler.isApplicationVisible()) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();
            mConEngineService = new Intent(context, ContextEngineService.class);
            mConEngineService
                    .setAction(com.simplicity.maged.mccobjectdetection.components.contextManager.Constants.ACTION.STARTFOREGROUND_ACTION);

            if (isConnected) {
                Log.i("simplicity", "NetworkChangeReceiver: isConnected");
                context.startService(mConEngineService);
            } else {
                try {
                    ContextEngine.UpdateNetworkStatus(context,
                            context.getString(R.string.serverURL)
                                    + "netprofile/SSMountain500.jpg",
                            context.getString(R.string.serverURL)
                                    + "netprofile");
                    Log.i("simplicity", "NetworkChangeReceiver: isNotConnected");
                    context.startService(mConEngineService
                            .setAction(com.simplicity.maged.mccobjectdetection.components.contextManager.Constants.ACTION.STOPFOREGROUND_ACTION));
                } catch (Exception e) {
                    Log.e("simplicity", e.toString());
                }
            }
        }
    }

}

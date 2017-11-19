package com.simplicity.maged.mccobjectdetection.components.decisionModel;

import java.util.ArrayList;

import com.simplicity.maged.mccobjectdetection.components.contextManager.EnvironmentContext;
import com.simplicity.maged.mccobjectdetection.components.contextManager.LocalContext;
import com.simplicity.maged.mccobjectdetection.components.contextManager.Provider;
import com.simplicity.maged.mccobjectdetection.components.executionPlans.Plans;
import com.simplicity.maged.mccobjectdetection.components.logger.SimplicityLogger;

import android.util.Log;

public class OffloadingDecision {


    static public boolean getObjDetWiFiDecision(double cpuTime, double pCompute,
                                                double upData, double downData, double upBW, double downBW,
                                                double pTransmit, double pIdle, long cpuMobile,
                                                long cpuCloud) {
        double F = cpuCloud * 5 / Double.valueOf(cpuMobile); // x=5 see Kumar et al
        Log.i("simplicity", "F: " + String.valueOf(F));
        double B0 = ((downData + upData) / cpuTime) * (pTransmit / (pCompute - pIdle / F));
        Log.i("simplicity", "B0: " + String.valueOf(B0));
        if (upBW >= B0) {
            Log.i("simplicity", "upBW >= B0");
            if (downBW >= B0) {
                Log.i("simplicity", "downBW >= B0");
                return true;
            }
        }
        return false;
    }

    static public boolean getObjDet3GDecision(double cpuTime, double pCompute,
                                              double upData, double downData, double upBW,
                                              double pTransmit, double pIdle, long cpuMobile,
                                              long cpuCloud) {
        double F = cpuCloud * 5 / Double.valueOf(cpuMobile); // x=5 see Kumar et al
        Log.i("simplicity", "F: " + String.valueOf(F));
        double B0 = (upData / cpuTime) * (pTransmit / (pCompute - pIdle / F));
        Log.i("simplicity", "B0: " + String.valueOf(B0));
        if (upBW >= B0) {
            Log.i("simplicity", "upBW >=B0");
            return true;
        }
        return false;
    }

    static double getTimeOnMobile(double cpuTime, double[] upData,
                                  double[] downData, double[] upBW, double[] downBW) {

        double cost = cpuTime;
        for (int i = 0; i < upData.length; i++) {
            if (upData[i] > 0 && upBW[i] > 0) {
                cost += (upData[i] / upBW[i]);
                Log.i("simplicity", "getTimeOnMobile: up: "
                        + (upData[i] / upBW[i]));
            }
            if (downData[i] > 0 && downBW[i] > 0) {
                cost += (downData[i] / downBW[i]);
                Log.i("simplicity", "getTimeOnMobile: down: "
                        + (downData[i] / downBW[i]));
            }
        }
        return cost;
    }

}

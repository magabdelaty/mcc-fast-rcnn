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

    static double getTimeOnOffloading(double computeTime, double[] upData,
                                      double[] downData, double[] upBW, double[] downBW) {

        double cost = computeTime;
        for (int i = 0; i < upData.length; i++) {
            if (upData[i] > 0 && upBW[i] > 0) {
                cost += (upData[i] / upBW[i]);
                Log.i("simplicity", "getTimeOnOffloading: up: "
                        + (upData[i] / upBW[i]));
            }
            if (downData[i] > 0 && downBW[i] > 0) {
                cost += (downData[i] / downBW[i]);
                Log.i("simplicity", "getTimeOnOffloading: down: "
                        + (downData[i] / downBW[i]));
            }
        }
        return cost;
    }

    static double getEnergyOnMobile(double cpuTime, double pCompute,
                                    double[] upData, double[] downData, double[] upBW, double[] downBW,
                                    double pTransmit) {

        double cost = cpuTime * pCompute;
        for (int i = 0; i < upData.length; i++) {

            if (upData[i] > 0 && upBW[i] > 0) {
                cost += (upData[i] / upBW[i]) * pTransmit;
                Log.i("simplicity", "getEnergyOnMobile: up: "
                        + (upData[i] / upBW[i]) * pTransmit);
            }
            if (downData[i] > 0 && downBW[i] > 0) {
                cost += (downData[i] / downBW[i]) * pTransmit;
                Log.i("simplicity", "getEnergyOnMobile: down: "
                        + (downData[i] / downBW[i]) * pTransmit);
            }
        }
        return cost;
    }

    static double getEnergyOnOffloading(double computeTime, double pIdeal,
                                        double[] upData, double[] downData, double[] upBW, double[] downBW,
                                        double pTransmit) {

        double cost = computeTime * pIdeal;
        for (int i = 0; i < upData.length; i++) {
            if (upData[i] > 0 && upBW[i] > 0) {
                cost += (upData[i] / upBW[i]) * pTransmit;
                Log.i("simplicity", "getEnergyOnOffloading: up: "
                        + (upData[i] / upBW[i]) * pTransmit);
            }
            if (downData[i] > 0 && downBW[i] > 0) {
                cost += (downData[i] / downBW[i]) * pTransmit;
                Log.i("simplicity", "getEnergyOnOffloading: down: "
                        + (downData[i] / downBW[i]) * pTransmit);
            }
        }
        return cost;
    }

    public static SelectedPlan selectPlan(double costLocal, double costMemory,
                                          double upDataSize, double downDataSizeLocal,
                                          double downDataSizeCloud, Boolean dataProvider, double[] costCloud,
                                          ArrayList<Provider> providers, LocalContext lContext,
                                          EnvironmentContext[] eContext, double memoryCritical,
                                          double energyCritical, Boolean lackOfResources,
                                          Boolean serviceProvMode) {

        SelectedPlan best_plan = null;
        if (serviceProvMode) {
            double energyOnMobile = 0;
            double timeOnMobile = 0;
            if (dataProvider) {
                energyOnMobile = getEnergyOnMobile(costLocal,
                        lContext.pCompute, new double[]{0, 0}, new double[]{
                                downDataSizeLocal, downDataSizeLocal},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2},
                        lContext.pTransmit);
                timeOnMobile = getTimeOnMobile(costLocal,
                        new double[]{0, 0}, new double[]{
                                downDataSizeLocal, downDataSizeLocal},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2});
            } else {
                energyOnMobile = getEnergyOnMobile(costLocal,
                        lContext.pCompute, new double[]{upDataSize, 0},
                        new double[]{downDataSizeLocal, 0}, new double[]{
                                lContext.upBW1, lContext.upBW2}, new double[]{
                                lContext.downBW1, lContext.downBW2},
                        lContext.pTransmit);
                timeOnMobile = getTimeOnMobile(costLocal, new double[]{
                                upDataSize, 0}, new double[]{downDataSizeLocal, 0},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2});
            }
            Log.i("simplicity", "energyOnMobile: " + energyOnMobile
                    + " timeOnMobile: " + timeOnMobile);
            SimplicityLogger.appendLine("," + energyOnMobile
                    + "," + timeOnMobile * 1000);
            /** < ***/
            if (costMemory != (lContext.memoryAvail - memoryCritical)
                    && energyOnMobile < (lContext.energyAvail - energyCritical)
                    && !lackOfResources) {
                if (dataProvider) {
                    /*
                     * best_plan = new SelectedPlan(); best_plan.plan =
					 * Plans.U_M_D_M_U; best_plan.estimatedTime = timeOnMobile;
					 * best_plan.provider = null; Log.i("simplicity",
					 * "selectPlan: dataProvider: best_plan.plan: " +
					 * best_plan.plan.toString() + " best_plan.estimatedTime: "
					 * + timeOnMobile); SimplicityLogger
					 * .appendLine(":selectPlan: dataProvider: best_plan.plan: "
					 * + best_plan.plan.toString() +
					 * " :best_plan.estimatedTime: " + timeOnMobile * 1000);
					 */
                } else {
                    best_plan = new SelectedPlan();
                    best_plan.plan = Plans.U_M_U;
                    best_plan.estimatedTime = timeOnMobile;
                    best_plan.estimatedEnergy = energyOnMobile;
                    best_plan.provider = null;
                    Log.i("simplicity",
                            "selectPlan: !dataProvider: plan.plan: "
                                    + best_plan.plan.toString()
                                    + " best_plan.estimatedTime: "
                                    + timeOnMobile);
                    SimplicityLogger
                            .appendLine(","
                                    + best_plan.plan.toString()
                                    + ","
                                    + timeOnMobile * 1000);
                }

            }

            for (int i = 0; i < providers.size(); i++) {

                for (Plans plan : Plans.values()) {

                    if (costMemory < eContext[i].memoryAvail) {

                        double timeOnOffloading = 0;
                        double energyOnOffloading = 0;
                        if (plan == Plans.U_M_U) {
                            continue;
                        }
                        if (dataProvider) {

                            if (plan == Plans.U_M_C_D_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i],
                                        new double[]{0, 0, 0},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud, upDataSize},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i],
                                        lContext.pIdeal,
                                        new double[]{0, 0, 0},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud, upDataSize},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4},
                                        lContext.pTransmit);

                            } else if (plan == Plans.U_M_C_D_C_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i],
                                        new double[]{0, 0, 0, 0},
                                        new double[]{0, 0, upDataSize,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4,
                                                eContext[i].upBW5},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4,
                                                eContext[i].downBW5});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{0, 0, 0, 0},
                                        new double[]{0, 0, upDataSize,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4,
                                                eContext[i].upBW5},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4,
                                                eContext[i].downBW5},
                                        lContext.pTransmit);

                            }

                        } else {

                            if (plan == Plans.U_M_C_M_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{
                                                upDataSize, upDataSize},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i],
                                        lContext.pIdeal,
                                        new double[]{upDataSize, upDataSize},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3},
                                        lContext.pTransmit);
                            } else if (plan == Plans.U_M_C_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{
                                                upDataSize, upDataSize},
                                        new double[]{0, downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i],
                                        lContext.pIdeal,
                                        new double[]{upDataSize, upDataSize},
                                        new double[]{0, downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3},
                                        lContext.pTransmit);

                            }

                        }
                        if (best_plan == null && timeOnOffloading > 0
                                && plan != Plans.U_M_C_D_C_U
                                && plan != Plans.U_M_C_U) {
                            best_plan = new SelectedPlan();
                            best_plan.estimatedTime = timeOnOffloading;
                            best_plan.estimatedEnergy = energyOnOffloading;
                            best_plan.plan = plan;
                            best_plan.provider = providers.get(i);

                        } else if (plan != Plans.U_M_C_D_C_U
                                && plan != Plans.U_M_C_U
                                && timeOnOffloading > 0
                                && timeOnOffloading <= best_plan.estimatedTime
                                && energyOnOffloading < (lContext.energyAvail - energyCritical)) {

                            best_plan = new SelectedPlan();
                            best_plan.estimatedTime = timeOnOffloading;
                            best_plan.estimatedEnergy = energyOnOffloading;
                            best_plan.plan = plan;
                            best_plan.provider = providers.get(i);
                        }

                        Log.i("simplicity",
                                "plan: " + plan.toString()
                                        + " timeOnOffloading: "
                                        + timeOnOffloading
                                        + " energyOnOffloading: "
                                        + energyOnOffloading + " provider: "
                                        + providers.get(i).id.toString());
                        SimplicityLogger.appendLine("," + plan.toString()
                                + "," + timeOnOffloading
                                * 1000 + ","
                                + energyOnOffloading + ","
                                + providers.get(i).id.toString());
                    }
                }
            }
            Log.i("simplicity", "best_plan: plan: "
                    + best_plan.plan.toString()
                    + " estimatedTime: "
                    + best_plan.estimatedTime
                    + " estimatedEnergy: "
                    + best_plan.estimatedEnergy
                    + " provider: "
                    + (best_plan.provider == null ? "Local Processing"
                    : best_plan.provider.id.toString()));
            SimplicityLogger.appendLine(","
                    + best_plan.plan.toString()
                    + ","
                    + best_plan.estimatedTime
                    * 1000
                    + ","
                    + best_plan.estimatedEnergy
                    + ","
                    + (best_plan.provider == null ? "Local Processing"
                    : best_plan.provider.id.toString()));
        } else {
            // user mode
            // costLocal += 2 * 1;
            // 1000 ms = 1 sec = Suspend_cost + Resume_cost ref: CloneCloud
            // paper
            double energyOnMobile = 0;
            double timeOnMobile = 0;
            if (dataProvider) {
                /*
                 * energyOnMobile = getEnergyOnMobile(costLocal,
				 * lContext.pCompute, new double[] { 0, 0 }, new double[] { 0,
				 * downDataSizeLocal }, new double[] { lContext.upBW1,
				 * lContext.upBW2 }, new double[] { lContext.downBW1,
				 * lContext.downBW2 }, lContext.pTransmit); timeOnMobile =
				 * getTimeOnMobile(costLocal, new double[] { 0, 0 }, new
				 * double[] { 0, downDataSizeLocal }, new double[] {
				 * lContext.upBW1, lContext.upBW2 }, new double[] {
				 * lContext.downBW1, lContext.downBW2 });
				 */
            } else {
                timeOnMobile = costLocal;
                energyOnMobile = costLocal * lContext.pCompute;
            }
            Log.i("simplicity", "energyOnMobile: " + energyOnMobile
                    + " timeOnMobile: " + timeOnMobile);
            SimplicityLogger.appendLine("," + energyOnMobile
                    + "," + timeOnMobile * 1000);
            if (costMemory < (lContext.memoryAvail - memoryCritical)
                    && energyOnMobile < (lContext.energyAvail - energyCritical)
                    && !lackOfResources) {
                if (dataProvider) {
                    best_plan = new SelectedPlan();
                    best_plan.plan = Plans.U_M_D_M_U;
                    best_plan.estimatedTime = timeOnMobile;
                    best_plan.estimatedEnergy = energyOnMobile;
                    best_plan.provider = null;
                } else {
                    best_plan = new SelectedPlan();
                    best_plan.plan = Plans.U_M_U;
                    best_plan.estimatedTime = timeOnMobile;
                    best_plan.estimatedEnergy = energyOnMobile;
                    best_plan.provider = null;
                }
            }
            for (int i = 0; i < providers.size(); i++) {

                // costCloud[i] += 2 * 1;
                for (Plans plan : Plans.values()) {

                    if (costMemory < eContext[i].memoryAvail) {
                        double timeOnOffloading = 0;
                        double energyOnOffloading = 0;
                        if (plan == Plans.U_M_U) {
                            continue;
                        }

                        if (dataProvider) {
                            if (plan == Plans.U_M_C_D_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{0, 0},
                                        new double[]{downDataSizeCloud,
                                                upDataSize}, new double[]{
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{eContext[i].downBW3,
                                                eContext[i].downBW4});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{0, 0},
                                        new double[]{downDataSizeCloud,
                                                upDataSize}, new double[]{
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{eContext[i].downBW3,
                                                eContext[i].downBW4},
                                        lContext.pTransmit);
                            }
                        } else {

                            if (plan == Plans.U_M_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i],
                                        new double[]{upDataSize},
                                        new double[]{downDataSizeCloud},
                                        new double[]{eContext[i].upBW3},
                                        new double[]{eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{upDataSize},
                                        new double[]{downDataSizeCloud},
                                        new double[]{eContext[i].upBW3},
                                        new double[]{eContext[i].downBW3},
                                        lContext.pTransmit);
                            }
                        }

                        Boolean initCondition = best_plan == null
                                && timeOnOffloading > 0;
                        if (initCondition
                                || timeOnOffloading > 0
                                && timeOnOffloading <= best_plan.estimatedTime
                                && energyOnOffloading < (lContext.energyAvail - energyCritical)) {

                            best_plan = new SelectedPlan();
                            best_plan.estimatedTime = timeOnOffloading;
                            best_plan.estimatedEnergy = energyOnOffloading;
                            best_plan.plan = plan;
                            best_plan.provider = providers.get(i);
                        }

                        Log.i("simplicity",
                                "userModePlan: " + plan.toString()
                                        + " timeOnOffloading: "
                                        + timeOnOffloading
                                        + " energyOnOffloading: "
                                        + energyOnOffloading + " provider: "
                                        + providers.get(i).id.toString());
                        SimplicityLogger.appendLine(","
                                + plan.toString() + ","
                                + timeOnOffloading * 1000
                                + "," + energyOnOffloading
                                + ","
                                + providers.get(i).id.toString());
                    }
                }
            }

        }

        Log.i("simplicity",
                "best_plan: "
                        + best_plan.plan.toString()
                        + " estimatedTime: "
                        + best_plan.estimatedTime
                        + " estimatedEnergy: "
                        + best_plan.estimatedEnergy
                        + " provider: "
                        + (best_plan.provider == null ? "local"
                        : best_plan.provider.id.toString()));
        SimplicityLogger.appendLine(","
                + best_plan.plan.toString()
                + ","
                + best_plan.estimatedTime
                * 1000
                + ","
                + best_plan.estimatedEnergy
                + ","
                + (best_plan.provider == null ? "local" : best_plan.provider.id
                .toString()));

        return best_plan;
    }

    public static SelectedPlan estimateSimpleDecision(double costLocal,
                                                      double costMemory, double upDataSize, double downDataSizeLocal,
                                                      double downDataSizeCloud, Boolean dataProvider, double[] costCloud,
                                                      ArrayList<Provider> providers, LocalContext lContext,
                                                      EnvironmentContext[] eContext, double memoryCritical,
                                                      double energyCritical, Boolean lackOfResources,
                                                      Boolean serviceProvMode) {

        SelectedPlan best_plan = null;
        if (serviceProvMode) {
            double energyOnMobile = 0;
            double timeOnMobile = 0;
            if (dataProvider) {
                energyOnMobile = getEnergyOnMobile(costLocal,
                        lContext.pCompute, new double[]{0, 0}, new double[]{
                                downDataSizeLocal, downDataSizeLocal},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2},
                        lContext.pTransmit);
                timeOnMobile = getTimeOnMobile(costLocal,
                        new double[]{0, 0}, new double[]{
                                downDataSizeLocal, downDataSizeLocal},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2});
            } else {
                energyOnMobile = getEnergyOnMobile(costLocal,
                        lContext.pCompute, new double[]{upDataSize, 0},
                        new double[]{downDataSizeLocal, 0}, new double[]{
                                lContext.upBW1, lContext.upBW2}, new double[]{
                                lContext.downBW1, lContext.downBW2},
                        lContext.pTransmit);
                timeOnMobile = getTimeOnMobile(costLocal, new double[]{
                                upDataSize, 0}, new double[]{downDataSizeLocal, 0},
                        new double[]{lContext.upBW1, lContext.upBW2},
                        new double[]{lContext.downBW1, lContext.downBW2});
            }
            Log.i("simplicity", "estimateSimpleDecision: energyOnMobile: "
                    + energyOnMobile + " timeOnMobile: " + timeOnMobile);
            SimplicityLogger
                    .appendLine(","
                            + energyOnMobile + ","
                            + timeOnMobile * 1000);
            if (costMemory < (lContext.memoryAvail - memoryCritical)
                    && energyOnMobile < (lContext.energyAvail - energyCritical)
                    && !lackOfResources) {
                if (dataProvider) {
                    /*
                     * best_plan = new SelectedPlan(); best_plan.plan =
					 * Plans.U_M_D_M_U; best_plan.estimatedTime = timeOnMobile;
					 * best_plan.provider = null; Log.i("simplicity",
					 * "estimateSimpleDecision: dataProvider: best_plan.plan: "
					 * + best_plan.plan.toString() +
					 * " best_plan.estimatedTime: " + timeOnMobile);
					 * SimplicityLogger .appendLine(
					 * ":estimateSimpleDecision: dataProvider: best_plan.plan: "
					 * + best_plan.plan.toString() +
					 * " :best_plan.estimatedTime: " + timeOnMobile * 1000);
					 */
                } else {
                    best_plan = new SelectedPlan();
                    best_plan.plan = Plans.U_M_U;
                    best_plan.estimatedTime = timeOnMobile;
                    best_plan.estimatedEnergy = energyOnMobile;
                    best_plan.provider = null;
                    Log.i("simplicity",
                            "estimateSimpleDecision: !dataProvider: plan.plan: "
                                    + best_plan.plan.toString()
                                    + " best_plan.estimatedTime: "
                                    + timeOnMobile);
                    SimplicityLogger
                            .appendLine(","
                                    + best_plan.plan.toString()
                                    + ","
                                    + timeOnMobile * 1000);
                }

            }
            for (int i = 0; i < providers.size(); i++) {

                for (Plans plan : Plans.values()) {

                    if (costMemory < eContext[i].memoryAvail) {

                        double timeOnOffloading = 0;
                        double energyOnOffloading = 0;
                        if (plan == Plans.U_M_U) {
                            continue;
                        }
                        if (dataProvider) {

                            if (plan == Plans.U_M_C_D_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{0, 0, 0},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{0, 0, 0}, new double[]{
                                                downDataSizeCloud,
                                                downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4},
                                        lContext.pTransmit);

                            } else if (plan == Plans.U_M_C_D_C_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i],
                                        new double[]{0, 0, 0, 0},
                                        new double[]{0, 0, downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4,
                                                eContext[i].upBW5},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4,
                                                eContext[i].downBW5});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{0, 0, 0, 0},
                                        new double[]{0, 0, downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3,
                                                eContext[i].upBW4,
                                                eContext[i].upBW5},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3,
                                                eContext[i].downBW4,
                                                eContext[i].downBW5},
                                        lContext.pTransmit);

                            }

                        } else {

                            if (plan == Plans.U_M_C_M_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{
                                                upDataSize, upDataSize},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i],
                                        lContext.pIdeal,
                                        new double[]{upDataSize, upDataSize},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3},
                                        lContext.pTransmit);
                            } else if (plan == Plans.U_M_C_U) {

                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{
                                                upDataSize, upDataSize},
                                        new double[]{0, downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i],
                                        lContext.pIdeal,
                                        new double[]{upDataSize, upDataSize},
                                        new double[]{0, downDataSizeCloud},
                                        new double[]{lContext.upBW1,
                                                eContext[i].upBW3},
                                        new double[]{lContext.downBW1,
                                                eContext[i].downBW3},
                                        lContext.pTransmit);

                            }

                        }
                        Boolean initCondition = best_plan == null
                                && plan != Plans.U_M_C_D_C_U
                                && plan != Plans.U_M_C_U
                                && timeOnOffloading > 0;
                        if (initCondition
                                || (timeOnOffloading > 0
                                && plan != Plans.U_M_C_D_C_U
                                && plan != Plans.U_M_C_U
                                && timeOnOffloading <= best_plan.estimatedTime && energyOnOffloading < (lContext.energyAvail - energyCritical))) {

                            best_plan = new SelectedPlan();
                            best_plan.estimatedTime = timeOnOffloading;
                            best_plan.estimatedEnergy = energyOnOffloading;
                            best_plan.plan = plan;
                            best_plan.provider = providers.get(i);
                        }

                        Log.i("simplicity",
                                "plan: " + plan.toString()
                                        + " timeOnOffloading: "
                                        + timeOnOffloading
                                        + " energyOnOffloading: "
                                        + energyOnOffloading + " provider: "
                                        + providers.get(i).id.toString());
                        SimplicityLogger.appendLine("," + plan.toString()
                                + "," + timeOnOffloading
                                * 1000 + ","
                                + energyOnOffloading + ","
                                + providers.get(i).id.toString());
                    }
                }
            }
            Log.i("simplicity", "estimateSimpleDecision: best_plan: plan: "
                    + best_plan.plan.toString()
                    + " estimatedTime: "
                    + best_plan.estimatedTime
                    + " estimatedEnergy: "
                    + best_plan.estimatedEnergy
                    + " provider: "
                    + (best_plan.provider == null ? "Local Processing"
                    : best_plan.provider.id.toString()));
            SimplicityLogger
                    .appendLine(","
                            + best_plan.plan.toString()
                            + ","
                            + best_plan.estimatedTime
                            * 1000
                            + ","
                            + best_plan.estimatedEnergy
                            + ","
                            + (best_plan.provider == null ? "Local Processing"
                            : best_plan.provider.id.toString()));
        } else {
            // user mode
            costLocal += 2 * 1;
            // 1000 ms = 1 sec = Suspend_cost + Resume_cost ref: CloneCloud
            // paper
            double energyOnMobile = 0;
            double timeOnMobile = 0;
            if (dataProvider) {
                energyOnMobile = getEnergyOnMobile(costLocal,
                        lContext.pCompute, new double[]{0, 0}, new double[]{
                                0, downDataSizeLocal}, new double[]{
                                lContext.upBW1, lContext.upBW2}, new double[]{
                                lContext.downBW1, lContext.downBW2},
                        lContext.pTransmit);
                timeOnMobile = getTimeOnMobile(costLocal,
                        new double[]{0, 0}, new double[]{0,
                                downDataSizeLocal}, new double[]{
                                lContext.upBW1, lContext.upBW2}, new double[]{
                                lContext.downBW1, lContext.downBW2});
            } else {
                timeOnMobile = costLocal;
                energyOnMobile = costLocal * lContext.pCompute;
            }
            Log.i("simplicity",
                    "estimateSimpleDecision: usermode: energyOnMobile: "
                            + energyOnMobile + " timeOnMobile: " + timeOnMobile);
            SimplicityLogger
                    .appendLine(","
                            + energyOnMobile
                            + ","
                            + timeOnMobile * 1000);
            if (costMemory < (lContext.memoryAvail - memoryCritical)
                    && energyOnMobile < (lContext.energyAvail - energyCritical)
                    && !lackOfResources) {
                if (dataProvider) {
                    /*
                     * best_plan = new SelectedPlan(); best_plan.plan =
					 * Plans.U_M_D_M_U; best_plan.estimatedTime = timeOnMobile;
					 * best_plan.estimatedEnergy = energyOnMobile;
					 * best_plan.provider = null;
					 */
                } else {
                    best_plan = new SelectedPlan();
                    best_plan.plan = Plans.U_M_U;
                    best_plan.estimatedTime = timeOnMobile;
                    best_plan.estimatedEnergy = energyOnMobile;
                    best_plan.provider = null;
                }
            }
            for (int i = 0; i < providers.size(); i++) {

                costCloud[i] += 2 * 1;
                for (Plans plan : Plans.values()) {

                    if (costMemory < eContext[i].memoryAvail) {
                        double timeOnOffloading = 0;
                        double energyOnOffloading = 0;
                        if (plan == Plans.U_M_U) {
                            continue;
                        }

                        if (dataProvider) {
                            if (plan == Plans.U_M_C_D_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i], new double[]{0, 0},
                                        new double[]{downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{eContext[i].downBW3,
                                                eContext[i].downBW4});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{0, 0}, new double[]{
                                                downDataSizeCloud,
                                                downDataSizeCloud},
                                        new double[]{eContext[i].upBW3,
                                                eContext[i].upBW4},
                                        new double[]{eContext[i].downBW3,
                                                eContext[i].downBW4},
                                        lContext.pTransmit);
                            }
                        } else {

                            if (plan == Plans.U_M_C_M_U) {
                                timeOnOffloading = getTimeOnOffloading(
                                        costCloud[i],
                                        new double[]{upDataSize},
                                        new double[]{downDataSizeCloud},
                                        new double[]{eContext[i].upBW3},
                                        new double[]{eContext[i].downBW3});

                                energyOnOffloading = getEnergyOnOffloading(
                                        costCloud[i], lContext.pIdeal,
                                        new double[]{upDataSize},
                                        new double[]{downDataSizeCloud},
                                        new double[]{eContext[i].upBW3},
                                        new double[]{eContext[i].downBW3},
                                        lContext.pTransmit);
                            }
                        }

                        Boolean initCondition = best_plan == null
                                && timeOnOffloading > 0;
                        if (initCondition
                                || timeOnOffloading > 0
                                && timeOnOffloading <= best_plan.estimatedTime
                                && energyOnOffloading < (lContext.energyAvail - energyCritical)) {

                            best_plan = new SelectedPlan();
                            best_plan.estimatedTime = timeOnOffloading;
                            best_plan.estimatedEnergy = energyOnOffloading;
                            best_plan.plan = plan;
                            best_plan.provider = providers.get(i);
                        }

                        Log.i("simplicity",
                                "estimateSimpleDecision: userModePlan: "
                                        + plan.toString()
                                        + " timeOnOffloading: "
                                        + timeOnOffloading
                                        + " energyOnOffloading: "
                                        + energyOnOffloading + " provider: "
                                        + providers.get(i).id.toString());
                        SimplicityLogger
                                .appendLine(","
                                        + plan.toString()
                                        + ","
                                        + timeOnOffloading
                                        * 1000
                                        + ","
                                        + energyOnOffloading
                                        + ","
                                        + providers.get(i).id.toString());
                    }
                }
            }

        }

        Log.i("simplicity",
                "estimateSimpleDecision: best_plan: "
                        + best_plan.plan.toString()
                        + " estimatedTime: "
                        + best_plan.estimatedTime
                        + " estimatedEnergy: "
                        + best_plan.estimatedEnergy
                        + " provider: "
                        + (best_plan.provider == null ? "local"
                        : best_plan.provider.id.toString()));
        SimplicityLogger.appendLine(","
                + best_plan.plan.toString()
                + ","
                + best_plan.estimatedTime
                * 1000
                + ","
                + best_plan.estimatedEnergy
                + ","
                + (best_plan.provider == null ? "local" : best_plan.provider.id
                .toString()));

        return best_plan;
    }

    public static Boolean getNoOverheadDecision(int gsmSignalStrength,
                                                Boolean mobileInternet, double dataSize, double batt_level) {

        if (mobileInternet && gsmSignalStrength < -100) {
            Log.i("simplicity", "noOverheadDecision signalStrength check");
            return false; // Do not offload
        } else {
            if (dataSize > 500) {
                if (batt_level > 25) {
                    return true; // offload
                } else {
                    return false; // Do not offload
                }
            } else {
                if (batt_level >= 10) {
                    return true; // offload
                } else {
                    return false; // Do not offload
                }
            }
        }
    }

    public static double[] getMigrationFeasibility(double costLocal,
                                                   double upDataSize, double upBW, double downDataSize, double downBW,
                                                   double costCloud, double costResume, double costSuspend) {
        double[] result = new double[2];
        if (upBW == 0 || downBW == 0) {
            return result;
        }
        double costMigration = upDataSize / upBW * 1000 + downDataSize / downBW
                * 1000 + costCloud + costResume + costSuspend; // milliseconds
        if (costLocal > costMigration) {
            result[0] = 1; // Service is migrated
            result[1] = costMigration;
        } else {
            result[0] = 0; // Service is executed locally
            result[1] = costLocal;
        }
        Log.i("simplicity", "MigrationFeasibility: costLocal: " + costLocal);
        Log.i("simplicity", "MigrationFeasibility: costMigration: "
                + costMigration);
        Log.i("simplicity", "MigrationFeasibility: Upload cost: " + upDataSize
                / upBW * 1000);
        Log.i("simplicity", "MigrationFeasibility: " + result[0]);
        return result;
    }
}

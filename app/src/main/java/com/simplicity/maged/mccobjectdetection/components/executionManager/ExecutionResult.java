package com.simplicity.maged.mccobjectdetection.components.executionManager;

import android.app.Activity;

public class ExecutionResult {
	public double downDataSize;
	public double costCloudExecution;
	public double costMobileExecution;
	public long responseTime;
	public long commCost;
	public double monetaryCost;
	public int result = Activity.RESULT_CANCELED;
	public String resultFilePath = "";
}

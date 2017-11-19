package com.simplicity.maged.mccobjectdetection.components;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

public class MyLifecycleHandler implements ActivityLifecycleCallbacks {
	// http://stackoverflow.com/questions/3667022/checking-if-an-android-application-is-running-in-the-background/13809991#13809991
	// I use four separate variables here. You can, of course, just use two and
	// increment/decrement them instead of using four and incrementing them all.
	private static int resumed;
	private static int paused;
	private static int started;
	private static int stopped;

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
	}

	@Override
	public void onActivityResumed(Activity activity) {
		++resumed;
	}

	@Override
	public void onActivityPaused(Activity activity) {
		++paused;
		android.util.Log.w("test", "application is in foreground: "
				+ (resumed > paused));
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
	}

	@Override
	public void onActivityStarted(Activity activity) {
		++started;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		++stopped;
		android.util.Log.w("test", "application is visible: "
				+ (started > stopped));
	}

	public static boolean isApplicationVisible() {
		return started > stopped;
	}

	public static boolean isApplicationInForeground() {
		return resumed > paused;
	}
	// If you want a static function you can use to check if your application is
	// foreground/background, you can use the following:
	/*
	 * // Replace the four variables above with these four
	 * 
	 * 
	 * // And these two public static functions
	 */
}

package com.simplicity.maged.mccobjectdetection.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tzutalin.vision.visionrecognition.SceneClassifier;
import com.tzutalin.vision.visionrecognition.VisionClassifierCreator;
import com.tzutalin.vision.visionrecognition.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maged on 06-Mar-17.
 */

public class SceneRecognitionService {
    private final static String TAG = "SceneRecognitionService";
    private static SceneClassifier mClassifier;

    public static String[] Execute(final String filePath, Context context) {
        try {
            long startTime;
            long endTime;
            startTime = System.currentTimeMillis();
            initCaffeMobile(context);
            List<VisionDetRet> rets = new ArrayList<>();
            Log.d(TAG, "PredictTask filePath:" + filePath);
            if (mClassifier != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Log.d(TAG, "format:" + options.inPreferredConfig);
                Bitmap bitmapImg = BitmapFactory.decodeFile(filePath, options);
                rets.addAll(mClassifier.classify(bitmapImg));
                mClassifier.deInit();
                mClassifier = null;
            }

            if (!rets.isEmpty()) {

                List<String> results = new ArrayList<>();
                endTime = System.currentTimeMillis();
                final double diffTime = (double) (endTime - startTime);
                // change the results path after drawing the rectangles
                results.add("/sdcard/temp.jpg");
                results.add(String.valueOf(diffTime));
                for (VisionDetRet ret : rets) {
                    if (!ret.getLabel().equalsIgnoreCase("background")) {
                        results.add(ret.toString());
                    }
                    Log.d(TAG, ret.toString());
                }
                return results.toArray(new String[results.size()]);
            }
            return new String[]{"Error: No object detected"};
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return new String[]{"Error: " + e.toString(), ""};
        }
    }

    static private void initCaffeMobile(Context context) {
        if (mClassifier == null) {
            try {
                mClassifier = VisionClassifierCreator.createSceneClassifier(context);
                Log.d(TAG, "Start Load model");
                // TODO : Fix it
                mClassifier.init(224, 224);  // init once
                Log.d(TAG, "End Load model");
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}

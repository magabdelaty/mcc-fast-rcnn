package com.simplicity.maged.mccobjectdetection.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.tzutalin.vision.visionrecognition.VisionClassifierCreator;
import com.tzutalin.vision.visionrecognition.VisionDetRet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maged on 05-Mar-17.
 */

public class ObjectDetectionService {
    private final static String TAG = "simplicity"; //"ObjectDetectionService";
    private static com.tzutalin.vision.visionrecognition.ObjectDetector mObjectDet;

    public static String[] Execute(final String filePath, Context context) {
        try {
            long startTime;
            long endTime;
            startTime = System.currentTimeMillis();
            Log.d(TAG, "DetectTask filePath:" + filePath);
            String resultFilePath = filePath + "_detect.jpg";
            if (mObjectDet == null) {
                try {
                    mObjectDet = VisionClassifierCreator.createObjectDetector(context);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, options);
                    mObjectDet.init(0, 0);//(options.outWidth, options.outHeight);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.toString());
                }
            }
            List<VisionDetRet> rets = new ArrayList<>();
            if (mObjectDet != null) {
                Log.d(TAG, "Start objDetect");
                rets.addAll(mObjectDet.classifyByPath(filePath));
                Log.d(TAG, "end objDetect");
                mObjectDet.deInit();
            }
            if (!rets.isEmpty()) {
                List<String> results = new ArrayList<String>();
                endTime = System.currentTimeMillis();
                final double diffTime = (double) (endTime - startTime);
                moveFile("/sdcard/temp.jpg", resultFilePath);
                results.add(resultFilePath);
                results.add(String.valueOf(diffTime));
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap btmp = BitmapFactory.decodeFile(resultFilePath, opt);
                Canvas canvas = new Canvas(btmp);
                for (VisionDetRet ret : rets) {
                    if (!ret.getLabel().equalsIgnoreCase("background")) {
                        results.add(ret.toString());
                        Log.d(TAG, ret.toString());
                        Path boxPath = new Path();
                        boxPath.addRect(ret.getLeft(), ret.getTop(), ret.getRight(),
                                ret.getBottom(), Path.Direction.CW);

                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.TRANSPARENT);
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(0.01f * opt.outWidth);
                        canvas.drawPath(boxPath, paint);
                        //=============================
                        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        float scale = context.getResources().getDisplayMetrics().density;
                        paint.setColor(Color.BLUE);
                        Rect outline = new Rect();
                        String text = ret.getConfidence() + ", " + ret.getLabel();
                        paint.getTextBounds(text, 0, text.length(), outline);
                        final float testTextSize = 48f;
                        float desiredTextSize = scale * opt.outWidth * 2 / outline.width();
                        paint.setTextSize(desiredTextSize);
                        int x = ret.getLeft();
                        int y = ret.getTop() - 10;
                        canvas.drawText(text, x, y, paint);
                    }
                }
                FileOutputStream out = new FileOutputStream(resultFilePath);
                btmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                out = null;
                btmp.recycle();
                btmp = null;
                canvas = null;
                mObjectDet = null;
                System.gc();
                return results.toArray(new String[results.size()]);
            }
            return new String[]{"Error: No object detected"};
        } catch (Exception e) {
            if (mObjectDet != null) {
                mObjectDet.deInit();
                mObjectDet = null;
                System.gc();
            }
            Log.e(TAG, e.toString());
            return new String[]{"Error: " + e.toString(), ""};
        }
    }

    private static void moveFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            // write the output file
            out.flush();
            out.close();
            out = null;
            // delete the original file
            new File(inputPath).delete();
        } catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }
}

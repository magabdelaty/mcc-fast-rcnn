package com.simplicity.maged.mccobjectdetection.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.ProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngine;
import com.simplicity.maged.mccobjectdetection.components.executionManager.ExecutionEngineService;

import java.util.List;

public class ObjectDetectionActivity extends AppCompatActivity {

    private String mPicturePath;
    private Button mBtnSelectImage;
    private Button mBtnRecognize;
    private TextView mTxtResult;
    private ImageView mImgView;
    private long startTime;
    private long endTime;
    private Button mBtnAutorun;
    private Button mBtnCancelRun;
    private EditText mTxt_interval;
    private EditText mTxtTicks;
    private TextView mTxtActualTicks;
    private CountDownTimer mCounterDownTimer;

    private static final String GALLERY_PHOTOS_PACKAGE_NAME = "android.gallery3d";
    private static final String GALLERY_EMU_PHOTOS_PACKAGE_NAME = "com.android.gallery";
    private static final int GALLERY_IMAGE_PICK = 1001;
    String TAG = "simplicity";//"ObjectRecognitionActiv";

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null
                    && intent.getAction().equals(
                    ExecutionEngineService.NOTIFICATION)) {
                Log.i(TAG, "ObjectDetectionActivity: BroadcastReceiver");
                String resultImg = bundle
                        .getString(ExecutionEngineService.RESULTFILE);
                int resultCode = bundle.getInt(ExecutionEngineService.RESULT);
                long responseTime = bundle
                        .getLong(ExecutionEngineService.RESPONSETIME);
                if (resultCode == RESULT_OK) {
                    decodeImage(resultImg, mImgView);
                    // TODO: delete result image
                    endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = getApplicationContext().getContentResolver()
                            .query(ProfilerContentProvider.CONTENT_URI,
                                    ProfilerContentProvider.COLUMNS, null,
                                    null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = getApplicationContext().getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + ": comCost "
                            + bundle.getLong(ExecutionEngineService.COMMCOST)
                            + " ms"
                            + ": exCost "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==========
                    mTxtResult.setText(Long.toString(responseTime) + " ms"
                            + ": " + ss); // Long.toString(duration)
                    // milliseconds
                    // ==================================
                } else {
                    mTxtResult.setText("Failed");
                    Toast.makeText(getApplicationContext(), resultImg,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        mImgView = (ImageView) findViewById(R.id.imgView_selected_image);

        mBtnSelectImage = (Button) findViewById(R.id.btn_select_image);
        mBtnSelectImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onclick_btn_select_image(v);
            }
        });

        mBtnRecognize = (Button) findViewById(R.id.btn_recognize);
        mBtnRecognize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onclick_btn_recognize(v);
            }
        });
        mTxtResult = (TextView) findViewById(R.id.txtview_time);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // =========
        mBtnAutorun = (Button) findViewById(R.id.btn_autorun);
        mBtnAutorun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_Autorun(v);
            }
        });
        mBtnCancelRun = (Button) findViewById(R.id.btn_cancelRun);
        mBtnCancelRun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_CancelRun(v);
            }
        });

        mTxt_interval = (EditText) findViewById(R.id.txt_seconds);
        mTxtTicks = (EditText) findViewById(R.id.txt_ticks);
        mTxtActualTicks = (TextView) findViewById(R.id.txt_actualTicks);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(
                        receiver,
                        new IntentFilter(
                                ExecutionEngineService.NOTIFICATION));
    }

    private void onclick_btn_recognize(View v) {
        if (mPicturePath != null) {
            if (mTxtResult.getText() == "Wait...") {
                return;
            }
            Intent intent = new Intent(this, ExecutionEngineService.class);
            intent.putExtra(ExecutionEngineService.LOCALSERVICEREQUEST,
                    "com.simplicity.maged.mccobjectdetection.services.ObjectDetectionService");
            intent.putExtra(ExecutionEngineService.SERVICEREQUESTPARAMS,
                    new String[]{""});
            intent.putExtra(ExecutionEngineService.INPUTFILES,
                    new String[]{mPicturePath});
            // check the heap and the image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPicturePath, options);

            double totalFreeHeap = ContextEngine
                    .GetLocalResources(getApplicationContext()).avial_memory * 1024;
            // three channels
            int matSize = options.outWidth * options.outHeight * 3;
            Log.i(TAG,
                    "totalFreeHeap (Bytes) = " + Math.round(totalFreeHeap));
            if (totalFreeHeap < matSize * 2) {
                // two images is saved in memory ??
                intent.putExtra(ExecutionEngineService.LACKOFRESOURCES, true);
            }
            startTime = endTime = System.nanoTime();
            mTxtResult.setText("Wait...");
            startService(intent);
        }
    }

    protected void onClick_CancelRun(View v) {
        if (mCounterDownTimer != null) {

            mCounterDownTimer.cancel();
            mCounterDownTimer = null;
            mTxtActualTicks.setText("0");
            mBtnAutorun.setText("Autorun");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (imageReturnedIntent != null && resultCode == RESULT_OK) {

            if (requestCode == GALLERY_IMAGE_PICK
                    && imageReturnedIntent.getData() != null) {

                String selectedImagePath = DocumentsContract.getPath(
                        ObjectDetectionActivity.this,
                        imageReturnedIntent.getData());
                mPicturePath = selectedImagePath;
                Toast.makeText(getApplicationContext(), mPicturePath, Toast.LENGTH_LONG).show();
                decodeImage(mPicturePath, mImgView);
            }
        }

    }

    protected void onClick_Autorun(View v) {
        // milliseconds
        final long interval = mTxt_interval.getText()
                .toString() != "" ? 1000 * Long.parseLong(mTxt_interval.getText()
                .toString()) : 0;
        final long ticks = mTxtTicks.getText().toString() != "" ?
                Long.parseLong(mTxtTicks.getText().toString()) : 0;
        mTxtActualTicks.setText("0");
        if (mCounterDownTimer == null && interval > 0 && ticks > 0) {
            mBtnAutorun.setText("Running.....");
            mCounterDownTimer = new CountDownTimer(interval * ticks, interval) {

                @Override
                public void onTick(long millisUntilFinished) {

                    long currentTick = ticks - millisUntilFinished / interval;
                    mTxtActualTicks.setText(String.valueOf(currentTick));

                    if (currentTick == ticks) {
                        mCounterDownTimer.cancel();
                        mCounterDownTimer = null;
                        mBtnAutorun.setText("Autorun");
                    }
                    if (currentTick == 1 || currentTick % 10 == 0) {
                        String s = "";
                        switch ((int) currentTick / 10) {
                            case 0:
                                s = "/storage/emulated/0/DCIM/Camera/67.jpg";
                                break;
                            case 1:
                                s = "/storage/emulated/0/DCIM/Camera/164.jpg";
                                break;
                            case 2:
                                s = "/storage/emulated/0/DCIM/Camera/214.jpg";
                                break;
                            case 3:
                                s = "/storage/emulated/0/DCIM/Camera/292.jpg";
                                break;
                            case 4:
                                s = "/storage/emulated/0/DCIM/Camera/444.jpg";
                                break;
                            case 5:
                                s = "/storage/emulated/0/DCIM/Camera/985.jpg";
                                break;
                            default:
                                break;
                        }
                        try {
                            mPicturePath = s;
                            decodeImage(mPicturePath, mImgView);
                            onclick_btn_recognize(null);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

    void onclick_btn_select_image(View sender) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        List<ResolveInfo> resolveInfoList = this.getPackageManager()
                .queryIntentActivities(intent, 0);
        for (int i = 0; i < resolveInfoList.size(); i++) {
            if (resolveInfoList.get(i) != null) {
                String packageName = resolveInfoList.get(i).activityInfo.packageName;
                if (packageName.endsWith(GALLERY_PHOTOS_PACKAGE_NAME)) {
                    intent.setComponent(new ComponentName(packageName,
                            resolveInfoList.get(i).activityInfo.name));
                    this.startActivityForResult(intent, GALLERY_IMAGE_PICK);
                    return;
                } else if (packageName.endsWith(GALLERY_EMU_PHOTOS_PACKAGE_NAME)) {
                    intent.setComponent(new ComponentName(packageName,
                            resolveInfoList.get(i).activityInfo.name));
                    this.startActivityForResult(intent, GALLERY_IMAGE_PICK);
                    return;
                }
            }
        }
    }

    private void decodeImage(final String path, final ImageView iv) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, iv.getWidth(),
                iv.getHeight());
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        iv.setImageBitmap(BitmapFactory.decodeFile(path, options));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}

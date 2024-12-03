package com.softnoesis.shakebugexample.shakebug_flutter;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.softnoesis.shakebuglibrary.ShakeBug;
import com.softnoesis.shakebuglibrary.ShakeBugShakeEventListener;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterView;

public class MainActivity extends FlutterActivity {
    private SensorManager mSensorManager;
    private ShakeBugShakeEventListener mSensorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShakeBug.sharedInstance().initiateWithFlutterKey(this, "RdkwlTH89DMg8nM8a3WtuMDakHEPwl");
        shakeSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager != null && mSensorListener != null) {
            Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                mSensorManager.registerListener(mSensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    private void shakeSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeBugShakeEventListener();
        mSensorListener.setOnShakeListener(() -> captureScreenshot());
    }

    private void captureScreenshot() {
        FrameLayout content = findViewById(android.R.id.content);
        FlutterView flutterView = (FlutterView) content.getChildAt(0);

        if (flutterView == null) {
            Log.e("ShakeBug", "FlutterView not found");
            return;
        }

        SurfaceView surfaceView = (SurfaceView) flutterView.getChildAt(0);
        surfaceView.post(() -> {
            Bitmap screenshot = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PixelCopy.request(surfaceView, screenshot, result -> {
                    if (result == PixelCopy.SUCCESS) {
                        ShakeBug.sharedInstance().flutterScreenshot(screenshot, this);
                    } else {
                        Log.e("ShakeBug", "Screenshot failed");
                    }
                }, new Handler());
            } else {
                Log.e("ShakeBug", "PixelCopy requires Android Nougat or higher.");
            }
        });
    }

}

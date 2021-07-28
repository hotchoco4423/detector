package com.example.detector.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.detector.R;
import com.example.detector.model.MainMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PopupService extends Service {
    private static final String TAG = "POPUP_SERVICE";

    private MainMessage msg;
    WindowManager.LayoutParams params;
    private WindowManager winManager;
    protected View rootView;

    @BindView(R.id.p_layout)
    LinearLayout p_layout;

    @BindView((R.id.p_result))
    TextView p_result;

    @BindView(R.id.p_sender)
    TextView p_sender;

    @BindView(R.id.p_time)
    TextView p_time;

    @BindView(R.id.p_msg)
    TextView p_msg;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        winManager = (WindowManager) getSystemService((WINDOW_SERVICE));

        Display display = winManager.getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.9);

        params = new WindowManager.LayoutParams(
                width, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.allarm_dialog, null);
        ButterKnife.bind(this, rootView);
//        setDraggable();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    private void setDraggable() {
        Log.d(TAG, "setDraggable()");
        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ((event.getAction())){
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if(rootView != null) {
                            winManager.updateViewLayout(rootView, params);
                        }

                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        winManager.addView(rootView, params);
        setExtra(intent);

        if(msg != null) {
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.msg_background);
            switch (msg.getResult()) {
                case "존재하지 않는 URL":
                    drawable.setColor(Color.parseColor("#D98CE6"));
                    break;
                case "위험":
                    drawable.setColor(Color.parseColor("#E49594"));
                    break;
                case "안전":
                    drawable.setColor(Color.parseColor("#6CCF70"));
                    break;
                default:
                    break;
            }
            p_layout.setBackground(drawable);
            p_result.setText(msg.getResult());
            p_sender.setText(msg.getSender());
            p_time.setText(msg.getTime());
            p_msg.setText(msg.getMessage());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setExtra(Intent intent) {
        if(intent == null) {
            removePopup();
            return;
        }

        msg = (MainMessage) intent.getSerializableExtra("msg");
    }

    @OnClick(R.id.p_close)
    public void removePopup() {
        Log.d(TAG, "removePopup()");
        if(rootView != null && winManager != null) winManager.removeView(rootView);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        removePopup();
    }
}
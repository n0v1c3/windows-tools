package com.example.bigbrother;

import com.example.bigbrother.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

/**
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements PictureCallback, OnClickListener{
	/** Debug Log: Class Name */
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	
	private View photoButton;
	private CameraSurfaceView cameraSurfaceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    //Debug
		Log.d(TAG, "MainActivity.onCreate(): Start...");
		
		setContentView(R.layout.activity_main);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.camera_preview);

		// Set up an instance of SystemUiHider to control the system UI
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
				} else {
					controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				}

				if (visible && AUTO_HIDE) {
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		//Initialize camera preview
		FrameLayout preview = (FrameLayout) contentView;
		cameraSurfaceView = new CameraSurfaceView(this);
		preview.addView(cameraSurfaceView);
		
		//Delay hide() after UI interaction
		photoButton = findViewById(R.id.photo_button);
		photoButton.setOnTouchListener(mDelayHideTouchListener);
		photoButton.setOnClickListener(this);
	    //Debug
		Log.d(TAG, "MainActivity.onCreate(): Stop...");
	}
	
	/**
	 * Trigger the initial delayedHide() shortly after the activity has been
	 * created, this will briefly hint to the user that UI controls are available.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    //Debug
		Log.d(TAG, "MainActivity.onPostCreate(): Start...");
		
		super.onPostCreate(savedInstanceState);
		delayedHide(100);

	    //Debug
		Log.d(TAG, "MainActivity.onPostCreate(): Stop...");
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
		    //Debug
			Log.d(TAG, "MainActivity.mDelayHideTouchListener.onTouch(): Start...");
			
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			
		    //Debug
			Log.d(TAG, "MainActivity.mDelayHideTouchListener.onTouch(): Stop...");
			
			return false;
		}
	};
	
	View.OnClickListener mTakePhotoClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    //Debug
			Log.d(TAG, "MainActivity.mTakePhotoClickListener.onClick(): Start...");
			
			
		    //Debug
			Log.d(TAG, "MainActivity.mTakePhotoClickListener.onClick(): Stop...");
			
		}
	};
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
		photoButton.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.cameraSurfaceView.takePicture(this);
	}
}

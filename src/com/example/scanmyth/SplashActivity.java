package com.example.scanmyth;

import com.example.scanmyth.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class SplashActivity extends Activity {
	private Thread mSplashThread;
	private boolean mblnClicou = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		mSplashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(2000);
						mblnClicou = true;
					}
				} catch (InterruptedException ex) {
				}

				if (mblnClicou) {
					finish();

					// Load Activity Principal
					Intent i = new Intent();
					i.setClass(SplashActivity.this, MainActivity.class);
					startActivity(i);
				}
			}
		};
		mSplashThread.start();	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSplashThread.interrupt();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (mSplashThread) {
				mblnClicou = true;
				mSplashThread.notifyAll();
			}
		}
		return true;
	}
}

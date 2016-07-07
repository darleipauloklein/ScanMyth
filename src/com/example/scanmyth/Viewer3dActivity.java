package com.example.scanmyth;

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.example.scanmyth.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Viewer3dActivity extends Activity implements OnClickListener, OnTouchListener {
	private My3DView m3dView;
	private Button btnUp;
	private Button btnDown;
	private Button btnLeft;
	private Button btnRight;
	private Button btnX;
	private Button btnY;
	private int executa = 0;
	public static boolean incX = false;

	private Thread incAltura;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OpenGLRenderer renderer = new OpenGLRenderer();
		setContentView(R.layout.activity_viewer3d);
		m3dView = (My3DView) findViewById(R.id.surfaceView1);
		m3dView.setRenderer(renderer);
		
		btnUp = (Button) findViewById(R.id.btnUp);
		btnDown = (Button) findViewById(R.id.btnDown);
		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnX = (Button) findViewById(R.id.btnX);
		btnY = (Button) findViewById(R.id.btnY);

		btnUp.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnDown.setOnClickListener(this);

		btnDown.setOnTouchListener((OnTouchListener) this);
		btnUp.setOnTouchListener((OnTouchListener) this);
		btnLeft.setOnTouchListener((OnTouchListener) this);
		btnRight.setOnTouchListener((OnTouchListener) this);
		btnX.setOnTouchListener((OnTouchListener) this);
		btnY.setOnTouchListener((OnTouchListener) this);

		incAltura = new Thread() {
			@Override
			public void run() {

				while (true) {
					if (executa == 1) {
						OpenGLRenderer.altura = OpenGLRenderer.altura + 0.10f;
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (executa == 2) {
						OpenGLRenderer.altura = OpenGLRenderer.altura - 0.10f;
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (executa == 3) {
						OpenGLRenderer.lado = OpenGLRenderer.lado + 0.20f;
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (executa == 4) {
						OpenGLRenderer.lado = OpenGLRenderer.lado - 0.20f;
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (executa == 5) {
						if (incX == true) {
							OpenGLRenderer.x = OpenGLRenderer.x + 0.20f;
							if (OpenGLRenderer.x > 4f) {
								incX = false;
							}
						} else {
							OpenGLRenderer.x = OpenGLRenderer.x - 0.20f;
							if (OpenGLRenderer.x < -4f) {
								incX = true;
							}
						}
						Log.e("x", ""+OpenGLRenderer.x);
						try {
							sleep(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (executa == 6) {
						OpenGLRenderer.y = OpenGLRenderer.y - 0.32f;
						try {
							sleep(8);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		};
		incAltura.start();
		Toast.makeText(getApplicationContext(), "Wait! Rendering...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		
		
	}
	
	public void screenShot(View view) {
		
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btnUp:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 3;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		case R.id.btnDown:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 4;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		case R.id.btnLeft:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 2;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		case R.id.btnRight:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 1;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		case R.id.btnX:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 5;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		case R.id.btnY:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				executa = 6;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				executa = 0;
			}
			break;
		}
		return false;
	}

}

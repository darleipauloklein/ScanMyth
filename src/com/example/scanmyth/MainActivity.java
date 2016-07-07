package com.example.scanmyth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.example.scanmyth.R;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;
import org.opencv.android.JavaCameraView;

public class MainActivity extends Activity implements CvCameraViewListener2 {

	static {
		if (!OpenCVLoader.initDebug())
			Log.d("", "");
		else
			Log.d("", "");
	}

	private CameraBridgeViewBase mOpenCvCameraView;
	Mat image1, image2, matchedMat;
	private int matcher = 0;
	private My3DView m3dView;
	boolean matched = false;
	double reErr = 0;
	int count = 0;
	CvCameraViewFrame frame;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.loadLibrary("sfmlib");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.CameraView);
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setMaxFrameSize(1280, 800);
		mOpenCvCameraView.setCvCameraViewListener(this);
		ApplicationContextSingleton.setContext(this); 
	}

	private void fullScreen() {
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}

	public native double updateCurrentImage(long addrIn);

	public native void setImage(long addrOut);

	public native int match(long addrOut);

	public native void setMatcher(int in);

	public native void ClearAll();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true; 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		frame = inputFrame;
	//	reErr = updateCurrentImage(inputFrame.rgba().getNativeObjAddr());
		if (!matched){
			return inputFrame.rgba();
		} else {
			Log.d("Re Error", "" + reErr);
			return matchedMat;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i("OpenCV", "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	private Mat mat1;

	public void setImage(View view) throws IOException {
		if (count < 1) { 
			reErr = updateCurrentImage(frame.rgba().getNativeObjAddr());
			image1 = new Mat();
			setImage(image1.getNativeObjAddr());
			Bitmap bm = Bitmap.createBitmap(image1.cols(), image1.rows(), Bitmap.Config.ARGB_8888);
	    	Utils.matToBitmap(image1, bm);	    
			ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
			iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		    iv1.setImageBitmap(bm);
			saveImageToDisk(bm, null);
		} else {
			reErr = updateCurrentImage(frame.rgba().getNativeObjAddr());
			image2 = new Mat();
			setImage(image2.getNativeObjAddr());
			Bitmap bm = Bitmap.createBitmap(image2.cols(), image2.rows(), Bitmap.Config.ARGB_8888);
	    	Utils.matToBitmap(image2, bm);    
			ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
			iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		    iv2.setImageBitmap(bm);
			saveImageToDisk(bm, null);
		} 
		count++;
	}

	public static Bitmap drawableToBitmap (Drawable drawable) {
		Drawable myDrawable = ApplicationContextSingleton.getContext().getResources().getDrawable(R.drawable.i2);
		Bitmap bm = ((BitmapDrawable) myDrawable).getBitmap();
	    return bm;
	}
	
	public void clearImage(View view) {
		Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
		ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
		ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
		iv1.setImageResource(R.drawable.ic_alert);
		iv2.setImageResource(R.drawable.ic_alert);
		count = 0;
		matched = false;
		ClearAll();
	}
	

	public void runMatcher(final View view) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					matchedMat = new Mat();
					if (match(matchedMat.getNativeObjAddr()) == 0) {
						clearImage(view);
						return;
					}
					Imgproc.resize(matchedMat, matchedMat, image1.size());
					matched = true;
					saveImageToDisk(null, matchedMat);
					progress.cancel();

				} catch (Exception e) {
				}
			}
		}).start();

		progress = new ProgressDialog(this);
		progress.setMessage("Processando...");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCancelable(false);
		progress.setIndeterminate(true);
		progress.show();

	}
	
	public Mat grabCut(Mat img){
        int r = img.rows();
        int c = img.cols();

        Point p1 = new Point(c/5, r/5);
        Point p2 = new Point(c-c/5, r-r/8);

        Rect rect = new Rect(p1,p2);
        //Rect rect = new Rect(50,30, 100,200);

        Mat mask = new Mat();
        mask.setTo(new Scalar(125));
        Mat fgdModel = new Mat();
        fgdModel.setTo(new Scalar(255, 255, 255));
        Mat bgdModel = new Mat();
        bgdModel.setTo(new Scalar(255, 255, 255));

        Mat imgC3 = new Mat();  
        Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);
        
        Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 5, Imgproc.GC_INIT_WITH_RECT);

        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));

        Core.compare(mask, source, mask, Core.CMP_EQ);
        Mat foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        img.copyTo(foreground, mask);
        Scalar color = new Scalar(255, 0, 0, 255);
        Core.rectangle(img, p1, p2, color);
        //Imgproc.rectangle(img, p1, p2, color);
        Mat background = new Mat();
        try {
           background = Utils.loadResource(getApplicationContext(), R.drawable.fundo_preto);
        } catch (IOException e) {

            e.printStackTrace();
        }
        Mat tmp = new Mat();
        Imgproc.resize(background, tmp, img.size());

        background = tmp;

        Mat tempMask = new Mat(foreground.size(), CvType.CV_8UC1, new Scalar(255, 255, 255));
        Imgproc.cvtColor(foreground, tempMask, 6/* COLOR_BGR2GRAY */);
        //Imgproc.threshold(tempMask, tempMask, 254, 255, 1 /* THRESH_BINARY_INV */);

        Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));
        Mat dst = new Mat();
        dst = new Mat();
        background.setTo(vals, tempMask);
        Imgproc.resize(foreground, tmp, mask.size());
        foreground = tmp;
        Core.add(background, foreground, dst, tempMask);

        img.release();
        imgC3.release();
        mask.release();
        fgdModel.release();
        bgdModel.release();

        return dst;
	}
	
	public void saveImageToDisk(Bitmap finalBitmap, Mat finalMat){
		    if (finalMat != null){
				Bitmap bm = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(finalMat, bm);
				finalBitmap = bm;
		    }
			File myDir = new File("/storage/emulated/0/dcim/sfm");
		    myDir.mkdirs();

			SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
	        Date now = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        String hora = sdf.format(Calendar.getInstance().getTime());   
		    String fname = formatter.format(now) + " - " + hora + ".jpg";
		    
		    File file = new File(myDir, fname);
		    if (file.exists())
		        file.delete();
		    try {
		        FileOutputStream out = new FileOutputStream(file);
		        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		        out.flush();
		        out.close();
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }	
		    
		    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		    intent.setData(Uri.fromFile(file));
		    ApplicationContextSingleton.getContext().sendBroadcast(intent);
		    
		    MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
		            new MediaScannerConnection.OnScanCompletedListener() {
		                public void onScanCompleted(String path, Uri uri) {
		                    Log.i("ExternalStorage", "Scanned " + path + ":");
		                    Log.i("ExternalStorage", "-> uri=" + uri);
		                }
		    });
		}

	public void goTo3DView(View view) {
		Intent intent = new Intent(this, Viewer3dActivity.class);
		startActivity(intent);
	}

	public void changeMatcherType(View view) {
		final TextView txtMatcher = (TextView) findViewById(R.id.textView1);
		ArrayList<String> dummies = new ArrayList<String>();
        dummies.add("Dense of Matcher");
        dummies.add("ORB feature Matcher");
        dummies.add("ORB feature + KLT hybrid Matcher");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_layout);
        dialog.setTitle("SELECT MATCHER TYPE:");
        ListView listView = (ListView) dialog.findViewById(R.id.list);

        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.single_item_layout , R.id.singleItem, dummies);
        listView.setAdapter(ad);

        listView.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int posicao, long arg3) {
        		switch (posicao) {
        		case 1:
        			setMatcher(0);
        			txtMatcher.setText("ORB feature Matcher");
        			break;
        		case 2:
        			setMatcher(1);
        			txtMatcher.setText("ORB feature+KLT hybrid Matcher");
        			break;
        		case 0:
        			setMatcher(2);
        			txtMatcher.setText("Dense OF Matcher");
        			break;
        		}
            	dialog.dismiss();
            }
        });
        dialog.show();
	}
}

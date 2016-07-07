package com.example.scanmyth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Points {
	private double[] vertices;
	private float[] fvertices;
	float[] colorArray;
	private FloatBuffer vertexBuffer;
	private FloatBuffer colorBuffer;
	public native double[] getPointsArray();
	public native float[] getColorsArray();
	public Points(){
		String quebraLinha = System.getProperty("line.separator"); 
	    //this.vertices=pointsArray;
		this.vertices = getPointsArray();
		fvertices = new float[vertices.length];
	    colorArray = getColorsArray();
	    Log.e("Size", ""+colorArray.length);
	    String show ="",color="";
	    for (int i = 0,j=0; i < vertices.length; i+=3,j+=4) {
	    	fvertices[i] = (float)vertices[i];
	    	fvertices[i+1] = (float)vertices[i+1];
	    	fvertices[i+2] = (float)vertices[i+2];
	    	show += fvertices[i]+" "+fvertices[i+1]+" "+fvertices[i+2]+ " " + (int)Math.round(colorArray[j]*255)+" "+(int)Math.round(colorArray[j+1]*255)+" "+(int)Math.round(colorArray[j+2]*255) + quebraLinha;
		}
	    writeToFile(show);
	    
	    ByteBuffer byteBuf = ByteBuffer.allocateDirect( vertices.length *4 );
	    byteBuf.order( ByteOrder.nativeOrder() );
	    vertexBuffer = byteBuf.asFloatBuffer();
	    vertexBuffer.put( fvertices );
	    vertexBuffer.position( 0 );
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colorArray.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colorArray);
	    colorBuffer.position(0);
	    
	}


	public void draw( final GL10 gl ) {     
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer); // NEW LINE ADDED.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
	    gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY); // NEW LINE ADDED.
	    //gl.glEnable( GL10.GL_POINT_SMOOTH );
	    //gl.glEnable( GL10.GL_BLEND );
	    //gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
	    // Enable the color array buffer to be used during rendering.
	    // Point out the where the color buffer is.
	    /**point size*/
	    gl.glPointSize(4);
	    gl.glDrawArrays(GL10.GL_POINTS, 0, vertices.length/3);
	    gl.glDisableClientState( GL10.GL_VERTEX_ARRAY );
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

	}
	
	public void writeToFile(String text){
		String path = "/storage/emulated/0/dcim/sfm";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String hora = sdf.format(Calendar.getInstance().getTime());     
        String fileName = formatter.format(now) + " - " + hora + ".ply";
        String quebraLinha = System.getProperty("line.separator"); 
        // Monta a estrutura do arquivo PLY
        text =  "ply" + quebraLinha +
		        "format ascii 1.0" + quebraLinha +
		        "element vertex " + String.valueOf(vertices.length/3) + quebraLinha +
		        "property float32 x" + quebraLinha +
		        "property float32 y" + quebraLinha +
		        "property float32 z" + quebraLinha +
		        "property uchar red" + quebraLinha +
				"property uchar green" + quebraLinha +
				"property uchar blue" + quebraLinha +
		        "element face 0" + quebraLinha +
		        "property list uint8 int32 vertex_indices" + quebraLinha +
		        "end_header" + quebraLinha +
		        text;
        
        
		 try {
		    File root = new File("/storage/emulated/0/dcim/sfm", "ScanMyth Files");		    
		    if (!root.exists()) 
		    {
		        root.mkdirs();
		    }
		    File gpxfile = new File(root, fileName);
		
		
		    FileWriter writer = new FileWriter(gpxfile,true);
		    writer.append(text);
		    writer.flush();
		    writer.close();
		   
		    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		    intent.setData(Uri.fromFile(gpxfile));
		    ApplicationContextSingleton.getContext().sendBroadcast(intent);
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	

}

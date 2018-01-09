package com.eightmile.elevatorcontrol.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

public class AutoScroll extends TextView {
	private float textLength = 0f;//��������������������
	private float viewWidth = 0f;
	private float step = 0f;//����������������������������
	private float y = 0f;//��������������������������������
	private float temp_view_plus_text_length = 0.0f;//�������������������������������������������
	private float temp_view_plus_two_text_length = 0.0f;//�������������������������������������������
	public boolean isStarting = false;//����������������������
	private Paint paint =new Paint();//����������������
	private String text = "";//��������������������
	Canvas acanvas=new Canvas(); 
	@SuppressLint("WrongCall") 
	private Handler handler = new Handler(){
	   @Override
	   public void handleMessage(Message msg){
	       onDraw(acanvas);
	   }    
	 };
	 public AutoScroll(Context context){
		 super(context);
	//   initView();
	 }
	 public AutoScroll(Context context, AttributeSet attrs){
		 super(context, attrs);
	//   initView();
	 }
	 public AutoScroll(Context context, AttributeSet attrs, int defStyle){
		 super(context, attrs, defStyle);
	//   initView();
	 }
	 
	// public void onClick(View v) {
	//   if(isStarting)
//	             stopScroll();
//	         else
//	             startScroll();
	// }
	//  private void initView()
//	     {
//	         setOnClickListener(this);
//	     }
	  public void init(WindowManager windowManager){
//	         paint.setTextSize(40);
//	         paint.setColor(Color.WHITE);
		  text = getText().toString();
	      textLength = paint.measureText(text);//textview��������������������������������
	      viewWidth = getWidth();
	      if(viewWidth == 0){
	    	  if(windowManager != null){
	                 Display display = windowManager.getDefaultDisplay();
	                 viewWidth = display.getWidth();
	          }
	      }
	      step = textLength;
	      temp_view_plus_text_length = viewWidth + textLength;
	      temp_view_plus_two_text_length = viewWidth + textLength * 2;
	      y = getTextSize() + getPaddingTop();
	  }
	  public void startScroll(){
		  isStarting = true;
	      invalidate();
	  }
	  public void stopScroll(){
		  isStarting = false;
		  invalidate();
	  }
	  public void onDraw(Canvas canvas) {
	      acanvas = canvas;
	      acanvas.drawText(text, temp_view_plus_text_length - step, y, paint);
	      if(!isStarting){
	          return;
	      }
	      step += 2;//0.5��������������������������������
	      if(step > temp_view_plus_two_text_length)
	             step = textLength;
	      invalidate();
	  }
	  public void setPaint(int fontSize, String[] fontColor){
		  paint.setTextSize(fontSize);
//	  	  paint.setColor(Color.BLUE);
		  paint.setColor(Color.rgb(Integer.parseInt(fontColor[0].trim()), Integer.parseInt(fontColor[1].trim()), Integer.parseInt(fontColor[2].trim())));
	  }
}

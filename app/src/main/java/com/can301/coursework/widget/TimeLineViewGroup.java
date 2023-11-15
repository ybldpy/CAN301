package com.can301.coursework.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.can301.coursework.util.ViewUtil;


public class TimeLineViewGroup extends ViewGroup {



    DisplayMetrics metrics = getResources().getDisplayMetrics();

    private static final int GROUP_HEIGHT_DP = 900;





    public TimeLineViewGroup(Context context) {
        super(context);
    }

    public TimeLineViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimeLineViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
//    private static final int HOUR = 24;
//    private float blockHeight = 50;
//
//
//    public float getBlockHeight() {
//        return blockHeight;
//    }
//
//    public void setBlockHeight(float blockHeight) {
//        this.blockHeight = blockHeight;
//    }
//
//    private static final String[] HOUR_STR = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
//    private float hourStrOffset;
//    private float eventStrOffset;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize((int)getGroupHeightInPixel(getContext())));

    }




    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);

        int childCount = getChildCount();
        int parentWidth = right - left;
        int parentHeight = bottom - top;
        // 遍历子视图并布局
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            // 计算子视图的位置和大小
            int childLeft = 0; // 定义子视图左边位置
            int childTop = i * 100; // 定义子视图顶部位置
            int childRight = parentWidth; // 定义子视图右边位置
            int childBottom = getHeight(); // 定义子视图底部位置
            // 对子视图进行布局
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }


//    public float getHourStrOffset() {
//        return hourStrOffset;
//    }
//
//    public void setHourStrOffset(float hourStrOffset) {
//        this.hourStrOffset = hourStrOffset;
//    }
//
//    public float getEventStrOffset() {
//        return eventStrOffset;
//    }
//
//    public void setEventStrOffset(float eventStrOffset) {
//        this.eventStrOffset = eventStrOffset;
//    }
//
//    private void calcSize(){
//        blockHeight = 100;
//        hourStrOffset = 50;
//        eventStrOffset = hourStrOffset + 40;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        calcSize();
//        drawTimeLine(canvas);
//    }
//
//    private void drawTimeLine(Canvas canvas){
//        Paint paint = new Paint();
//        paint.setColor(Color.rgb(100,100,100));
//        Log.d("MYVIEW","123");
//        paint.setTextSize(20);
//        for(int i=0;i<HOUR;i++){
//            canvas.drawLine(0,i*getBlockHeight(),getWidth(),i*getBlockHeight(),paint);
//            canvas.drawText(HOUR_STR[i],getHourStrOffset(),i*getBlockHeight()+getBlockHeight()/2,paint);
//        }
//        paint.setColor(Color.BLUE);
//        canvas.drawRoundRect(getEventStrOffset(),2*getBlockHeight(),getWidth()/2,3.5f*getBlockHeight(),5,5,paint);
//        paint.setColor(Color.WHITE);
//        paint.setTextSize(getBlockHeight());
//        canvas.drawText("event",getEventStrOffset(),3f*getBlockHeight(),paint);
//    }


    public static float getGroupHeightInPixel(Context context){
        return ViewUtil.dp2pixel(context,GROUP_HEIGHT_DP);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);



    }
}

package com.can301.coursework.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.can301.coursework.R;
import com.can301.coursework.activity.PlanActivity;
import com.can301.coursework.model.Event;
import com.can301.coursework.util.ViewUtil;

import java.time.LocalDateTime;
import java.util.List;

public class TimeLine extends View {


    private static final int HOUR = 24;
    private float blockHeight;
    private static final int TEXT_SIZE = 10;
    private static final String[] HOUR_STR = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
    private static final int TIMELINE_HOUR_OFFSET_DP = 30;
    private static final int TIMELINE_EVENT_OFFSET_DP = 60;
    private static final int TIMELINE_EVENT_NAME_OFFSET_DP = 65;
    private static final int HEIGHT_DP = 1100;
    private List<Event> eventList;
    private static final int CLICK_OFFSET = 10;

    private long lastDown;

    private boolean state = true;

    private class OnClickListenerImpl implements DialogInterface.OnClickListener {

        private Event event;
        private View view;

        public void setEvent(Event event) {
            this.event = event;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Activity activity = ViewUtil.searchActivityFromView(TimeLine.this);
            if (activity != null) {
                PlanActivity planActivity = (PlanActivity) activity;
                planActivity.deleteEvent(event);
            }
            dialogInterface.dismiss();
        }
    }
    private OnClickListenerImpl longClickPositiveListener = new OnClickListenerImpl();


    private DialogInterface.OnClickListener negetiveListener = ((dialogInterface, i) -> {

        dialogInterface.dismiss();
    });


//    private float eventStrOffset;

    public TimeLine(Context context) {
        super(context);
    }

    public TimeLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    private List<Event> getEventList(){
        return eventList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),ViewUtil.dp2pixel(getContext(),HEIGHT_DP));
    }

    public float getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(float blockHeight) {
        this.blockHeight = blockHeight;
    }

    private int getHourOffsetPixel(){
        return ViewUtil.dp2pixel(getContext(),TIMELINE_HOUR_OFFSET_DP);
    }

    private int getEventOffsetPixel(){
        return ViewUtil.dp2pixel(getContext(),TIMELINE_EVENT_OFFSET_DP);
    }

    private int getEventNameOffsetPixel(){
        return ViewUtil.dp2pixel(getContext(),TIMELINE_EVENT_NAME_OFFSET_DP);
    }


    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener((view -> {



            Log.d("XANDY",view.getX() + ","+view.getY());
            return true;
        }));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            lastDown = System.currentTimeMillis();
            state = true;
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && state){
            long diff = System.currentTimeMillis() - lastDown;
            if (diff >= 6*100l){
                state = false;
                longClick(motionEvent);
            }
        }
        else {
            long diff  = System.currentTimeMillis() - lastDown;
            if (diff < 3*100){
                singleClick(motionEvent);
            }
        }
        return true;
    }


    private void singleClick(MotionEvent motionEvent){
        Event event = findEvent(motionEvent);
        if (event == null){return;}

    }

    private Event findEvent(MotionEvent motionEvent){
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Event event = null;
        for(Event e:eventList){
            if (checkClickInsect(e,x,y)){
                event = e;
            }
        }
        return event;
    }

    private int calcEventWidth(Event event){
        return getWidth();
    }
    private boolean checkClickInsect(Event event,float x,float y){
        int top =calcEventTop(event);
        int height = calcEventHeight(event);
        int left = getEventOffsetPixel();
        int right = calcEventWidth(event);
        if (x < left-CLICK_OFFSET || x > right+CLICK_OFFSET || y < top-CLICK_OFFSET || y > top+height+CLICK_OFFSET){return false;}
        return true;
    }

    private void longClick(MotionEvent motionEvent){
        Event event = findEvent(motionEvent);
        if (event == null){return;}
        Resources resources = getResources();
        longClickPositiveListener.setEvent(event);
        AlertDialog alertDialog = ViewUtil.makeAlertDialog(getContext(),
                resources.getString(R.string.delete),
                resources.getString(R.string.delete_event),
                resources.getString(R.string.delete),
                resources.getString(R.string.cancel),
                longClickPositiveListener,
                ((dialog,i)->{
                    dialog.dismiss();
                }));
        alertDialog.show();

    }

    private int getHeightPixel(){
        return ViewUtil.dp2pixel(getContext(),HEIGHT_DP);
    }

    private void calcSize(){
        blockHeight = getHeightPixel()/HOUR;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calcSize();
        drawTimeLine(canvas);
        if (eventList!=null){
            drawEvent(canvas);
        }
    }


    private int calcEventTop(Event event){
        float startHour = event.getStartHour()+ event.getStartMin() / 60f;
        return (int) (getBlockHeight() * startHour);
    }

    private int calcTextSize(){
        return ViewUtil.dp2pixel(getContext(),TEXT_SIZE);
    }

    private int calcEventHeight(Event event){
        return (int) ((event.getLength() / 60f) * getBlockHeight());
    }


    private boolean doDrawEventName(int height){
        return height > calcTextSize();
    }
    private void drawEvent(Canvas canvas){

        Paint paint = new Paint();
        paint.setTextSize(calcTextSize());
        for(Event event:getEventList()){
            paint.setColor(event.getStartHour()%2==0?Color.BLACK:Color.RED);
            int left = getEventOffsetPixel();
            int top = calcEventTop(event);
            int right = calcEventWidth(event);
            int height = calcEventHeight(event);
            canvas.drawRect(left,top,right,top+height,paint);
            if (doDrawEventName(height)){
                paint.setColor(Color.WHITE);
                canvas.drawText(event.getEventName(),getEventNameOffsetPixel(),top+(height + calcTextSize())/2,paint);
            }
        }

    }

    private void drawTimeLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(100,100,100));
//        Log.d("MYVIEW","123");
        paint.setTextSize(ViewUtil.dp2pixel(getContext(),TEXT_SIZE));
        paint.setColor(Color.rgb(250,252,214));
        for(int i=0;i<HOUR;i++){
            canvas.drawLine(0,i*getBlockHeight(),getWidth(),i*getBlockHeight(),paint);
            canvas.drawText(HOUR_STR[i],getHourOffsetPixel(),i*getBlockHeight()+getBlockHeight()/2,paint);
        }
//        paint.setColor(Color.BLUE);
//        canvas.drawRoundRect(getEventStrOffset(),2*getBlockHeight(),getWidth()/2,3.5f*getBlockHeight(),5,5,paint);
//        paint.setColor(Color.WHITE);
//        paint.setTextSize(getBlockHeight());
//        canvas.drawText("event",getEventStrOffset(),3f*getBlockHeight(),paint);
    }
}

//package com.can301.coursework.widget;
//
//import static com.can301.coursework.widget.TimeLine.getHourOffsetPixel;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.Nullable;
//
//import com.can301.coursework.model.Event;
//import com.can301.coursework.util.ViewUtil;
//
//public class EventView extends View {
//    private Event event;
//    private float hourHeight;
//
//
//    private static final int TIMELINE_EVENT_OFFSET_DP = 30;
//    public float getHourHeight() {
//        return hourHeight;
//    }
//    public void setHourHeight(float hourHeight) {
//        this.hourHeight = hourHeight;
//    }
//
//    public static int getTimelineEventOffsetPixel(Context context){
//        return getHourOffsetPixel(context) + ViewUtil.dp2pixel(context,TIMELINE_EVENT_OFFSET_DP);
//    }
//
//
//    @Override
//    public void layout(int l, int t, int r, int b) {
//
//        int left = getTimelineEventOffsetPixel(getContext());
//        int top = calcTop();
//        int right = getWidth();
//        int bottom = getHeight();
//        super.layout(left,top,right,bottom);
//    }
//
//    private int calcTop(){
//        float beginTime = event.getBegin().getHour() + event.getBegin().getMinute()/60f;
//        return (int) (beginTime*(TimeLineViewGroup.getGroupHeightInPixel(getContext())/24f));
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = (int) TimeLineViewGroup.getGroupHeightInPixel(getContext());
//        width-=getTimelineEventOffsetPixel(getContext());
//        double diffInHour = event.getLength()/60d;
//        setMeasuredDimension(width,(int)(height/24*diffInHour));
//    }
//
//
//    public EventView(Context context) {
//        super(context);
//    }
//
//    public Event getEvent() {
//        return event;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        return super.onTouchEvent(event);
//
//    }
//
//    public void setEvent(Event event) {
//        this.event = event;
//    }
//
//    public EventView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public EventView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public EventView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
//    }
//
//
//}

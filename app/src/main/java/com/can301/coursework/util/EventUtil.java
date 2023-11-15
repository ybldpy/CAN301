package com.can301.coursework.util;

import android.content.Context;
import android.widget.Toast;

import com.can301.coursework.R;
import com.can301.coursework.activity.PlanActivity;
import com.can301.coursework.model.Event;

import java.util.List;

public class EventUtil {

    public static int convertConsumingTimeStr2Length(Context context,String str){
        if (str == null || str.isEmpty()){
            Toast.makeText(context,context.getString(R.string.min_minutes),Toast.LENGTH_LONG);
            return -1;
        }
        int length = Integer.parseInt(str);
        return length;
    }
    public static boolean checkAddParameters(Context context, String name, int length, int startHour, int startMinute, int min, int max, List<Event> events){
        if (name == null || name.length() <= 0){
            Toast.makeText(context,context.getString(R.string.empty_event_name),Toast.LENGTH_LONG).show();
            return false;
        }
        if (length < min){
            Toast.makeText(context,context.getString(R.string.min_minutes),Toast.LENGTH_LONG).show();
            return false;
        }
        if (length > max){
            Toast.makeText(context,context.getString(R.string.max_minutes),Toast.LENGTH_LONG).show();
            return false;
        }
        int end = startMinute + startHour * 60 + length;
        if (end >= 60*24){
            Toast.makeText(context,context.getString(R.string.two_day),Toast.LENGTH_LONG).show();
            return false;
        }

        boolean collision = checkCollision(events,startHour*60+startMinute,end);
        if (collision){
            Toast.makeText(context,context.getString(R.string.collision),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private static boolean checkCollision(List<Event> events,int begin,int end){
        if (events == null){return true;}
        for(Event e:events){
            int eBegin = e.getStartHour() * 60 + e.getStartMin();
            int eEnd = eBegin + e.getLength();
            if(doIntervalsIntersect(begin,end,eBegin,eEnd)){
                return true;
            }
        }
        return false;
    }

    private static boolean doIntervalsIntersect(int begin,int end,int begin1,int end1){
        if (begin >= begin1 && end<=end1){
            return true;
        }
        else if (begin >= begin1 && begin <= end1){
            return true;
        }
        else if (end >= begin1 && end <= end1){
            return true;
        }
        return false;
    }

}

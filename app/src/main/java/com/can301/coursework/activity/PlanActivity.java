package com.can301.coursework.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.can301.coursework.R;
import com.can301.coursework.adapter.QuickEventAdapter;
import com.can301.coursework.database.EventDatabaseManager;
import com.can301.coursework.model.Event;

import com.can301.coursework.util.EventUtil;
import com.can301.coursework.util.FileUtil;
import com.can301.coursework.util.SyncUtil;
import com.can301.coursework.util.ViewUtil;
import com.can301.coursework.widget.TimeLine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;


import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanActivity extends AppCompatActivity {
//    private static final int MIN_CONSUMING_TIME = 15;
//    private static final int MAX_CONSUMING_TIME = 60*2;
    private List<Event> events;
    private Set<String> quickEvents;
    private RecyclerView recyclerView;
    private TimeLine timeLine;
    private Button calendarBtn;
    private FloatingActionButton addEventBtn;
    private static final String QUICK_EVENTS_FILE = "QUICK_EVENTS";
    private File quickEventFile;


    public void modifyQuickEvent(final String from,final String to){
        if (quickEvents.contains(to)){return;}
        quickEvents.remove(from);
        addQuickEvent(to);
    }

    public void deleteQuickEvent(final String name){
        int before = quickEvents.size();
        Set<String> copiedQuickEvents = new HashSet<>(quickEvents);
        copiedQuickEvents.remove(name);
        if (before == copiedQuickEvents.size()){return;}
        String jsonStr = set2Json(copiedQuickEvents);
        storeQuickEvent(jsonStr,name,false);
    }


    public void deleteEvent(Event event){
        new Thread(()->{
            EventDatabaseManager.deleteEvent(event);
            updateEvent();
        }).start();

    }

    private void storeQuickEvent(String jsonStr,String name,boolean addFlag){
        new Thread(()->{
            boolean success = FileUtil.writeJsonToFile(jsonStr,quickEventFile);
            if (success){
                runOnUiThread(()->{
                    if (addFlag){
                        quickEvents.add(name);
                    }
                    else {
                        quickEvents.remove(name);
                    }
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    QuickEventAdapter quickEventAdapter = null;
                    if (adapter == null){
                        quickEventAdapter = new QuickEventAdapter(quickEvents);
                        recyclerView.setAdapter(quickEventAdapter);
                    }
                    else {
                        quickEventAdapter = (QuickEventAdapter) adapter;
                        quickEventAdapter.setDataSet(quickEvents);
                        quickEventAdapter.notifyDataSetChanged();
                    }
                    if (recyclerView.getVisibility() == View.GONE && quickEventAdapter.getItemCount() >= 1){
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    else if (quickEventAdapter.getItemCount() <=0){
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
    private void readQuickEvent(){
        new Thread(()->{
            String quickEventsStr = FileUtil.readJsonFile(quickEventFile);
            if (quickEventsStr == null || quickEventsStr.isEmpty()){
                return;
            }
            Gson gson = new Gson();
            String[] quickEvents = gson.fromJson(quickEventsStr,String[].class);
            this.quickEvents.clear();
            for(String s:quickEvents){
                this.quickEvents.add(s);
            }
            if(this.quickEvents.size() > 0){
                runOnUiThread(()->{
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (adapter == null){
                        QuickEventAdapter quickEventAdapter = new QuickEventAdapter(PlanActivity.this.quickEvents);
                        recyclerView.setAdapter(quickEventAdapter);
                    }
                    else {
                        QuickEventAdapter quickEventAdapter = (QuickEventAdapter)adapter;
                        quickEventAdapter.setDataSet(PlanActivity.this.quickEvents);
                        quickEventAdapter.notifyDataSetChanged();
                    }
                    if (recyclerView.getVisibility() != View.VISIBLE){
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                });
            }
        }).start();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    private void showMakeEventDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        // 加载自定义布局
        View customView = inflater.inflate(R.layout.make_event_dialog, null);
        final AlertDialog customDialog = ViewUtil.makeDialog(this,customView);
        TimePicker timePicker = customView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        // 设置关闭按钮点击事件
        customDialog.setCancelable(false);
        Button cancelBtn = customView.findViewById(R.id.add_cancel);
        cancelBtn.setOnClickListener((view -> {
            customDialog.dismiss();
        }));

        TextInputEditText eventNameInput = customView.findViewById(R.id.event_name_input);
        eventNameInput.setOnFocusChangeListener(((view, b) -> {
            if (!b){
                hideKeyBoard(view);
            }
        }));
        Button addEventBtn = customView.findViewById(R.id.add_add);
        addEventBtn.setOnClickListener((view -> {
            hideKeyBoard(view);
            // check params
            TextInputEditText eventName = eventNameInput;
            String eventNameStr = eventName.getText().toString();
            TextInputEditText consumingTime = customView.findViewById(R.id.consuming_time);
            String consumingStr = consumingTime.getText().toString();
            int length = EventUtil.convertConsumingTimeStr2Length(PlanActivity.this,consumingStr);
            int startHour = timePicker.getHour();
            int startMinute = timePicker.getMinute();
            if (!EventUtil.checkAddParameters(PlanActivity.this,eventNameStr,length,startHour,startMinute,PlanActivity.this.getResources().getInteger(R.integer.min_minutes),PlanActivity.this.getResources().getInteger(R.integer.max_minutes),events)){
                return;
            }
            CheckBox addToEventCheckBox = customView.findViewById(R.id.quick_event_checkbox);
            boolean addToQuickEvent = addToEventCheckBox.isChecked();
            ProgressDialog progressDialog1 = SyncUtil.makeProcessDialog(this,"Processing",false);
            progressDialog1.show();
            new Thread(()->{
                Event event = new Event();
                event.setEventName(eventNameStr);
                event.setLength(length);
                event.setDay(dateTime);
                event.setStartHour(startHour);
                event.setStartMin(startMinute);
                EventDatabaseManager.insertEvent(event);
                if (addToQuickEvent){
                    addQuickEvent(event.getEventName());
                }
                runOnUiThread(()->{
                    progressDialog1.dismiss();
                    customDialog.dismiss();
                    updateEvent();});
            }).start();
        }));
        // 显示对话框
        customDialog.show();
    }


    private void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     *
     * @return false -- no collision
     */
    private boolean checkCollision(List<Event> events,int begin,int end){
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

//    private AlertDialog makeAlertDialog(Context context,String title,String message){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(title);
//        builder.setMessage(message);
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 处理确定按钮点击事件
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        return alertDialog;
//    }


    public void startEvent(Event event){
        Intent i = new Intent();
        i.putExtra(PrepareActivity.countLengthKey,event.getLength());
        i.setAction(getString(R.string.action_prepare_count));
        startActivity(i);
    }

    private void addQuickEvent(String name){

        Set<String> copiedQuickEvents = new HashSet<>(quickEvents);
        int before = copiedQuickEvents.size();
        copiedQuickEvents.add(name);
        if (before == copiedQuickEvents.size()){return;}
        // start a async to store new quick event
        String jsonStr = set2Json(copiedQuickEvents);
        storeQuickEvent(jsonStr,name,true);
    }

    private static String set2Json(Set<String> set){
        List<String> stringList = new ArrayList<>(Math.max(1,set.size()));
        for(String s:set){
            stringList.add(s);
        }
        Gson gson = new Gson();
        return gson.toJson(stringList);
    }

    public void updateEvent(){
        if(getMainLooper().isCurrentThread()){
            ProgressDialog progressDialog1 = SyncUtil.makeProcessDialog(this,"Processing",false);
            progressDialog1.show();
            new Thread(()->{
                List<Event> eventList = EventDatabaseManager.getEventsByDate(getDateTime());
                this.events = eventList;
                runOnUiThread(()->{
                    updateEvent(progressDialog1);
                });
            }).start();
        }
        else {
            runOnUiThread(()->{
                ProgressDialog progressDialog1 = SyncUtil.makeProcessDialog(this,"Processing",false);
                progressDialog1.show();
                new Thread(()->{
                    List<Event> eventList = EventDatabaseManager.getEventsByDate(getDateTime());
                    this.events = eventList;
                    runOnUiThread(()->{
                        updateEvent(progressDialog1);
                    });
                }).start();
            });
        }
    }
    private void updateEvent(ProgressDialog progressDialog){
        Button button = findViewById(R.id.calender_btn);
        button.setText(String.format("%d/%d/%d",dateTime.getYear(),dateTime.getMonth().getValue(),dateTime.getDayOfMonth()));
        TimeLine timeLine = findViewById(R.id.timeline);
        timeLine.setEventList(this.events);
        timeLine.postInvalidate();
//        runOnUiThread(()->{
//            TimeLine timeLine = findViewById(R.id.timeline);
//            timeLine.setEventList(this.events);
//            timeLine.postInvalidate();
//            progressDialog.dismiss();
//        });
        progressDialog.dismiss();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            PlanActivity.this.setDate(LocalDateTime.of(i,i1+1,i2,0,0));
        }
    };
    private void setDate(LocalDateTime day){
        dateTime = day;
        updateEvent();
    }
    LocalDateTime dateTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        quickEvents = new HashSet<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        init();
    }
    private void init(){
        calendarBtn = findViewById(R.id.calender_btn);
        timeLine = findViewById(R.id.timeline);
        timeLine.setOnLongClickListener(null);
        calendarBtn.setOnClickListener((view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PlanActivity.this);
            datePickerDialog.setOnDateSetListener(dateSetListener);
            datePickerDialog.show();
        }));
        addEventBtn = findViewById(R.id.add_event);
        addEventBtn.setOnClickListener((view -> {
            showMakeEventDialog();
        }));
        recyclerView = findViewById(R.id.quick_event_list);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        quickEventFile = new File(getFilesDir(),QUICK_EVENTS_FILE);
        readQuickEvent();
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalDateTime now = LocalDateTime.now();
        dateTime = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        setDate(dateTime);
    }

    public List<Event> getEvents() {
        return events;
    }
}





package com.can301.coursework.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.can301.coursework.R;
import com.can301.coursework.activity.PlanActivity;
import com.can301.coursework.database.EventDatabaseManager;
import com.can301.coursework.model.Event;
import com.can301.coursework.util.EventUtil;
import com.can301.coursework.util.SyncUtil;
import com.can301.coursework.util.ViewUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuickEventAdapter extends RecyclerView.Adapter<QuickEventAdapter.ViewHolder> {

    private List<String> dataSet;

    public List<String> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<String> dataSet) {
        this.dataSet = dataSet;
    }

    public void setDataSet(Set<String> dataSet) {
        List<String> quickEvents = new ArrayList<>(dataSet.size());
        for(String s:dataSet){
            quickEvents.add(s);
        }
        setDataSet(quickEvents);
    }

    public QuickEventAdapter(List<String> dataSet) {
        this.dataSet = dataSet;
    }
    public QuickEventAdapter(Set<String> dataSet){
        List<String> quickEvents = new ArrayList<>(dataSet.size());
        for(String s:dataSet){
            quickEvents.add(s);
        }
        this.dataSet = quickEvents;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_quick_event_view, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataSet.get(position);
        holder.bind(item);
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewLeft;
        private ImageView buttonRight;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewLeft = itemView.findViewById(R.id.quick_event_item);
            buttonRight = itemView.findViewById(R.id.buttonRight);
        }

        public void bind(final String item) {
            textViewLeft.setText(item);
            textViewLeft.setOnClickListener((view -> {
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                final String original = textViewLeft.getText().toString();
                // 加载自定义布局
                View customView = inflater.inflate(R.layout.quick_event_edit, null);
                TextInputEditText inputText = customView.findViewById(R.id.quick_event_name);
                inputText.setText(textViewLeft.getText());
                inputText.setSelection(textViewLeft.getText().length());
                final AlertDialog editDialog = ViewUtil.makeDialog(view.getContext(),customView);
                editDialog.setTitle(view.getContext().getString(R.string.edit_quick_event));
                editDialog.setCancelable(false);
                Button cancel = customView.findViewById(R.id.cancel);
                editDialog.show();
                cancel.setOnClickListener((btnView -> {
                    editDialog.dismiss();
                }));
                Button modify = customView.findViewById(R.id.modify);
                modify.setOnClickListener((modifyBtn)->{
                    String newName = inputText.getText().toString();
                    if (newName == null || newName.isEmpty()){
                        Toast.makeText(view.getContext(),view.getContext().getString(R.string.empty_event_name),Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (newName.equals(original)){
                        editDialog.dismiss();
                    }
                    PlanActivity planActivity = (PlanActivity) ViewUtil.searchActivityFromView(view);
                    planActivity.modifyQuickEvent(original,newName);
                    editDialog.dismiss();
                });


            }));
            textViewLeft.setOnLongClickListener((view -> {
                Context context = view.getContext();
                AlertDialog alertDialog = ViewUtil.makeAlertDialog(context,
                        context.getString(R.string.delete),
                        context.getString(R.string.delete_event),
                        context.getString(R.string.delete),
                        context.getString(R.string.cancel),
                        (dialog, i) -> {
                            Activity activity = ViewUtil.searchActivityFromView(view);
                            if (!(activity instanceof PlanActivity)){return;}
                            PlanActivity planActivity = (PlanActivity) activity;
                            planActivity.deleteQuickEvent(textViewLeft.getText().toString());
                        },
                        (dialog,i)->{
                            dialog.dismiss();
                        }
                );

                alertDialog.show();
                return true;
            }));
            buttonRight.setOnClickListener((view) -> {
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                View dialogView = inflater.inflate(R.layout.make_event_dialog, null);

                AlertDialog dialog = ViewUtil.makeDialog(itemView.getContext(),dialogView);

                CheckBox checkBox = dialogView.findViewById(R.id.quick_event_checkbox);
                checkBox.setVisibility(View.GONE);
                TextInputEditText inputEditText = dialogView.findViewById(R.id.event_name_input);
                inputEditText.setVisibility(View.GONE);
                TextView textView = dialogView.findViewById(R.id.quick_event_name);
                textView.setText(textViewLeft.getText());
                textView.setVisibility(View.VISIBLE);
                Button addBtn = dialogView.findViewById(R.id.add_add);
                addBtn.setOnClickListener((btnView -> {
                    Context context = itemView.getContext();
                    String name = textView.getText().toString();
                    TextInputEditText consumingInput = dialogView.findViewById(R.id.consuming_time);
                    int length = EventUtil.convertConsumingTimeStr2Length(context,consumingInput.getText().toString());
                    TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
                    int startHour = timePicker.getHour();
                    int startMinute = timePicker.getMinute();
                    List<Event> events = null;
                    Activity activity = ViewUtil.searchActivityFromView(itemView);
                    PlanActivity planActivity = null;
                    LocalDateTime day = null;
                    if (activity!=null){
                        planActivity = (PlanActivity) activity;
                        events = planActivity.getEvents();
                        day = planActivity.getDateTime();
                    }
                    if (!EventUtil.checkAddParameters(context,name,length,startHour,startMinute,context.getResources().getInteger(R.integer.min_minutes),context.getResources().getInteger(R.integer.max_minutes),events)){
                        return;
                    }
                    final PlanActivity finalPlanActivity = planActivity;
                    Event event = new Event();
                    event.setEventName(name);
                    event.setLength(length);
                    event.setDay(day);
                    event.setStartHour(startHour);
                    event.setStartMin(startMinute);
                    ProgressDialog progressDialog1 = SyncUtil.makeProcessDialog(finalPlanActivity,"Processing",false);
                    progressDialog1.show();
                    new Thread(()->{
                        EventDatabaseManager.insertEvent(event);
                        finalPlanActivity.runOnUiThread(()->{
                            progressDialog1.dismiss();
                            dialog.dismiss();
                            finalPlanActivity.updateEvent();
                        });
                    }).start();
                }));
                Button cancelBtn = dialogView.findViewById(R.id.add_cancel);
                cancelBtn.setOnClickListener((view1 -> {

                    dialog.dismiss();

                }));
                dialog.show();
            });
        }
    }
}

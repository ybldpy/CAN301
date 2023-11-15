package com.can301.coursework.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;


@Entity(tableName = "Events")
public class Event {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return length == event.length && id == event.id && startHour == event.startHour && startMin == event.startMin && Objects.equals(eventName, event.eventName) && Objects.equals(day, event.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, day, length, id, startHour, startMin);
    }

    @ColumnInfo(name = "event_name")
    private String eventName;

    @ColumnInfo(name = "day")
    private LocalDateTime day;
    @ColumnInfo(name = "length")
    private int length;

    @PrimaryKey(autoGenerate = true)
    public long id;
    public LocalDateTime getDay() {
        return day;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
    }


    @ColumnInfo(name = "start_hour")
    private int startHour;



    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    @ColumnInfo(name = "start_min")
    private int startMin;
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }



    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}

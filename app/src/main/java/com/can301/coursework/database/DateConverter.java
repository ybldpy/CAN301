package com.can301.coursework.database;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ValueRange;

public class DateConverter {


    private static final LocalDateTime DEFAULT_DATE =  LocalDateTime.of(2000,1,1,0,0);

    @TypeConverter
    public static LocalDateTime fromTimestamp(Long value) {
        if (value == null){
            return DEFAULT_DATE;
        }
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    @TypeConverter
    public static Long dateToTimestamp(LocalDateTime date) {
        if (date == null) {
            return DEFAULT_DATE.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

package com.can301.coursework.database;

import android.content.Context;
import androidx.room.Room;

import com.can301.coursework.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventDatabaseManager {

    private static final String DATABASE_NAME = "app_database_event";

    private static AppDatabase appDatabase;

    public static AppDatabase initDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .build();
        }
        return appDatabase;
    }

    public static void insertEvent(Event event) {
        if (appDatabase != null) {
            appDatabase.eventDao().insertEvent(event);
        }
    }

    public static List<Event> getEventsByDate(LocalDateTime searchDate) {
        if (appDatabase != null) {
            return appDatabase.eventDao().getEventsByDate(searchDate);
        }
        return null;
    }


    public static void deleteEvent(Event event){
        if (appDatabase!=null){
            appDatabase.eventDao().deleteEventById(event.id);
        }

    }
}

package com.can301.coursework.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.can301.coursework.database.Dao.EventDao;
import com.can301.coursework.model.Event;

@Database(entities = {Event.class},version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();

}

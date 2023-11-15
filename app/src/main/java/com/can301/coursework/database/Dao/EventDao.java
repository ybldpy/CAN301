package com.can301.coursework.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.can301.coursework.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM Events WHERE day = :searchDate")
    List<Event> getEventsByDate(LocalDateTime searchDate);
    @Insert
    void insertEvent(Event event);

    @Query("DELETE FROM Events WHERE id = :eventId")
    void deleteEventById(long eventId);

}

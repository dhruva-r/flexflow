package com.example.flexflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexflow.data.entity.EventEntity
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
//    @Insert
//    suspend fun insert(event: EventEntity)
//
//    @Query("SELECT * FROM event WHERE month = :month AND day = :day AND year = :year " +
//            "ORDER BY startDate ASC")
//     fun getEventsFromDate(day: Int, month: Int, year: Int): Flow<List<EventEntity>>
//    @Query("SELECT * FROM event WHERE month = :month AND day = :day AND year = :year " +
//            "ORDER BY startDate ASC")
//    suspend fun getEventsFromDateList(day: Int, month: Int, year: Int): List<EventEntity>
//    @Query("SELECT * FROM event WHERE (:start BETWEEN startDate AND endDate) OR (:end BETWEEN startDate AND endDate)")
//    suspend fun getEventsBetweenDates(start: Date, end: Date): List<EventEntity>
//    @Query("SELECT * FROM event ")
//    fun getEvents(): Flow<List<EventEntity>>
//    @Update
//    suspend fun update(event: EventEntity)
//
//    @Query("DELETE FROM event WHERE id = :eventId")
//    suspend fun deleteById(eventId: Long)
    @Insert
    fun insert(event: EventEntity)

    @Query("SELECT * FROM event WHERE month = :month AND day = :day AND year = :year " +
            "ORDER BY startDate ASC")
    fun getEventsFromDate(day: Int, month: Int, year: Int): Flow<List<EventEntity>>
    @Query("SELECT * FROM event WHERE month = :month AND day = :day AND year = :year " +
            "ORDER BY startDate ASC")
    fun getEventsFromDateList(day: Int, month: Int, year: Int): List<EventEntity>
    @Query("SELECT * FROM event WHERE month = :month AND day = :day AND year = :year " +
            "ORDER BY priority DESC")
    fun getEventsFromDateListPrio(day: Int, month: Int, year: Int): List<EventEntity>
    @Query("SELECT * FROM event WHERE (:start BETWEEN startDate AND endDate) OR (:end BETWEEN startDate AND endDate)")
    fun getEventsBetweenDates(start: Date, end: Date): List<EventEntity>
    @Query("SELECT * FROM event ")
    fun getEvents(): Flow<List<EventEntity>>
    @Update
    fun update(event: EventEntity)

    @Query("DELETE FROM event WHERE id = :eventId")
    fun deleteById(eventId: Long)

    @Query("SELECT * FROM event WHERE taskId = :taskId")
    fun getEventsFromTaskId(taskId: Long) : List<EventEntity>
}


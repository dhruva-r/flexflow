package com.example.flexflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.flexflow.data.entity.ScheduleEntity
import com.example.flexflow.data.entity.TaskEntity

@Dao
interface ScheduleDao {
    @Insert
    //suspend fun insert(schedule: ScheduleEntity): Long
    fun insert(schedule: ScheduleEntity): Long

    @Query("SELECT * FROM schedule")
    //suspend fun getAllSchedules(): List<ScheduleEntity>
    fun getAllSchedules(): List<ScheduleEntity>

    @Query("DELETE FROM schedule WHERE id = :id")
    //suspend fun deleteById(id: Long)
    fun deleteById(id: Long)


    //need to add more for schedule like getDaily(), getWeekly() etc ...

}

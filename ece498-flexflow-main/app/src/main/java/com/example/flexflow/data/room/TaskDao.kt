package com.example.flexflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexflow.data.entity.JournalEntity
import com.example.flexflow.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import org.checkerframework.checker.units.qual.Current
import java.util.Date

@Dao
interface TaskDao {
//    @Insert
//    suspend fun insert(task: TaskEntity)
//
//    @Query("SELECT * FROM task WHERE scheduleId = :scheduleId")
//    suspend fun getTasksByScheduleId(scheduleId: Long): List<TaskEntity>
//
//    @Update
//    suspend fun update(task: TaskEntity)
//
//    @Query("DELETE FROM task WHERE id = :taskId")
//    suspend fun deleteById(taskId: Long)
//
//    @Query("SELECT COALESCE(AVG(priority), 0.0) AS avg_priority FROM task WHERE dueDate >= :current AND dueDate <= :due")
//    suspend fun getAvgPriorityInBetween(current: Date, due: Date): Double

    @Insert
    fun insert(task: TaskEntity)

    @Query("SELECT * FROM task WHERE scheduleId = :scheduleId")
    fun getTasksByScheduleId(scheduleId: Long): List<TaskEntity>

    @Query("SELECT * FROM task GROUP BY dueDate")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Update
    fun update(task: TaskEntity)

    @Query("DELETE FROM task WHERE id = :taskId")
    fun deleteById(taskId: Long)

    @Query("SELECT COALESCE(AVG(priority), 0.0) AS avg_priority FROM task WHERE dueDate >= :current AND dueDate <= :due")
    fun getAvgPriorityInBetween(current: Date, due: Date): Double

    @Query("SELECT * FROM task WHERE name = :name AND dueDate = :dueDate AND priority = :priority")
    fun getTaskWithoutId(name: String, dueDate: Date, priority: Double) : TaskEntity

}

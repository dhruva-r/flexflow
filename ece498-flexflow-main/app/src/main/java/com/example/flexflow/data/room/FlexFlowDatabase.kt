package com.example.flexflow.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.flexflow.data.DateTimeConverter
import com.example.flexflow.data.entity.EventEntity
import com.example.flexflow.data.entity.TaskEntity
import com.example.flexflow.data.entity.ScheduleEntity
import com.example.flexflow.data.entity.UserEntity
import com.example.flexflow.data.entity.JournalEntity
@TypeConverters(DateTimeConverter::class)
@Database(
    entities = [
        JournalEntity::class,
        UserEntity::class,
        ScheduleEntity::class,
        TaskEntity::class,
        EventEntity::class
    ],
    version = 3
)

abstract class FlexFlowDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
}

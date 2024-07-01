package com.example.flexflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var isUpdated: Boolean = false
//    val currentDate: Date
)


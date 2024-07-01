package com.example.flexflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = "",
    var details: String = "",
    var startDate: Date = Date(0),
    var endDate: Date = Date(0),
    var taskId: Long = 0,
    var priority: Double = 0.0,
    var eventCompletion: Boolean = false,
    var taskCompletion: Boolean = false,
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0
)


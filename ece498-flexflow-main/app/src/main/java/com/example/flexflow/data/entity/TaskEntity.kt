package com.example.flexflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var scheduleId: Long = 0,
    var name: String = "",
    var details: String = "",
    var priority: Double = 0.0,
    var deadline: Int = 0,
    var timeRestraint: Double = 0.0,
    var requisite: Double = 0.0,
    var commitment: Double = 0.0,
    var complexity: Double = 0.0,
    var importance: Double = 0.0,
    var dueDate: Date = Date(0),
    var startDate: Date = Date(0),
    var endDate: Date = Date(0)
)


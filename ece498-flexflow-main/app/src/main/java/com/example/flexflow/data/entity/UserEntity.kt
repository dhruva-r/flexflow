package com.example.flexflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var firstName: String = "",
    var lastName: String = "",
    var moodId: Int = 0,
    var gender: String = "",
    var showDailyQuote: Boolean = true,
    var darkTheme: Boolean = false
)


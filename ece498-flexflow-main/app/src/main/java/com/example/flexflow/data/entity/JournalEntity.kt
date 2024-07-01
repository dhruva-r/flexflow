package com.example.flexflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "journal")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var journal_entry: String = "",   // Stores the journal entry
    var mood: String = "",            // Stores the emoji mood entry
    var date: Date = Date(0) // initializes the date with the current date and time
)
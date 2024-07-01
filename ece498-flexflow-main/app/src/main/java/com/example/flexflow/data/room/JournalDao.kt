package com.example.flexflow.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexflow.data.entity.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert
    //suspend fun insert(journal: JournalEntity)
    fun insert(journal: JournalEntity)

    @Update
    //suspend fun update(journal: JournalEntity)
    fun update(journal: JournalEntity)

    @Delete
    //suspend fun delete(journal: JournalEntity)
    fun delete(journal: JournalEntity)

    // Will probs only need this one
    @Query("SELECT * FROM journal ORDER BY date DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>

    // May need this
    @Query("SELECT * FROM journal ORDER BY date ASC")
    //suspend fun getListJournals(): List<JournalEntity>
    fun getListJournals(): List<JournalEntity>

    // TODO Need similar to mydecksviewmodel from chummerly
    // TODO Make ui page for viewing all journals
    // TDO Rob Dhruva
}

package com.example.flexflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flexflow.data.entity.UserEntity

@Dao
interface UserDao {
//    @Insert
//    suspend fun insert(user: UserEntity): Long
//
//    @Query("SELECT * FROM user")
//    suspend fun getAllUsers(): List<UserEntity>
//
//    @Query("SELECT * FROM user WHERE id = :id")
//    suspend fun getUserById(id: Long): UserEntity?
//
//    @Update
//    suspend fun update(user: UserEntity)
//
//    @Query("DELETE FROM user WHERE id = :id")
//    suspend fun deleteById(id: Long)
    @Insert
    fun insert(user: UserEntity): Long

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserById(id: Long): UserEntity?

    @Update
    fun update(user: UserEntity)

    @Query("DELETE FROM user WHERE id = :id")
    fun deleteById(id: Long)
}

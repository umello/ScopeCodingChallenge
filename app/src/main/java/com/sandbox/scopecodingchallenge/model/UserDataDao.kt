package com.sandbox.scopecodingchallenge.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDataDao {
    @Insert
    suspend fun insertAll(vararg userData: UserData)

    @Query("SELECT * FROM userdata")
    suspend fun getAll(): List<UserData>

    @Query("DELETE FROM userdata")
    suspend fun deleteAll()
}
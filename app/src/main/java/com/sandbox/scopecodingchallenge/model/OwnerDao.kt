package com.sandbox.scopecodingchallenge.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OwnerDao {
    @Insert
    suspend fun insertAll(vararg owners: Owner)

    @Query("SELECT * FROM owner WHERE userId = :userId")
    suspend fun getByUserId(userId: Long) : Owner

    @Query("DELETE FROM owner")
    suspend fun deleteAll()
}
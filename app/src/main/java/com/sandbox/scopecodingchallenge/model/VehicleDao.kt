package com.sandbox.scopecodingchallenge.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VehicleDao {
    @Insert
    suspend fun insertAll(vararg vehicles: Vehicle)

    @Query("SELECT * FROM vehicle")
    suspend fun getAllVehicles(): List<Vehicle>

    @Query("SELECT * FROM vehicle WHERE ownerId = :userId")
    suspend fun getUserVehicles(userId: Long) : List<Vehicle>

    @Query("DELETE FROM vehicle")
    suspend fun deleteAllVehicles()
}
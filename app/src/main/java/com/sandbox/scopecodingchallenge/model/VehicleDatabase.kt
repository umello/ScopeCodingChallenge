package com.sandbox.scopecodingchallenge.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Vehicle::class], version = 1)
abstract class VehicleDatabase: RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao

    companion object {
        @Volatile private var instance: VehicleDatabase? = null
        private val lock = Any()

        operator fun invoke(context:Context) = instance ?: synchronized(lock) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            VehicleDatabase::class.java,
            "vehicledatabase"
        ).build()
    }
}
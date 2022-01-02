package com.sandbox.scopecodingchallenge.model

import android.content.Context
import androidx.room.*

@Database(entities = [UserData::class, Owner::class, Vehicle::class], version = 1)
@TypeConverters(Converters::class)
abstract class VehicleDatabase: RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun ownerDao(): OwnerDao
    abstract fun userDataDao(): UserDataDao

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

class Converters {
    @TypeConverter
    fun fromStringToOwner(value: String?): Owner? {
        return null
    }

    @TypeConverter
    fun ownerToNull(owner: Owner?): String {
        return "empty"
    }
    @TypeConverter
    fun fromStringToVehicleListPlaceholder(value: String?): List<Vehicle>? {
        return listOf()
    }

    @TypeConverter
    fun vehicleListToPlaceholder(list: List<Vehicle>?): String {
        return "empty"
    }
}
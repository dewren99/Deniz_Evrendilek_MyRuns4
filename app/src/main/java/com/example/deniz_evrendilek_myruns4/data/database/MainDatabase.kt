package com.example.deniz_evrendilek_myruns4.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.deniz_evrendilek_myruns4.data.converters.ArrayListLatLngConverter
import com.example.deniz_evrendilek_myruns4.data.converters.CalendarConverter
import com.example.deniz_evrendilek_myruns4.data.dao.ExerciseEntryDao
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry

@Database(entities = [ExerciseEntry::class], version = 1, exportSchema = false)
@TypeConverters(CalendarConverter::class, ArrayListLatLngConverter::class)
abstract class MainDatabase : RoomDatabase() {
    abstract val exerciseEntryDao: ExerciseEntryDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, MainDatabase::class.java, "main_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}
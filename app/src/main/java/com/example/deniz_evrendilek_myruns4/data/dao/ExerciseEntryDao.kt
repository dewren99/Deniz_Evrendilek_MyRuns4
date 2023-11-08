package com.example.deniz_evrendilek_myruns4.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDao {
    @Query("SELECT * from exercise_entry_table")
    fun getAll(): Flow<List<ExerciseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exerciseEntry: ExerciseEntry)

    @Delete
    suspend fun delete(exerciseEntry: ExerciseEntry)

    @Query("DELETE FROM exercise_entry_table")
    suspend fun deleteAll()

    @Query("DELETE FROM exercise_entry_table WHERE id= :id")
    suspend fun delete(id: Long)

}
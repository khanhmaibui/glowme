package com.example.khanh_bui.database

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDatabaseDao {

    @Insert
    suspend fun insertEntry(entry: ExerciseEntry)

    @Query("SELECT * FROM entry_table")
    fun getAllEntries(): Flow<List<ExerciseEntry>>

    @Query("DELETE FROM entry_table")
    suspend fun deleteAll()

    @Query("DELETE FROM entry_table WHERE id = :key")
    suspend fun deleteEntry(key: Long)

    @Query("SELECT COUNT(*) FROM entry_table")
    suspend fun getSize(): Int
}

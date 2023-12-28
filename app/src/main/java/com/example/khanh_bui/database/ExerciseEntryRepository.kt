package com.example.khanh_bui.database

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ExerciseEntryRepository(private val exerciseEntryDatabaseDao: ExerciseEntryDatabaseDao) {

    val allEntries: Flow<List<ExerciseEntry>> = exerciseEntryDatabaseDao.getAllEntries()

    fun insert(entry: ExerciseEntry){
        CoroutineScope(IO).launch{
            exerciseEntryDatabaseDao.insertEntry(entry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteEntry(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteAll()
        }
    }

    fun getSize() = runBlocking {
        val result = async {
            exerciseEntryDatabaseDao.getSize()
        }
        result.start()
        result.await()
    }
}
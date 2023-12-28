package com.example.khanh_bui.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class ExerciseEntryViewModel(private val repository: ExerciseEntryRepository) : ViewModel() {
    val allEntriesLiveData: LiveData<List<ExerciseEntry>> = repository.allEntries.asLiveData()

    fun insert(entry: ExerciseEntry) {
        repository.insert(entry)
    }

    fun delete(id: Long) {
        val entryList = allEntriesLiveData.value
        if (!entryList.isNullOrEmpty()){
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val entryList = allEntriesLiveData.value
        if (!entryList.isNullOrEmpty()) {
            repository.deleteAll()
        }
    }

    fun getSize(): Int {
        return repository.getSize()
    }

}

class ExerciseEntryViewModelFactory (private val repository: ExerciseEntryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(ExerciseEntryViewModel::class.java))
            return ExerciseEntryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
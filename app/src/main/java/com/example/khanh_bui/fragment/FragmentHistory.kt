package com.example.khanh_bui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.khanh_bui.R
import com.example.khanh_bui.database.ExerciseEntry
import com.example.khanh_bui.database.ExerciseEntryDatabase
import com.example.khanh_bui.database.ExerciseEntryDatabaseDao
import com.example.khanh_bui.database.ExerciseEntryRepository
import com.example.khanh_bui.database.ExerciseEntryViewModel
import com.example.khanh_bui.database.ExerciseEntryViewModelFactory
class FragmentHistory : Fragment() {

    private lateinit var historyListView: ListView

    private lateinit var arrayList: ArrayList<ExerciseEntry>
    private lateinit var arrayAdapter: HistoryListAdapter

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var viewModelFactory: ExerciseEntryViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        historyListView = view.findViewById(R.id.history_list_view)

        arrayList = ArrayList()
        arrayAdapter = HistoryListAdapter(requireActivity(), arrayList)
        historyListView.adapter = arrayAdapter

        database = ExerciseEntryDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        viewModelFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ExerciseEntryViewModel::class.java]

        exerciseEntryViewModel.allEntriesLiveData.observe(requireActivity()) {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        }

        return view
    }

    //update even when onResume
    override fun onResume() {
        super.onResume()
        arrayAdapter.notifyDataSetChanged()
    }
}
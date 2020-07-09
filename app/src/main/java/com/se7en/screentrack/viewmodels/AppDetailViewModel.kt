package com.se7en.screentrack.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.repository.AppDetailRepository

class AppDetailViewModel(
    db: AppDatabase
): ViewModel() {

    private val repository = AppDetailRepository.getInstance(db)

    fun getDayStats(packageName: String): LiveData<List<DayStats>> {
        return repository.getDayStats(packageName)
    }
}

package com.se7en.screentrack.viewmodels

import androidx.lifecycle.*
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.AppDatabase
import com.se7en.screentrack.models.UsageData
import com.se7en.screentrack.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    db: AppDatabase,
    appUsageManager: AppUsageManager
): ViewModel() {

    val filterLiveData = MutableLiveData<AppUsageManager.FILTER>()
    private val repository = HomeRepository.getInstance(db, appUsageManager)

    val usageStatsLiveData: LiveData<UsageData> =
        Transformations.switchMap(filterLiveData) { filter ->
            getUsageData(filter)
        }

    private fun getUsageData(filter: AppUsageManager.FILTER) =
        when(filter) {
            AppUsageManager.FILTER.TODAY -> repository.todayUsageData
            AppUsageManager.FILTER.THIS_WEEK -> repository.last7DaysUsageData
        }

    init {
        viewModelScope.launch { repository.fetchTodayUsageData() }
        viewModelScope.launch { repository.fetchWeekUsageData() }
        viewModelScope.launch { repository.updateData() }
    }
}

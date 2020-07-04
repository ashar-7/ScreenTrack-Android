package com.se7en.screentrack.viewmodels

import androidx.lifecycle.*
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.data.database.UsageDao
import com.se7en.screentrack.models.UsageData
import com.se7en.screentrack.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    usageDao: UsageDao,
    appUsageManager: AppUsageManager
): ViewModel() {

    val filterLiveData = MutableLiveData<AppUsageManager.FILTER>()
    private val repository = HomeRepository.getInstance(usageDao, appUsageManager)

    val usageStatsLiveData: LiveData<UsageData> =
        Transformations.switchMap(filterLiveData) { filter ->
            getUsageData(filter)
        }

    private fun getUsageData(filter: AppUsageManager.FILTER) =
        when(filter) {
            AppUsageManager.FILTER.TODAY -> repository.todayUsageData
            AppUsageManager.FILTER.LAST_7_DAYS -> repository.last7DaysUsageData
        }

    init {
        viewModelScope.launch {
            repository.fetchUsageData(AppUsageManager.FILTER.TODAY)
            repository.fetchUsageData(AppUsageManager.FILTER.LAST_7_DAYS)
        }
    }
}

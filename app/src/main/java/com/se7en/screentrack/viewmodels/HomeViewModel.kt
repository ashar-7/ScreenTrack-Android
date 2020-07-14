package com.se7en.screentrack.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.UsageData
import com.se7en.screentrack.repository.HomeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val repository: HomeRepository
): ViewModel() {

    private val todayUsageData =  MutableLiveData<UsageData>()
    private val last7DaysUsageData = MutableLiveData<UsageData>()

    fun getUsageLiveData(filter: AppUsageManager.FILTER) =
        when(filter) {
            AppUsageManager.FILTER.TODAY -> todayUsageData
            AppUsageManager.FILTER.THIS_WEEK -> last7DaysUsageData
        }

    init {
        viewModelScope.launch {
            repository.getTodayUsageData().collect {
                todayUsageData.value = it
            }
        }
        viewModelScope.launch {
            repository.getWeekUsageData().collect {
                last7DaysUsageData.value = it
            }
        }
        viewModelScope.launch { repository.updateData() }
    }
}

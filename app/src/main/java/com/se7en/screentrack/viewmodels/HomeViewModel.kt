package com.se7en.screentrack.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.UsageData
import com.se7en.screentrack.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

package com.se7en.screentrack.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.models.SessionMinimal
import com.se7en.screentrack.repository.AppDetailRepository
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime

class AppDetailViewModel @ViewModelInject constructor(
    private val repository: AppDetailRepository
): ViewModel() {

    val sessionsLiveData = MutableLiveData<List<SessionMinimal>>()

    fun getDayStats(packageName: String): LiveData<List<DayStats>> {
        return repository.getDayStats(packageName)
    }

    fun fetchSessions(packageName: String, date: ZonedDateTime) {
        viewModelScope.launch {
            sessionsLiveData.postValue(repository.getSessions(packageName, date))
        }
    }
}

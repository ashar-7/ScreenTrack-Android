package com.se7en.screentrack.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.screentrack.models.Session
import com.se7en.screentrack.repository.TimelineRepository
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime

class TimelineViewModel @ViewModelInject constructor(
    private val repository: TimelineRepository
): ViewModel() {

    private val sessions = MutableLiveData<List<Session>>()

    init {
        viewModelScope.launch {
            fetchSessions(ZonedDateTime.now())
        }
    }

    private suspend fun fetchSessions(date: ZonedDateTime) {
        sessions.value = repository.getSessions(date)
    }

    fun getSessions(): LiveData<List<Session>> = sessions
}

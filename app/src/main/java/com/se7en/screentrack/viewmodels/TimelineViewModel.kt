package com.se7en.screentrack.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.screentrack.models.Session
import com.se7en.screentrack.repository.TimelineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
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

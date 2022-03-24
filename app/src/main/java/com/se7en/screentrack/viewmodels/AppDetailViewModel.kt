package com.se7en.screentrack.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.screentrack.data.database.entity.DayStats
import com.se7en.screentrack.models.SessionMinimal
import com.se7en.screentrack.models.timelimit.AppTimeLimit
import com.se7en.screentrack.repository.AppDetailRepository
import com.se7en.screentrack.repository.TimeLimitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AppDetailViewModel @Inject constructor(
    private val appDetailRepository: AppDetailRepository,
    private val timeLimitRepository: TimeLimitRepository
) : ViewModel() {

    val sessionsLiveData = MutableLiveData<List<SessionMinimal>>()

    fun getDayStats(packageName: String): LiveData<List<DayStats>> {
        return appDetailRepository.getDayStats(packageName)
    }

    fun fetchSessions(packageName: String, date: ZonedDateTime) {
        viewModelScope.launch {
            sessionsLiveData.postValue(appDetailRepository.getSessions(packageName, date))
        }
    }

    fun getTimeLimit(packageName: String) = timeLimitRepository.getTimeLimit(packageName)

    fun setTimeLimit(packageName: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            timeLimitRepository.setTimeLimit(
                packageName = packageName,
                hour = hour,
                minute = minute
            )
        }
    }

    fun removeTimeLimit(packageName: String) {
        viewModelScope.launch {
            timeLimitRepository.deleteTimeLimit(
                packageName = packageName
            )
        }
    }
}

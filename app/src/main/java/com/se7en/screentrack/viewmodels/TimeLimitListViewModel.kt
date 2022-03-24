package com.se7en.screentrack.viewmodels

import androidx.lifecycle.ViewModel
import com.se7en.screentrack.repository.TimeLimitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeLimitListViewModel @Inject constructor(
    private val repository: TimeLimitRepository
) : ViewModel() {

    val timeLimitList = repository.getTimeLimitList()
}

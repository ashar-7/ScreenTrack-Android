package com.se7en.screentrack.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.se7en.screentrack.data.database.TimeLimitDao
import com.se7en.screentrack.data.database.entity.TimeLimit
import com.se7en.screentrack.models.timelimit.AppTimeLimit
import javax.inject.Inject

class TimeLimitRepository @Inject constructor(
    private val timeLimitDao: TimeLimitDao
) {
    fun getTimeLimitList(): LiveData<List<AppTimeLimit>> =
        timeLimitDao.getTimeLimitList().map { timeLimitList ->
            timeLimitList.map {
                AppTimeLimit(
                    packageName = it.packageName,
                    hour = it.hour,
                    minute = it.minute
                )
            }
        }

    fun getTimeLimit(packageName: String): LiveData<AppTimeLimit?> =
        timeLimitDao.getTimeLimit(packageName).map { timeLimit ->
            timeLimit?.let {
                AppTimeLimit(
                    packageName = it.packageName,
                    hour = it.hour,
                    minute = it.minute
                )
            }
        }

    suspend fun setTimeLimit(packageName: String, hour: Int, minute: Int) {
        timeLimitDao.insert(
            TimeLimit(
                packageName = packageName,
                hour = hour,
                minute = minute
            )
        )
    }

    suspend fun deleteTimeLimit(packageName: String) {
        timeLimitDao.delete(packageName)
    }

    fun getTimeLimitBlocking(packageName: String): AppTimeLimit? =
        timeLimitDao.getTimeLimitBlocking(packageName)?.let {
            AppTimeLimit(
                packageName = it.packageName,
                hour = it.hour,
                minute = it.minute
            )
        }

}

package com.se7en.screentrack.data.database

import androidx.room.*
import com.se7en.screentrack.data.AppUsageManager
import com.se7en.screentrack.models.UsageData

@Dao
interface UsageDao {
    @Query("SELECT * FROM UsageData WHERE filter IS :filter")
    suspend fun getUsage(filter: AppUsageManager.FILTER): UsageData?

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insert(usageData: UsageData)

    @Update
    suspend fun update(usageData: UsageData)

    @Delete
    suspend fun delete(usageData: UsageData)
}

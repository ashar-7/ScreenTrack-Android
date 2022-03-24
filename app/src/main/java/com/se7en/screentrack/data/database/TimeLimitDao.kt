package com.se7en.screentrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.se7en.screentrack.data.database.entity.TimeLimit

@Dao
abstract class TimeLimitDao {

    @Query("SELECT * FROM timelimit")
    abstract fun getTimeLimitList(): LiveData<List<TimeLimit>>

    @Query("SELECT * FROM timelimit WHERE packageName = :packageName")
    abstract fun getTimeLimit(packageName: String): LiveData<TimeLimit?>

    @Query("SELECT * FROM timelimit WHERE packageName = :packageName")
    abstract fun getTimeLimitBlocking(packageName: String): TimeLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(timeLimit: TimeLimit)

    @Query("DELETE FROM timelimit WHERE packageName = :packageName")
    abstract suspend fun delete(packageName: String)
}

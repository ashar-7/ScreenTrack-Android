package com.se7en.screentrack.data.database

import android.util.Log
import androidx.room.*
import com.se7en.screentrack.models.DayWithDayStats
import com.se7en.screentrack.models.DayStats
import com.se7en.screentrack.models.Day
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.ZonedDateTime

@Dao
abstract class StatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(day: Day): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(dayStats: DayStats): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(day: Day)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(dayStats: DayStats)

    @Transaction
    open suspend fun upsert(dayWithDayStats: List<DayWithDayStats>) {
        dayWithDayStats.forEach {
            if(insert(it.day) == (-1).toLong()) {
                update(it.day)
            }

            it.dayStats.forEach { stats ->
                if(insert(stats) == (-1).toLong()) {
                    update(stats)
                }
            }
        }
    }

    @Delete
    abstract suspend fun delete(day: Day)

    @Delete
    abstract suspend fun delete(vararg dayStats: DayStats)

    @Transaction
    @Delete
    open suspend fun delete(daysWithDayStats: List<DayWithDayStats>) {
        daysWithDayStats.forEach {
            delete(it.day)
        }
    }

    @Transaction
    @Query("SELECT * FROM Day")
    abstract fun getDaysWithDayStats(): Flow<List<DayWithDayStats>?>

    @Transaction
    @Query("SELECT * FROM Day WHERE date IS NOT :exceptDate")
    abstract suspend fun getDaysWithDayStats(exceptDate: ZonedDateTime): List<DayWithDayStats>

    @Transaction
    @Query("SELECT * FROM Day WHERE date IS :date")
    abstract fun getDayWithDayStats(date: ZonedDateTime): Flow<DayWithDayStats?>

    @Query("SELECT * FROM DayStats WHERE packageName IS :packageName")
    abstract fun getDayStats(packageName: String): Flow<List<DayStats>>
}

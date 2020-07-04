package com.se7en.screentrack.data.database

import androidx.room.*
import com.se7en.screentrack.models.App
import com.se7en.screentrack.models.AppsWithDayStatsAndSessions
import com.se7en.screentrack.models.Session

@Dao
interface StatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg apps: App)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(vararg dayStats: DayStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg sessions: Session)

    @Transaction
    suspend fun insert(app: App, sessions: List<Session>) { //dayStats: List<DayStats>,
        insert(app)

//        for(stats in dayStats) {
//            insert(stats)
//        }

        for(session in sessions) {
            insert(session)
        }
    }

//    @Transaction
//    @Query("SELECT * FROM App")
//    suspend fun getAppsWithDayStatsAndSessions(): List<AppsWithDayStatsAndSessions>
}

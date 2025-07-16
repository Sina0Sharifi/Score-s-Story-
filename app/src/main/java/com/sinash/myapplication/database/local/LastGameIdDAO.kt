package com.sinash.myapplication.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinash.myapplication.database.local.DBHandler.Companion.LAST_GAME_ID_TABLE
import com.sinash.myapplication.database.model.LastGameIdEntity

@Dao
interface LastGameIdDAO {
    @Insert
    fun insertLastGameID(lastGame: LastGameIdEntity)

    @get:Query("SELECT *FROM $LAST_GAME_ID_TABLE WHERE id=1")
    val getLastGameId: LastGameIdEntity?

    @Query("SELECT (SELECT COUNT(*) FROM lastGameId) == 0")
    fun isEmpty(): Boolean

    @Update
        fun updateLastGame(lastGame: LastGameIdEntity)

}
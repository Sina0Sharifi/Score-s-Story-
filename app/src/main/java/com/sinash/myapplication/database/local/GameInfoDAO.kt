package com.sinash.myapplication.database.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinash.myapplication.database.local.DBHandler.Companion.GAME_INFO_TABLE
import com.sinash.myapplication.database.model.GameInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameInfoDAO {
    @Insert
    fun insertGameInfo(gameInfo: GameInfoEntity)

    @get:Query("SELECT *FROM $GAME_INFO_TABLE")
    val getGameInfo: Flow<List<GameInfoEntity>>

    @Query("SELECT  * FROM $GAME_INFO_TABLE WHERE id = :lastId ")
    fun getGameInfoFromId(lastId:Int):GameInfoEntity

    @get:Query("SELECT COUNT(id) FROM $GAME_INFO_TABLE")
    val  getGameCount:Int

    @Update
    fun gameInfoUpdate(gameInfo: GameInfoEntity)

    @get:Query("SELECT * FROM $GAME_INFO_TABLE WHERE id = (SELECT MAX(id) FROM $GAME_INFO_TABLE) ")
    val  getLastItemId: GameInfoEntity

    @Delete
    fun deleteGame (gameInfo: GameInfoEntity)

}
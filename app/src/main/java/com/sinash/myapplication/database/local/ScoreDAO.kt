package com.sinash.myapplication.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinash.myapplication.database.local.DBHandler.Companion.SCORE_TABLE
import com.sinash.myapplication.database.model.GameInfoEntity
import com.sinash.myapplication.database.model.ScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDAO {

    @Insert
    fun insertScore(score:ScoreEntity)

    @get:Query("SELECT * FROM ${SCORE_TABLE}")
    val  getScores:Flow<List<ScoreEntity>>

    @Query("SELECT * FROM ${SCORE_TABLE} WHERE :id = gameID")
    fun  getScoresFromId(id:Int): Flow<List<ScoreEntity>>

    @Query("DELETE FROM ${SCORE_TABLE} WHERE gameID=:id ")
    fun deleteAllScoreFromGame(id:Int)

    @Query("DELETE FROM ${SCORE_TABLE} WHERE id=:id ")
    fun deleteAScoreFromGame(id:Int)
    @Update
    fun scoreInfoUpdate(score: ScoreEntity)



}
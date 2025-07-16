package com.sinash.ScoresStory.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sinash.ScoresStory.database.local.DBHandler.Companion.SCORE_TABLE


@Entity(tableName = SCORE_TABLE)
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id:Int =0,
    @ColumnInfo var scoreTeam1:Int = 0,
    @ColumnInfo var scoreTeam2:Int = 0,
    @ColumnInfo val gameID:Int =0

)

package com.sinash.ScoresStory.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sinash.ScoresStory.database.local.DBHandler.Companion.GAME_INFO_TABLE

@Entity(tableName = GAME_INFO_TABLE)
data class GameInfoEntity(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    @ColumnInfo var isFinished:Boolean=false ,
    @ColumnInfo var team1Name:String = "we",
    @ColumnInfo var team2Name:String = "them",
    @ColumnInfo var maxScore:Int = 0,
    @ColumnInfo var team1WenCount:Int = 0,
    @ColumnInfo var team2WenCount:Int = 0,
    @ColumnInfo var team1TotalScore:Int = 0,
    @ColumnInfo var team2TotalScore:Int = 0

)

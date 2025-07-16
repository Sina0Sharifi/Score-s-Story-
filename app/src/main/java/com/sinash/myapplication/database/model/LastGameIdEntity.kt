package com.sinash.myapplication.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sinash.myapplication.database.local.DBHandler.Companion.LAST_GAME_ID_TABLE

@Entity(tableName = LAST_GAME_ID_TABLE)
data class LastGameIdEntity(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    @ColumnInfo var lastGameId:Int = -1,
    @ColumnInfo var gameState:Boolean = false
)
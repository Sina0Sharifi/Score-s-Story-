package com.sinash.ScoresStory.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sinash.ScoresStory.database.local.DBHandler.Companion.DATABASE_VERSION
import com.sinash.ScoresStory.database.model.GameInfoEntity
import com.sinash.ScoresStory.database.model.LastGameIdEntity
import com.sinash.ScoresStory.database.model.ScoreEntity

@Database(
    entities = [ScoreEntity::class , GameInfoEntity::class ,LastGameIdEntity::class],
    version = DATABASE_VERSION
)
abstract class DBHandler:RoomDatabase() {
    companion object{
        const val SCORE_TABLE ="scoreTable"
        const val GAME_INFO_TABLE ="gameInfoTable"
        const val LAST_GAME_ID_TABLE="lastGameId"
        const val DATABASE_NAME ="mainDatabase"
        const val DATABASE_VERSION =3
        private var INSTANCE:DBHandler?=null

        fun getDatabase(context: Context):DBHandler{
            if (INSTANCE ==null)
                INSTANCE= Room.databaseBuilder(
                     context,
                     DBHandler::class.java,
                     DATABASE_NAME
                 )
                     .fallbackToDestructiveMigration()
                     .build()

            return INSTANCE!!
        }

    }

     abstract fun scoreDao():ScoreDAO
     abstract fun gameInfoDao():GameInfoDAO
     abstract fun lasGameDao():LastGameIdDAO
}
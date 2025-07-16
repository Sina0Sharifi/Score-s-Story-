package com.sinash.myapplication.adapter

import com.sinash.myapplication.database.model.ScoreEntity

interface ScoreItemFunction {
    fun editScoreClicked(score:ScoreEntity)
    fun deleteScoreClicked(score:ScoreEntity)
}
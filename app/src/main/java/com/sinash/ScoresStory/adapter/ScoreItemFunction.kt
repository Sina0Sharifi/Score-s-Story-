package com.sinash.ScoresStory.adapter

import com.sinash.ScoresStory.database.model.ScoreEntity

interface ScoreItemFunction {
    fun editScoreClicked(score:ScoreEntity)
    fun deleteScoreClicked(score:ScoreEntity)
}
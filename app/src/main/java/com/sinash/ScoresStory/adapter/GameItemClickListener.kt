package com.sinash.ScoresStory.adapter

interface GameItemClickListener {
    fun onGameItemClicked(gameId:Int)
    fun deleteGameClicked(gameId: Int)
    fun editGameClicked(gameId: Int)
}
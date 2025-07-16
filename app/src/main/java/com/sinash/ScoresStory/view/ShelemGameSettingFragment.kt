package com.sinash.ScoresStory.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.scores.story.R
import com.scores.story.databinding.FragmentShelemGameSettingBinding
import com.sinash.ScoresStory.database.local.DBHandler
import com.sinash.ScoresStory.database.model.GameInfoEntity
import com.sinash.ScoresStory.database.model.LastGameIdEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShelemGameSettingFragment: Fragment(R.layout.fragment_shelem_game_setting) {
    private lateinit var binding: FragmentShelemGameSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        binding = FragmentShelemGameSettingBinding.inflate(inflater)
        val db = DBHandler.getDatabase(context)
        lifecycleScope.launch {
            val lastGame: LastGameIdEntity
            var bool=false
            withContext(Dispatchers.IO) {
                if (db.gameInfoDao().getGameInfo!=null) {
                    lastGame = db.lasGameDao().getLastGameId!!
                    bool=lastGame.gameState && lastGame.lastGameId!=-1 && !db.gameInfoDao().getGameInfoFromId(lastGame.lastGameId).isFinished

                    if (bool){
                        val game = db.gameInfoDao().getGameInfoFromId(lastGame.lastGameId)
                        withContext(Dispatchers.Main) {
                            binding.EditTextTeam1Name.setText(game.team1Name)
                            binding.EditTextTeam2Name.setText(game.team2Name)
                            binding.EditTextMaxScore.setText(game.maxScore.toString())
                        }
                    }
                }
            }
        }

        binding.btnSetSetting.setOnClickListener {
            var team1Name =binding.EditTextTeam1Name.text.toString()
            var team2Name =binding.EditTextTeam2Name.text.toString()
            val maxScore = binding.EditTextMaxScore.text.toString()
            if(team1Name=="")
                team1Name="Team 1"
            if(team2Name=="")
                team2Name="Team 2"
            if (maxScore=="")
                Toast.makeText(context,"No field can be empty ! ",Toast.LENGTH_LONG).show()
            else{
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (db.lasGameDao().isEmpty()) {
                            db.lasGameDao().insertLastGameID(LastGameIdEntity())
                        }
                        val lastGame = db.lasGameDao().getLastGameId
                        if (!lastGame!!.gameState) {
                            db.gameInfoDao().insertGameInfo(
                                GameInfoEntity(
                                    0,
                                    false,
                                    team1Name,
                                    team2Name,
                                    maxScore.toInt(),
                                    0,
                                    0,
                                    0,
                                    0
                                )
                            )
                            db.lasGameDao().updateLastGame(
                                LastGameIdEntity(
                                    1,
                                    db.gameInfoDao().getLastItemId.id,
                                    true
                                )
                            )
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, ScoreListFragment())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            val lastGameInfo = db.gameInfoDao().getGameInfoFromId(lastGame.lastGameId)
                            db.gameInfoDao().gameInfoUpdate(
                                GameInfoEntity(
                                    lastGame.lastGameId,
                                    false,
                                    team1Name,
                                    team2Name,
                                    maxScore.toInt(),
                                    lastGameInfo.team1WenCount,
                                    lastGameInfo.team2WenCount,
                                    lastGameInfo.team1TotalScore,
                                    lastGameInfo.team2TotalScore
                                )
                            )
                            parentFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, ScoreListFragment())
                                .commit()
                        }

                    }
                }
            }
        }



        return binding.root
    }
}
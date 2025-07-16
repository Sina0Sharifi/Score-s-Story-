package com.sinash.myapplication.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sinash.myapplication.R
import com.sinash.myapplication.adapter.AdapterScore
import com.sinash.myapplication.adapter.ScoreItemFunction
import com.sinash.myapplication.database.local.DBHandler
import com.sinash.myapplication.database.model.GameInfoEntity
import com.sinash.myapplication.database.model.LastGameIdEntity
import com.sinash.myapplication.database.model.ScoreEntity
import com.sinash.myapplication.databinding.BottomSheetDialogBinding
import com.sinash.myapplication.databinding.FragmentMaimPageBinding
import com.sinash.myapplication.databinding.FragmentScoreListBinding
import com.sinash.myapplication.databinding.PointCardViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreListFragment : Fragment(R.layout.fragment_score_list), ScoreItemFunction {
    private lateinit var binding: FragmentScoreListBinding
    private lateinit var bindingPoint: PointCardViewBinding
    private lateinit var recAdapter: AdapterScore
    private lateinit var db: DBHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentScoreListBinding.inflate(inflater)
        bindingPoint = PointCardViewBinding.inflate(inflater)
        val data = mutableListOf<ScoreEntity>()

        //TODO (that has error )

        binding.recyclerViewScore.layoutManager = GridLayoutManager(
            requireContext(),
            1
        )
        recAdapter = AdapterScore(this, requireContext(), data)
        binding.recyclerViewScore.adapter = recAdapter

        binding.addScoreBtn.setOnClickListener {
            dialogAddScore()
            visibilityG()

        }
        binding.editScoreBtn.setOnClickListener {
            recAdapter.toggleButtonsVisibility()
            visibilityG()
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db = DBHandler.getDatabase(requireContext())
                val lastId = db.lasGameDao().getLastGameId!!
                val gameInfo = db.gameInfoDao().getGameInfoFromId(lastId.lastGameId)
                val scores = db.scoreDao().getScoresFromId(lastId.lastGameId)
                withContext(Dispatchers.Main) {

                    if (gameInfo.maxScore <= gameInfo.team1TotalScore || gameInfo.maxScore <= gameInfo.team2TotalScore)
                        gameFinished(gameInfo)
                }

                db.lasGameDao()
                    .updateLastGame(LastGameIdEntity(1, lastId.lastGameId, true))
                withContext(Dispatchers.Main) {
                    binding.team2NameScoreList.text = gameInfo.team2Name
                    binding.team1NameScoreList.text = gameInfo.team1Name

                    showScore(gameInfo)
                    scores.collect { scoresList ->
                        data.clear()
                        scoresList.forEach {
                            data.add(it)
                        }

                        recAdapter.notifyDataSetChanged()
                    }
                    if (gameInfo.isFinished) {
                        Toast.makeText(requireContext(), gameInfo.toString(), Toast.LENGTH_LONG)
                            .show()
                        binding.addScoreBtn.visibility = View.GONE
                        binding.floatingActionButton2.visibility = View.GONE
                        binding.btnContinue.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.btnContinue.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val game: GameInfoEntity = db.gameInfoDao()
                        .getGameInfoFromId(db.lasGameDao().getLastGameId!!.lastGameId)
                    game.isFinished = false
                    db.gameInfoDao().gameInfoUpdate(game)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShelemGameSettingFragment())
                .commit()
        }


        binding.gameSettingsBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShelemGameSettingFragment())
                .addToBackStack(null)
                .commit()
            visibilityG()
        }
        binding.floatingActionButton2.setOnClickListener {
            if (binding.editScoreBtn.visibility == View.GONE)
                visibilityV()
            else
                visibilityG()

            //https://www.sitepoint.com/animating-android-floating-action-button/     animation for floating action bar

        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, MainPageFragment())
                        .commit()
                    isEnabled = false
                    // parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            })

        return binding.root
    }

    override fun editScoreClicked(score: ScoreEntity) {
        dialogChangeScore(score)
        visibilityG()
        recAdapter.toggleButtonsVisibility()
    }

    override fun deleteScoreClicked(score: ScoreEntity) {

        lifecycleScope.launch {
            val game: GameInfoEntity
            withContext(Dispatchers.IO) {
                db.scoreDao().deleteAScoreFromGame(score.id)
                game = db.gameInfoDao().getGameInfoFromId(score.gameID)

                game.team1TotalScore -= score.scoreTeam1
                game.team2TotalScore -= score.scoreTeam2
                if (score.scoreTeam1 > score.scoreTeam2)
                    game.team1WenCount--
                else if (score.scoreTeam1 < score.scoreTeam2)
                    game.team2WenCount--
                else {
                    game.team2WenCount--
                    game.team1WenCount--
                }

                db.gameInfoDao().gameInfoUpdate(game)

            }
            withContext(Dispatchers.Main) {
                showScore(game)
            }
        }
        recAdapter.toggleButtonsVisibility()
    }

    private fun showScore(gameInfo: GameInfoEntity) {
        binding.team1WensCont.text = gameInfo.team1WenCount.toString()
        binding.team2WensCont.text = gameInfo.team2WenCount.toString()
        binding.team2ScoreScoreList.text =
            gameInfo.team2TotalScore.toString()
        binding.team1ScoreScoreList.text =
            gameInfo.team1TotalScore.toString()
    }

    private fun dialogChangeScore(score: ScoreEntity) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.add_new_score_dialog, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialogView.findViewById<AppCompatEditText>(R.id.team1ScoreInput)
            .setText("${score.scoreTeam1}")
        dialogView.findViewById<AppCompatEditText>(R.id.team2ScoreInput)
            .setText("${score.scoreTeam2}")
        dialogView.findViewById<AppCompatButton>(R.id.btnAddScore).setOnClickListener {
            val score1 =
                dialogView.findViewById<AppCompatEditText>(R.id.team1ScoreInput).text.toString()
            val score2 =
                dialogView.findViewById<AppCompatEditText>(R.id.team2ScoreInput).text.toString()
            if (score2 != "" && score1 != "") {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val dbGameInfo = db.gameInfoDao()
                        val lastId = db.lasGameDao().getLastGameId!!
                        var thisGame = db.gameInfoDao().getGameInfoFromId(lastId.lastGameId)

                        if (score.scoreTeam1 > score.scoreTeam2)
                            thisGame.team1WenCount--
                        else if (score.scoreTeam1 < score.scoreTeam2)
                            thisGame.team2WenCount--
                        else {
                            thisGame.team2WenCount--
                            thisGame.team1WenCount--
                        }
                        thisGame.team1TotalScore += score1.toInt() - score.scoreTeam1
                        thisGame.team2TotalScore += score2.toInt() - score.scoreTeam2
                        db.scoreDao().scoreInfoUpdate(
                            ScoreEntity(
                                id = score.id,
                                scoreTeam1 = score1.toInt(),
                                scoreTeam2 = score2.toInt(),
                                gameID = lastId.lastGameId
                            )
                        )
                        if (score1.toInt() > score2.toInt())
                            thisGame.team1WenCount++
                        else if (score1.toInt() < score2.toInt())
                            thisGame.team2WenCount++
                        else {
                            thisGame.team2WenCount++
                            thisGame.team1WenCount++
                        }

                        dbGameInfo.gameInfoUpdate(thisGame)
                        thisGame = db.gameInfoDao().getGameInfoFromId(lastId.lastGameId)

                        if (thisGame.maxScore <= thisGame.team1TotalScore || thisGame.maxScore <= thisGame.team2TotalScore)
                            withContext(Dispatchers.Main) {
                                gameFinished(thisGame)
                            }
                        withContext(Dispatchers.Main) {
                            showScore(thisGame)
                        }

                    }
                }
                alertDialog.dismiss()
            } else
                Toast.makeText(context, "set valid score ! ", Toast.LENGTH_LONG).show()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun dialogAddScore() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.add_new_score_dialog, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialogView.findViewById<AppCompatButton>(R.id.btnAddScore).setOnClickListener {
            val score1 =
                dialogView.findViewById<AppCompatEditText>(R.id.team1ScoreInput).text.toString()
            val score2 =
                dialogView.findViewById<AppCompatEditText>(R.id.team2ScoreInput).text.toString()
            if (score2 != "" && score1 != "") {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val dbGameInfo = db.gameInfoDao()
                        val lastId = db.lasGameDao().getLastGameId!!
                        var thisGame = db.gameInfoDao().getGameInfoFromId(lastId.lastGameId)
                        db.scoreDao().insertScore(
                            ScoreEntity(
                                scoreTeam1 = score1.toInt(),
                                scoreTeam2 = score2.toInt(),
                                gameID = lastId.lastGameId
                            )
                        )
                        thisGame.team1TotalScore += score1.toInt()
                        thisGame.team2TotalScore += score2.toInt()
                        if (score1.toInt() > score2.toInt())
                            thisGame.team1WenCount++
                        else if (score1.toInt() < score2.toInt())
                            thisGame.team2WenCount++
                        else {
                            thisGame.team2WenCount++
                            thisGame.team1WenCount++
                        }

                        dbGameInfo.gameInfoUpdate(thisGame)
                        thisGame = db.gameInfoDao().getGameInfoFromId(lastId.lastGameId)

                        if (thisGame.maxScore <= thisGame.team1TotalScore || thisGame.maxScore <= thisGame.team2TotalScore) {
                            withContext(Dispatchers.Main) {
                                gameFinished(thisGame)
                            }
                        }


                        withContext(Dispatchers.Main) {
                            showScore(thisGame)
                        }
                    }
                }

                alertDialog.dismiss()
            } else
                Toast.makeText(context, "set valid score ! ", Toast.LENGTH_LONG).show()

        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun visibilityV() {
        binding.gameSettingsBtn.visibility = View.VISIBLE
        binding.editScoreBtn.visibility = View.VISIBLE
    }

    private fun visibilityG() {
        binding.gameSettingsBtn.visibility = View.GONE
        binding.editScoreBtn.visibility = View.GONE
    }

    private fun gameFinished(gameInfo: GameInfoEntity) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setCancelable(false)
        val view = BottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.show()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val game: GameInfoEntity = gameInfo
                game.isFinished = true
                db.gameInfoDao().gameInfoUpdate(game)
                withContext(Dispatchers.Main) {
                    if (gameInfo.team1TotalScore >= gameInfo.maxScore && gameInfo.team1TotalScore>gameInfo.team2TotalScore) {
                        view.textScoreSummary.text = "score : ${game.team1TotalScore}"
                        view.textWinner.text = "${game.team1Name} WIN !"
                    } else if (gameInfo.team2TotalScore >= gameInfo.maxScore && gameInfo.team2TotalScore>gameInfo.team1TotalScore) {
                        view.textScoreSummary.text = "score : ${game.team2TotalScore}"
                        view.textWinner.text = "${game.team2Name} WIN !"
                    }else{
                        view.textVictory.visibility =View.GONE
                        view.textWinner.text = " EQUAL!"
                        view.textScoreSummary.text = "score ${game.team1Name} : ${game.team1TotalScore}  \n score ${game.team2Name} : ${game.team2TotalScore}"
                    }

                }
            }
        }

        view.btnNewGame.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    db.lasGameDao().updateLastGame(LastGameIdEntity(id = 1, lastGameId = -3 , false))
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShelemGameSettingFragment())
                .commit()
            dialog.dismiss()
        }
        view.btnSeeScores.setOnClickListener {
            binding.addScoreBtn.visibility = View.GONE
            binding.floatingActionButton2.visibility = View.GONE
            binding.btnContinue.visibility = View.VISIBLE
            dialog.dismiss()
        }
        view.btnHome.setOnClickListener {
/*            lifecycleScope.launch {
                val db=DBHandler.getDatabase(requireContext())
                withContext(Dispatchers.IO){
                db.lasGameDao().updateLastGame(LastGameIdEntity(id = 1, lastGameId = -3 , false))
                }
            }*/
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainPageFragment())
                .commit()
            dialog.dismiss()
        }
    }
}


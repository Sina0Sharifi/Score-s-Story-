package com.sinash.ScoresStory.view
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.scores.story.R
import com.scores.story.databinding.FragmentAllGameListBinding
import com.sinash.ScoresStory.adapter.AdapterAllGames
import com.sinash.ScoresStory.adapter.GameItemClickListener
import com.sinash.ScoresStory.database.local.DBHandler
import com.sinash.ScoresStory.database.model.GameInfoEntity
import com.sinash.ScoresStory.database.model.LastGameIdEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllGameListFragment:Fragment(R.layout.fragment_all_game_list),GameItemClickListener {
    private lateinit var binding: FragmentAllGameListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAllGameListBinding.inflate(inflater)
        val data= mutableListOf<GameInfoEntity>()
        val db = DBHandler.getDatabase(requireContext())
        binding.recyclerViewAllGame.layoutManager = LinearLayoutManager(requireContext())
        val recAdapter = AdapterAllGames(this,requireContext(), data)
        binding.recyclerViewAllGame.adapter = recAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val games = db.gameInfoDao().getGameInfo
                withContext(Dispatchers.Main) {
                val oldSize=data.size
                    //Toast.makeText(context,newSize.toString(), Toast.LENGTH_LONG).show()
                var newSize=0
                    games.collect { scoresList ->
                        data.clear()
                        scoresList.forEach {
                            data.add(it)
                        }
                       // Toast.makeText(context,data.toString(), Toast.LENGTH_LONG).show()
                        newSize=data.size
                        if (newSize>oldSize)
                            recAdapter.notifyDataSetChanged()
                        else
                            recAdapter.notifyDataSetChanged()
                    }
                }
            }

        }


        return binding.root
    }

    override fun onGameItemClicked(gameId: Int) {
        lifecycleScope.launch {
            val db = DBHandler.getDatabase(requireContext())
            withContext(Dispatchers.IO){
                db.lasGameDao().updateLastGame(LastGameIdEntity(1,gameId,false))
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,ScoreListFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun deleteGameClicked(gameId: Int) {
        lifecycleScope.launch {
            val db = DBHandler.getDatabase(requireContext())
            withContext(Dispatchers.IO){
                if(gameId==db.lasGameDao().getLastGameId?.lastGameId){
                    db.lasGameDao().updateLastGame(LastGameIdEntity(1,-2,false))
                }
                db.scoreDao().deleteAllScoreFromGame(gameId)
                db.gameInfoDao().deleteGame(db.gameInfoDao().getGameInfoFromId(gameId))
            }
        }
    }

    override fun editGameClicked(gameId: Int) {
        lifecycleScope.launch {
            val db = DBHandler.getDatabase(requireContext())
            withContext(Dispatchers.IO){
                    db.lasGameDao().updateLastGame(LastGameIdEntity(1,gameId,true))
            }
        }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,ShelemGameSettingFragment())
                .commit()
    }

}
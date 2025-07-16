package com.sinash.ScoresStory.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.scores.story.R
import com.scores.story.databinding.FragmentMaimPageBinding
import com.sinash.ScoresStory.database.local.DBHandler
import com.sinash.ScoresStory.database.model.LastGameIdEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPageFragment:Fragment(R.layout.fragment_maim_page){
        private lateinit var binding: FragmentMaimPageBinding
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            binding = FragmentMaimPageBinding.inflate(inflater)

            binding.shelemBtn.setOnClickListener {
                dialogNewOrLastGame(requireContext())
            }
            binding.btnAllGamesList.setOnClickListener {
                goToFragment(AllGameListFragment())
            }

            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        isEnabled = false
                         parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                })

            return binding.root
        }
    private fun dialogNewOrLastGame(context: Context){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_last, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialogView.findViewById<AppCompatButton>(R.id.new_game_btn).setOnClickListener{

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val db = DBHandler.getDatabase(requireContext())
                    if (db.lasGameDao().isEmpty())
                        db.lasGameDao().insertLastGameID(LastGameIdEntity())

                    val lastGameId=db.lasGameDao().getLastGameId!!.lastGameId
                    db.lasGameDao().updateLastGame(LastGameIdEntity(1,lastGameId,false))
                }
            }

            goToFragment(ShelemGameSettingFragment())
            alertDialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.last_game_btn).setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val db=DBHandler.getDatabase(context)
                    val lastGame = db.lasGameDao().getLastGameId
                    var bool=true
                    if (!db.lasGameDao().isEmpty()) {
                        if (lastGame != null) {
                            bool =  lastGame.lastGameId == -1||db.gameInfoDao().getGameCount==0
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (bool) {
                            Toast.makeText(
                                requireContext(),
                                "you don't have game",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else if (lastGame?.lastGameId==-2){
                            Toast.makeText(
                                requireContext(),
                                "last game deleted !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else if (lastGame?.lastGameId==-3){
                            Toast.makeText(
                                requireContext(),
                                "last game finished!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        else {

                            goToFragment(ScoreListFragment())
                            alertDialog.dismiss()
                        }
                    }
                }
            }

        }
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_new_last);
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

    }
    private fun goToFragment(fragment:Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)
            .commit()
    }
}
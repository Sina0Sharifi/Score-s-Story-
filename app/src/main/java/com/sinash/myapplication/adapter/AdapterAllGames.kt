package com.sinash.myapplication.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.sinash.myapplication.R
import com.sinash.myapplication.database.model.GameInfoEntity
import com.sinash.myapplication.databinding.ItemGameCardViewBinding

class AdapterAllGames(
    private val listener: GameItemClickListener,
        private val context: Context
        ,private val gamesList: MutableList<GameInfoEntity>
    ): RecyclerView.Adapter<AdapterAllGames.AllGameViewHolder>() {
    class AllGameViewHolder(val binding: ItemGameCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setDataProduct(game: GameInfoEntity, position: Int, context: Context) {
            binding.team1Score.text=game.team1TotalScore.toString()
            binding.team2Score.text=game.team2TotalScore.toString()
            binding.team1NameGameList.text=game.team1Name
            binding.team2NameGameList.text=game.team2Name
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllGameViewHolder {
            val binding =
                ItemGameCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return AllGameViewHolder(binding)
        }

        override fun getItemCount(): Int = gamesList.size

        override fun onBindViewHolder(holder: AllGameViewHolder, position: Int) {
            holder.setDataProduct(gamesList[position], position, context)
            holder.binding.root.setOnClickListener {
                listener.onGameItemClicked(gamesList[position].id)
            }
            holder.binding.btnEdit.setOnClickListener {
                listener.editGameClicked(gamesList[position].id)
            }
            holder.binding.btnTrash.setOnClickListener {
                dialogDeleteGame(position)
            }

        }
    private fun dialogDeleteGame(position: Int){

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_game_from_list, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
        dialogView.findViewById<AppCompatButton>(R.id.delete_game).setOnClickListener{
            listener.deleteGameClicked(gamesList[position].id)
            alertDialog.dismiss()
            }
        dialogView.findViewById<AppCompatButton>(R.id.cancel_delete).setOnClickListener{
                alertDialog.dismiss()
            }


        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }
}
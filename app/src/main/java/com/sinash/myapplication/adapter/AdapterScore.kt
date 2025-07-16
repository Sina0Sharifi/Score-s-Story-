package com.sinash.myapplication.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sinash.myapplication.R
import com.sinash.myapplication.database.model.ScoreEntity
import com.sinash.myapplication.databinding.PointCardViewBinding

class AdapterScore(
    private val listener: ScoreItemFunction,
         private val context:Context
        ,private val products: MutableList<ScoreEntity>
    ): RecyclerView.Adapter<AdapterScore.ProductViewHolder>(){
            var showBtn =false
        inner class ProductViewHolder( val binding : PointCardViewBinding):RecyclerView.ViewHolder(binding.root)
        {

            fun setDataProduct(product: ScoreEntity,position:Int ,context: Context){
                binding.team1Score.text=product.scoreTeam1.toString()
                binding.team2Score.text= product.scoreTeam2.toString()
                binding.numOfItem.text = (position+1).toString()
                    val drawable1 = ContextCompat.getDrawable(context,R.drawable.ic_crowned_heart)
                binding.team1Score.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                binding.team2Score.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                if (product.scoreTeam1>product.scoreTeam2){
                    binding.team1Score.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
                }
                else if (product.scoreTeam1<product.scoreTeam2)
                {
                    binding.team2Score.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
                }
                else{
                    binding.team1Score.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
                    binding.team2Score.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
                }

            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val binding = PointCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)

            return ProductViewHolder(binding)
        }

/*        private fun clearFocusOnTouchOutside(view: View) {
            if (view !is EditText) {
                view.setOnTouchListener { v, event ->
                    v.performClick()
                    hideKeyboard(v)
                    false
                }
            }
        }
        private fun hideKeyboard(view: View) {
            val imm = context.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }*/

/*        fun addItem(){
            notifyItemInserted((products.size -1))
        }*/

        override fun getItemCount(): Int = products.size

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            Log.e("RecyclerDebug", "Binding item at position: $position, score: ${products[position].scoreTeam1}")
            holder.setDataProduct(products[position],position,context)
            holder.binding.btnRemove.setOnClickListener {
                dialogDeleteScore(position)
            }
            holder.binding.btnEditScore.setOnClickListener {
                listener.editScoreClicked(products[position])
            }

            holder.binding.btnRemove.visibility = if (showBtn) View.VISIBLE else View.GONE
            holder.binding.btnEditScore.visibility = if (showBtn) View.VISIBLE else View.GONE

        }
    fun toggleButtonsVisibility(){
        showBtn =!showBtn
        notifyDataSetChanged()
    }
    private fun dialogDeleteScore(position: Int){

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_game_from_list, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
        dialogView.findViewById<AppCompatButton>(R.id.delete_game).setOnClickListener{
            listener.deleteScoreClicked(products[position])
            alertDialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.cancel_delete).setOnClickListener{
            alertDialog.dismiss()
        }


        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }

    }
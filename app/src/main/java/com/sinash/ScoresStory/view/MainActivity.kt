package com.sinash.ScoresStory.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.scores.story.R
import com.scores.story.databinding.ActivityMainBinding
import com.sinash.ScoresStory.database.local.DBHandler
import com.sinash.ScoresStory.database.model.LastGameIdEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,MainPageFragment())
            .commit()



      /*  binding.setNameBtn.setOnClickListener {

        var l :Int=0
        var r = 0
        for (x in 0..dataaa.size-1){
            if (x%2==0)
            l+=dataaa[x].score
            else
            r+=dataaa[x].score
        }
        binding.groupOneTotal.text ="Total = $l"
        binding.groupTwoTotal.text ="Total = $r"
        }
        binding.recyclerViewScore.layoutManager = GridLayoutManager(
            this,
            2
        )
        val recAdapter = AdapterScore(this , dataaa)
        binding.recyclerViewScore.adapter = recAdapter*/
    }
    override fun onDestroy() {
        val context  =this
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = DBHandler.getDatabase(context).lasGameDao()
                db.updateLastGame(LastGameIdEntity(id =1, gameState =  false))
            }
        }
        super.onDestroy()
    }
}
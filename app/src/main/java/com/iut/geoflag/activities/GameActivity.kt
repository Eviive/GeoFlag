package com.iut.geoflag.activities

import android.os.Bundle
import android.view.SurfaceControl.Transaction
import androidx.appcompat.app.AppCompatActivity
import com.iut.geoflag.R
import com.iut.geoflag.databinding.ActivityGameBinding
import com.iut.geoflag.fragments.QuestionFragment

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var transaction = supportFragmentManager.beginTransaction()

        transaction.replace(binding.questionFragment.id, QuestionFragment())

        transaction.commit()
    }
}
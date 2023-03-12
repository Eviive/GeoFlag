package com.iut.geoflag.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.iut.geoflag.databinding.ActivityGameBinding
import com.iut.geoflag.fragments.QuestionFragment
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Game

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        game = intent.getSerializableExtra("game") as Game

        updateQuestion()

        object : CountDownTimer(game.getTimer(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val text = "Time: ${millisUntilFinished / 1000 + 1}s"
                binding.timer.text = text
            }

            override fun onFinish() {
                val text = "Times up!"
                binding.timer.text = text
                gameOver()
            }
        }.start()
    }

    fun submitAnswer(response : Country) {

        if (game.isFinished())
            return

        if (game.submitAnswer(response)) {
            val text = "Score: ${game.getScore()}"
            binding.score.text = text
        }
        updateQuestion()
    }

    private fun gameOver() {
        game.finish()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("Your score is ${game.getScore()}")

        builder.setCancelable(false)

        builder.setPositiveButton("OK") {
            dialog, which ->

            val intent = Intent()
            intent.putExtra("score", game.getScore())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        builder.show()

    }

    private fun updateQuestion() {

        val question = game.getCurrentQuestion()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.questionFragment.id, QuestionFragment(question))
        transaction.commit()
    }

}
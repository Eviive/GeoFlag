package com.iut.geoflag.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iut.geoflag.databinding.ActivityGameBinding
import com.iut.geoflag.fragments.QuestionFragment
import com.iut.geoflag.models.Game
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var game: Game
    private lateinit var timer: CountDownTimer
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        game = intent.getSerializableExtra("game") as Game

        db = Firebase.database("https://geoflag-ceab3-default-rtdb.europe-west1.firebasedatabase.app/").reference

        updateQuestion()
        updateBestScore()
        binding.score.text = game.getScore().toString()

        timer = object : CountDownTimer(game.getTimer(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val text = "${millisUntilFinished / 1000 + 1}s"
                binding.timer.text = text
            }

            override fun onFinish() {
                val text = "up!"
                binding.timer.text = text
                gameOver()
            }
        }.start()
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    fun submitAnswer(correct: Boolean) {

        if (game.isFinished())
            return

        game.submitAnswer(correct)

        var cooldown : Long = 1200

        if (correct) {
            binding.score.text = game.getScore().toString()
            cooldown = 500
        }

        Executors.newSingleThreadScheduledExecutor().schedule({
            if (!game.isFinished()) {
                updateQuestion()
            }
        }, cooldown, TimeUnit.MILLISECONDS)
    }

    private fun gameOver() {
        game.finish()
        updateBestScore()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("Your score is ${game.getScore()}")

        builder.setCancelable(false)

        builder.setPositiveButton("OK") { _, _ ->
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

    private fun updateBestScore() {
        Firebase.auth.currentUser?.uid?.let { uid ->
            db.child("users").child(uid).child("bestScore").get().addOnSuccessListener {
                val bestScore = if (it.value != null) it.value as Long else 0

                if (game.getScore() > bestScore) {
                    it.ref.setValue(game.getScore())
                }
            }
        }
    }

}
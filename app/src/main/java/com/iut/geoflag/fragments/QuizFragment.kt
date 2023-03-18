package com.iut.geoflag.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.example.formapp.utils.StorageManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.DialogSettingsBinding
import com.iut.geoflag.databinding.FragmentQuizBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Difficulty
import com.iut.geoflag.models.Game
import com.iut.geoflag.models.Settings

class QuizFragment(private var countries: ArrayList<Country>, private var gameLuncher: ActivityResultLauncher<Intent>): Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var db: DatabaseReference
    private lateinit var settings: Settings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        db = Firebase.database("https://geoflag-ceab3-default-rtdb.europe-west1.firebasedatabase.app/").reference

        settings = StorageManager.load<Settings>(requireContext(), "settings")
            ?: Settings(
                countries,
                30_000,
                3,
                Difficulty.MEDIUM
            )

        updateSettings()

        binding.startButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            val game = Game(settings)
            intent.putExtra("game", game)
            gameLuncher.launch(intent)
        }

        binding.settings.setOnClickListener {
            showSettingsDialog()
        }

        updateBestScore()

        binding.playerName.text = Firebase.auth.currentUser?.displayName ?: "Player"

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateBestScore()
        updateSettings()
    }

    private fun updateBestScore() {
        var text = "Login to see your best score"

        if (Firebase.auth.currentUser == null) {
            binding.bestScore.text = text
            return
        }

        Firebase.auth.currentUser?.uid?.let { uid ->
            db.child("users").child(uid).child("bestScore").get().addOnSuccessListener {
                text = if (it.value != null) it.value.toString() else "0"
                binding.bestScore.text = text
            }
        }
    }

    private fun updateSettings() {
        binding.difficulty.text = settings.difficulty.toString()
        val time = "${settings.time / 1000}s"
        binding.time.text = time
        binding.choices.text = "${settings.possibilities}"
    }

    private fun showSettingsDialog() {

        val defaultColor = Color.GRAY
        val selectedColor = Color.parseColor("#FF018786")

        val builderBinding = DialogSettingsBinding.inflate(layoutInflater)

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(builderBinding.root)
        val dialog = builder.create()

        val activeDifficulty =  {  difficulty:Difficulty ->

            builderBinding.easyButton.background.setTint(defaultColor)
            builderBinding.mediumButton.background.setTint(defaultColor)
            builderBinding.hardButton.background.setTint(defaultColor)

            when (difficulty) {
                Difficulty.EASY -> {
                    builderBinding.easyButton.background.setTint(selectedColor)
                    builderBinding.allCheckBox.isChecked = false
                }
                Difficulty.MEDIUM -> {
                    builderBinding.mediumButton.background.setTint(selectedColor)
                    builderBinding.allCheckBox.isChecked = false
                }
                Difficulty.HARD -> {
                    builderBinding.hardButton.background.setTint(selectedColor)
                    builderBinding.allCheckBox.isChecked = false
                }
                Difficulty.ANY -> {
                    builderBinding.easyButton.background.setTint(selectedColor)
                    builderBinding.mediumButton.background.setTint(selectedColor)
                    builderBinding.hardButton.background.setTint(selectedColor)
                }
            }
        }
        val activePossibilities =  {  possibilities:Int ->

            builderBinding.possibility3Button.background.setTint(defaultColor)
            builderBinding.possibility5Button.background.setTint(defaultColor)

            when (possibilities) {
                3 -> builderBinding.possibility3Button.background.setTint(selectedColor)
                5 -> builderBinding.possibility5Button.background.setTint(selectedColor)
            }
        }

        var difficulty = settings.difficulty
        var time = settings.time
        var possibilities = settings.possibilities

        activeDifficulty(difficulty)
        if (difficulty == Difficulty.ANY)
            builderBinding.allCheckBox.isChecked = true

        activePossibilities(possibilities)
        builderBinding.rangeSlider.value = time.toFloat() / 1000
        val text = "(${time/1000}s)"
        builderBinding.timeTextView.text = text

        builderBinding.easyButton.setOnClickListener {
            difficulty = Difficulty.EASY
            activeDifficulty(difficulty)
        }

        builderBinding.mediumButton.setOnClickListener {
            difficulty = Difficulty.MEDIUM
            activeDifficulty(difficulty)
        }

        builderBinding.hardButton.setOnClickListener {
            difficulty = Difficulty.HARD
            activeDifficulty(difficulty)
        }

        builderBinding.possibility3Button.setOnClickListener {
            possibilities = 3
            activePossibilities(possibilities)
        }

        builderBinding.possibility5Button.setOnClickListener {
            possibilities = 5
            activePossibilities(possibilities)
        }

        builderBinding.allCheckBox.setOnClickListener{
            if (builderBinding.allCheckBox.isChecked) {
                difficulty = Difficulty.ANY
                activeDifficulty(difficulty)
            } else {
                difficulty = Difficulty.MEDIUM
                activeDifficulty(difficulty)
            }
        }

        builderBinding.rangeSlider.addOnChangeListener { _, value, _ ->
            time = value.toLong() * 1000
            val timerText = "(${value.toInt()}s)"
            builderBinding.timeTextView.text = timerText
        }

        builderBinding.saveButton.setOnClickListener {

            val newSettings = Settings(
                countries,
                time,
                possibilities,
                difficulty
            )

            StorageManager.save(requireContext(), "settings", newSettings)
            settings = newSettings

            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            updateSettings()
        }

        dialog.show()
    }
}
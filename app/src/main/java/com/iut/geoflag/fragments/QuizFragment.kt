package com.iut.geoflag.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.iut.geoflag.R
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.activities.LoginActivity
import com.iut.geoflag.databinding.DialogSettingsBinding
import com.iut.geoflag.databinding.FragmentQuizBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Difficulty
import com.iut.geoflag.models.Game
import com.iut.geoflag.models.Settings
import com.iut.geoflag.utils.StorageManager

class QuizFragment(private var countries: ArrayList<Country>): Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var settings: Settings

    private var signInClient: GoogleSignInClient? = null
    private var game: Game? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        db = Firebase.database("https://geoflag-ceab3-default-rtdb.europe-west1.firebasedatabase.app/").reference
        settings = StorageManager.load<Settings>(requireContext(), "settings")
            ?: Settings(
                countries,
                30_000,
                3,
                Difficulty.MEDIUM
            )

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        signInClient = GoogleSignIn.getClient(requireActivity(), gso)

        updateSettings()

        binding.startButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)

            if (game != null && !game!!.isFinished()) {
                return@setOnClickListener
            }
            game = Game(settings)

            intent.putExtra("game", game)
            startActivity(intent)
        }

        binding.settings.setOnClickListener { showSettingsDialog() }

        if (auth.currentUser == null) {
            binding.authButton.text = getString(R.string.login)
            binding.authButton.setOnClickListener { login() }
        } else {
            binding.authButton.text = getString(R.string.logout)
            binding.authButton.setOnClickListener { logout() }
        }

        updatePlayerUI()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        game?.finish()
        updatePlayerUI()
        updateSettings()
    }

    private fun updatePlayerUI() {
        var text = getString(R.string.login_to_save_score)

        if (Firebase.auth.currentUser == null) {
            binding.bestScore.text = text
            binding.playerName.text = getString(R.string.player)
            return
        }

        Firebase.auth.currentUser?.let {
            binding.playerName.text = it.displayName
            db.child("users").child(it.uid).child("bestScore").get().addOnSuccessListener { data ->
                text = if (data.value != null) data.value.toString() else "0"
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

    private fun login(): Boolean {
        auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            return false
        }

        binding.authButton.text = getString(R.string.logout)
        binding.authButton.setOnClickListener { logout() }
        updatePlayerUI()

        return true
    }

    private fun logout() {
        auth.signOut()
        signInClient?.signOut()?.addOnCompleteListener(requireActivity()) {
            binding.authButton.text = getString(R.string.login)
            binding.authButton.setOnClickListener { login() }
            updatePlayerUI()
        }
    }

}
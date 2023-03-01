package com.iut.geoflag.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.iut.geoflag.R
import com.iut.geoflag.databinding.ActivityMainBinding
import com.iut.geoflag.fragments.QuizFragment
import com.iut.geoflag.fragments.HomeFragment
import com.iut.geoflag.fragments.MapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())

        binding.navigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_quiz -> {
                    loadFragment(QuizFragment())
                    true
                }
                R.id.navigation_map -> {
                    loadFragment(MapFragment())
                    true
                }
                else -> {
                    loadFragment(HomeFragment())
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainer.id, fragment)
        transaction.commit()
    }

}
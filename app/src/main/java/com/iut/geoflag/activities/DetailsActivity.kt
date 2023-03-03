package com.iut.geoflag.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iut.geoflag.R
import com.iut.geoflag.databinding.ActivityDetailsBinding
import com.iut.geoflag.models.Country

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private var country: Country? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        country = intent.getSerializableExtra("country") as Country
    }

}
package com.iut.geoflag.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.iut.geoflag.databinding.FragmentFlagQuestionBinding
import retrofit2.Response

class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentFlagQuestionBinding

    private var responses: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFlagQuestionBinding.inflate(inflater, container, false)

        for (child in binding.answers.children) {
            var response = child as TextView
            child.setOnClickListener {
                Snackbar.make(binding.root, "Réponse: ${response.text}", Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
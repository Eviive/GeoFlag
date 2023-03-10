package com.iut.geoflag.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentFlagQuestionBinding
import com.iut.geoflag.models.Question

class QuestionFragment(private var question: Question) : Fragment() {

    private lateinit var binding: FragmentFlagQuestionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFlagQuestionBinding.inflate(inflater, container, false)

        val identifier = "question ${question.number}"
        binding.question.text = identifier

        Glide.with(this)
            .load(question.question.flags["png"])
            .into(binding.flag)

        for (possibility in question.possibilities) {
            val button = Button(context)

            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            button.text = possibility.name.common
            binding.answers.addView(button)

            button.setOnClickListener {
                Snackbar.make(binding.root, "response : ${button.text}", Snackbar.LENGTH_SHORT).show()
                val validity = question.isCorrect(possibility)
                (activity as GameActivity).submitAnswer(validity)
            }
        }

        return binding.root
    }
}
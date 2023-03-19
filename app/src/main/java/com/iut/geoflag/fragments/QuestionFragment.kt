package com.iut.geoflag.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentFlagQuestionBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Question

class QuestionFragment(private var question: Question) : Fragment() {

    private lateinit var binding: FragmentFlagQuestionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFlagQuestionBinding.inflate(inflater, container, false)

        binding.question.text = question.number.toString()

        Glide.with(this)
            .load(question.question.flags["png"])
            .into(binding.flag)

        val questionsButton = HashMap<Country, Button>()

        for (possibility in question.possibilities) {

            val button = Button(context)
            questionsButton[possibility] = button

            button.background.setTint(Color.GRAY)

            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            button.text = possibility.name.common
            binding.answers.addView(button)

            button.setOnClickListener {

                if (question.isAnswered())
                    return@setOnClickListener
                question.setAnswered()

                val correct = question.isCorrect(possibility)

                if (correct){
                    button.background.setTint(Color.GREEN)
                }else{
                    button.background.setTint(Color.RED)
                    val correctButton = questionsButton[question.question]
                    correctButton?.background?.setTint(Color.GREEN)
                }

                (activity as GameActivity).submitAnswer(correct)

            }
        }

        return binding.root
    }
}
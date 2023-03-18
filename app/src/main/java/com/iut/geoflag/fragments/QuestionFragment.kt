package com.iut.geoflag.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentFlagQuestionBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Question
import java.util.TreeMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class QuestionFragment(private var question: Question) : Fragment() {

    private lateinit var binding: FragmentFlagQuestionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFlagQuestionBinding.inflate(inflater, container, false)

        val identifier = "question ${question.number}"
        binding.question.text = identifier

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

                var cooldown:Long= 400

                if (question.isAnswered())
                    return@setOnClickListener
                question.setAnswered()

                if (question.isCorrect(possibility)){
                    button.background.setTint(Color.GREEN)
                }else{
                    button.background.setTint(Color.RED)
                    val correctButton = questionsButton[question.question]
                    correctButton?.background?.setTint(Color.GREEN)
                    cooldown = 1000
                }

                Executors.newSingleThreadScheduledExecutor().schedule({
                    (activity as GameActivity).submitAnswer(possibility)
                }, cooldown, TimeUnit.MILLISECONDS)
            }
        }

        return binding.root
    }
}
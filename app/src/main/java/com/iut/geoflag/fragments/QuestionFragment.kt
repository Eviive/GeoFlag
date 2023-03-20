package com.iut.geoflag.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.iut.geoflag.R
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

        val questionsButton = HashMap<Country, View>()

        for (possibility in question.possibilities) {

            val button = generateAnswerView(possibility)
            questionsButton[possibility] = button
            binding.answers.addView(button)

            button.setOnClickListener {

                if (question.isAnswered())
                    return@setOnClickListener
                question.setAnswered()

                val correct = question.isCorrect(possibility)

                if (correct){
                    button.background.setTint(requireContext().getColor(R.color.teal_200))
                }else{
                    button.background.setTint(requireContext().getColor(R.color.red))
                    val correctButton = questionsButton[question.question]
                    correctButton?.background?.setTint(requireContext().getColor(R.color.teal_200))
                }

                (activity as GameActivity).submitAnswer(correct)
                (activity as GameActivity).submitAnswer(correct)

            }
        }

        return binding.root
    }

    private fun generateAnswerView(Country: Country): CardView {
        val cardView = CardView(requireContext())

        cardView.radius = 50f
        cardView.setContentPadding(16, 16, 16, 16)
        cardView.cardElevation = 8f
        cardView.useCompatPadding = true

        val textView = TextView(requireContext())
        textView.text = Country.getName().common
        textView.textSize = 18f
        textView.gravity = Gravity.CENTER

        cardView.addView(textView)

        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardView.layoutParams = layoutParams

        return cardView
    }
}
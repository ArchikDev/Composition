package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import java.lang.RuntimeException

class GameFinishedFragment: Fragment() {

  private lateinit var gameResult: GameResult

  private var _binding: FragmentGameFinishedBinding? = null
  private val binding: FragmentGameFinishedBinding
    get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupClickListeners()
    bindViews()
  }

  private fun setupClickListeners() {
    val callback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        retryGame()
      }
    }
    // переопределяем событие кнопки назад
    // viewLifecycleOwner - Callback(слушатель ниже) удалится из Activity при удалении fragment
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    binding.buttonRetry.setOnClickListener {
      retryGame()
    }
  }

  private fun bindViews() {
    with(binding) {
      emojiResult.setImageResource(getSmileResId())
      tvRequiredAnswers.text = String.format(
        getString(R.string.required_score),
        gameResult.gameSettings.minCountOfRightAnswers
      )
      tvScoreAnswers.text = String.format(
        getString(R.string.score_answers),
        gameResult.countOfRightAnswers
      )
      tvRequiredPercentage.text = String.format(
        getString(R.string.required_percentage),
        gameResult.gameSettings.minPercentOfRightAnswers
      )
      tvScorePercentage.text = String.format(
        getString(R.string.score_percentage),
        getPercentOfRightAnswers()
      )
    }
  }

  private fun getSmileResId(): Int {
    return if (gameResult.winner) {
      R.drawable.smile
    } else {
      R.drawable.smile_no
    }
  }

  private fun getPercentOfRightAnswers() = with(gameResult) {
    if (countOfQuestions == 0) {
      0
    } else {
      ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    parseArgs()
  }

  // если вдруг обратились к binding в методах где view недоступно
  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun parseArgs() {
     requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let  {
      gameResult = it
    }
  }

  private fun retryGame() {
    // FragmentManager.POP_BACK_STACK_INCLUSIVE - удалить фрагмент из BackStack, а если укажем 0, то не удалит
    // т.е. мы вернемся на фрагмент перед GameFragment.NAME
    requireActivity().supportFragmentManager.popBackStack(GameFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
  }

  companion object {

    private const val KEY_GAME_RESULT = "GAME_RESULT"

    fun newInstance(gameResult: GameResult): GameFinishedFragment {
      return GameFinishedFragment().apply {
        arguments = Bundle().apply {
          putParcelable(KEY_GAME_RESULT, gameResult)
        }
      }
    }

  }


}
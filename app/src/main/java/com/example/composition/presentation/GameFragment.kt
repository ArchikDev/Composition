package com.example.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.databinding.FragmentWelcomeBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import java.lang.RuntimeException

class GameFragment: Fragment() {

  private lateinit var level: Level
  private val viewModelFactory by lazy {
    GameViewModelFactory(level, requireActivity().application)
  }
  // (by lazy) при первом обращении в коде будет проинициализирован
  private val viewModel: GameViewModel by lazy {
    ViewModelProvider(
      this,
      viewModelFactory
      // ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    )[GameViewModel::class.java]
  }

  // by lazy - потому, что если мы просто присвоим значение через равно и обратимся к binding.tvOption1 при обьявлении
  // свойства tvOptions, binding еще не будет. Поэтому нужен by lazy
  private val tvOptions by lazy {
    mutableListOf<TextView>().apply {
      add(binding.tvOption1)
      add(binding.tvOption2)
      add(binding.tvOption3)
      add(binding.tvOption4)
      add(binding.tvOption5)
      add(binding.tvOption6)
    }
  }

  private var _binding: FragmentGameBinding? = null
  private val binding: FragmentGameBinding
    get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    parseArgs()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentGameBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observeViewModel()
    setClickToOptions()
  }

  private fun setClickToOptions() {
    for (tvOption in tvOptions) {
      tvOption.setOnClickListener {
        viewModel.chooseAnswer(tvOption.text.toString().toInt())
      }
    }
  }

  private fun observeViewModel() {
    viewModel.question.observe(viewLifecycleOwner) {
      binding.tvSum.text = it.sum.toString()
      binding.tvLeftNumber.text = it.visibleNumber.toString()

      for (i in 0 until tvOptions.size) {
        tvOptions[i].text = it.options[i].toString()
      }
    }
    viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
      binding.progressBar.setProgress(it, true)
    }
    viewModel.enoughCount.observe(viewLifecycleOwner) {
      binding.tvAnswersProgress.setTextColor(getColorByState(it))
    }
    viewModel.enoughPercent.observe(viewLifecycleOwner) {
      val color = getColorByState(it)
      binding.progressBar.progressTintList = ColorStateList.valueOf(color)
    }
    viewModel.formattedTime.observe(viewLifecycleOwner) {
      binding.tvTimer.text = it
    }
    viewModel.minPercent.observe(viewLifecycleOwner) {
      binding.progressBar.secondaryProgress = it
    }
    viewModel.gameResult.observe(viewLifecycleOwner) {
      launchGameFinishedFragment(it)
    }
    viewModel.progressAnswers.observe(viewLifecycleOwner) {
      binding.tvAnswersProgress.text = it
    }
  }

  private fun getColorByState(goodState: Boolean): Int {
    val colorResId = if (goodState) {
      android.R.color.holo_green_light
    } else {
      android.R.color.holo_red_light
    }

    return ContextCompat.getColor(requireContext(), colorResId)
  }

  private fun parseArgs() {
    requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
      level = it
    }
  }

  private fun launchGameFinishedFragment(gameResult: GameResult) {
    requireActivity().supportFragmentManager.beginTransaction()
      .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
      .addToBackStack(null)
      .commit()
  }

  // если вдруг обратились к binding в методах где view недоступно
  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  companion object {

    const val NAME = "GameFragment"
    private const val KEY_LEVEL = "level"

    fun newInstance(level: Level): GameFragment {
      return GameFragment().apply {
        arguments = Bundle().apply {
          putParcelable(KEY_LEVEL, level)
        }
      }
    }

  }

}
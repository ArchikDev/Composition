package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composition.R
import com.example.composition.databinding.FragmentWelcomeBinding
import java.lang.RuntimeException

class WelcomeFragment: Fragment() {

  private var _binding: FragmentWelcomeBinding? = null
  private val binding: FragmentWelcomeBinding
    get() = _binding ?: throw RuntimeException("FragmentWelcomeBinding == null")

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
    return binding.root
//    return inflater.inflate(R.layout.fragment_welcome, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.buttonUnderstand.setOnClickListener {
      launchChooseLevelFragment()
    }
  }

  private fun launchChooseLevelFragment() {
    requireActivity().supportFragmentManager.beginTransaction()
      .replace(R.id.main_container, ChooseLevelFragment.newInstance())
      .addToBackStack(ChooseLevelFragment.NAME)
      .commit()
  }

  // если вдруг обратились к binding в методах где view недоступно
  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }


}
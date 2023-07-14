package sem.ua.androidtest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sem.ua.androidtest.R
import sem.ua.androidtest.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        navToAuth()

        binding.backArrowIc.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_mainActivity)
        }

        return binding.root
    }

    private fun navToAuth() {
        binding.startBtnv.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
        }
    }


}
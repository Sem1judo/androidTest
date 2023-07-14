package sem.ua.androidtest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sem.ua.androidtest.R
import sem.ua.androidtest.databinding.FragmentAuthBinding


class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        navBack()
        navEmail()
        navAnon()

        return binding.root
    }

    private fun navEmail() {
        binding.emailBtnv.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_emailFragment)
        }
    }

    private fun navBack() {

        binding.backArrowIc.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_welcomeFragment)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_authFragment_to_welcomeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun navAnon() {
        binding.anonBtnv.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_wheelFragment)
        }
    }
}

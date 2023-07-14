package sem.ua.androidtest.ui

import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.underline
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sem.ua.androidtest.R
import sem.ua.androidtest.data.UserData
import sem.ua.androidtest.databinding.FragmentEmailBinding
import sem.ua.androidtest.model.User

class EmailFragment : Fragment() {
    private lateinit var binding: FragmentEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailBinding.inflate(inflater, container, false)


        val data = UserData(requireContext())

        binding.backArrowIc.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.startBtnv.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (validateEmail(email)) {
                CoroutineScope(Dispatchers.IO).launch {
                    data.saveUser(User(email, 10000))
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_emailFragment_to_wheelFragment)
                    }
                }
            } else {
                binding.emailErrorText.text = getString(R.string.invalid_email)
                binding.emailErrorText.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}

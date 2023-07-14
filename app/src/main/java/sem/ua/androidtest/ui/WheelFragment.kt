package sem.ua.androidtest.ui

import android.content.Context
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.text.buildSpannedString
import androidx.core.text.underline
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cleowild.slo.ui.game.GameViewModel
import sem.ua.androidtest.R
import sem.ua.androidtest.data.UserData
import sem.ua.androidtest.databinding.FragmentWheelBinding
import sem.ua.androidtest.model.User
import java.util.*

class WheelFragment : Fragment() {

    private lateinit var binding: FragmentWheelBinding
    private val viewModel: GameViewModel by viewModels()
    private var currentBet = 100
    private var previousScore: Int = 0
    private val sectors = intArrayOf(0, 250, 0, 400, 0, 500, 0, 50, 0, 150, 0)
    private val sectorsDegrees = IntArray(sectors.size)
    private var randomSectorIndex = 0
    private var spinning = false
    private var mediaPlayer: MediaPlayer? = null
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelBinding.inflate(inflater, container, false)
        mediaPlayer = MediaPlayer()
        setupViews()
        setupBackButton()

        val rotationAnimation = RotateAnimation(
            0F,
            17f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        rotationAnimation.fillAfter = true
        rotationAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimation.setAnimationListener(animationListener)

        binding.wheel.startAnimation(rotationAnimation)

        return binding.root
    }

    private fun setupViews() {
        val userSharedPref = UserData(requireContext())
        val restoredUser = userSharedPref.restoreUser()

        if (restoredUser != null) {
            userSharedPref.currentUser = restoredUser
            previousScore = restoredUser.balance // Store the previous balance
            viewModel.setPlayerScore(previousScore)
        } else {
            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            previousScore = sharedPrefs.getInt("balance", -1) // Store the previous balance
            if (previousScore != -1) {
                viewModel.setPlayerScore(previousScore)
            }
        }

        binding.wheel.setOnClickListener {
            startSpin()
        }
        binding.start.setOnClickListener {
            startSpin()
        }

        viewModel.playerWin.observe(viewLifecycleOwner) { winUser ->
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.score_anim)
            binding.winUser.startAnimation(animation)
            binding.winUser.text = getString(R.string.user_win, winUser)

        }

        viewModel.scorePlayer.observe(viewLifecycleOwner) { userScore ->
            binding.scoreUser.text = getString(R.string.user_score, userScore)
            updateUserScore(userScore)
        }

        binding.moreBet.setOnClickListener {
            increaseBet()
            animateBetChange(currentBet)
        }

        binding.lessBet.setOnClickListener {
            decreaseBet()
            animateBetChange(currentBet)
        }

        updateBetText()

        underlineText()
    }

    private fun setupBackButton() {
        binding.backArrowIc.setOnClickListener {
            findNavController().navigate(R.id.action_wheelFragment_to_authFragment)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_wheelFragment_to_authFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun startSpin() {
        generateSectorDegrees()
        if (!spinning) {
            spin()
            spinning = true
        }
    }

    private fun spin() {
        randomSectorIndex = random.nextInt(sectors.size)
        val randomDegree = generateRandomDegreeToSpinTo()

        binding.winUser.visibility = View.GONE
        binding.scoreUser.visibility = View.GONE

        val rotationAnimation = RotateAnimation(
            0F,
            randomDegree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )

        rotationAnimation.duration = 3000
        rotationAnimation.fillAfter = true
        rotationAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimation.setAnimationListener(animationListener)

        binding.wheel.startAnimation(rotationAnimation)
    }

    private fun generateRandomDegreeToSpinTo(): Int {
        return (360 * sectors.size) + sectorsDegrees[randomSectorIndex]
    }

    private fun generateSectorDegrees() {
        val sectorDegree = 360 / sectors.size
        for (i in sectors.indices) {
            sectorsDegrees[i] = (i + 1) * sectorDegree
        }
    }

    private val animationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            var earnedCoins = sectors[sectors.size - randomSectorIndex - 1]
            binding.winUser.visibility = View.VISIBLE
            binding.scoreUser.visibility = View.VISIBLE
            if (earnedCoins == 0) {
                val currentBalance = viewModel.scorePlayer.value ?: 0
                val newBalance = currentBalance - currentBet
                viewModel.setPlayerScore(newBalance)
            } else {
                earnedCoins += currentBet
                val currentBalance = viewModel.scorePlayer.value ?: 0
                val newBalance = currentBalance + earnedCoins
                viewModel.setPlayerWin(earnedCoins)
                viewModel.setPlayerScore(newBalance)
            }
            binding.winUser.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.score_anim
                )
            )

            spinning = false
            mediaPlayer?.stop()
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    }

    private fun animateBetChange(newBet: Int) {
        val animation = ScaleAnimation(
            1f, 1.2f, 1f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            repeatMode = Animation.REVERSE
            repeatCount = 1
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.currentBet.text = "$newBet"
                }
            })
        }
        binding.currentBet.startAnimation(animation)
    }

    private fun increaseBet() {
        currentBet += 100
        updateBetText()
    }

    private fun decreaseBet() {
        if (currentBet > 0) {
            currentBet -= 100
            updateBetText()
        }
    }

    private fun underlineText() {
        binding.resetBalance.apply {
            text = buildSpannedString {
                underline {
                    append(text)
                }
            }
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                resetBalance()
            }
        }
    }

    private fun updateBetText() {
        binding.currentBet.text = "$currentBet"
        viewModel.updateBet(currentBet)
    }

    private fun updateUserScore(userScore: Int) {
        if (userScore != previousScore) {
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.score_anim)
            binding.scoreUser.startAnimation(animation)
        }

        val userSharedPref = UserData(requireContext())
        val restoredUser = userSharedPref.restoreUser()

        if (restoredUser != null) {
            val user = User(restoredUser.email, userScore)
            userSharedPref.saveUser(user)
        } else {
            val sharedPrefs =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putInt("balance", userScore)
            }
        }

        previousScore = userScore
    }

    private fun resetBalance() {
        val confirmationDialog = AlertDialog.Builder(requireContext())
            .setTitle("Reset Balance")
            .setMessage("Are you sure you want to reset your score?")
            .setPositiveButton("Reset") { _, _ ->
                val sharedPrefs =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().remove("balance").apply()
                val userManager = UserData(requireContext())
                val restoredUser = userManager.restoreUser()
                if (restoredUser != null) {
                    val user = User(restoredUser.email, 10000)
                    userManager.saveUser(user)
                }
                viewModel.setPlayerScore(10000)
                Toast.makeText(
                    requireContext(),
                    "Score reset successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        confirmationDialog.show()
    }
}

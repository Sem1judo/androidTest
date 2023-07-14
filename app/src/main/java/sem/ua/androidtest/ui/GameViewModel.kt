package com.cleowild.slo.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _playerWin = MutableLiveData(0)
    val playerWin: LiveData<Int>
        get() = _playerWin

    private val _scorePlayer = MutableLiveData(10000)
    val scorePlayer: LiveData<Int>
        get() = _scorePlayer

    private val _bet = MutableLiveData(0)
    private val bet: LiveData<Int>
        get() = _bet

    private val _slots = MutableLiveData<List<Int>>()
    val slots: LiveData<List<Int>>
        get() = _slots

    private val _isSpinning = MutableLiveData(false)
    val isSpinning: LiveData<Boolean>
        get() = _isSpinning

    fun updateBet(amount: Int) {
        _bet.value = amount
    }

    fun setPlayerScore(balance: Int) {
        _scorePlayer.value = balance
    }

    fun setPlayerWin(balance: Int) {
        _playerWin.value = balance
    }
}


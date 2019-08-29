/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

data class Game(val word: String, var corrected: Boolean = false, var skipped: Boolean = false)
//data class Board(val currentWord: String = "", val score: Int = 0)

class GameViewModel(val wordList: MutableList<Game>) : ViewModel() {
    // MAINTAINING STATE!!!
    // The current currentWord
    private val _currentWordNotifier = MutableLiveData<String>()
    val currentWordNotifier: LiveData<String>
        get() = _currentWordNotifier

    private val _scoreNotifier = MutableLiveData<Int>()
    val scoreNotifier: LiveData<Int>
        get() = _scoreNotifier

    // Game Finish toggle
    private val _isGameFinished = MutableLiveData<Boolean>()
    val isGameFinished: LiveData<Boolean>
        get() = _isGameFinished

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val timer: CountDownTimer

    // Constants for Timer
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }

    /** Local State !!! */
    // private var currentWord: String = ""

    init {
        Timber.i("GameView Model Created!")
        Log.i("GameViewModel", "GameView Model Created!")
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
            }

            override fun onFinish() {
                _isGameFinished.value = true
            }
        }

        timer.start()

        _currentWordNotifier.value = getNextWord().word
        _scoreNotifier.value = getCorrected()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("GameView Model Destroyed!")
        Log.i("GameViewModel", "GameView Model Destroyed!")
        timer.cancel()
    }

    /** Methods for buttons presses **/
    /** Only methods to modify LiveData!! **/
    fun onSkip() {
        // score--
        setWordAsSkipped(getWordAtPlay())
        // update score and word
        _scoreNotifier.value = getCorrected()
        nextWord()
    }

    fun onCorrect() {
        // score++
        setWordAsCorrected(getWordAtPlay())
        // update score and word
        _scoreNotifier.value = getCorrected()
        nextWord()
    }

    /**
     * Moves to the next currentWord in the list
     */
    fun nextWord() {
        //Select and remove a currentWord from the list
        if (isGameFinished()) {
            resetWordList()
        } else {
            _currentWordNotifier.value = getNextWord().word
        }
    }

    fun getWordAtPlay(): String {
        // This is a hack!!
        return _currentWordNotifier.value?: ""
    }

    fun resetGameFinished() {
        _isGameFinished.value = false
    }


    /* Methods to maintain the mutable list */
    fun resetWordList(): Unit {
        // Why is w.copy doesn't work???
        wordList.map { w -> w.corrected = false; w.skipped = false }
    }

    fun getNextWord(): Game {
        return wordList.filterNot { w -> (w.corrected || w.skipped) }.shuffled().first()
    }

    fun setWordAsSkipped(word: String): Unit {
        wordList.filter { w -> w.word == word }.map { w -> w.skipped = true }
    }

    fun setWordAsCorrected(word: String): Unit {
        wordList.filter { w -> w.word == word }.map { w -> w.corrected = true }
    }

    fun isGameFinished(): Boolean {
        return wordList.filterNot {  w -> (w.corrected || w.skipped) }.isEmpty()
    }

    fun getCorrected(): Int {
        return wordList.filter { w -> w.corrected }.size
    }
}
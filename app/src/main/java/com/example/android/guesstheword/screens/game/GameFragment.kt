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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    // The current word
    private var word = ""

    // The current score
    private var score = 0

    data class Game(val word: String, var corrected: Boolean = false, var skipped: Boolean = false)

    // The list of words - the front of the list is the next word to guess
    // Need to be mutable #sad
    val wordList: MutableList<Game> = mutableListOf(
            Game("queen"), Game("hospital"), Game("basketball"),
            Game("cat"), Game("change"), Game("snail"),
            Game("soup"), Game("calendar"), Game("sad"),
            Game("desk"), Game("guitar"), Game("home"),
            Game("railway"), Game("zebra"), Game("jelly"),
            Game("car"), Game("crow"), Game("trade"),
            Game("bag"), Game("roll"), Game("bubble")
    )

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        // TODO (04) Create and initialize a GameViewModel, using ViewModelProviders; Add a log
        // statement

        // resetList()
        nextWord()

        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        updateScoreText()
        updateWordText()
        return binding.root

    }

    /* Methods to maintain the mutable list */
    private fun getCurrentWord(): Game {
        return wordList.filterNot { w -> (w.corrected || w.skipped) }.shuffled().first()
    }

    private fun setWordAsSkipped(word: String): Unit {
        wordList.filter { w -> w.word == word }.map { w -> w.skipped = true }
    }

    private fun setWordAsCorrected(word: String): Unit {
        wordList.filter { w -> w.word == word }.map { w -> w.corrected = true }
    }

    private fun isGameFinished(): Boolean {
        return wordList.filterNot {  w -> (w.corrected || w.skipped) }.isEmpty()
    }

    private fun getCorrected(): Int {
        return wordList.filter { w -> w.corrected }.size
    }

    /**
     * Resets the list of words and randomizes the order
     */
//    private fun resetList() {
//        wordList = mutableListOf(
//                "queen",
//                "hospital",
//                "basketball",
//                "cat",
//                "change",
//                "snail",
//                "soup",
//                "calendar",
//                "sad",
//                "desk",
//                "guitar",
//                "home",
//                "railway",
//                "zebra",
//                "jelly",
//                "car",
//                "crow",
//                "trade",
//                "bag",
//                "roll",
//                "bubble"
//        )
        // wordList.shuffle()
//    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(getCorrected())
        findNavController(this).navigate(action)
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (isGameFinished()) {
            gameFinished()
        } else {
//            word = wordList.removeAt(0)
            val current: Game = getCurrentWord()
            word = current.word
        }

        updateWordText()
        updateScoreText()
    }

    /** Methods for buttons presses **/

    private fun onSkip() {
        // score--
        setWordAsSkipped(word)
        nextWord()
    }

    private fun onCorrect() {
        // score++
        setWordAsCorrected(word)
        nextWord()
    }

    /** Methods for updating the UI **/

    private fun updateWordText() {
        binding.wordText.text = word

    }

    private fun updateScoreText() {
        binding.scoreText.text = getCorrected().toString()
    }
}

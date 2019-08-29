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
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding
import timber.log.Timber

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    // The list of words - the front of the list is the next currentWord to guess
    // Need to be mutable #sad
    val wordList = mutableListOf(Game("queen"), Game("hospital"), Game("basketball"))
//            ,
//            Game("cat"), Game("change"), Game("snail"),
//            Game("soup"), Game("calendar"), Game("sad"),
//            Game("desk"), Game("guitar"), Game("home"),
//            Game("railway"), Game("zebra"), Game("jelly"),
//            Game("car"), Game("crow"), Game("trade"),
//            Game("bag"), Game("roll"), Game("bubble")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        Timber.i( "Called ViewModelProviders.of")
        Log.i("GameFragment", "Called ViewModelProviders.of")
        viewModel = ViewModelProviders.of(this, GameViewModelFactory(wordList)).get(GameViewModel::class.java)
        // Bind LiveData with Activity
        viewModel.currentWordNotifier.observe(this, Observer { text -> updateWordText(text) })
        viewModel.scoreNotifier.observe(this, Observer { score -> updateScoreText(score) })
        viewModel.isGameFinished.observe(this, Observer { isFinished ->
            if (isFinished) {
                gameFinished()
            }
        })
        viewModel.currentTime.observe(this, Observer { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
        })

        binding.correctButton.setOnClickListener {
            viewModel.onCorrect()
        }
        binding.skipButton.setOnClickListener {
            viewModel.onSkip()
        }

        return binding.root

    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(viewModel.getCorrected())
        findNavController(this).navigate(action)
        viewModel.resetGameFinished()
    }

    /** Methods for updating the UI **/
    private fun updateWordText(text: String) {
        binding.wordText.text = text

    }

    private fun updateScoreText(score: Int) {
        binding.scoreText.text = score.toString()
    }
}

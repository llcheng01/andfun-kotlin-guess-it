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

import android.util.Log
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel : ViewModel() {
    // MAINTAINING STATE!!!
    // The current currentWord
    var currentWord = ""

    data class Game(val word: String, var corrected: Boolean = false, var skipped: Boolean = false)

    // The list of words - the front of the list is the next currentWord to guess
    // Need to be mutable #sad
    val wordList: MutableList<Game> = mutableListOf(
            Game("queen"), Game("hospital"), Game("basketball")
//            ,
//            Game("cat"), Game("change"), Game("snail"),
//            Game("soup"), Game("calendar"), Game("sad"),
//            Game("desk"), Game("guitar"), Game("home"),
//            Game("railway"), Game("zebra"), Game("jelly"),
//            Game("car"), Game("crow"), Game("trade"),
//            Game("bag"), Game("roll"), Game("bubble")
    )

    init {
        Timber.i("GameView Model Created!")
        Log.i("GameViewModel", "GameView Model Created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("GameView Model Destroyed!")
        Log.i("GameViewModel", "GameView Model Destroyed!")
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

    fun getCorrected(): Int {
        return wordList.filter { w -> w.corrected }.size
    }

    /** Methods for buttons presses **/
    fun onSkip() {
        // score--
        setWordAsSkipped(currentWord)
        nextWord()
    }

    fun onCorrect() {
        // score++
        setWordAsCorrected(currentWord)
        nextWord()
    }

    /**
     * Moves to the next currentWord in the list
     */
    fun nextWord() {
        //Select and remove a currentWord from the list
        if (isGameFinished()) {
            //TODO
//            gameFinished()
        } else {
            val current: Game = getCurrentWord()
            currentWord = current.word
        }
    }
}
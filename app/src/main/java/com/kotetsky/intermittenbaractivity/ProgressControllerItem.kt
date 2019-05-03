package com.kotetsky.intermittenbaractivity

import com.kotetsky.intermittenprogressbar.ProcessingListener
import android.util.Log.d as log

enum class CurrentState {
    UPLOADING_FOOTAGE,
    ANALYSING_FOOTAGE,
    CREATING_STORYBOARD,
    ADDING_GRAPHICS,
    ADDING_MUSIC,
    NEARBY_FINISHED
}

// number of seconds which are spent in one percent
private const val SECONDS_IN_PERCENT = 0.6f

// number of percent which are spent in one tick and one tick is 500 ms
private const val PERCENT_IN_TICK = 0.8333f

class ProgressControllerItem(val vsid: String) {
    val startTime = System.currentTimeMillis()
    var currentState: CurrentState =
        CurrentState.UPLOADING_FOOTAGE
    var listener: ProcessingListener? = null
    var currentProgress: Float = 0f

    fun registerListener(processingListener: ProcessingListener) {
        listener = processingListener
        setInitialState()
    }

    fun unregisterListener() {
        listener = null
    }

    fun done() {
        doOnUi {
            listener?.done()
        }
    }

    fun nextProgress() {
        currentProgress += PERCENT_IN_TICK
        if (currentProgress > 100) {
            // do not change the progress and listener state when ticker is going on further
            return
        }
        val currentSecond = currentProgress * SECONDS_IN_PERCENT
        doOnUi {
            when (currentSecond) {
                in 0f..10f -> {
                    if (currentState != CurrentState.UPLOADING_FOOTAGE) {
                        currentState =
                            CurrentState.UPLOADING_FOOTAGE
                        listener?.uploadingFootage()
                    }
                }
                in 10f..20f -> {
                    if (currentState != CurrentState.ANALYSING_FOOTAGE) {
                        currentState =
                            CurrentState.ANALYSING_FOOTAGE
                        listener?.analysingFootage()
                    }
                }
                in 20f..30f -> {
                    if (currentState != CurrentState.CREATING_STORYBOARD) {
                        currentState =
                            CurrentState.CREATING_STORYBOARD
                        listener?.creatingStoryboard()
                    }
                }
                in 30f..40f -> {
                    if (currentState != CurrentState.ADDING_GRAPHICS) {
                        currentState =
                            CurrentState.ADDING_GRAPHICS
                        listener?.addingGraphics()
                    }
                }
                in 40f..50f -> {
                    if (currentState != CurrentState.ADDING_MUSIC) {
                        currentState = CurrentState.ADDING_MUSIC
                        listener?.addingMusic()
                    }
                }
                in 50f..60f -> {
                    if (currentState != CurrentState.NEARBY_FINISHED) {
                        currentState =
                            CurrentState.NEARBY_FINISHED
                        listener?.nearbyFinished()
                    }
                }
            }
            listener?.setProgress(currentProgress)
        }
    }

    private fun setInitialState() {
        doOnUi {
            listener?.apply {
                if (currentProgress > 100) {
                    nearbyFinished()
                    setProgress(100f)
                }
                when (currentState) {
                    CurrentState.UPLOADING_FOOTAGE -> uploadingFootage()
                    CurrentState.ANALYSING_FOOTAGE -> analysingFootage()
                    CurrentState.CREATING_STORYBOARD -> creatingStoryboard()
                    CurrentState.ADDING_GRAPHICS -> addingGraphics()
                    CurrentState.ADDING_MUSIC -> addingMusic()
                    CurrentState.NEARBY_FINISHED -> nearbyFinished()
                }
            }
        }
    }
}

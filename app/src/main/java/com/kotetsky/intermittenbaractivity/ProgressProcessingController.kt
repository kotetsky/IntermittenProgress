package com.kotetsky.intermittenprogressbar

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.kotetsky.intermittenbaractivity.CurrentState
import com.kotetsky.intermittenbaractivity.ProgressControllerItem
import com.kotetsky.intermittenbaractivity.ProgressProcessingActivity
import com.kotetsky.intermittenbaractivity.doOnUi
import android.util.Log.d as log
import java.util.*

private const val SESSION_RESPONSE_KEY = "SESSION_RESPONSE_KEY"
private const val TICK_INTERVAL_TIME: Long = 500
private const val DEFAULT_VSID = "DEFAULT_VSID"

class ProgressProcessingController(
    private val context: Context
) {
    private val tag = ProgressProcessingController::class.java.toString()
    private var isRefreshing = false
    private var timer: Timer = Timer()
    private var progressItemMap = mutableMapOf<String, ProgressControllerItem>()

    init {
        startNewSession(DEFAULT_VSID)
    }

    fun register(newVsid: String, processingListener: ProcessingListener) {
        progressItemMap[newVsid] ?: return
        val progressControllerItem = progressItemMap[newVsid] as ProgressControllerItem
        progressControllerItem.registerListener(processingListener)
        initListenerState(progressControllerItem, processingListener)
        log(tag, "register for $newVsid this = $processingListener")
        log(tag, "progressItemMap size= ${progressItemMap.size}")
    }

    fun unregister(newVsid: String) {
        progressItemMap[newVsid]?.unregisterListener()
        log(tag, "progressItemMap size= ${progressItemMap.size}")
    }

    fun startNewSession(newVsid: String) {
        if (progressItemMap.isEmpty()) {
            // 500 miliseconds that means two times in second
            log(tag, " start ticker in startSession() with vsid = $newVsid")
            progressItemMap[newVsid] = ProgressControllerItem(newVsid)
            timer = Timer()
            timer.schedule(TickerTask(), 0, TICK_INTERVAL_TIME)
        } else {
            log(tag, " in startSession() with vsid = $newVsid")
            progressItemMap[newVsid] = ProgressControllerItem(newVsid)
        }
    }

    fun stopTimer() {
        log(tag, "timer.cancel()")
        timer.cancel()
    }

    private fun initListenerState(
        progressControllerItem: ProgressControllerItem,
        processingListener: ProcessingListener
    ) = doOnUi {
        processingListener.setProgress(progressControllerItem.currentProgress)
        when (progressControllerItem.currentState) {
            CurrentState.UPLOADING_FOOTAGE -> processingListener.uploadingFootage()
            CurrentState.ANALYSING_FOOTAGE -> processingListener.analysingFootage()
            CurrentState.ADDING_GRAPHICS -> processingListener.addingGraphics()
            CurrentState.ADDING_MUSIC -> processingListener.addingMusic()
            CurrentState.CREATING_STORYBOARD -> processingListener.creatingStoryboard()
            CurrentState.NEARBY_FINISHED -> processingListener.nearbyFinished()
        }
    }

    inner class TickerTask : TimerTask() {

        override fun run() {
            if (progressItemMap.isEmpty()) {
                log(tag, "progressItemMap.isEmpty()")
                return
            }
            progressItemMap.forEach { it.value.nextProgress() }
        }
    }
}

interface ProcessingListener {
    fun setProgress(progress: Float)
    fun uploadingFootage()
    fun analysingFootage()
    fun creatingStoryboard()
    fun addingGraphics()
    fun addingMusic()
    fun nearbyFinished()
    fun done()
}
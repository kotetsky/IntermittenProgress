package com.kotetsky.intermittenbaractivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.kotetsky.intermittenprogressbar.IntermittentProgressBar
import com.kotetsky.intermittenprogressbar.ProcessingListener
import com.kotetsky.intermittenprogressbar.ProgressProcessingController
import android.util.Log.d as log

const val START_COUNTER_EXTRA = "START_COUNTER_EXTRA"
const val VSID_EXTRA = "VSID_EXTRA"

private val TAG = ProgressProcessingActivity::class.java.simpleName

class ProgressProcessingActivity : AppCompatActivity(), ProcessingListener {
    private val backButton by view<View>(R.id.back_button)
    private val processingImage by view<ImageView>(R.id.progress_image)
    private val intermittentProgressBar by view<IntermittentProgressBar>(
        R.id.activity_intermittent_progress_bar
    )
    private val progressTitle by view<TextView>(R.id.progress_title)
    private val processingPager by view<ViewPager>(R.id.progress_tip_pager)

    private val progressProcessingController = ProgressProcessingController(this)

    private val vsid by lazy {
        intent.getStringExtra(VSID_EXTRA) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setContentView(R.layout.activity_progress_processing_layout)

        // set last item as placeholder. Last  item index = count - 2
        // iterate from zero with dummy fragment excluded
        processingPager.adapter = ProcessingPagerAdapter(supportFragmentManager)
        processingPager.currentItem = processingPager.adapter!!.count - 2
        backButton.setOnClickListener {
            finish()
        }
        val startCounter = intent.getBooleanExtra(START_COUNTER_EXTRA, true)
        log(TAG, "START_COUNTER_EXTRA = $startCounter")

        val onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                log(TAG, "onPageSelected $position")
                val pagerCount = processingPager.adapter!!.count
                log(TAG, "pagerCount =  $pagerCount")
                if (position == 0) {
                    processingPager.setCurrentItem(pagerCount - 2, false)
                }
                if (position == pagerCount - 1) {
                    processingPager.setCurrentItem(1, false)
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                log(TAG, "onPageScrolled $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                log(TAG, "onPageScrollStateChanged")
            }
        }
        processingPager.addOnPageChangeListener(onPageChangeListener)

        if (startCounter) {
            log(TAG, "startCounter")
            log(TAG, "progressProcessingController = $progressProcessingController")
            progressProcessingController.startNewSession(vsid)
        }
    }

    override fun onBackPressed() {
        log(TAG, "backPressed")
        finish()
    }

    override fun onResume() {
        super.onResume()
        log(
            TAG,
            "onResume register activity for progressController = $progressProcessingController"
        )
        progressProcessingController.register(vsid, this)
    }

    override fun onPause() {
        super.onPause()
        progressProcessingController.unregister(vsid)
    }

    override fun uploadingFootage() {
        log(TAG, "uploadingFootage")
        progressTitle.setText(R.string.Processing_slide_uploading)
        processingImage.setImageDrawable(getDrawable(R.drawable.progress_uploading))
        processingPager.setCurrentItem(1, true)
    }

    override fun analysingFootage() {
        log(TAG, "analysingFootage")
        progressTitle.setText(R.string.Processing_slide_analyzing)
        processingPager.setCurrentItem(2, true)
    }

    override fun creatingStoryboard() {
        log(TAG, "creatingStoryboard")
        progressTitle.setText(R.string.Processing_slide_creating)
        processingPager.setCurrentItem(3, true)
    }

    override fun addingGraphics() {
        log(TAG, "addingGraphics")
        progressTitle.setText(R.string.Processing_slide_adding)
        processingPager.setCurrentItem(4, true)
    }

    override fun addingMusic() {
        log(TAG, "addingMusic")
        progressTitle.setText(R.string.Processing_slide_music)
        processingPager.setCurrentItem(5, true)
    }

    override fun nearbyFinished() {
        log(TAG, "nearbyFinished")
        progressTitle.setText(R.string.Processing_slide_rendering)
        processingPager.setCurrentItem(6, true)
    }

    override fun done() {
        log(TAG, "done and finish")
        progressTitle.text = getString(R.string.Processing_slide_uploading)
        finish()
    }

    override fun setProgress(progress: Float) {
        log(TAG, "setProgress $progress")
        intermittentProgressBar.setProgress(progress)
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, vsid: String) {
            val intent = Intent(context, ProgressProcessingActivity::class.java)
            val bundle = Bundle()
            bundle.putString(VSID_EXTRA, vsid)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun startActivityProgressing(context: Context, vsid: String) {
            val intent = Intent(context, ProgressProcessingActivity::class.java)
            val bundle = Bundle()
            bundle.putString(VSID_EXTRA, vsid)
            bundle.putBoolean(START_COUNTER_EXTRA, true)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}

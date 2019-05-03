package com.kotetsky.intermittenbaractivity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.Log.d as log

const val PROCESSING_TIP_TITLE_ARG = "PROCESSING_TIP_TITLE_ARG"
const val PROCESSING_TIP_TEXT_ARG = "PROCESSING_TIP_TEXT_ARG"

class ProcessingPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {

    private val pagerList: List<ProcessingTipFragment>

    init {
        val dummyFirstFragment = ProcessingTipFragment.getDummyInstance()
        val uploadFootageFragment = ProcessingTipFragment.getInstance(
            R.string.Processing_uploading_title,
            R.string.Processing_uploading_message
        )
        val analysingFootageFragment = ProcessingTipFragment.getInstance(
            R.string.Processing_analyzing_title,
            R.string.Processing_analyzing_message
        )
        val creatingStoryboardFragment = ProcessingTipFragment.getInstance(
            R.string.Processing_creating_title,
            R.string.Processing_creating_message
        )
        val addingGraphicsFragment = ProcessingTipFragment.getInstance(
            R.string.Processing_adding_title,
            R.string.Processing_adding_message
        )
        val addingMusicFragment = ProcessingTipFragment.getInstance(
            R.string.Processing__music_title,
            R.string.Processing_music_message
        )
        val nearlyFinishedFragment = ProcessingTipFragment.getInstance(
            R.string.Processing_rendering_title,
            R.string.Processing_rendering_message
        )
        val dummyLastFragment = ProcessingTipFragment.getDummyInstance()

        pagerList = listOf(
            dummyFirstFragment,
            uploadFootageFragment,
            analysingFootageFragment,
            creatingStoryboardFragment,
            addingGraphicsFragment,
            addingMusicFragment,
            nearlyFinishedFragment,
            dummyLastFragment
        )
    }

    override fun getCount() = pagerList.size

    override fun getItem(position: Int): Fragment = pagerList[position]
}

class ProcessingTipFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.processing_tip_fragment, null)

        val title = arguments?.getInt(PROCESSING_TIP_TITLE_ARG) ?: R.string.GENERIC__UNTITLED
        val text = arguments?.getInt(PROCESSING_TIP_TEXT_ARG) ?: R.string.GENERIC__UNTITLED
        if (title == 0 || text == 0) {
            return view
        }
        val processingTitleView = view.findViewById<TextView>(R.id.progress_tip_title)
        val processingTextView = view.findViewById<TextView>(R.id.progress_tip_text)
        processingTitleView.setText(title)
        processingTextView.setText(text)

        return view
    }

    companion object {
        fun getInstance(titleStringId: Int, @StringRes textStringId: Int): ProcessingTipFragment {
            val fragment = ProcessingTipFragment()
            val bundle = Bundle()
            bundle.putInt(PROCESSING_TIP_TITLE_ARG, titleStringId)
            bundle.putInt(PROCESSING_TIP_TEXT_ARG, textStringId)
            fragment.arguments = bundle
            return fragment
        }

        fun getDummyInstance(): ProcessingTipFragment {
            val fragment = ProcessingTipFragment()
            val bundle = Bundle()
            bundle.putInt(PROCESSING_TIP_TITLE_ARG, 0)
            bundle.putInt(PROCESSING_TIP_TEXT_ARG, 0)
            fragment.arguments = bundle
            return fragment
        }
    }
}

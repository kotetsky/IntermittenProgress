package com.kotetsky.intermittenprogressbar


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.util.Log.d as log
import android.util.TypedValue
import android.view.ViewGroup

private val TAG = IntermittentProgressBar::class.java.simpleName

private const val DEFAULT_STROKE_WIDTH = 16f
private const val DEFAULT_INTERMITTENT_COUNT = 5
private const val DEFAULT_INTERMITTENT_WIDTH = 15f
private const val MIN_LINE_WIDTH = 10

class IntermittentProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val primaryPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val intermittentCount: Int
    private var intermittentWidth: Float
    private var strokeWidth: Float

    private val linesList = mutableListOf<ProgressLine>()
    private var currentPercent = 100f

    init {
        primaryPaint.style = Paint.Style.STROKE
        primaryPaint.strokeCap = Paint.Cap.BUTT
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.BUTT

        if (attrs == null) {
            primaryPaint.color = Color.GREEN
            progressPaint.color = Color.BLUE

            primaryPaint.strokeWidth = DEFAULT_STROKE_WIDTH
            progressPaint.strokeWidth = DEFAULT_STROKE_WIDTH
            intermittentCount = 0
            intermittentWidth = 0f
            strokeWidth = 0f
        } else {
            with(context.obtainStyledAttributes(attrs, R.styleable.IntermittentProgressBar)) {
                intermittentCount = getInt(
                    R.styleable.IntermittentProgressBar_intermittent_count,
                    DEFAULT_INTERMITTENT_COUNT
                )

                intermittentWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    getFloat(
                        R.styleable.IntermittentProgressBar_intermittent_width,
                        DEFAULT_INTERMITTENT_WIDTH
                    ),
                    resources.displayMetrics
                )

                strokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    getFloat(
                        R.styleable.IntermittentProgressBar_stroke_width,
                        DEFAULT_STROKE_WIDTH
                    ),
                    resources.displayMetrics
                )

                primaryPaint.color =
                    getColor(R.styleable.IntermittentProgressBar_primary_color, Color.GREEN)
                progressPaint.color =
                    getColor(R.styleable.IntermittentProgressBar_progress_color, Color.BLUE)
                primaryPaint.strokeWidth = strokeWidth
                progressPaint.strokeWidth = strokeWidth
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (linesList.isNullOrEmpty() || currentPercent < 0 || currentPercent > 100) {
            return
        }
        val lineOrdinate = paddingTop.toFloat()
        log(TAG, "lineOrdinate = $lineOrdinate")
        linesList.forEach { line ->
            when {
                currentPercent < line.startPercent ->
                    canvas.drawLine(
                        line.start,
                        lineOrdinate,
                        line.end,
                        lineOrdinate,
                        primaryPaint
                    )
                currentPercent > line.endPercent ->
                    canvas.drawLine(
                        line.start,
                        lineOrdinate,
                        line.end,
                        lineOrdinate,
                        progressPaint
                    )
                else -> {
                    canvas.drawLine(
                        line.start,
                        lineOrdinate,
                        line.end,
                        lineOrdinate,
                        primaryPaint
                    )
                    val progressEnd = line.start +
                            (line.percentInPixels * (currentPercent - line.startPercent))
                    canvas.drawLine(
                        line.start,
                        lineOrdinate,
                        progressEnd,
                        lineOrdinate,
                        progressPaint
                    )
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        log(TAG, "onMeasure")
        val xPadding = paddingLeft + paddingRight
        val yPadding = paddingTop + paddingBottom

        val viewHeight = if (isMeasured()) {
            yPadding + strokeWidth
        } else {
            measuredHeight + 0f
        }

        val viewWidth = measuredWidth - xPadding

        setMeasuredDimension(measuredWidth, viewHeight.toInt())

        log(TAG, "width = ${viewWidth + xPadding}, height = $viewHeight")
        log(TAG, "xPadding = $xPadding and yPadding = $yPadding")

        calculateBaseProgressLines(viewWidth)
        calculateProgress()
    }

    private fun isMeasured() = measuredHeight == 0
            || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT

    private fun calculateBaseProgressLines(viewWidth: Int) {
        var linesCount = intermittentCount + 1
        val minWidth = intermittentWidth * intermittentCount + (linesCount * MIN_LINE_WIDTH)
        if (minWidth > viewWidth) {
            linesCount = 1
        }
        log(TAG, "linesCount = $linesCount")
        linesList.clear()

        var nextLineBegin = paddingLeft.toFloat()
        val lineWidth = (viewWidth - (intermittentCount * intermittentWidth)) / linesCount
        log(TAG, "lineWidth = $lineWidth")

        for (i in 1..linesCount) {
            val nextLineEnd = nextLineBegin + lineWidth
            val eachLine = ProgressLine(nextLineBegin, nextLineEnd)
            linesList.add(eachLine)

            log(TAG, "make line [$nextLineBegin, $nextLineEnd]")
            nextLineBegin += (lineWidth + intermittentWidth)
        }
    }

    private fun calculateProgress() {
        val linesCount = intermittentCount + 1
        val eachLinePercentCount = 100f / linesCount
        var nextLinePercentBegin = 0f

        linesList.forEach { line ->
            line.startPercent = nextLinePercentBegin
            line.endPercent = nextLinePercentBegin + eachLinePercentCount
            log(TAG, "LINE startPercent = [${line.startPercent}, endPercent = ${line.endPercent}]")

            line.percentInPixels = (line.end - line.start) / eachLinePercentCount
            log(TAG, "LINE percentInPixels = [${line.percentInPixels}]")
            nextLinePercentBegin = line.endPercent + 0.01f
        }
    }

    fun setProgress(percent: Float) {
        log(TAG, "in intermittent progress bar set progress $percent")
        if (percent < 0 || percent > 100) {
            return
        }
        currentPercent = percent
        invalidate()
    }
}

class ProgressLine(
    val start: Float,
    val end: Float,
    var percentInPixels: Float = 0f,
    var startPercent: Float = 0f,
    var endPercent: Float = 100f
)

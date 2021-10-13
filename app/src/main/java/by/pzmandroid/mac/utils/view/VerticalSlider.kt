package by.pzmandroid.mac.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import by.pzmandroid.mac.R


class VerticalSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var heightWithPadding = 0
    private var widthWithPadding = 0

    private var rowHeightPercent = 0F
    private var rowDelimiterPercent = 0F

    private var padding = 0
    private var cornerRadius = 0F

    private var rowHeight = 0F
    private var rowDelimiter = 0F

    private var topColor = 0
    private var bottomColor = 0

    private var maxValue = 0F
    private var minValue = 0F

    private var progress = 500F

    private var scalePaint: Paint

    private var defaultListener: (value: Int) -> (Unit) = {}

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.VerticalSlider, 0, 0).apply {
            try {
                rowHeightPercent = getFloat(R.styleable.VerticalSlider_customRowHeight, 0.1F)
                rowDelimiterPercent = getFloat(R.styleable.VerticalSlider_customDelimiterHeight, 0.01F)
                topColor = getColor(R.styleable.VerticalSlider_topColor, context.getColor(R.color.colorModeSelected))
                bottomColor = getColor(R.styleable.VerticalSlider_bottomColor, context.getColor(R.color.colorModeNormal))
                padding = getDimensionPixelSize(R.styleable.VerticalSlider_sliderPadding, 0)
                cornerRadius = getFloat(R.styleable.VerticalSlider_rowCornerRadius, 0.0F)
                maxValue = getFloat(R.styleable.VerticalSlider_maxValue, 0.0F)
                minValue = getFloat(R.styleable.VerticalSlider_minValue, 0.0F)
            } finally {
                recycle()
            }
        }
        scalePaint = Paint().apply {
            isAntiAlias = true
            color = bottomColor
            style = Paint.Style.FILL
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        heightWithPadding = MeasureSpec.getSize(heightMeasureSpec).minus(padding)
        widthWithPadding = MeasureSpec.getSize(widthMeasureSpec).minus(padding)

        rowHeight = rowHeightPercent.times(heightWithPadding)
        rowDelimiter = rowDelimiterPercent.times(heightWithPadding)
        scalePaint.shader = LinearGradient(0F, 0F, 0F, heightWithPadding.toFloat(), topColor, bottomColor, Shader.TileMode.REPEAT)

        if (cornerRadius == 0F && padding != 0) {
            cornerRadius = padding.toFloat()
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            var currentBottom = heightWithPadding.toFloat()
            val counterRows = (currentBottom.minus(progress) / rowHeight.plus(rowDelimiter)).toInt()
            for (i in 1..counterRows) {
                drawRoundRect(
                    0F.plus(padding),
                    currentBottom.minus(rowHeight),
                    widthWithPadding.toFloat(),
                    currentBottom,
                    cornerRadius,
                    cornerRadius,
                    scalePaint
                )
                currentBottom -= rowHeight.plus(rowDelimiter)
            }
            drawRoundRect(
                0F.plus(padding), progress, widthWithPadding.toFloat(), currentBottom, cornerRadius, cornerRadius, scalePaint
            )

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.y > padding && event.y <= heightWithPadding) {
                    progress = event.y
                    defaultListener.invoke(getValue())
                    invalidate()
                }
            }
        }
        return true
    }

    fun setupSlider(current: Float) {
        progress = heightWithPadding.minus((heightWithPadding.div(maxValue - minValue).times(current.minus(minValue))))
        defaultListener.invoke(getValue())
        invalidate()
    }

    fun getValue() = (((maxValue - minValue).times(heightWithPadding - progress)).div(heightWithPadding)).plus(minValue).toInt()

    fun setOnValueChangeListener(listener: (value: Int) -> (Unit)) {
        defaultListener = listener
    }

}
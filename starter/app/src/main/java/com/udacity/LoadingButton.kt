package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val TEXT_SIZE = 55.0f
private const val ANIMATION_DURATION = 3000L

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonClickedText = 0
    private var buttonLoadingText = 0
    private var buttonCompleteText = 0
    private var buttonTextColor = 0
    private var buttonBackgroundColor = 0
    private var loadingProgress: Float = 0f

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed)
    { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                animateColorChange()
                invalidate()

            }
            ButtonState.Completed -> {
                completeAnimation()
                invalidate()
            }
        }
    }

    private fun completeAnimation() {
        valueAnimator.end()
    }

    fun setNewButtonState(newButtonState: ButtonState) {
        buttonState = newButtonState
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TEXT_SIZE
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorPrimaryDark)
    }

    init {
        isClickable = true
        buttonState = ButtonState.Clicked

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonClickedText = getInt(R.styleable.LoadingButton_buttonClickedText, 0)
            buttonLoadingText = getInt(R.styleable.LoadingButton_buttonLoadingText, 0)
            buttonCompleteText = getInt(R.styleable.LoadingButton_buttonCompletedText, 0)
            buttonTextColor = getInt(R.styleable.LoadingButton_buttonTextColor, context.getColor(R.color.white))
            buttonBackgroundColor = getInt(R.styleable.LoadingButton_buttonBackgroundColor, context.getColor(R.color.colorPrimary))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)

        if (buttonState == ButtonState.Loading) {
            drawLoadingProgress(canvas)
        }

        drawText(canvas)
    }

    private fun drawLoadingProgress(canvas: Canvas) {
        canvas.drawRect(0f, heightSize.toFloat(), widthSize.toFloat() * loadingProgress / 100,
                0f, loadingPaint)
    }

    private fun drawText(canvas: Canvas) {
        val buttonText = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.download)
            ButtonState.Loading -> context.getString(R.string.button_loading)
            ButtonState.Completed -> context.getString(R.string.download)
        }

        textPaint.color = buttonTextColor
        canvas.drawText(buttonText, widthSize / 2f, heightSize / 1.7f, textPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundPaint.color = buttonBackgroundColor

        canvas.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(),
                0f, backgroundPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun animateColorChange() {
        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                loadingProgress = (valueAnimator.animatedValue as Int).toFloat()
                invalidate()
            }
        }
        valueAnimator.start()
    }
}
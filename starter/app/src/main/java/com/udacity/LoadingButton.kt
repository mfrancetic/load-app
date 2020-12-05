package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonClickedText = 0
    private var buttonLoadingText = 0
    private var buttonCompleteText = 0
    private lateinit var frame: View
    private var loadingProgress: Float = 0f

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed)
    { property, old, new ->
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
    };

    private fun completeAnimation() {
        valueAnimator.end()
    }

    fun setNewButtonState(newButtonState: ButtonState) {
        buttonState = newButtonState
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = Color.WHITE
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.resources.getColor(R.color.colorPrimary)
    }

    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.resources.getColor(R.color.colorPrimaryDark)
    }

    init {
        isClickable = true
        buttonState = ButtonState.Clicked
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonClickedText = getInt(R.styleable.LoadingButton_buttonClickedText, 0)
            buttonLoadingText = getInt(R.styleable.LoadingButton_buttonLoadingText, 0)
            buttonCompleteText = getInt(R.styleable.LoadingButton_buttonCompletedText, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val buttonText = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.download)
            ButtonState.Loading -> context.getString(R.string.button_loading)
            ButtonState.Completed -> context.getString(R.string.download)
        }

        canvas.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(),
                0f, backgroundPaint)
        
        if (buttonState == ButtonState.Loading) {
            canvas.drawRect(0f, heightSize.toFloat(), widthSize.toFloat() * loadingProgress / 100,
                    0f, loadingPaint)
        }

        canvas.drawText(buttonText, widthSize / 2f, heightSize / 1.7f, paint)
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
            duration = 3000
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                loadingProgress = (valueAnimator.animatedValue as Int).toFloat()
                invalidate()
            }
        }
        valueAnimator.start()
    }
}
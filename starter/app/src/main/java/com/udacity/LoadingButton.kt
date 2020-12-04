package com.udacity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
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

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed)
    { property, old, new ->
        when (new) {
            //  // if ButtonState.Loading -> show "Loading" text and start the animation
            ButtonState.Loading -> {
                animateColorChange()
            }
            // // if ButtonState.Completed -> show "Completed" text and end the animation
            ButtonState.Completed -> completeAnimation()
        }
        invalidate()
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
        setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
        textSize = 55.0f
        color = Color.WHITE
        typeface = Typeface.create("", Typeface.NORMAL)
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

        if (buttonState != ButtonState.Loading) {
            setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
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
        val colorFrom = resources.getColor(R.color.colorPrimary)
        val colorTo = resources.getColor(R.color.colorPrimaryDark)
        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)

        valueAnimator.addUpdateListener { animator ->
            setBackgroundColor(animator.animatedValue as Int)
        }
        valueAnimator.start()
    }
}
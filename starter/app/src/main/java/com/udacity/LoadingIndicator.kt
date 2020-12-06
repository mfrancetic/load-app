package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

private const val START_ANGLE = 30f
private const val SWEEP_SIZE = 360f
private const val ANIMATION_DURATION = 2000

private class Arc(val start: Float, val sweep: Float, val color: Int)

class LoadingIndicator @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    var colors: List<Int> = emptyList()
        set(value) {
            if (field != value || arcs.isEmpty()) {
                field = value
                computeArcs()
            }
        }

    fun stopLoadingAnimation() {
        arcs = emptyList()
        animator?.cancel()
    }

    private var animator: ValueAnimator? = null
    private var currentSweepAngle = 0

    private val paint: Paint = Paint()
    private var arcs = emptyList<Arc>()

    private fun computeArcs() {
        arcs = if (colors.isEmpty()) {
            listOf(Arc(0f, SWEEP_SIZE, android.R.color.darker_gray))
        } else {
            colors.mapIndexed { index, _ ->
                val startAngle = index * SWEEP_SIZE
                Arc(start = startAngle, sweep = SWEEP_SIZE, color = Color.YELLOW)
            }
        }
        startAnimation()
    }

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        computeArcs()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, SWEEP_SIZE.toInt()).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = INFINITE
            duration = ANIMATION_DURATION.toLong()
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        arcs.forEach { arc ->
            if (currentSweepAngle > arc.start + arc.sweep) {
                paint.color = arc.color
                canvas.drawArc(rect,
                        START_ANGLE + arc.start,
                        arc.sweep,
                        true,
                        paint)
            } else {
                if (currentSweepAngle > arc.start) {
                    paint.color = arc.color
                    canvas.drawArc(rect,
                            START_ANGLE,
                            currentSweepAngle - arc.start,
                            true,
                            paint)
                }
            }
        }
    }
}
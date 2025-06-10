package com.example.k_app_v1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.random.Random

class BubbleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Bubble(
        var x: Float,
        var y: Float,
        val baseRadius: Float,
        var dx: Float,
        var dy: Float,
        val paint: Paint,
        // Pop (spargere)
        var isPopping: Boolean = false,
        var popStartTime: Long = 0,
        var popProgress: Float = 0f
    )

    private val bubbles = mutableListOf<Bubble>()
    private val numBubbles = 28
    private val popDuration = 350L // ms

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bubbles.clear()
        repeat(numBubbles) {
            bubbles.add(createBubble(w, h))
        }
    }

    private fun createBubble(viewWidth: Int, viewHeight: Int): Bubble {
        val baseRadius = Random.nextFloat() * 60 + 110  // 110 - 190 px
        val x = Random.nextFloat() * (viewWidth - 2 * baseRadius) + baseRadius
        val y = Random.nextFloat() * (viewHeight - 2 * baseRadius) + baseRadius
        val dx = Random.nextFloat() * 0.8f - 0.4f  // -0.4 până la 0.4
        val dy = Random.nextFloat() * 0.8f - 0.4f

        val pastelColors = listOf(
            Color.argb(120, 255, 182, 193),  // roz pal
            Color.argb(120, 173, 216, 230),  // albastru pal
            Color.argb(120, 255, 255, 153),  // galben pastel
            Color.argb(120, 102, 240, 200),  // turcoaz pastel
            Color.argb(120, 221, 160, 221),  // lavandă
            Color.argb(120, 255, 218, 185)   // piersică
        )

        val paint = Paint().apply {
            color = pastelColors.random()
            isAntiAlias = true
        }

        return Bubble(x, y, baseRadius, dx, dy, paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentTime = System.currentTimeMillis()

        val bubblesToRemove = mutableListOf<Bubble>()
        val bubblesToAdd = mutableListOf<Bubble>()

        for (bubble in bubbles) {
            if (bubble.isPopping) {
                // Efect de pop: creștere rapidă și fade-out
                val elapsed = currentTime - bubble.popStartTime
                val progress = (elapsed / popDuration.toFloat()).coerceAtMost(1f)
                bubble.popProgress = progress

                val popScale = 1f + progress * 1f  // se mărește până la 2.3x
                val alpha = ((1f - progress) * 120).toInt().coerceAtLeast(0)
                bubble.paint.alpha = alpha

                canvas.drawCircle(bubble.x, bubble.y, bubble.baseRadius * popScale, bubble.paint)

                if (progress >= 1f) {
                    bubblesToRemove.add(bubble)
                    bubblesToAdd.add(createBubble(width, height))
                }
            } else {
                // Bula normală, alpha 120 pastel
                bubble.paint.alpha = 120
                canvas.drawCircle(bubble.x, bubble.y, bubble.baseRadius, bubble.paint)
                // Actualizare poziție (mișcare lentă aleatoare)
                bubble.x += bubble.dx
                bubble.y += bubble.dy

                // Rebound la margini
                if (bubble.x - bubble.baseRadius < 0 || bubble.x + bubble.baseRadius > width) {
                    bubble.dx = -bubble.dx
                }
                if (bubble.y - bubble.baseRadius < 0 || bubble.y + bubble.baseRadius > height) {
                    bubble.dy = -bubble.dy
                }
            }
        }

        // Elimină bulele sparte și adaugă unele noi
        bubbles.removeAll(bubblesToRemove)
        bubbles.addAll(bubblesToAdd)

        postInvalidateOnAnimation()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val touchX = event.x
            val touchY = event.y

            for (bubble in bubbles) {
                val distance = hypot(touchX - bubble.x, touchY - bubble.y)
                if (distance <= bubble.baseRadius && !bubble.isPopping) {
                    bubble.isPopping = true
                    bubble.popStartTime = System.currentTimeMillis()
                    bubble.popProgress = 0f
                }
            }
        }
        return true
    }
}

package com.example.pictureoftheday.view.customViews

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.values
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import kotlin.math.sqrt


class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        scaleType = ScaleType.MATRIX
    }

    private val mMatrix = Matrix()
    private val viewSize = PointF()
    private val imageSize = PointF()
    private val currentSize = PointF()
    private var currentScale = 1F
    private var originScale = 1F
    private val imageOriginPoint = PointF()
    private val imageCurrentPoint = PointF()
    private val clickPoint = PointF()

    var doubleClickZoom = 4F
    var maxScale = 20f

    private val doubleClickTimeSpan: Long = 250
    private var lastClickTime: Long = 0
    private var startFingersDistance = 0f
    private val fingersCenter = PointF()
    private var startFingersScale = 1F


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        viewSize.set(width.toFloat(), height.toFloat())
        imageSize.set(drawable.minimumWidth.toFloat(), drawable.minimumHeight.toFloat())
        preparations()
    }

    private fun preparations() {
        val scaleX = viewSize.x / imageSize.x
        val scaleY = viewSize.y / imageSize.y
        originScale = if (scaleX < scaleY) scaleX else scaleY
        scaleImage(originScale)

        if (scaleX < scaleY) {
            imageOriginPoint.set(0F, viewSize.y / 2 - currentSize.y / 2)
        } else {
            imageOriginPoint.set(viewSize.x / 2 - currentSize.x / 2, 0F)
        }

        imageCurrentPoint.set(imageOriginPoint)
        mMatrix.postTranslate(imageOriginPoint.x, imageOriginPoint.y)
        imageMatrix = mMatrix
    }

    private fun showCenter() {
        scaleImage(originScale)
        imageCurrentPoint.set(imageOriginPoint)
        mMatrix.postTranslate(imageOriginPoint.x, imageOriginPoint.y)
        imageMatrix = mMatrix
    }

    private fun scaleImage(scale: Float) {
        mMatrix.setScale(scale, scale)
        currentSize.set(scale * imageSize.x, scale * imageSize.y);
        currentScale = scale
        imageMatrix = mMatrix
    }

    private fun scaleImage(scale: Float, point: PointF) {

        if (scale < originScale) {
            showCenter()
            return
        }

        val relativeClickPoint = PointF(
            (point.x - imageCurrentPoint.x) / currentSize.x,
            (point.y - imageCurrentPoint.y) / currentSize.y
        )

        mMatrix.setScale(scale, scale)
        currentSize.set(scale * imageSize.x, scale * imageSize.y);
        currentScale = scale

        mMatrix.postTranslate(
            point.x - relativeClickPoint.x * currentSize.x,
            point.y - relativeClickPoint.y * currentSize.y
        )

        imageMatrix = mMatrix

        imageCurrentPoint.set(getImageOriginPoint())
    }

    private fun translationImage(pointF: PointF) {
        imageCurrentPoint.apply {

            if (currentSize.x > viewSize.x) {
                if (x + pointF.x > 0) pointF.x = -x
                else if (x + currentSize.x + pointF.x < viewSize.x) pointF.x =
                    viewSize.x - x - currentSize.x
            } else {
                pointF.x = 0f
            }

            if (currentSize.y > viewSize.y) {
                if (y + pointF.y > 0) pointF.y = -y
                else if (y + currentSize.y + pointF.y < viewSize.y) pointF.y =
                    viewSize.y - y - currentSize.y
            } else {
                pointF.y = 0f
            }
        }

        mMatrix.postTranslate(pointF.x, pointF.y)
        imageMatrix = mMatrix
        imageCurrentPoint.apply {
            set(x + pointF.x, y + pointF.y)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.println(
                    Log.ASSERT,
                    "НАЖАТА КНОПКА",
                    String.format(
                        "x = %f, y = %f, originPoint = %f, %f",
                        event.x,
                        event.y,
                        imageCurrentPoint.x,
                        imageCurrentPoint.y
                    )
                )
                clickPoint.set(event.x, event.y)
                if (event.pointerCount == 1) {
                    if (System.currentTimeMillis() - lastClickTime <= doubleClickTimeSpan) {

                        TransitionManager.beginDelayedTransition(
                            rootView as ViewGroup,
                            TransitionSet().addTransition(ChangeImageTransform())
                        )

                        if (currentScale == originScale) {
                            scaleImage(originScale * doubleClickZoom, clickPoint)
                        } else {
                            showCenter()
                         }

                    } else {
                        lastClickTime = System.currentTimeMillis()
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                startFingersDistance = getFingersDistance(event);
                fingersCenter.set(getFingersCenter(event))
                startFingersScale = currentScale
//                Log.println(Log.ASSERT, "НАЖАТА ВТОРАЯ КНОПКА", "Нажата вторая кнопка")
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
//                    Log.println(Log.ASSERT, "Движение при 1 кнопке", "Движение при 1 кнопке")
                    moveImage(event)
                } else {
                    val currentFingerDistance = getFingersDistance(event)
                    val fingersDistanceDiff = currentFingerDistance - startFingersDistance

                    if (currentScale >= maxScale && fingersDistanceDiff > 0) {
                        return super.onTouchEvent(event)
                    }

                    scaleImage(
                        startFingersScale * (currentFingerDistance / startFingersDistance),
                        fingersCenter
                    )

                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }

        return true
    }

    private fun getFingersDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun getFingersDistance(point1: PointF, point2: PointF): Float {
        val x = point1.x - point2.x
        val y = point1.y - point2.y
        return sqrt(x * x + y * y)
    }

    private fun getFingersCenter(event: MotionEvent): PointF {
        return PointF(
            (event.getX(0) + event.getX(1)) / 2,
            (event.getY(0) + event.getY(1)) / 2
        )
    }

    private fun getImageOriginPoint(): PointF {
        return imageMatrix.values().let {
            PointF(it[2], it[5])
        }
    }

    private fun moveImage(event: MotionEvent) {
        if (event.historySize == 0) return

        val diff = PointF(
            event.x - event.getHistoricalX(0),
            event.y - event.getHistoricalY(0)
        )
        translationImage(diff)
    }


    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.MATRIX)
        //TODO
    }

}


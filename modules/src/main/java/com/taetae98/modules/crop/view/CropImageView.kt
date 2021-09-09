package com.taetae98.modules.crop.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.taetae98.modules.gesture.listener.GestureListener
import kotlin.math.abs

class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): ShapeableImageView(context, attrs, defStyle) {
    private val gestureListener by lazy {
        object : GestureListener(0F, 0F, 0F) {
            private val dragMatrix by lazy { Matrix() }
            private val zoomMatrix by lazy { Matrix() }

            override fun onDragStart(view: View, event: MotionEvent) {
                dragMatrix.set(imageMatrix)
            }

            override fun onDrag(view: View, event: MotionEvent, movementX: Float, movementY: Float) {
                val values = MatrixValue(dragMatrix)
                val matrixWidth = drawable.intrinsicWidth * values.matrixWidthScale
                val matrixHeight = drawable.intrinsicHeight * values.matrixHeightScale

                val translationX = when {
                    movementX + values.matrixX + matrixWidth <= measuredWidth -> {
                        measuredWidth - values.matrixX - matrixWidth
                    }
                    movementX + values.matrixX >= 0F -> {
                        -values.matrixX
                    }
                    else -> {
                        movementX
                    }
                }
                val translationY = when {
                    movementY + values.matrixY + matrixHeight <= measuredHeight -> {
                        measuredHeight - values.matrixY - matrixHeight
                    }
                    movementY + values.matrixY >= 0F -> {
                        -values.matrixY
                    }
                    else -> {
                        movementY
                    }
                }

                imageMatrix = Matrix(dragMatrix).apply {
                    postTranslate(translationX, translationY)
                }
            }

            override fun onZoomStart(view: View, event: MotionEvent) {
                zoomMatrix.set(imageMatrix)
            }

            override fun onZoom(view: View, event: MotionEvent, scale: Float) {
                val values = MatrixValue(zoomMatrix)
                val matrixWidth = drawable.intrinsicWidth * values.matrixWidthScale
                val matrixHeight = drawable.intrinsicHeight * values.matrixHeightScale

                if (matrixWidth*scale < measuredWidth || matrixHeight*scale < measuredHeight) {
                    return
                }

                val movementLeft = twoPoint.x - values.matrixX
                val movementRight = values.matrixX + matrixWidth - twoPoint.x
                val movementTop = twoPoint.y - values.matrixY
                val movementBottom = values.matrixY + matrixHeight - twoPoint.y

                val imageLeft = twoPoint.x - movementLeft*scale
                val imageRight = twoPoint.x + movementRight*scale
                val imageTop = twoPoint.y - movementTop*scale
                val imageBottom = twoPoint.y + movementBottom*scale

                val translationX = when {
                    imageLeft > 0 -> {
                        -imageLeft
                    }
                    imageRight < measuredWidth -> {
                        measuredWidth - imageRight
                    }
                    else -> {
                        0F
                    }
                }
                val translationY = when {
                    imageTop > 0 -> {
                        -imageTop
                    }
                    imageBottom < measuredHeight -> {
                        measuredHeight - imageBottom
                    }
                    else -> {
                        0F
                    }
                }

                imageMatrix = Matrix(zoomMatrix).apply {
                    postScale(scale, scale, twoPoint.x, twoPoint.y)
                    postTranslate(translationX, translationY)
                }
            }

            override fun onRotate(view: View, event: MotionEvent, degree: Float) {

            }
        }
    }

    init {
        adjustViewBounds = true
        scaleType = ScaleType.MATRIX
        setOnTouchListener(gestureListener)

        viewTreeObserver.addOnGlobalLayoutListener {
            if (drawable is BitmapDrawable) {
                setImageCenter()
            }
        }
    }

    private fun setImageCenter() {
        val imageRectF = RectF(0F, 0F, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())

        val viewRectF = if (bitmap.width > bitmap.height) {
            val scale = measuredHeight.toFloat() / drawable.intrinsicHeight.toFloat()
            val scaledWidth = drawable.intrinsicWidth.toFloat() * scale
            val x = (scaledWidth - measuredWidth) / 2F
            RectF(-x, 0F, measuredWidth + x, measuredHeight.toFloat())
        } else {
            val scale = measuredWidth.toFloat() / drawable.intrinsicWidth.toFloat()
            val scaledHeight = drawable.intrinsicHeight.toFloat() * scale
            val y = (scaledHeight - measuredHeight) / 2F
            RectF(0F, -y, measuredWidth.toFloat(), measuredHeight + y)
        }

        imageMatrix = Matrix(imageMatrix).apply {
            setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER)
        }
    }

    val croppedBitmap: Bitmap
        get() {
            val values = MatrixValue(imageMatrix)

            return Bitmap.createBitmap(
                bitmap,
                abs(values.matrixX/values.matrixWidthScale).toInt(),
                abs(values.matrixY/values.matrixHeightScale).toInt(),
                (measuredWidth/values.matrixWidthScale + 0.5).toInt(),
                (measuredHeight/values.matrixHeightScale + 0.5).toInt()
            )
        }

    private val bitmap: Bitmap
        get() {
            return (drawable as BitmapDrawable).bitmap
        }

    class MatrixValue(matrix: Matrix) {
        private val values by lazy { FloatArray(9) }

        init {
            matrix.getValues(values)
        }

        val matrixX: Float
            get() {
                return values[2]
            }

        val matrixY: Float
            get() {
                return values[5]
            }

        val matrixWidthScale: Float
            get() {
                return values[0]
            }

        val matrixHeightScale: Float
            get() {
                return values[4]
            }
    }
}
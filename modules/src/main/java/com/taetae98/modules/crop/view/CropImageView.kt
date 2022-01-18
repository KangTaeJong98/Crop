package com.taetae98.modules.crop.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.taetae98.modules.gesture.listener.GestureListener
import java.io.Serializable
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
                if (!isDraggable) return

                dragMatrix.set(imageMatrix)
            }

            override fun onDrag(view: View, event: MotionEvent, movementX: Float, movementY: Float) {
                if (!isDraggable) return

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
                if (!isZoomable) return

                zoomMatrix.set(imageMatrix)
            }

            override fun onZoom(view: View, event: MotionEvent, scale: Float) {
                if (!isZoomable) return

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
    private var savedMatrixValue: MatrixValue? = null

    var isDraggable: Boolean = true
    var isZoomable: Boolean = true

    init {
        isSaveEnabled = true
        adjustViewBounds = true
        scaleType = ScaleType.MATRIX
        setOnTouchListener(gestureListener)

        viewTreeObserver.addOnGlobalLayoutListener {
            if (drawable is BitmapDrawable) {
                setImageCenter()
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()?.let {
            Bundle().apply {
                putParcelable("super", it)
                putSerializable("matrixValue", MatrixValue(imageMatrix))
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable("super"))
            savedMatrixValue = state.getSerializable("matrixValue") as? MatrixValue
        } else {
            super.onRestoreInstanceState(state)
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

        if (savedMatrixValue == null) {
            imageMatrix = Matrix(imageMatrix).apply {
                setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER)
            }
        } else {
            restoreImageMatrix()
        }
    }

    private fun restoreImageMatrix() {
        imageMatrix = savedMatrixValue?.let {
            Matrix().apply {
                postScale(it.matrixWidthScale, it.matrixHeightScale)
                postTranslate(it.matrixX, it.matrixY)
            }
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

    class MatrixValue(private val values: FloatArray) : Serializable {
        constructor(matrix: Matrix) : this(FloatArray(9)) {
            matrix.getValues(values)
        }

        init {
            if (values.size < 9) {
                throw Exception("Minimum size is 9. (size : ${values.size})")
            }
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

        override fun toString(): String {
            return "MatrixValue(matrixX:$matrixX, matrixY:$matrixY, matrixWidthScale:$matrixWidthScale, matrixHeightScale:$matrixHeightScale)"
        }
    }
}
package com.andyha.coreutils.glideTransformation

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest


class RoundedCornersTransformation : BitmapTransformation {
    private val radius: Int
    private val diameter: Int
    private val margin: Int
    private val cornerType: CornerType
    private val crossCutType: CrossCutType
    private val angleCut: Int

    @JvmOverloads
    constructor(radius: Int, margin: Int, cornerType: CornerType = CornerType.ALL) {
        this.radius = radius
        diameter = this.radius * 2
        this.margin = margin
        this.cornerType = cornerType
        crossCutType = CrossCutType.NONE
        angleCut = 0
    }

    constructor(
        radius: Int,
        margin: Int,
        cornerType: CornerType,
        crossCutType: CrossCutType,
        angleCut: Int
    ) {
        this.radius = radius
        diameter = this.radius * 2
        this.margin = margin
        this.cornerType = cornerType
        this.crossCutType = crossCutType
        this.angleCut = angleCut
    }

    override fun transform(
        context: Context, pool: BitmapPool,
        toTransform: Bitmap, outWidth: Int, outHeight: Int
    ): Bitmap {
        val bitmap = pool[outWidth, outHeight, Bitmap.Config.ARGB_8888]
        bitmap.setHasAlpha(true)
        setCanvasBitmapDensity(toTransform, bitmap)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        drawRoundRect(canvas, paint, outWidth.toFloat(), outHeight.toFloat())
        crossCutImage(canvas, paint)
        return bitmap
    }

    private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float) {
        val right = width - margin
        val bottom = height - margin
        when (cornerType) {
            CornerType.ALL -> canvas.drawRoundRect(
                RectF(
                    margin.toFloat(), margin.toFloat(), right, bottom
                ), radius.toFloat(), radius.toFloat(), paint
            )
            CornerType.TOP_LEFT -> drawTopLeftRoundRect(canvas, paint, right, bottom)
            CornerType.TOP_RIGHT -> drawTopRightRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM_LEFT -> drawBottomLeftRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM_RIGHT -> drawBottomRightRoundRect(canvas, paint, right, bottom)
            CornerType.TOP -> drawTopRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM -> drawBottomRoundRect(canvas, paint, right, bottom)
            CornerType.LEFT -> drawLeftRoundRect(canvas, paint, right, bottom)
            CornerType.RIGHT -> drawRightRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_TOP_LEFT -> drawOtherTopLeftRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_TOP_RIGHT -> drawOtherTopRightRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_BOTTOM_LEFT -> drawOtherBottomLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_BOTTOM_RIGHT -> drawOtherBottomRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_LEFT -> drawDiagonalFromTopLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_RIGHT -> drawDiagonalFromTopRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            else -> canvas.drawRoundRect(
                RectF(margin.toFloat(), margin.toFloat(), right, bottom),
                radius.toFloat(),
                radius.toFloat(),
                paint
            )
        }
    }

    private fun drawTopLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(
                margin.toFloat(),
                margin.toFloat(),
                (margin + diameter).toFloat(),
                (margin + diameter).toFloat()
            ), radius.toFloat(),
            radius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                margin.toFloat(),
                (margin + radius).toFloat(),
                (margin + radius).toFloat(),
                bottom
            ), paint
        )
        canvas.drawRect(RectF((margin + radius).toFloat(), margin.toFloat(), right, bottom), paint)
    }

    private fun drawTopRightRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(right - diameter, margin.toFloat(), right, (margin + diameter).toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(margin.toFloat(), margin.toFloat(), right - radius, bottom), paint)
        canvas.drawRect(RectF(right - radius, (margin + radius).toFloat(), right, bottom), paint)
    }

    private fun drawBottomLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), bottom - diameter, (margin + diameter).toFloat(), bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                margin.toFloat(),
                margin.toFloat(),
                (margin + diameter).toFloat(),
                bottom - radius
            ), paint
        )
        canvas.drawRect(RectF((margin + radius).toFloat(), margin.toFloat(), right, bottom), paint)
    }

    private fun drawBottomRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - diameter, bottom - diameter, right, bottom), radius.toFloat(),
            radius.toFloat(), paint
        )
        canvas.drawRect(RectF(margin.toFloat(), margin.toFloat(), right - radius, bottom), paint)
        canvas.drawRect(RectF(right - radius, margin.toFloat(), right, bottom - radius), paint)
    }

    private fun drawTopRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), right, (margin + diameter).toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(margin.toFloat(), (margin + radius).toFloat(), right, bottom), paint)
    }

    private fun drawBottomRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), bottom - diameter, right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(margin.toFloat(), margin.toFloat(), right, bottom - radius), paint)
    }

    private fun drawLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), (margin + diameter).toFloat(), bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(RectF((margin + radius).toFloat(), margin.toFloat(), right, bottom), paint)
    }

    private fun drawRightRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(right - diameter, margin.toFloat(), right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(margin.toFloat(), margin.toFloat(), right - radius, bottom), paint)
    }

    private fun drawOtherTopLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), bottom - diameter, right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(right - diameter, margin.toFloat(), right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(margin.toFloat(), margin.toFloat(), right - radius, bottom - radius),
            paint
        )
    }

    private fun drawOtherTopRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), (margin + diameter).toFloat(), bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(margin.toFloat(), bottom - diameter, right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                (margin + radius).toFloat(),
                margin.toFloat(),
                right,
                bottom - radius
            ), paint
        )
    }

    private fun drawOtherBottomLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), right, (margin + diameter).toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(right - diameter, margin.toFloat(), right, bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                margin.toFloat(),
                (margin + radius).toFloat(),
                right - radius,
                bottom
            ), paint
        )
    }

    private fun drawOtherBottomRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), right, (margin + diameter).toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(margin.toFloat(), margin.toFloat(), (margin + diameter).toFloat(), bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                (margin + radius).toFloat(),
                (margin + radius).toFloat(),
                right,
                bottom
            ), paint
        )
    }

    private fun drawDiagonalFromTopLeftRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(
                margin.toFloat(),
                margin.toFloat(),
                (margin + diameter).toFloat(),
                (margin + diameter).toFloat()
            ), radius.toFloat(),
            radius.toFloat(), paint
        )
        canvas.drawRoundRect(
            RectF(right - diameter, bottom - diameter, right, bottom), radius.toFloat(),
            radius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                margin.toFloat(),
                (margin + radius).toFloat(),
                right - radius,
                bottom
            ), paint
        )
        canvas.drawRect(
            RectF(
                (margin + radius).toFloat(),
                margin.toFloat(),
                right,
                bottom - radius
            ), paint
        )
    }

    private fun drawDiagonalFromTopRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - diameter, margin.toFloat(), right, (margin + diameter).toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(margin.toFloat(), bottom - diameter, (margin + diameter).toFloat(), bottom),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(margin.toFloat(), margin.toFloat(), right - radius, bottom - radius),
            paint
        )
        canvas.drawRect(
            RectF(
                (margin + radius).toFloat(),
                (margin + radius).toFloat(),
                right,
                bottom
            ), paint
        )
    }

    private fun crossCutImage(canvas: Canvas, paint: Paint) {
        val lengthEraser: Int
        when (crossCutType) {
            CrossCutType.NONE -> {}
            CrossCutType.TOP_RIGHT -> {
                val tanAngle = Math.tan(Math.toRadians(angleCut.toDouble()))
                lengthEraser = (tanAngle * canvas.height).toInt()
                crossCutTopRight(canvas, paint, lengthEraser)
            }
            CrossCutType.BOTTOM_LEFT -> {
                val tanAngle1 = Math.tan(Math.toRadians(angleCut.toDouble()))
                lengthEraser = (tanAngle1 * canvas.height).toInt()
                crossCutBottomLeft(canvas, paint, lengthEraser)
            }
            else -> {}
        }
    }

    private fun crossCutTopRight(canvas: Canvas, paint: Paint, lengthEraser: Int) {
        val pathEraser = Path()
        //start at top_right -> end at bottom_left
        pathEraser.moveTo(canvas.width.toFloat(), 0f)
        pathEraser.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
        pathEraser.lineTo((canvas.width - lengthEraser).toFloat(), canvas.height.toFloat())
        paint.style = Paint.Style.FILL
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawPath(pathEraser, paint)
    }

    private fun crossCutBottomLeft(canvas: Canvas, paint: Paint, lengthEraser: Int) {
        val pathEraser = Path()
        //start at bottom_left -> end at top_right
        pathEraser.moveTo(0f, canvas.height.toFloat())
        pathEraser.lineTo(0f, 0f)
        pathEraser.lineTo(lengthEraser.toFloat(), 0f)
        paint.style = Paint.Style.FILL
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawPath(pathEraser, paint)
    }

    override fun toString(): String {
        return ("RoundedTransformation(radius=" + radius + ", margin=" + margin + ", diameter="
                + diameter + ", cornerType=" + cornerType.name + ")")
    }

    override fun equals(o: Any?): Boolean {
        return o is RoundedCornersTransformation && o.radius == radius && o.diameter == diameter && o.margin == margin && o.cornerType == cornerType
    }

    override fun hashCode(): Int {
        return ID.hashCode() + radius * 10000 + diameter * 1000 + margin * 100 + cornerType.ordinal * 10
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + diameter + margin + cornerType).toByteArray(Key.CHARSET))
    }

    enum class CornerType {
        ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT, OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT, DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    enum class CrossCutType {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TWO_LINE_TOP_RIGHT_BOTTOM_LEFT, TWO_LINE_TOP_LEFT_BOTTOM_RIGHT
    }

    companion object {
        private const val VERSION = 1
        private const val ID =
            "RoundedCornersTransformation." + VERSION
    }
}
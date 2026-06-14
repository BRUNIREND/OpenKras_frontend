package ru.sibfu.openkras.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

fun createNumberedPinBitmap(context: Context, number: Int): Bitmap {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8B1A34")
    }

    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius - 6f, circlePaint)
    canvas.drawCircle(radius, radius, radius - 6f, strokePaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 38f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    val yPos = radius - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(number.toString(), radius, yPos, textPaint)

    return bitmap
}
package ru.sibfu.openkras.ui.theme

import androidx.compose.animation.core.copy
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.width
import androidx.core.text.color

fun Modifier.customShadow(color: Color = Color.Black.copy(alpha = 0.2f),
                          borderRadius: Dp = 16.dp,
                          blurRadius: Dp = 8.dp,
                          offsetY: Dp = 4.dp, // Смещение по вертикали
                          offsetX: Dp = 0.dp  // Смещение по горизонтали
) = this.drawBehind {
    val shadowColor = color.toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor

        // Установка размытия и тени
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )

        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}
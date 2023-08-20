package com.networthtracker.presentation.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.networthtracker.presentation.trimToNearestThousandth

@Composable
internal fun PriceChart(
    yPoints: List<Double>,
    graphColor: Color = Color.Green
) {
    val paint = Paint().asFrameworkPaint().apply {
        this.textSize = 35f
        this.color = Color.White.toArgb()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(bottom = 20.dp)
            .padding(top = 30.dp)
    ) {
        Canvas(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            val newYMax = yPoints.max()
            val newYMin = yPoints.min()
            val diff = newYMax - newYMin
            val xModifier = (size.height / (diff)) * 0.90
            val newY = ArrayList<Float>()
            yPoints.forEach {
                newY.add(((it - newYMin) * xModifier).toFloat())
            }

            val spacing = (size.width / yPoints.size) - .5f

            val normX = mutableListOf<Float>()
            val normY = mutableListOf<Float>()

            val strokePath = Path().apply {
                for (i in newY.indices) {
                    val currentX =
                        (spacing + i * spacing) + 150

                    if (i == 0) {
                        moveTo(currentX, newY[i])
                    } else {
                        val previousX = (spacing + (i - 1) * spacing) + 150

                        val conX1 = (previousX + currentX) / 2f
                        val conX2 = (previousX + currentX) / 2f

                        val conY1 = newY[i - 1]
                        val conY2 = newY[i]

                        cubicTo(
                            x1 = conX1,
                            y1 = conY1,
                            x2 = conX2,
                            y2 = conY2,
                            x3 = currentX,
                            y3 = newY[i]
                        )
                    }
                    normX.add(currentX)
                    normY.add(newY[i])
                }
            }

            drawPath(
                path = strokePath,
                color = graphColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            val minY = newY.min()
            val maxY = newY.max() + 10

            drawLine(
                color = Color.White,
                start = Offset(150f, (maxY / 2f)),
                end = Offset(size.width, (maxY / 2f)),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            drawLine(
                color = Color.White,
                start = Offset(150f, 0f),
                end = Offset(150f, maxY),
            )


            drawLine(
                color = Color.White,
                start = Offset(150f, (maxY / 1.33f)),
                end = Offset(size.width, (maxY / 1.33f)),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            drawLine(
                color = Color.White,
                start = Offset(150f, (maxY / 4)),
                end = Offset(size.width, (maxY / 4)),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            drawIntoCanvas {
                it.nativeCanvas.drawLine(150f, maxY, size.width, maxY, paint)

                it.nativeCanvas.drawText(
                    newYMin.toString(),
                    0f,
                    (maxY + 10),
                    paint
                )

                it.nativeCanvas.drawText(
                    "${newYMin + (diff * .5)}".trimToNearestThousandth(),
                    0f,
                    (maxY / 2) + 10,
                    paint
                )

                it.nativeCanvas.drawText(
                    "${newYMin + (diff * .25)}".trimToNearestThousandth(),
                    0f,
                    (maxY / 1.33f) + 10,
                    paint
                )

                it.nativeCanvas.drawText(
                    "${newYMin + (diff * .75)}".trimToNearestThousandth(),
                    0f,
                    (maxY / 4) + 10,
                    paint
                )

                it.nativeCanvas.drawText(
                    newYMax.toString(),
                    0f,
                    minY + 10,
                    paint
                )
            }
        }
    }
}

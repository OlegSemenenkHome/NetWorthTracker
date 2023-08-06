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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
internal fun PriceChart(
    yPoints: List<Float> = listOf(
        199f, 52f, 193f, 290f, 150f, 445f, 1000f, 800f, 990f, 900f, 300f
    ),
    graphColor: Color = Color.Green
) {
    val paint = Paint().asFrameworkPaint().apply {
        this.color = Color.White.hashCode()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(bottom = 20.dp)
    ) {
        Canvas(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {

            val modifier = ((size.height - 20) / yPoints.max())
            val newY = ArrayList<Float>()
            yPoints.forEach {
                newY.add(size.height - it * modifier)
            }

            val spacing = (size.width / yPoints.size) - 10

            val normX = mutableListOf<Float>()
            val normY = mutableListOf<Float>()

            val strokePath = Path().apply {

                for (i in newY.indices) {

                    val currentX = spacing + i * spacing

                    if (i == 0) {

                        moveTo(currentX, newY[i])
                    } else {

                        val previousX = spacing + (i - 1) * spacing

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

                    // Circle dot points
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
            val maxY = newY.max()

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "Min Val ${yPoints.min()}",
                    0f,
                    maxY,
                    paint
                )

                it.nativeCanvas.drawText(
                    "Max Val ${yPoints.max()}",
                    0f,
                    minY,
                    paint
                )
            }

            (normX.indices).forEach { point ->

                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "Date",
                        normX[point],
                        size.height - 10,
                        paint
                    )
                }

                drawCircle(
                    Color.Black,
                    radius = 3.dp.toPx(),
                    center = Offset(normX[point], normY[point])
                )
            }
        }
    }
}
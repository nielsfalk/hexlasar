package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.*
import de.nielsfalk.laserhexagon.ui.Icons.Companion.hint
import de.nielsfalk.laserhexagon.ui.Icons.Companion.next

@Composable
fun Buttons(
    onEvent: (HexlaserEvent) -> Unit,
    state: HexLaserState
) {
    Row(
        Modifier
            .padding(horizontal = 5.dp)
            .fillMaxWidth()
    ) {

        Button(
            onClick = { onEvent(LevelUp) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Text(state.levelType.lable)
        }
        Button(
            onClick = { onEvent(Next) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Icon(imageVector = next, contentDescription = null)
        }
        Button(
            onClick = { onEvent(Retry) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
        }
        Button(
            onClick = { onEvent(Hint) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Icon(imageVector = hint, contentDescription = null)
        }
    }
}

class Icons {
    companion object {
        val next by lazy {
            ImageVector.Builder(
                name = "New_window",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(200f, 840f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(120f, 760f)
                    verticalLineToRelative(-560f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(200f, 120f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(80f)
                    horizontalLineTo(200f)
                    verticalLineToRelative(560f)
                    horizontalLineToRelative(560f)
                    verticalLineToRelative(-240f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(240f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(760f, 840f)
                    close()
                    moveToRelative(440f, -400f)
                    verticalLineToRelative(-120f)
                    horizontalLineTo(520f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(120f)
                    verticalLineToRelative(-120f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(120f)
                    horizontalLineToRelative(120f)
                    verticalLineToRelative(80f)
                    horizontalLineTo(720f)
                    verticalLineToRelative(120f)
                    close()
                }
            }.build()
        }

        val hint by lazy {
            ImageVector.Builder(
                name = "Magic",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(9.5f, 2.672f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 1f, 0f)
                    verticalLineTo(0.843f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 0f)
                    close()
                    moveToRelative(4.5f, 0.035f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 13.293f, 2f)
                    lineTo(12f, 3.293f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0.707f, 0.707f)
                    close()
                    moveTo(7.293f, 4f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 8f, 3.293f)
                    lineTo(6.707f, 2f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 2.707f)
                    close()
                    moveToRelative(-0.621f, 2.5f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -1f)
                    horizontalLineTo(4.843f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, 1f)
                    close()
                    moveToRelative(8.485f, 0f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -1f)
                    horizontalLineToRelative(-1.829f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 1f)
                    close()
                    moveTo(13.293f, 10f)
                    arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 14f, 9.293f)
                    lineTo(12.707f, 8f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, -0.707f, 0.707f)
                    close()
                    moveTo(9.5f, 11.157f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 0f)
                    verticalLineTo(9.328f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 0f)
                    close()
                    moveToRelative(1.854f, -5.097f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -0.706f)
                    lineToRelative(-0.708f, -0.708f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.707f, 0f)
                    lineTo(8.646f, 5.94f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 0.707f)
                    lineToRelative(0.708f, 0.708f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.707f, 0f)
                    lineToRelative(1.293f, -1.293f)
                    close()
                    moveToRelative(-3f, 3f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -0.706f)
                    lineToRelative(-0.708f, -0.708f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.707f, 0f)
                    lineTo(0.646f, 13.94f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 0.707f)
                    lineToRelative(0.708f, 0.708f)
                    arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.707f, 0f)
                    close()
                }
            }.build()
        }
    }
}

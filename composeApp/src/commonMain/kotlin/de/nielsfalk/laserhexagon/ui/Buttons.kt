package de.nielsfalk.laserhexagon.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import de.nielsfalk.laserhexagon.LevelType
import de.nielsfalk.laserhexagon.ui.HexlaserEvent.*
import de.nielsfalk.laserhexagon.ui.Icons.Companion.down
import de.nielsfalk.laserhexagon.ui.Icons.Companion.hint
import de.nielsfalk.laserhexagon.ui.Icons.Companion.refresh
import de.nielsfalk.laserhexagon.ui.Icons.Companion.right
import de.nielsfalk.laserhexagon.ui.Icons.Companion.up

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
            Text("Difficulty ${state.levelType.ordinal + 1}")

            Icon(
                imageVector = if (state.levelType == LevelType.entries.last())
                    down
                else
                    up,
                contentDescription = null
            )

        }
        Button(
            onClick = { onEvent(Next) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Icon(imageVector = right, contentDescription = null)
        }
        Button(
            onClick = { onEvent(Retry) },
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Icon(imageVector = refresh, contentDescription = null)
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
        val up by lazy {
            ImageVector.Builder(
                name = "Arrow_upward",
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
                    moveTo(440f, 800f)
                    verticalLineToRelative(-487f)
                    lineTo(216f, 537f)
                    lineToRelative(-56f, -57f)
                    lineToRelative(320f, -320f)
                    lineToRelative(320f, 320f)
                    lineToRelative(-56f, 57f)
                    lineToRelative(-224f, -224f)
                    verticalLineToRelative(487f)
                    close()
                }
            }.build()
        }

        val down by lazy {
            ImageVector.Builder(
                name = "Arrow_downward",
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
                    moveTo(440f, 160f)
                    verticalLineToRelative(487f)
                    lineTo(216f, 423f)
                    lineToRelative(-56f, 57f)
                    lineToRelative(320f, 320f)
                    lineToRelative(320f, -320f)
                    lineToRelative(-56f, -57f)
                    lineToRelative(-224f, 224f)
                    verticalLineToRelative(-487f)
                    close()
                }
            }.build()

        }

        val right by lazy {
            ImageVector.Builder(
                name = "Arrow_forward",
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
                    moveTo(647f, 520f)
                    horizontalLineTo(160f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(487f)
                    lineTo(423f, 216f)
                    lineToRelative(57f, -56f)
                    lineToRelative(320f, 320f)
                    lineToRelative(-320f, 320f)
                    lineToRelative(-57f, -56f)
                    close()
                }
            }.build()
        }

        val refresh by lazy {
            ImageVector.Builder(
                name = "Cycle",
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
                    moveTo(314f, 845f)
                    quadToRelative(-104f, -48f, -169f, -145f)
                    reflectiveQuadTo(80f, 481f)
                    quadToRelative(0f, -26f, 2.5f, -51f)
                    reflectiveQuadToRelative(8.5f, -49f)
                    lineToRelative(-46f, 27f)
                    lineToRelative(-40f, -69f)
                    lineToRelative(191f, -110f)
                    lineToRelative(110f, 190f)
                    lineToRelative(-70f, 40f)
                    lineToRelative(-54f, -94f)
                    quadToRelative(-11f, 27f, -16.5f, 56f)
                    reflectiveQuadToRelative(-5.5f, 60f)
                    quadToRelative(0f, 97f, 53f, 176.5f)
                    reflectiveQuadTo(354f, 775f)
                    close()
                    moveToRelative(306f, -485f)
                    verticalLineToRelative(-80f)
                    horizontalLineToRelative(109f)
                    quadToRelative(-46f, -57f, -111f, -88.5f)
                    reflectiveQuadTo(480f, 160f)
                    quadToRelative(-55f, 0f, -104f, 17f)
                    reflectiveQuadToRelative(-90f, 48f)
                    lineToRelative(-40f, -70f)
                    quadToRelative(50f, -35f, 109f, -55f)
                    reflectiveQuadToRelative(125f, -20f)
                    quadToRelative(79f, 0f, 151f, 29.5f)
                    reflectiveQuadTo(760f, 195f)
                    verticalLineToRelative(-55f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(220f)
                    close()
                    moveTo(594f, 960f)
                    lineTo(403f, 850f)
                    lineToRelative(110f, -190f)
                    lineToRelative(69f, 40f)
                    lineToRelative(-57f, 98f)
                    quadToRelative(118f, -17f, 196.5f, -107f)
                    reflectiveQuadTo(800f, 480f)
                    quadToRelative(0f, -11f, -0.5f, -20.5f)
                    reflectiveQuadTo(797f, 440f)
                    horizontalLineToRelative(81f)
                    quadToRelative(1f, 10f, 1.5f, 19.5f)
                    reflectiveQuadToRelative(0.5f, 20.5f)
                    quadToRelative(0f, 135f, -80.5f, 241.5f)
                    reflectiveQuadTo(590f, 865f)
                    lineToRelative(44f, 26f)
                    close()
                }
            }.build()
        }

        val hint by lazy {
            ImageVector.Builder(
                name = "Emoji_objects",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(androidx.compose.ui.graphics.Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(480f, 880f)
                    quadToRelative(-26f, 0f, -47f, -12.5f)
                    reflectiveQuadTo(400f, 834f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(320f, 754f)
                    verticalLineToRelative(-142f)
                    quadToRelative(-59f, -39f, -94.5f, -103f)
                    reflectiveQuadTo(190f, 370f)
                    quadToRelative(0f, -121f, 84.5f, -205.5f)
                    reflectiveQuadTo(480f, 80f)
                    reflectiveQuadToRelative(205.5f, 84.5f)
                    reflectiveQuadTo(770f, 370f)
                    quadToRelative(0f, 77f, -35.5f, 140f)
                    reflectiveQuadTo(640f, 612f)
                    verticalLineToRelative(142f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(560f, 834f)
                    quadToRelative(-12f, 21f, -33f, 33.5f)
                    reflectiveQuadTo(480f, 880f)
                    moveToRelative(-80f, -126f)
                    horizontalLineToRelative(160f)
                    verticalLineToRelative(-36f)
                    horizontalLineTo(400f)
                    close()
                    moveToRelative(0f, -76f)
                    horizontalLineToRelative(160f)
                    verticalLineToRelative(-38f)
                    horizontalLineTo(400f)
                    close()
                    moveToRelative(-8f, -118f)
                    horizontalLineToRelative(58f)
                    verticalLineToRelative(-108f)
                    lineToRelative(-88f, -88f)
                    lineToRelative(42f, -42f)
                    lineToRelative(76f, 76f)
                    lineToRelative(76f, -76f)
                    lineToRelative(42f, 42f)
                    lineToRelative(-88f, 88f)
                    verticalLineToRelative(108f)
                    horizontalLineToRelative(58f)
                    quadToRelative(54f, -26f, 88f, -76.5f)
                    reflectiveQuadTo(690f, 370f)
                    quadToRelative(0f, -88f, -61f, -149f)
                    reflectiveQuadToRelative(-149f, -61f)
                    reflectiveQuadToRelative(-149f, 61f)
                    reflectiveQuadToRelative(-61f, 149f)
                    quadToRelative(0f, 63f, 34f, 113.5f)
                    reflectiveQuadToRelative(88f, 76.5f)
                    moveToRelative(88f, -200f)
                }
            }.build()
        }
    }
}

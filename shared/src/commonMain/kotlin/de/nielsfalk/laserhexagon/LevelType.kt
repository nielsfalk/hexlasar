package de.nielsfalk.laserhexagon

enum class LevelType(
    vararg properties: LevelProperties,
    val infiniteX: Boolean = true,
    val infiniteY: Boolean = false
) {
    ABSOLUTE_BEGINNER(
        LevelProperties(x = 1, y = 3, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0),
        LevelProperties(x = 2, y = 2, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0),
        infiniteX = false
    ),
    EASY(
        LevelProperties(x = 2, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0),
        LevelProperties(x = 2, y = 3, sourceCount = 2, rotateObvious = true, maxPrismaCount = 0),
        infiniteX = false
    ),
    INTERMEDIATE(
        LevelProperties(x = 3, sourceCount = 2, maxPrismaCount = 1),
        LevelProperties(x = 3, sourceCount = 3, maxPrismaCount = 1),
        LevelProperties(x = 3, sourceCount = 4, maxPrismaCount = 2),
        infiniteX = false
    ),
    INTERMEDIATE_PLUS(
        LevelProperties(x = 3, y = 3, sourceCount = 2, maxPrismaCount = 0),
    ),
    INTERMEDIATE_PLUS2(
        LevelProperties(x = 3, sourceCount = 2, maxPrismaCount = 1),
        LevelProperties(x = 3, sourceCount = 3, maxPrismaCount = 1),
        LevelProperties(x = 3, sourceCount = 4, maxPrismaCount = 2),
    ),
    HARD(
        LevelProperties(x = 4, sourceCount = 1, maxPrismaCount = 3),
        LevelProperties(x = 4, sourceCount = 2, maxPrismaCount = 3),
        LevelProperties(x = 4, sourceCount = 3, maxPrismaCount = 3),
        LevelProperties(x = 4, sourceCount = 9, maxPrismaCount = 3),
        LevelProperties(x = 4, sourceCount = 10, maxPrismaCount = 3),
    ),
    HARDER(
        LevelProperties(x = 5, sourceCount = 1),
        LevelProperties(x = 5, sourceCount = 2),
        LevelProperties(x = 5, sourceCount = 3),
        LevelProperties(x = 5, sourceCount = 19),
        LevelProperties(x = 5, sourceCount = 20),
        LevelProperties(x = 6, sourceCount = 30),
        LevelProperties(x = 6, sourceCount = 35)
    ),
    INSANE(
        LevelProperties(x = 4, y = 4, sourceCount = 2, maxPrismaCount = 0),
        LevelProperties(x = 4, y = 4, sourceCount = 3, maxPrismaCount = 0),
        LevelProperties(x = 4, y = 4, sourceCount = 4, maxPrismaCount = 0),
        infiniteX = true,
        infiniteY = true
    ),
    NIGHTMARE(
        LevelProperties(x = 4, sourceCount = 1),
        LevelProperties(x = 4, sourceCount = 2),
        LevelProperties(x = 4, sourceCount = 3),
        LevelProperties(x = 4, sourceCount = 9),
        LevelProperties(x = 4, sourceCount = 10),
        infiniteX = true,
        infiniteY = true
    ),
    NIGHTMARE_PLUS(
        LevelProperties(x = 6, sourceCount = 1),
        LevelProperties(x = 6, sourceCount = 2),
        LevelProperties(x = 6, sourceCount = 3),
        LevelProperties(x = 6, sourceCount = 4),
        LevelProperties(x = 6, sourceCount = 29),
        LevelProperties(x = 6, sourceCount = 30),
        infiniteX = true,
        infiniteY = true
    ),
    NIGHTMARE_PLUS2(
        LevelProperties(x = 8, sourceCount = 1),
        LevelProperties(x = 8, sourceCount = 2),
        LevelProperties(x = 8, sourceCount = 3),
        LevelProperties(x = 8, sourceCount = 4),
        LevelProperties(x = 8, sourceCount = 4),
        LevelProperties(x = 8, sourceCount = 5),
        LevelProperties(x = 8, sourceCount = 6),
        LevelProperties(x = 8, sourceCount = 30),
        LevelProperties(x = 8, sourceCount = 40),
        infiniteX = true,
        infiniteY = true
    );

    val levelProperties = properties.toList()
}

fun LevelType.next(): LevelType =
    LevelType.entries[(ordinal + 1) % LevelType.entries.size]

data class LevelProperties(
    val x: Int = 3,
    val y: Int = x * 2,
    val sourceCount: Int = 3,
    val rotateObvious: Boolean = false,
    val maxPrismaCount: Int = 17
)
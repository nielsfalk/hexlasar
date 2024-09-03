package de.nielsfalk.laserhexagon

enum class LevelType(
    val lable: String,
    vararg properties: LevelProperties,
    val connectBorders: Boolean = false
) {
    ABSOLUTE_BEGINNER(
        "Absolute beginner",
        LevelProperties(x = 1, y = 3, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0),
        LevelProperties(x = 2, y = 2, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0)
    ),
    EASY(
        "Easy",
        LevelProperties(x = 2, sourceCount = 1, rotateObvious = true, maxPrismaCount = 0),
        LevelProperties(x = 2, y = 3, sourceCount = 2, rotateObvious = true, maxPrismaCount = 0)
    ),
    INTERMEDIATE(
        "Intermediate",
        LevelProperties(x = 3, sourceCount = 2),
        LevelProperties(x = 3, sourceCount = 3, maxPrismaCount = 1),
        LevelProperties(x = 3, sourceCount = 4, maxPrismaCount = 2)
    ),
    HARD(
        "Hard",
        LevelProperties(x = 4, sourceCount = 1),
        LevelProperties(x = 4, sourceCount = 2),
        LevelProperties(x = 4, sourceCount = 3),
        LevelProperties(x = 4, sourceCount = 9),
        LevelProperties(x = 4, sourceCount = 10),
    ),
    HARDER(
        "Harder",
        LevelProperties(x = 5, sourceCount = 1),
        LevelProperties(x = 5, sourceCount = 2),
        LevelProperties(x = 5, sourceCount = 3),
        LevelProperties(x = 5, sourceCount = 19),
        LevelProperties(x = 5, sourceCount = 20),
        LevelProperties(x = 7, sourceCount = 49),
        LevelProperties(x = 6, sourceCount = 30),
    ),
    VERY_HARD(
        "Very hard",
        LevelProperties(x = 6, sourceCount = 1),
        LevelProperties(x = 6, sourceCount = 2),
        LevelProperties(x = 6, sourceCount = 3),
        LevelProperties(x = 6, sourceCount = 20),
        LevelProperties(x = 6, sourceCount = 30),
    ),
    INSANE(
        "Insane",
        LevelProperties(x = 4, y = 4, sourceCount = 2),
        LevelProperties(x = 4, y = 4, sourceCount = 3),
        LevelProperties(x = 4, y = 4, sourceCount = 4),
        connectBorders = true
    ),
    NIGHTMARE(
        "Nightmare",
        LevelProperties(x = 4, sourceCount = 1),
        LevelProperties(x = 4, sourceCount = 2),
        LevelProperties(x = 4, sourceCount = 3),
        LevelProperties(x = 4, sourceCount = 9),
        LevelProperties(x = 4, sourceCount = 10),
        connectBorders = true
    ),
    NIGHTMARE_PLUS(
        "Nightmare +",
        LevelProperties(x = 6, sourceCount = 1),
        LevelProperties(x = 6, sourceCount = 2),
        LevelProperties(x = 6, sourceCount = 3),
        LevelProperties(x = 6, sourceCount = 4),
        LevelProperties(x = 6, sourceCount = 29),
        LevelProperties(x = 6, sourceCount = 30),
        connectBorders = true
    ),
    NIGHTMARE_PLUS2(
        "Nightmare ++",
        LevelProperties(x = 8, sourceCount = 1),
        LevelProperties(x = 8, sourceCount = 2),
        LevelProperties(x = 8, sourceCount = 3),
        LevelProperties(x = 8, sourceCount = 4),
        LevelProperties(x = 8, sourceCount = 4),
        LevelProperties(x = 8, sourceCount = 5),
        LevelProperties(x = 8, sourceCount = 6),
        LevelProperties(x = 8, sourceCount = 30),
        LevelProperties(x = 8, sourceCount = 40),
        connectBorders = true
    ),
    NIGHTMARE_PLUS3(
        "Nightmare +++",
        LevelProperties(x = 10, sourceCount = 1),
        LevelProperties(x = 10, sourceCount = 2),
        LevelProperties(x = 10, sourceCount = 3),
        LevelProperties(x = 10, sourceCount = 4),
        LevelProperties(x = 10, sourceCount = 4),
        LevelProperties(x = 10, sourceCount = 5),
        LevelProperties(x = 10, sourceCount = 6),
        LevelProperties(x = 10, sourceCount = 59),
        LevelProperties(x = 10, sourceCount = 60),
        connectBorders = true
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
    val maxPrismaCount: Int = 4
)
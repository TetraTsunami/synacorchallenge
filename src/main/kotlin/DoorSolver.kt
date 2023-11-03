val grid = arrayOf(
    arrayOf("22", "-", "9", "*"),
    arrayOf("+", "4", "-", "18"),
    arrayOf("4", "*", "11", "*"),
    arrayOf("*", "8", "-", "1"),
)

enum class OPERATION {
    NONE, ADD, SUBTRACT, MULTIPLY
}

fun main() {
    val path = doorSolverRec(0, 0, 0, OPERATION.NONE, arrayOf(), 0)
    println(path?.size)
    for (i in path!!) {
        println(i.contentToString())
    }
    println()
    var curOp = OPERATION.NONE
    var curVal = 0
    for (i in path) {
        val x = i[0]
        val y = i[1]
        when (grid[y][x]) {
            "+" -> curOp = OPERATION.ADD
            "-" -> curOp = OPERATION.SUBTRACT
            "*" -> curOp = OPERATION.MULTIPLY
            else -> {
                curVal = when (curOp) {
                    OPERATION.ADD -> (curVal + grid[y][x].toInt()) % 32768
                    OPERATION.SUBTRACT -> Math.floorMod(curVal - grid[y][x].toInt(), 32768)
                    OPERATION.MULTIPLY -> (curVal * grid[y][x].toInt()) % 32768
                    else -> 22
                }
            }
        }
        println("($y, $x) = ${grid[y][x]}; $curVal ($curOp)")
    }
}

fun doorSolverRec(x: Int, y: Int, value: Int, operation: OPERATION, path: Array<Array<Int>>, i: Int ): Array<Array<Int>>? {
    // println("$x, $y = $value ($operation)")
    // base case: back at pedistal with non-zero value
    if (x == 0 && y == 0 && value != 0) return null
    // base case: more than 15 steps
    if (i > 15) return null
    // base case: value more than 32767
    if (value > 32767) return null
    // do operations depending on tile
    var curOp = operation
    var curVal = value
    val curPath = path.copyOf().plus(arrayOf(x, y))
    when (grid[y][x]) {
        "+" -> curOp = OPERATION.ADD
        "-" -> curOp = OPERATION.SUBTRACT
        "*" -> curOp = OPERATION.MULTIPLY
        else -> {
            // it is a number, use current operation
            curVal = when (curOp) {
                OPERATION.ADD -> (curVal + grid[y][x].toInt()) % 32768
                OPERATION.SUBTRACT -> Math.floorMod(curVal - grid[y][x].toInt(), 32768)
                OPERATION.MULTIPLY -> (curVal * grid[y][x].toInt()) % 32768
                else -> 22
            }
        }
    }
    // base case: at door with value 30
    if (x == 3 && y == 3 && curVal == 30) return curPath
    // base case: at door with value != 30
    if (x == 3 && y == 3) return null

    // try all 4 directions
    val newPaths = mutableListOf<Array<Array<Int>>>()
    if (x > 0) {
        val newPath = doorSolverRec(x - 1, y, curVal, curOp, curPath, i + 1)
        if (newPath != null) newPaths.add(newPath)
    }
    if (x < 3) {
        val newPath = doorSolverRec(x + 1, y, curVal, curOp, curPath, i + 1)
        if (newPath != null) newPaths.add(newPath)
    }
    if (y > 0) {
        val newPath = doorSolverRec(x, y - 1, curVal, curOp, curPath, i + 1)
        if (newPath != null) newPaths.add(newPath)
    }
    if (y < 3) {
        val newPath = doorSolverRec(x, y + 1, curVal, curOp, curPath, i + 1)
        if (newPath != null) newPaths.add(newPath)
    }
    // return the shortest path
    return newPaths.minByOrNull { it.size }
}

// notably only works if you run the VM with -Xss8m (big stack)
fun main(args: Array<String>) {
    val desiredReturn = 6
    // given the code, we want iterative to return 6
    for (i in 0..32767) {
        println(i)
        if (teleporterCalibration(4, 1, i) == desiredReturn) {
            println("Found $i")
            break
        }
    }
    // for (a in 0..5) {
    //     for (b in 0..5) {
    //         for (c in 0..5) {
    //             println("$a, $b, $c: ${func1(a, b, c)}")
    //         }
    //         println()
    //     }
    // }
}
/*
6027:
if a != 0: go to 6035
a = b + 1
return

6035:
if b != 0: go to 6048
a = a - 1
b = c
call 6027
return

6048:
push a
b = b - 1
call 6027
b = a
a = pop
a = a - 1
call 6027
return
*/

fun teleporterCalibration(a: Int, b: Int, index: Int): Int {
    var a = a
    var b = b
    when (a) {
        0 -> return (b + 1) % 32768
        1 -> return (b + 1 + index) % 32768
        2 -> return (b + 1 + index * (b + 2)) % 32768
    }

    if (b != 0) {
        b = teleporterCalibration(a, (b - 1) % 32768, index)
        return teleporterCalibration((a - 1) % 32768, b, index)
    }
    return teleporterCalibration((a - 1) % 32768, index, index)
}

import java.util.Stack
fun main(args: Array<String>) {
    val desiredReturn = 6
    // given the code, we want iterative to return 6
    for (i in 0..32767) {
        println("$i: ${func1(4, 1, i)}")
        if (func1(4, 1, i) == desiredReturn) {
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

var stack = Stack<Int>()
fun func1(a: Int, b: Int, c: Int): Int {
    var a = a
    var b = b
    when (a) {
        1 -> return (b + 1 + c) % 32768
        2 -> return (b + 1 + c * (b + 2)) % 32768
    }
    if (a != 0) {
        if (b != 0) {
            val keep = a
            a = func1(a, (b - 1) % 32768, c)
            b = a
            a = (keep - 1) % 32768
            return func1(a, b, c)
        }
        a = (a - 1) % 32768
        b = c
        return func1(a, b, c)
    }
    return (b + 1) % 32768
}

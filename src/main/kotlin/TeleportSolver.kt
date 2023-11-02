import java.util.Stack
fun main(args: Array<String>) {
    val desiredReturn = 6
    // given the code, we want iterative to return 6
    for (i in 0..32767) {
        // if (iterative(4, 1, i) == desiredReturn) {
        //     println(i)
        // }
        println("$i: ${func1(4, 1, i)}")
    }
}

fun iterative(reg1: Int, reg2: Int, reg3: Int): Int {
    var a = reg1
    var b = reg2
    while (a != 0) {
        if (b != 0) {
            b = (b - 1) % 32768
        } else {
            a -= 1
            b = reg3
        }
        // println("$reg1 $reg2 $reg3")
    }
    return b + 1 % 32768
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
    var bm = b
    if (a != 0) {
        bm = func2(a, bm, c)
    }
    return bm + 1
}

fun func2(a:Int, b: Int, c: Int): Int {
    var am = a
    var bm = b
    if (bm != 0) func3(am, bm, c)
    am--
    bm = c
    return func1(am, bm, c)
}

fun func3(a: Int, b: Int, c: Int): Int {
    var am = a
    var bm = b
    stack.push(a)
    bm--
    am = func1(am, bm, c)
    bm = am
    am = stack.pop()
    am--
    return func1(am, bm, c)
}

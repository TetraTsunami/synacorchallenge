import java.util.*

// 0x178b: jt 0x8000 (32768) 0x1793 (6035)
// 0x178e: add 0x8000 (32768) 0x8001 (32769) 0x0001 (1)
// 0x1792: ret
// 0x1793: ???

// 0x17a8: set 0x8001 (32769) 0x8000 (32768)
// 0x17ab: pop 0x8000 (32768)
// 0x17ad: add 0x8000 (32768) 0x8000 (32768) 0x7fff (32767)

// 0x17b1: call 0x178b (6027)
// 0x17b3: ret

// ------------------
// initial caller
// noop
// 0x156b (5483): set 0x8000 (32768) 0x0004 (4)
// 0x156e (5486): set 0x8001 (32769) 0x0001 (1)
// 0x1571 (5489): call 0x178b (6027)

// func 1
// 0x178b (6027): jt 0x8000 (32768) 0x1793 (6035)
// 0x178e (6030): add 0x8000 (32768) 0x8001 (32769) 0x0001 (1)
// 0x1792 (6034): ret

// func 2
// 0x1793 (6035): jt 0x8001 (32769) 0x17a0 (6048)
// 0x1796 (6038): add 0x8000 (32768) 0x8000 (32768) 0x7fff (32767)
// 0x179a (6042): set 0x8001 (32769) 0x8007 (32775)
// 0x179d (6045): call 0x178b (6027)
// 0x179f (6047): ret

// func 3
// 0x17a0 (6048): push 0x8000 (32768)
// 0x17a2 (6050): add 0x8001 (32769) 0x8001 (32769) 0x7fff (32767)
// 0x17a6 (6054): call 0x178b (6027)

// func 4
// 0x17a8 (6056): set 0x8001 (32769) 0x8000 (32768)
// 0x17ab (6059): pop 0x8000 (32768)
// 0x17ad (6061): add 0x8000 (32768) 0x8000 (32768) 0x7fff (32767)
// 0x17b1 (6065): call 0x178b (6027)
// 0x17b3 (6067): ret

// func 5
// 0x17b4 (6068): mod 0x0054 (84) 0x0065 (101) 0x0073 (115)
// error

var regs = Array<Int>(8) {0}
var stack = Stack<Int>()
val input = 1

fun main(args: Array<String>) {
    regs[0] = 4
    regs[1] = 1
    regs[7] = input
    println(func1(regs[0], regs[1], regs[7]))
    println(regs.contentToString())
}

fun func1(reg1: Int, reg2: Int, reg3: Int): Int {
    return if (reg1 != 0) func2(reg1, reg2, reg3)
    else (reg2 + 1) % 32768
}

fun func2(reg1: Int, reg2: Int, reg3: Int): Int {
    return if (reg2 != 0) func1(reg1 - 1, reg2, reg3)
    else func1(reg1 + 1, reg3, reg3)
}

// fun func3(reg1: Int, reg2: Int): Int {
//     return func1(reg1 - 1, reg2)
// }

// fun loopingFunc(reg1: Int, reg2: Int): Int {
//     while(true) {
//
//     }
// }

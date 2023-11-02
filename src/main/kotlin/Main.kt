import java.io.File
import java.io.RandomAccessFile
import java.util.*
import java.util.regex.Pattern

val opArgs = mapOf(
    0 to 0,
    1 to 2,
    2 to 1,
    3 to 1,
    4 to 3,
    5 to 3,
    6 to 1,
    7 to 2,
    8 to 2,
    9 to 3,
    10 to 3,
    11 to 3,
    12 to 3,
    13 to 3,
    14 to 2,
    15 to 2,
    16 to 2,
    17 to 1,
    18 to 0,
    19 to 1,
    20 to 1,
    21 to 0
)
val opNames = mapOf(
    0 to "halt",
    1 to "set",
    2 to "push",
    3 to "pop",
    4 to "eq",
    5 to "gt",
    6 to "jmp",
    7 to "jt",
    8 to "jf",
    9 to "add",
    10 to "mult",
    11 to "mod",
    12 to "and",
    13 to "or",
    14 to "not",
    15 to "rmem",
    16 to "wmem",
    17 to "call",
    18 to "ret",
    19 to "out",
    20 to "in",
    21 to "noop"
)
fun main(args: Array<String>) {
    Execution(loadChallenge()).run()
}

fun challengeFromString(input: String): IntArray {
    val arr1 = input.split(",").map { it.toInt() }.toIntArray()
    val arr2 = IntArray(32768 - arr1.size)
    return arr1 + arr2
}

fun loadPatches(): MutableMap<Int, Int> {
    val patchSource = File("patches.txt").readLines()
    val patches = mutableMapOf<Int, Int>()
    val comments = Pattern.compile("#.*")
    for (patch in patchSource) {
        val noComments = comments.matcher(patch).replaceAll("").trim()
        if (noComments.isEmpty()) continue
        val (addr, value) = noComments.split(":").map { it.trim().toInt() }
        patches[addr] = value
    }
    return patches
}
fun loadChallenge(): IntArray {
    val challenge = RandomAccessFile("challenge.bin", "r")
    val memory = IntArray(32768)
    var i = 0
    while (challenge.filePointer < challenge.length()) {
        memory[i] = challenge.readUnsignedByte() + challenge.readUnsignedByte() * 256
        i++
    }
    return memory
}

class Register(var value: Int)
class Argument(val value: Int, private val registers: Array<Register>) {
    fun get(): Int {
        return when (value) {
            in 0..32767 -> value
            in 32768..32775 -> registers[value - 32768].value
            else -> throw IllegalArgumentException("Unknown argument $value")
        }
    }

    fun regNum(): Int {
        return when (value) {
            in 0..32767 -> -1
            in 32768..32775 -> value - 32768
            else -> throw IllegalArgumentException("Unknown argument $value")
        }
    }

    fun set(input: Argument) {
        if (this.value in 32768..32775) {
            registers[this.value - 32768].value = input.get() % 32768
        } else {
            throw IllegalArgumentException("Unknown argument $input")
        }
    }

    fun set(input: Int) {
        if (this.value in 32768..32775) {
            registers[this.value - 32768].value = input % 32768
        } else {
            throw IllegalArgumentException("Unknown argument $input")
        }
    }
}

class Execution(private val mem: IntArray) {
    private val registers = Array<Register>(8) { Register(0) }
    private val stack = Stack<Int>()
    private var ip = 0
    private val scanner = Scanner(System.`in`)
    private val inputCache = LinkedList<Char>()
    private val replayCache = LinkedList<String>()
    private val outputCache = LinkedList<Char>()
    private var stepping = false
    private var printExec = false
    private var stepWait = -1
    private val disassembler = Disassembler(mem)
    fun run() {
        while (ip < mem.size) {
            val opcode = mem[ip]
            val a = Argument(mem[ip + 1], registers)
            val b = Argument(mem[ip + 2], registers)
            val c = Argument(mem[ip + 3], registers)
            if (stepping) {
                print(disassembler.formatInstruction(ip, registers) + " ")
                handleInput()
            }
            if (printExec && !stepping) println(disassembler.formatInstruction(ip, registers))
            if (stepWait != -1) {
                if (--stepWait <= 0) {
                    printExec = false
                    stepping = true
                    stepWait = -1
                }
            }
            if (mem[ip] != opcode) continue
            val delta = when (opcode) {
                // halt: stop execution and terminate the program
                0 -> return
                // set: set register <a> to the value of <b>
                1 -> {
                    a.set(b.get())
                    3
                }
                // push: push <a> onto the stack
                2 -> {
                    stack.push(a.get())
                    2
                }
                // pop: remove the top element from the stack and write it into <a>; empty stack = error
                3 -> {
                    a.set(stack.pop())
                    2
                }
                // eq: set <a> to 1 if <b> is equal to <c>; set it to 0 otherwise
                4 -> {
                    a.set(if (b.get() == c.get()) 1 else 0)
                    4
                }
                // gt: set <a> to 1 if <b> is greater than <c>; set it to 0 otherwise
                5 -> {
                    a.set(if (b.get() > c.get()) 1 else 0)
                    4
                }
                // jmp: jump to <a>
                6 -> {
                    // println("$ip: jmp ${a.get()}")
                    ip = a.get()
                    0
                }
                // jt: if <a> is nonzero, jump to <b>
                7 -> {
                    // println("$ip: jt ${a.get()} ${b.get()}")
                    if (a.get() != 0) ip = b.get()
                    if (a.get() != 0) 0 else 3
                }
                // jf: if <a> is zero, jump to <b>
                8 -> {
                    // println("$ip: jf ${a.get()} ${b.get()}")
                    if (a.get() == 0) ip = b.get()
                    if (a.get() == 0) 0 else 3
                }
                // add: assign into <a> the sum of <b> and <c> (modulo 32768)
                9 -> {
                    a.set(b.get() + c.get())
                    4
                }
                // mult: store into <a> the product of <b> and <c> (modulo 32768)
                10 -> {
                    a.set(b.get() * c.get())
                    4
                }
                // mod: store into <a> the remainder of <b> divided by <c>
                11 -> {
                    a.set(b.get() % c.get())
                    4
                }
                // and: stores into <a> the bitwise and of <b> and <c>
                12 -> {
                    a.set(b.get() and c.get())
                    4
                }
                // or: stores into <a> the bitwise or of <b> and <c>
                13 -> {
                    a.set(b.get() or c.get())
                    4
                }
                // not: stores 15-bit bitwise inverse of <b> in <a>
                14 -> {
                    a.set(b.get().inv() and 0x7FFF)
                    3
                }
                // rmem: read memory at address <b> and write it to <a>
                15 -> {
                    a.set(mem[b.get()])
                    3
                }
                // wmem: write the value from <b> into memory at address <a>
                16 -> {
                    mem[a.get()] = b.get()
                    3
                }
                // call: write the address of the next instruction to the stack and jump to <a>
                17 -> {
                    // println("$ip: call ${a.get()}")
                    stack.push(ip + 2)
                    ip = a.get()
                    0
                }
                // ret: remove the top element from the stack and jump to it; empty stack = halt
                18 -> {
                    if (stack.isEmpty()) return
                    ip = stack.pop()
                    0
                }
                // out: write the character represented by ascii code <a> to the terminal
                19 -> {
                    print(a.get().toChar())
                    if (stepping || printExec) {
                        outputCache.add(a.get().toChar())
                    }
                    2
                }
                // in: read a character from the terminal and write its ascii code to <a>;
                20 -> {
                    handleInput(a)
                    // we may have jumped :)
                    if (mem[ip] != opcode) continue
                    2
                }
                // noop: no operation
                21 -> 1
                else -> throw IllegalArgumentException("Unknown opcode $opcode")
            }
            if (delta != 0) ip += delta
        }
    }

    fun dump() {
        println("IP: $ip")
        println("Registers:")
        for ((i, r) in registers.withIndex()) {
            println("  $i: ${r.value}")
        }
        println("Stack:")
        for (i in stack) {
            println("  $i")
        }
    }

    private fun patch() {
        val patches = loadPatches()
        for ((key, value) in patches) {
            mem[key] = value
        }
    }

    private fun handleInput() {
        when (val input = scanner.nextLine()) {
            "!s" -> {
                println("Saving replay...")
                File("replay.txt").writeText(replayCache.joinToString("\n"))
            }

            "!r" -> {
                println("Replaying...")
                replayCache.addAll(File("replay.txt").readLines())
                inputCache.addAll(File("replay.txt").readText().toCharArray().toList())
            }

            "!set" -> {
                print("Register: ")
                val reg = scanner.nextInt()
                print("Value: ")
                val value = scanner.nextInt()
                registers[reg].value = value
            }

            "!jmp" -> {
                print("IP: ")
                ip = scanner.nextInt()
            }

            "!dump" -> {
                dump()
            }

            "!step" -> {
                stepping = !stepping
                printExec = false
                println("Toggled stepping to $stepping")
                if (outputCache.isNotEmpty()) {
                    println("Output while stepping:")
                    println(outputCache.joinToString(""))
                    outputCache.clear()
                }
            }

            "!stepwait" -> {
                print("How many instructions until step: ")
                stepWait = scanner.nextInt()
                printExec = true
                stepping = false
            }

            "!next" -> {
                print("How long: ")
                val howLong = scanner.nextInt()
                disassembler.run(howLong, ip)
            }

            "!look" -> {
                print("What address: ")
                val addr = scanner.nextInt()
                print("How long: ")
                val howLong = scanner.nextInt()
                disassembler.run(howLong, addr)
            }

            "!patch" -> {
                println("Patching...")
                patch()
            }

            else -> {
                replayCache.add(input)
                inputCache.addAll(input.toCharArray().toList())
                if (input.isNotEmpty()) inputCache.add('\n')
            }
        }
    }

    private fun handleInput(a: Argument) {
        while (inputCache.isEmpty()) this.handleInput()
        a.set(inputCache.removeFirst().code)
    }
}


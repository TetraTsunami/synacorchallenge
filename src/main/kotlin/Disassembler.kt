import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val dissasembly = Disassembler(loadChallenge()).run()
    File("dissasembly.txt").writeText(dissasembly.joinToString("\n"))
}
class Disassembler(val mem: IntArray) {
    val charRepCol = 40

    fun print(howLong: Int, startingPointer: Int) {
        println(run(howLong, startingPointer).joinToString("\n"))
    }
    fun run(howLong: Int, startingPointer: Int): LinkedList<String> {
        var ip = startingPointer
        val record = LinkedList<String>()
        var i = 0
        while (ip < mem.size && (howLong == 0 || i < howLong)) {
            try {
                record.add(formatInstruction(ip))
                val opCode = mem[ip]
                ip += opArgs[opCode]!! + 1
                i++
            }
            catch (e: Exception) {
                i++
                ip++
            }
        }
        return record
    }

    fun formatInstruction(ip: Int): String {
        val opCode = mem[ip]
        if (opCode == 19) {
            var charRep = if (mem[ip + 1] < 255) mem[ip + 1].toChar() else ' '
            charRep = if (charRep == '\n') ' ' else charRep
            val strRep = "${toBits(ip)}: ${opNames[opCode]} ${toBits(mem[ip + 1])}"
            return "${strRep.padEnd(charRepCol)}'$charRep'"

        }
        return when (opArgs[opCode]!!) {
            0 -> "${toBits(ip)}: ${opNames[opCode]}"
            1 -> "${toBits(ip)}: ${opNames[opCode]} ${toBits(mem[ip + 1])}"
            2 -> "${toBits(ip)}: ${opNames[opCode]} ${toBits(mem[ip + 1])} ${toBits(mem[ip + 2])}"
            3 -> "${toBits(ip)}: ${opNames[opCode]} ${toBits(mem[ip + 1])} ${toBits(mem[ip + 2])} ${
                toBits(
                    mem[ip + 3]
                )
            }"

            else -> throw IllegalArgumentException("Unknown opcode $opCode")
        }
    }

    private fun toBits(input: Int): String {
        val bits: ByteArray = byteArrayOf((input / 256).toByte(), (input % 256).toByte())
        return "0x${bits.joinToString("") { "%02x".format(it) }} ($input)"
    }

    fun run(): LinkedList<String> {
        return run(0, 0)
    }
}

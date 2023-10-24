import kotlin.math.pow

val red = 2
val corroded = 3
val shiny = 5
val concave = 7
val blue = 9

fun main(args: Array<String>) {
    val coins = listOf(red, corroded, shiny, concave, blue)
    for (p in coins.permutations()) {
        if (p[0] + p[1] * p[2].toDouble().pow(2.0) + p[3].toDouble().pow(3.0) - p[4] == 399.0)
            println(p.joinToString(","))
    }

}

fun <T> List<T>.permutations(): List<List<T>> = if(isEmpty()) listOf(emptyList()) else  mutableListOf<List<T>>().also{result ->
    for(i in this.indices){
        (this - this[i]).permutations().forEach{
            result.add(it + this[i])
        }
    }
}

package fr.imacaron.kaamelott.gif

fun main() {
    val ep1 = Episode(1, 1)
//    println(ep1.info.sceneChange)
//    val start = ep1.info.sceneChange[0]
//    val duration = ep1.getSceneDuration(1)
//    println("start: ${ep1.info.sceneChange[0]}, end = ${ep1.info.sceneChange[1]}, duration = $duration")
//    val file = ep1.createScene(start, duration)
//    println(file)
//    println(ep1.getTextLength(file, "Un texte vraiment trop long pour que ça dépasse"))
//    ep1.writeText(file, listOf("La femelle lièvre", "La hase"), "le_lievre")
    for( i in 0..<ep1.info.sceneChange.size) {
        ep1.createMeme("meme$i", i, "Oui, mais j'aime pas les harengs")
    }
}
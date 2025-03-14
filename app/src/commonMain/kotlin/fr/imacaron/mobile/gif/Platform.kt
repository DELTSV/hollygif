package fr.imacaron.mobile.gif

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
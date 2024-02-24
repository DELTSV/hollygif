package fr.imacaron.kaamelott.gif

class NotEnoughTimeException: Exception()

class ErrorWhileDrawingText: Exception()

sealed class KaamelottGifException(message: String, cause: Exception? = null): Exception(message, cause)

class NotFoundException(message: String): KaamelottGifException(message)

class CannotCreate(message: String): KaamelottGifException(message)
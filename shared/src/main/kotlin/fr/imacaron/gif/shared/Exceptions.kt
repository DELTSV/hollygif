package fr.imacaron.gif.shared

class NotEnoughTimeException: Exception()

class ErrorWhileDrawingText: Exception()

class InvalidParamException: Exception()

sealed class KaamelottGifException(message: String, cause: Exception? = null): Exception(message, cause)

class NotFoundException(message: String): KaamelottGifException(message)

class CannotCreate(message: String): KaamelottGifException(message)
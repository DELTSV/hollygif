package fr.imacaron.gif.shared.enums


/**
 * The interface that any enum to serialize (in DB and in JSON) must implement)
 * @author Denis TURBIEZ
 * @param T The type of the value parameter
 */
interface TEnum<T> {

    /**
     * The property that hold the value of the enum of type [T]
     * @author Denis TURBIEZ
     */
    val value: T

    /**
     * A function that return the actual enum value for the [value]
     * @author Denis TURBIEZ
     * @param value The value of the enum instance we need
     * @return The enum instance that match with the [value]
     */
    fun getValue(value: T): TEnum<T>
}
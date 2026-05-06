// port-lint: source src/kv/value.rs
package io.github.kotlinmania.log.kv

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * A type that can be converted into a [Value].
 */
public fun interface ToValue {
    /**
     * Perform the conversion.
     */
    public fun toValue(): Value
}

/**
 * A value in a key-value.
 *
 * Values are an anonymous bag containing some structured datum.
 *
 * # Capturing values
 *
 * There are a few ways to capture a value:
 *
 * - Using the `Value.from*` methods.
 * - Using the [ToValue] functional interface.
 * - Using the standard Kotlin conversion APIs.
 *
 * ## Using the `Value.from*` methods
 *
 * `Value` offers a few constructor methods that capture values of different kinds.
 *
 * ```
 * val value = Value.fromDebug(42)
 *
 * kotlin.test.assertEquals(null, value.toI64())
 * ```
 *
 * ## Using the [ToValue] functional interface
 *
 * The [ToValue] interface can be used to capture values generically.
 * It's the bound used by `Source`.
 *
 * ```
 * val value = 42.toValue()
 *
 * kotlin.test.assertEquals(42, value.toI64())
 * ```
 *
 * ## Using the standard Kotlin conversion APIs
 *
 * Standard types that can be converted into a [Value] also provide `toValue`.
 *
 * ```
 * val value = 42.toValue()
 *
 * kotlin.test.assertEquals(42, value.toI64())
 * ```
 *
 * # Data model
 *
 * Values can hold one of a number of types:
 *
 * - **Null:** The absence of any other meaningful value. Note that
 *   `Value.nullValue()` is not the same as `null`. The former is
 *   `null` while the latter is `undefined`. This is important to be
 *   able to tell the difference between a key-value that was logged,
 *   but its value was empty (`Value.nullValue()`) and a key-value
 *   that was never logged at all (`null`).
 * - **Strings:** `String`, `Char`.
 * - **Booleans:** `Boolean`.
 * - **Integers:** `UByte`-`ULong`, `Byte`-`Long`.
 * - **Floating point numbers:** `Float`-`Double`.
 */
public class Value internal constructor(
    internal val inner: ValueInner.Inner,
) : ToValue {
    public companion object {
        /**
         * Get a value from a type implementing [ToValue].
         */
        public fun <T : ToValue> fromAny(value: T): Value {
            return value.toValue()
        }

        /**
         * Get a value from a type implementing debug formatting.
         */
        public fun fromDebug(value: Any?): Value {
            return Value(ValueInner.Inner.fromDebug(value))
        }

        /**
         * Get a value from a type implementing display formatting.
         */
        public fun fromDisplay(value: Any?): Value {
            return Value(ValueInner.Inner.fromDisplay(value))
        }

        /**
         * Get a value from a dynamic debug value.
         */
        public fun fromDynDebug(value: Any?): Value {
            return Value(ValueInner.Inner.fromDynDebug(value))
        }

        /**
         * Get a value from a dynamic display value.
         */
        public fun fromDynDisplay(value: Any?): Value {
            return Value(ValueInner.Inner.fromDynDisplay(value))
        }

        /**
         * Get a `null` value.
         */
        public fun nullValue(): Value {
            return Value(ValueInner.Inner.empty())
        }

        internal fun fromInner(value: ValueInner.Inner): Value {
            return Value(value)
        }
    }

    override fun toValue(): Value = Value(inner)

    /**
     * Inspect this value using a simple visitor.
     */
    public fun visit(visitor: VisitValue): Result<Unit> {
        return ValueInner.visit(inner, visitor)
    }

    /**
     * Try convert this value into a `ULong`.
     */
    public fun toU64(): ULong? = inner.toU64()

    /**
     * Try convert this value into a `Long`.
     */
    public fun toI64(): Long? = inner.toI64()

    /**
     * Try convert this value into a big unsigned integer.
     */
    public fun toU128(): BigInteger? = inner.toU128()

    /**
     * Try convert this value into a big signed integer.
     */
    public fun toI128(): BigInteger? = inner.toI128()

    /**
     * Try convert this value into a `Double`.
     */
    public fun toF64(): Double? = inner.toF64()

    /**
     * Try convert this value into a `Char`.
     */
    public fun toChar(): Char? = inner.toChar()

    /**
     * Try convert this value into a `Boolean`.
     */
    public fun toBool(): Boolean? = inner.toBool()

    /**
     * Try to convert this value into a borrowed string.
     */
    public fun toBorrowedStr(): String? = inner.toBorrowedStr()

    public override fun toString(): String = ValueInner.formatDisplay(inner)
}

/**
 * A visitor for a [Value].
 *
 * Also see [Value]'s documentation on serialization. Value visitors are a simple alternative
 * to a more fully-featured serialization framework like `serde` or `sval`. A value visitor
 * can differentiate primitive types through methods like [visitBool] and [visitStr], but more
 * complex types like maps and sequences will fallthrough to [visitAny].
 */
public interface VisitValue {
    /**
     * Visit a [Value].
     *
     * This is the only required method on [VisitValue] and acts as a fallback for any
     * more specific methods that aren't overridden.
     */
    public fun visitAny(value: Value): Result<Unit>

    /**
     * Visit an empty value.
     */
    public fun visitNull(): Result<Unit> = visitAny(Value.nullValue())

    /**
     * Visit an unsigned integer.
     */
    public fun visitU64(value: ULong): Result<Unit> = visitAny(value.toValue())

    /**
     * Visit a signed integer.
     */
    public fun visitI64(value: Long): Result<Unit> = visitAny(value.toValue())

    /**
     * Visit a big unsigned integer.
     */
    public fun visitU128(value: BigInteger): Result<Unit> = visitAny(value.toU128Value())

    /**
     * Visit a big signed integer.
     */
    public fun visitI128(value: BigInteger): Result<Unit> = visitAny(value.toI128Value())

    /**
     * Visit a floating point.
     */
    public fun visitF64(value: Double): Result<Unit> = visitAny(value.toValue())

    /**
     * Visit a boolean.
     */
    public fun visitBool(value: Boolean): Result<Unit> = visitAny(value.toValue())

    /**
     * Visit a string.
     */
    public fun visitStr(value: String): Result<Unit> = visitAny(value.toValue())

    /**
     * Visit a string.
     */
    public fun visitBorrowedStr(value: String): Result<Unit> = visitStr(value)

    /**
     * Visit a Unicode character.
     */
    public fun visitChar(value: Char): Result<Unit> = visitStr(value.toString())
}

internal object ValueInner {
    internal sealed interface Inner {
        data object None : Inner

        data class Bool(val value: Boolean) : Inner

        data class Str(val value: String) : Inner

        data class CharValue(val value: Char) : Inner

        data class I64(val value: Long) : Inner

        data class U64(val value: ULong) : Inner

        data class F64(val value: Double) : Inner

        data class I128(val value: BigInteger) : Inner

        data class U128(val value: BigInteger) : Inner

        data class Debug(val value: Any?) : Inner

        data class Display(val value: Any?) : Inner

        companion object {
            fun fromDebug(value: Any?): Inner = Debug(value)

            fun fromDisplay(value: Any?): Inner = Display(value)

            fun fromDynDebug(value: Any?): Inner = Debug(value)

            fun fromDynDisplay(value: Any?): Inner = Display(value)

            fun empty(): Inner = None
        }

        fun toBool(): Boolean? = when (this) {
            is Bool -> value
            else -> null
        }

        fun toChar(): Char? = when (this) {
            is CharValue -> value
            else -> null
        }

        fun toF64(): Double? =
            when (this) {
                is F64 -> value
                is I64 -> {
                    val intValue = value.toInt()
                    if (intValue.toLong() != value) null else intValue.toDouble()
                }
                is U64 -> {
                    val uintValue = value.toUInt()
                    if (uintValue.toULong() != value) null else uintValue.toDouble()
                }
                is I128 -> {
                    val intValue = tryToInt(value)
                    if (intValue == null) null else intValue.toDouble()
                }
                is U128 -> {
                    val uintValue = tryToUInt(value)
                    if (uintValue == null) null else uintValue.toDouble()
                }
                else -> null
            }

        fun toI64(): Long? =
            when (this) {
                is I64 -> value
                is U64 -> value.toLong().takeIf { it >= 0 && it.toULong() == value }
                is I128 -> tryToLong(value)
                is U128 -> tryToULong(value)?.toLong()
                else -> null
            }

        fun toU64(): ULong? =
            when (this) {
                is U64 -> value
                is I64 -> value.toULong().takeIf { value >= 0 && it.toLong() == value }
                is I128 -> tryToLong(value)?.toULong()
                is U128 -> tryToULong(value)
                else -> null
            }

        fun toU128(): BigInteger? =
            when (this) {
                is U128 -> value
                is I64 -> if (value >= 0) BigInteger.fromLong(value) else null
                is U64 -> BigInteger.fromULong(value)
                is I128 -> if (value >= BigInteger.ZERO) value else null
                else -> null
            }

        fun toI128(): BigInteger? =
            when (this) {
                is I128 -> value
                is I64 -> BigInteger.fromLong(value)
                is U64 -> BigInteger.fromULong(value)
                is U128 -> value
                else -> null
            }

        fun toBorrowedStr(): String? = when (this) {
            is Str -> value
            else -> null
        }
    }

    fun formatDisplay(inner: Inner): String =
        when (inner) {
            Inner.None -> "None"
            is Inner.Bool -> inner.value.toString()
            is Inner.Str -> inner.value
            is Inner.CharValue -> inner.value.toString()
            is Inner.I64 -> inner.value.toString()
            is Inner.U64 -> inner.value.toString()
            is Inner.F64 -> inner.value.toString()
            is Inner.I128 -> inner.value.toString()
            is Inner.U128 -> inner.value.toString()
            is Inner.Debug -> inner.value.toString()
            is Inner.Display -> inner.value.toString()
        }

    fun visit(inner: Inner, visitor: VisitValue): Result<Unit> {
        return when (inner) {
            Inner.None -> visitor.visitNull()
            is Inner.Bool -> visitor.visitBool(inner.value)
            is Inner.Str -> visitor.visitBorrowedStr(inner.value)
            is Inner.CharValue -> visitor.visitChar(inner.value)
            is Inner.I64 -> visitor.visitI64(inner.value)
            is Inner.U64 -> visitor.visitU64(inner.value)
            is Inner.F64 -> visitor.visitF64(inner.value)
            is Inner.I128 -> visitor.visitI128(inner.value)
            is Inner.U128 -> visitor.visitU128(inner.value)
            is Inner.Debug -> visitor.visitAny(Value.fromDynDebug(inner.value))
            is Inner.Display -> visitor.visitAny(Value.fromDynDisplay(inner.value))
        }
    }

    private fun tryToLong(value: BigInteger): Long? {
        val stringValue = value.toString()
        val longValue = stringValue.toLongOrNull() ?: return null
        return if (BigInteger.fromLong(longValue) == value) longValue else null
    }

    private fun tryToULong(value: BigInteger): ULong? {
        if (value < BigInteger.ZERO) return null
        val stringValue = value.toString()
        val uLongValue = stringValue.toULongOrNull() ?: return null
        return if (BigInteger.fromULong(uLongValue) == value) uLongValue else null
    }

    private fun tryToInt(value: BigInteger): Int? {
        val stringValue = value.toString()
        val intValue = stringValue.toIntOrNull() ?: return null
        return if (BigInteger.fromInt(intValue) == value) intValue else null
    }

    private fun tryToUInt(value: BigInteger): UInt? {
        if (value < BigInteger.ZERO) return null
        val stringValue = value.toString()
        val uIntValue = stringValue.toUIntOrNull() ?: return null
        return if (BigInteger.fromUInt(uIntValue) == value) uIntValue else null
    }
}

public fun String.toValue(): Value = Value.fromInner(ValueInner.Inner.Str(this))

public fun Char.toValue(): Value = Value.fromInner(ValueInner.Inner.CharValue(this))

public fun Boolean.toValue(): Value = Value.fromInner(ValueInner.Inner.Bool(this))

public fun Double.toValue(): Value = Value.fromInner(ValueInner.Inner.F64(this))

public fun Float.toValue(): Value = Value.fromInner(ValueInner.Inner.F64(this.toDouble()))

public fun Long.toValue(): Value = Value.fromInner(ValueInner.Inner.I64(this))

public fun Int.toValue(): Value = this.toLong().toValue()

public fun Short.toValue(): Value = this.toLong().toValue()

public fun Byte.toValue(): Value = this.toLong().toValue()

public fun ULong.toValue(): Value = Value.fromInner(ValueInner.Inner.U64(this))

public fun UInt.toValue(): Value = this.toULong().toValue()

public fun UShort.toValue(): Value = this.toULong().toValue()

public fun UByte.toValue(): Value = this.toULong().toValue()

public fun BigInteger.toI128Value(): Value = Value.fromInner(ValueInner.Inner.I128(this))

public fun BigInteger.toU128Value(): Value = Value.fromInner(ValueInner.Inner.U128(this))

public fun <T> T?.toValue(toValue: (T) -> Value): Value = if (this == null) Value.nullValue() else toValue(this)

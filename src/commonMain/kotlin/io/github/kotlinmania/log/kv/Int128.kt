// port-lint: ignore — Kotlin-only fixed-width 128-bit integer wrappers. Rust has native u128/i128; this file fills that gap so the kv u128/i128 surface in value.rs has a portable Kotlin counterpart without pulling an arbitrary-precision big-integer dependency that isn't published for every Kotlin Multiplatform target.
package io.github.kotlinmania.log.kv

/**
 * Immutable 128-bit unsigned integer used for the [Value] u128 arm.
 *
 * Stored as two [ULong] limbs ([hi], [lo]) interpreted little-endian by limb:
 * the logical value is `hi * 2^64 + lo`. Implements only the operations the
 * kv [Value] surface needs — construction from smaller integers, comparison
 * against [ZERO], equality, and overflow-checked truncating conversions back
 * to native widths.
 */
public class UInt128 internal constructor(
    public val hi: ULong,
    public val lo: ULong,
) : Comparable<UInt128> {
    public companion object {
        public val ZERO: UInt128 = UInt128(0u, 0u)

        public fun fromULong(value: ULong): UInt128 = UInt128(0u, value)

        public fun fromLong(value: Long): UInt128 {
            require(value >= 0) { "UInt128 cannot represent negative value $value" }
            return UInt128(0u, value.toULong())
        }

        public fun fromUInt(value: UInt): UInt128 = UInt128(0u, value.toULong())

        public fun fromInt(value: Int): UInt128 {
            require(value >= 0) { "UInt128 cannot represent negative value $value" }
            return UInt128(0u, value.toULong())
        }
    }

    public fun toULong(): ULong? = if (hi == 0uL) lo else null

    public fun toLong(): Long? = toULong()?.takeIf { it <= Long.MAX_VALUE.toULong() }?.toLong()

    public fun toUInt(): UInt? = toULong()?.takeIf { it <= UInt.MAX_VALUE.toULong() }?.toUInt()

    public fun toInt(): Int? = toLong()?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt()

    override fun equals(other: Any?): Boolean = other is UInt128 && hi == other.hi && lo == other.lo

    override fun hashCode(): Int = hi.hashCode() * 31 + lo.hashCode()

    override fun compareTo(other: UInt128): Int {
        val hiCmp = hi.compareTo(other.hi)
        return if (hiCmp != 0) hiCmp else lo.compareTo(other.lo)
    }

    override fun toString(): String = unsignedDecimal(hi, lo)
}

/**
 * Immutable 128-bit signed integer used for the [Value] i128 arm.
 *
 * Stored as two [ULong] limbs ([hi], [lo]) interpreted as a two's-complement
 * 128-bit signed integer: the most-significant bit of [hi] is the sign bit.
 * Implements only the operations the kv [Value] surface needs.
 */
public class Int128 internal constructor(
    public val hi: ULong,
    public val lo: ULong,
) : Comparable<Int128> {
    public companion object {
        public val ZERO: Int128 = Int128(0u, 0u)

        public fun fromLong(value: Long): Int128 {
            val hi: ULong = if (value < 0) ULong.MAX_VALUE else 0u
            return Int128(hi, value.toULong())
        }

        public fun fromULong(value: ULong): Int128 = Int128(0u, value)

        public fun fromInt(value: Int): Int128 = fromLong(value.toLong())

        public fun fromUInt(value: UInt): Int128 = Int128(0u, value.toULong())
    }

    public fun isNegative(): Boolean = (hi and (1uL shl 63)) != 0uL

    public fun toLong(): Long? {
        return if (isNegative()) {
            if (hi != ULong.MAX_VALUE) return null
            val signed = lo.toLong()
            if (signed >= 0) null else signed
        } else {
            if (hi != 0uL) return null
            val signed = lo.toLong()
            if (signed < 0) null else signed
        }
    }

    public fun toULong(): ULong? = if (hi == 0uL) lo else null

    public fun toInt(): Int? = toLong()?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt()

    public fun toUInt(): UInt? = toULong()?.takeIf { it <= UInt.MAX_VALUE.toULong() }?.toUInt()

    override fun equals(other: Any?): Boolean = other is Int128 && hi == other.hi && lo == other.lo

    override fun hashCode(): Int = hi.hashCode() * 31 + lo.hashCode()

    override fun compareTo(other: Int128): Int {
        val aNeg = isNegative()
        val bNeg = other.isNegative()
        if (aNeg != bNeg) return if (aNeg) -1 else 1
        val hiCmp = hi.compareTo(other.hi)
        return if (hiCmp != 0) hiCmp else lo.compareTo(other.lo)
    }

    override fun toString(): String {
        if (!isNegative()) return unsignedDecimal(hi, lo)
        // Negate the magnitude (two's complement: invert then add 1) and prefix with '-'.
        val invHi = hi.inv()
        val invLo = lo.inv()
        val sumLo = invLo + 1uL
        val sumHi = invHi + if (sumLo < invLo) 1uL else 0uL
        return "-" + unsignedDecimal(sumHi, sumLo)
    }
}

/**
 * Decimal rendering of an unsigned 128-bit value laid out as (hi, lo). Done
 * by repeated `divmod` of the 128-bit value by 10^19, the largest power of 10
 * that fits in a [ULong].
 */
private fun unsignedDecimal(hi: ULong, lo: ULong): String {
    if (hi == 0uL) return lo.toString()
    var curHi = hi
    var curLo = lo
    val chunks = mutableListOf<ULong>()
    val divisor = 10_000_000_000_000_000_000uL
    while (curHi != 0uL || curLo != 0uL) {
        val (qHi, qLo, rem) = divmod128By64(curHi, curLo, divisor)
        chunks.add(rem)
        curHi = qHi
        curLo = qLo
    }
    val sb = StringBuilder()
    sb.append(chunks.last().toString())
    for (i in chunks.size - 2 downTo 0) {
        val piece = chunks[i].toString()
        repeat(19 - piece.length) { sb.append('0') }
        sb.append(piece)
    }
    return sb.toString()
}

/**
 * Divide the 128-bit unsigned value (hi, lo) by a 64-bit unsigned [divisor],
 * returning (quotientHi, quotientLo, remainder). Implemented bit-by-bit so it
 * avoids any compiler-specific 128-bit intrinsics and works identically on
 * every Kotlin target.
 */
private fun divmod128By64(hi: ULong, lo: ULong, divisor: ULong): Triple<ULong, ULong, ULong> {
    var quotientHi: ULong = 0u
    var quotientLo: ULong = 0u
    var remainder: ULong = 0u
    for (i in 127 downTo 0) {
        // Shift remainder left by 1 and pull in the next bit of the dividend.
        val bit: ULong = if (i >= 64) {
            (hi shr (i - 64)) and 1uL
        } else {
            (lo shr i) and 1uL
        }
        remainder = (remainder shl 1) or bit
        if (remainder >= divisor) {
            remainder -= divisor
            if (i >= 64) {
                quotientHi = quotientHi or (1uL shl (i - 64))
            } else {
                quotientLo = quotientLo or (1uL shl i)
            }
        }
    }
    return Triple(quotientHi, quotientLo, remainder)
}

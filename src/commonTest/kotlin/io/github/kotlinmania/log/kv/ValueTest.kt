// port-lint: source kv/value.rs
package io.github.kotlinmania.log.kv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// Translation of the upstream `kv::value::tests` module. The Kotlin port
// uses the dependency-free variant of upstream's Inner enum (the
// non-value-bag implementation), so this test module mirrors that
// implementation's test fixtures and assertions.

/**
 * Test-only token enum mirroring the upstream test token type used in the
 * Rust implementation. Used to assert the structural shape of a [Value]
 * regardless of how it was captured — see [toToken]. Kept inside the test
 * source set so it does not become part of the published API.
 */
internal sealed class Token {
    object None : Token() {
        override fun toString() = "None"
    }

    data class Bool(val value: Boolean) : Token()

    data class CharValue(val value: Char) : Token()

    data class Str(val value: String) : Token()

    data class F64(val value: Double) : Token()

    data class I64(val value: Long) : Token()

    data class U64(val value: ULong) : Token()
}

/**
 * Test-only structural projection returning a token matching the inner
 * variant of a value. In the upstream Rust implementation, this corresponds
 * to a to_test_token method on the Inner type.
 *
 * Upstream marks four arms (I128, U128, Debug, Display) as unimplemented
 * because no upstream test exercises them; the Kotlin counterpart surfaces
 * those as errors with the same intent.
 */
internal fun Value.toToken(): Token =
    when (val i = inner) {
        ValueInner.Inner.None -> Token.None
        is ValueInner.Inner.Bool -> Token.Bool(i.value)
        is ValueInner.Inner.Str -> Token.Str(i.value)
        is ValueInner.Inner.CharValue -> Token.CharValue(i.value)
        is ValueInner.Inner.I64 -> Token.I64(i.value)
        is ValueInner.Inner.U64 -> Token.U64(i.value)
        is ValueInner.Inner.F64 -> Token.F64(i.value)
        is ValueInner.Inner.I128 -> error("Token has no I128 arm; test must not feed I128 to toToken().")
        is ValueInner.Inner.U128 -> error("Token has no U128 arm; test must not feed U128 to toToken().")
        is ValueInner.Inner.Debug -> error("Token has no Debug arm; test must not feed Debug to toToken().")
        is ValueInner.Inner.Display -> error("Token has no Display arm; test must not feed Display to toToken().")
        is ValueInner.Inner.ErrorValue -> error("Token has no Error arm; test must not feed Error to toToken().")
        is ValueInner.Inner.Serde -> error("Token has no Serde arm; test must not feed Serde to toToken().")
        is ValueInner.Inner.Sval -> error("Token has no Sval arm; test must not feed Sval to toToken().")
    }

// --- Test fixture iterators ----------------------------------------------
// Direct translations of upstream's `unsigned()`, `signed()`, `float()`,
// `bool()`, `str()`, `char()` test fixture functions. Kotlin lists stand in
// for `impl Iterator<Item = Value<'static>>`; callers iterate with `for`.

/**
 * Test-fixture: unsigned-integer Values across every Kotlin unsigned width.
 * Upstream tests two halves: ordinary unsigned primitives, plus the
 * `NonZero*` analogues. Kotlin has no `NonZero*` wrappers, so the second
 * half collapses to a second pass of ordinary positive primitives.
 */
internal fun unsigned(): List<Value> =
    listOf(
        8.toUByte().toValue(),
        16.toUShort().toValue(),
        32u.toValue(),
        64UL.toValue(),
        1u.toValue(),
        // Non-zero analogues — Kotlin has no NonZero* wrappers, so the
        // upstream NonZero invariant collapses to positive primitive values.
        8.toUByte().toValue(),
        16.toUShort().toValue(),
        32u.toValue(),
        64UL.toValue(),
        1u.toValue(),
    )

/**
 * Test-fixture: signed-integer Values across every Kotlin signed width.
 */
internal fun signed(): List<Value> =
    listOf(
        (-8).toByte().toValue(),
        (-16).toShort().toValue(),
        (-32).toValue(),
        (-64L).toValue(),
        (-1).toValue(),
        // Non-zero analogues — Kotlin has no NonZero* wrappers.
        (-8).toByte().toValue(),
        (-16).toShort().toValue(),
        (-32).toValue(),
        (-64L).toValue(),
        (-1).toValue(),
    )

/**
 * Test-fixture: floating-point Values across both Kotlin widths.
 */
internal fun float(): List<Value> =
    listOf(32.32f.toValue(), 64.64.toValue())

/**
 * Test-fixture: boolean Values.
 */
internal fun bool(): List<Value> =
    listOf(true.toValue(), false.toValue())

/**
 * Test-fixture: string Values.
 */
internal fun str(): List<Value> =
    listOf("a string".toValue(), "a loong string".toValue())

/**
 * Test-fixture: character Values.
 */
internal fun char(): List<Value> =
    listOf('a'.toValue(), '⛰'.toValue())

class ValueTest {
    @Test
    fun testToValueDisplay() {
        assertEquals("42", 42UL.toValue().toString())
        assertEquals("42", 42L.toValue().toString())
        assertEquals("42.01", 42.01.toValue().toString())
        assertEquals("true", true.toValue().toString())
        assertEquals("a", 'a'.toValue().toString())
        assertEquals("a loong string", "a loong string".toValue().toString())
        val someBool: Boolean? = true
        assertEquals("true", someBool.toValue { it.toValue() }.toString())
        assertEquals("None", Value.nullValue().toString())
        val noneBool: Boolean? = null
        assertEquals("None", noneBool.toValue { it.toValue() }.toString())
    }

    @Test
    fun testToValueStructured() {
        assertEquals(Token.U64(42UL), 42UL.toValue().toToken())
        assertEquals(Token.I64(42L), 42L.toValue().toToken())
        assertEquals(Token.F64(42.01), 42.01.toValue().toToken())
        assertEquals(Token.Bool(true), true.toValue().toToken())
        assertEquals(Token.CharValue('a'), 'a'.toValue().toToken())
        assertEquals(Token.Str("a loong string"), "a loong string".toValue().toToken())
        val someBool: Boolean? = true
        assertEquals(Token.Bool(true), someBool.toValue { it.toValue() }.toToken())
        assertEquals(Token.None, Value.nullValue().toToken())
        val noneBool: Boolean? = null
        assertEquals(Token.None, noneBool.toValue { it.toValue() }.toToken())
    }

    @Test
    fun testToNumber() {
        for (v in unsigned()) {
            assertNotNull(v.toU64(), "unsigned $v should produce a U64")
            assertNotNull(v.toI64(), "unsigned $v should produce an I64")
        }

        for (v in signed()) {
            assertNotNull(v.toI64(), "signed $v should produce an I64")
        }

        for (v in unsigned() + signed() + float()) {
            assertNotNull(v.toF64(), "$v should produce an F64")
        }

        for (v in bool() + str() + char()) {
            assertNull(v.toU64(), "non-numeric $v must not produce a U64")
            assertNull(v.toI64(), "non-numeric $v must not produce an I64")
            assertNull(v.toF64(), "non-numeric $v must not produce an F64")
        }
    }

    @Test
    fun testToFloat() {
        // Only integers from Int.MIN_VALUE..=UInt.MAX_VALUE can be converted
        // into Doubles without loss of magnitude; outside that window
        // toF64 returns null. Mirrors upstream's i32::MIN..=u32::MAX gate.
        assertNotNull(Int.MIN_VALUE.toValue().toF64())
        assertNotNull(UInt.MAX_VALUE.toValue().toF64())

        assertNull((Int.MIN_VALUE.toLong() - 1L).toValue().toF64())
        assertNull((UInt.MAX_VALUE.toULong() + 1UL).toValue().toF64())
    }

    @Test
    fun testToCowStr() {
        for (v in str()) {
            assertNotNull(v.toBorrowedStr())
            assertNotNull(v.toCowStr())
        }

        val shortLived = "short lived"
        val v = shortLived.toValue()
        assertNotNull(v.toBorrowedStr())
        assertNotNull(v.toCowStr())

        for (other in unsigned() + signed() + float() + bool()) {
            assertNull(other.toBorrowedStr())
            assertNull(other.toCowStr())
        }
    }

    @Test
    fun testToBool() {
        for (v in bool()) {
            assertNotNull(v.toBool())
        }

        for (v in unsigned() + signed() + float() + str() + char()) {
            assertNull(v.toBool())
        }
    }

    @Test
    fun testToChar() {
        for (v in char()) {
            assertNotNull(v.toChar())
        }

        for (v in unsigned() + signed() + float() + str() + bool()) {
            assertNull(v.toChar())
        }
    }

    @Test
    fun testVisitInteger() {
        // Translation of upstream's local `struct Extract(Option<u64>)`
        // inside test_visit_integer. Kept as a named inner class so the
        // upstream helper has a Kotlin counterpart that participates in
        // symbol parity tracking.
        class Extract(var captured: ULong? = null) : VisitValue {
            override fun visitAny(value: Value): Result<Unit> =
                error("unexpected value: $value")

            override fun visitU64(value: ULong): Result<Unit> {
                captured = value
                return Result.success(Unit)
            }
        }

        val extract = Extract()
        42UL.toValue().visit(extract).getOrThrow()
        assertEquals(42UL, extract.captured)
    }

    @Test
    fun testVisitBorrowedStr() {
        // Translation of upstream's local `struct Extract<'v>(Option<&'v str>)`
        // inside test_visit_borrowed_str. Kotlin has no lifetimes; the
        // borrowed/owned distinction collapses to ordinary String references.
        class Extract(var captured: String? = null) : VisitValue {
            override fun visitAny(value: Value): Result<Unit> =
                error("unexpected value: $value")

            override fun visitBorrowedStr(value: String): Result<Unit> {
                captured = value
                return Result.success(Unit)
            }
        }

        val extract = Extract()
        val shortLived = "A short-lived string"
        shortLived.toValue().visit(extract).getOrThrow()
        assertEquals("A short-lived string", extract.captured)
    }
}

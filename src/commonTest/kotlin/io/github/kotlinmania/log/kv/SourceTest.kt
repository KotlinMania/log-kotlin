// port-lint: source kv/source.rs
package io.github.kotlinmania.log.kv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * A minimal [Source] implementation used by the upstream test for counting a
 * single pair. Kept as a named class so the upstream `OnePair` helper struct
 * has a Kotlin counterpart that participates in symbol-parity tracking.
 */
private class OnePair(
    val key: String,
    val value: Int,
) : Source {
    override fun visit(visitor: VisitSource): Result<Unit> =
        visitor.visitPair(key.toKey(), value.toValue())
}

class SourceTest {
    @Test
    fun count() {
        // ("a", 1) — single pair source.
        val singlePair = (Key.fromStr("a") to 1.toValue()).asSource()
        assertEquals(1, singlePair.count())

        // [("a", 1), ("b", 2)] — slice of pair sources.
        val twoPairs =
            listOf(
                (Key.fromStr("a") to 1.toValue()).asSource(),
                (Key.fromStr("b") to 2.toValue()).asSource(),
            ).asSource()
        assertEquals(2, twoPairs.count())

        // None::<(&str, i32)> — empty optional source.
        val emptyOption: Source? = null
        assertEquals(0, emptyOption.asSource().count())

        // OnePair { key: "a", value: 1 } — a user-defined Source impl.
        assertEquals(1, OnePair("a", 1).count())
    }

    @Test
    fun get() {
        // [("a", 1), ("b", 2), ("a", 1)] — slice; "a" appears twice but
        // get returns whichever match the source decides; integer value is
        // checked rather than upstream's value-inner Token wrapper.
        val source =
            listOf(
                (Key.fromStr("a") to 1.toValue()).asSource(),
                (Key.fromStr("b") to 2.toValue()).asSource(),
                (Key.fromStr("a") to 1.toValue()).asSource(),
            ).asSource()
        assertEquals(1L, source.get(Key.fromStr("a"))?.toI64())
        assertEquals(2L, source.get(Key.fromStr("b"))?.toI64())
        assertNull(source.get(Key.fromStr("c")))

        // None — empty optional source.
        val emptyOption: Source? = null
        assertNull(emptyOption.asSource().get(Key.fromStr("a")))
    }

    @Test
    fun hashMap() {
        // Kotlin's standard map covers both the upstream HashMap and
        // BTreeMap cases since Map<K, V>.asSource() is map-shape-agnostic.
        val map: Map<Key, Value> =
            mapOf(
                Key.fromStr("a") to 1.toValue(),
                Key.fromStr("b") to 2.toValue(),
            )
        val source = map.asSource()
        assertEquals(2, source.count())
        assertEquals(1L, source.get(Key.fromStr("a"))?.toI64())
    }

    @Test
    fun btreeMap() {
        // Sorted-map variant of the previous test, mirroring upstream's
        // separate BTreeMap case. KMP common code has no portable
        // sorted-map factory, so the entries are pre-sorted by key and
        // placed in a linked map that preserves insertion order. The
        // assertions only care about count + get, both of which are
        // sort-invariant.
        val sortedEntries =
            listOf(
                Key.fromStr("a") to 1.toValue(),
                Key.fromStr("b") to 2.toValue(),
            ).sortedBy { (k, _) -> k.asStr() }
        val map: Map<Key, Value> = linkedMapOf(*sortedEntries.toTypedArray())
        val source = map.asSource()
        assertEquals(2, source.count())
        assertEquals(1L, source.get(Key.fromStr("a"))?.toI64())
    }

    @Test
    fun visitorIsCallable() {
        // Upstream's source_is_object_safe/visitor_is_object_safe tests verify
        // the Rust traits are object-safe (can become `dyn Source` / `dyn
        // VisitSource`). Kotlin interfaces are reference-typed by construction,
        // so this is trivially true. The Kotlin test instead exercises the
        // round trip: construct a custom VisitSource, run a Source through
        // it, and assert that visitPair was called.
        var observedKey: Key? = null
        var observedValue: Value? = null
        val visitor =
            object : VisitSource {
                override fun visitPair(key: Key, value: Value): Result<Unit> {
                    observedKey = key
                    observedValue = value
                    return Result.success(Unit)
                }
            }
        val source = (Key.fromStr("a") to 42.toValue()).asSource()
        assertEquals(Result.success(Unit), source.visit(visitor))
        assertEquals("a", assertNotNull(observedKey).asStr())
        assertEquals(42L, observedValue?.toI64())
    }
}

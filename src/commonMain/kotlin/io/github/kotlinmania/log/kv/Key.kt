// port-lint: source kv/key.rs
package io.github.kotlinmania.log.kv

import io.github.kotlinmania.serde.core.ser.serialize

// Structured keys.
//
// # Serialization support
//
// The upstream Rust implementation provides optional sval and serde serialization
// support through feature-gated `stream`, `stream_ref`, and `serialize` methods.
// These features require the `kv_sval` and `kv_serde` Cargo features respectively.
//
// The Kotlin port provides serde serialization support through serde-kotlin when
// that dependency is available. The `serialize` method delegates to serde-kotlin's
// Serialize implementation for String.
//
// The sval-based methods (`stream` and `stream_ref`) are not ported because sval
// has no Kotlin Multiplatform equivalent.

/**
 * A type that can be converted into a [Key].
 */
public fun interface ToKey {
    /**
     * Perform the conversion.
     */
    public fun toKey(): Key
}

/**
 * A key in a key-value.
 *
 * Equality, ordering, and hashing on [Key] must only consider the
 * [asStr] representation of the key. If new fields (such as an optional
 * index) are ever added to [Key] they must not affect comparison.
 */
public class Key private constructor(
    private val key: String,
) : ToKey, Comparable<Key> {
    public companion object {
        /**
         * Get a key from a string.
         */
        public fun fromStr(key: String): Key {
            return Key(key)
        }

        /**
         * Build a [Key] from a string. Delegates to [fromStr].
         */
        public fun from(s: String): Key {
            return fromStr(s)
        }
    }

    /**
     * Get the string content of this key.
     */
    public fun asStr(): String = key

    /**
     * Try to get the string originally supplied to this key.
     *
     * If the key was constructed by borrowing a longer-lived string, this
     * method returns that string. If the key is internally buffered, this
     * method returns `null`. The Kotlin port always stores the original
     * string by reference, so the result is currently always non-null; the
     * nullable return shape is preserved so the contract stays compatible
     * when internal buffering is added later.
     */
    public fun toBorrowedStr(): String? {
        return key
    }

    /**
     * Return the underlying string. Mirrors a string-reference view of the
     * key.
     */
    public fun asRef(): String = asStr()

    /**
     * Return the underlying string. Mirrors a string-reference view of the
     * key.
     */
    public fun borrow(): String = asStr()

    override fun toKey(): Key = Key(key)

    /**
     * Format the key as a string. Delegates to [toString].
     */
    public fun fmt(): String = toString()

    /**
     * Serialize this key using serde.
     *
     * The key is serialized as its underlying string value.
     */
    public fun <Ok, E> serialize(serializer: io.github.kotlinmania.serde.core.ser.Serializer<Ok, E>): Result<Ok>
            where E : io.github.kotlinmania.serde.core.ser.Error {
        return key.serialize(serializer)
    }

    override fun compareTo(other: Key): Int = key.compareTo(other.key)

    override fun equals(other: Any?): Boolean = other is Key && key == other.key

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = key
}

/**
 * Promote any [String] into a [Key]. Provides the [ToKey] conversion for
 * strings.
 */
public fun String.toKey(): Key = Key.fromStr(this)

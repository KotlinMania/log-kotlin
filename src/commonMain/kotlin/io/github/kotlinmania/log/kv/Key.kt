// port-lint: source src/kv/key.rs
package io.github.kotlinmania.log.kv

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
 * These impls must only be based on the [asStr] representation of the key.
 * If a new field (such as an optional index) is added to the key they must not affect comparison.
 */
public class Key private constructor(
    private val key: String,
) : ToKey, Comparable<Key> {
    public companion object {
        /**
         * Get a key from a borrowed string.
         */
        public fun fromStr(key: String): Key {
            return Key(key)
        }
    }

    /**
     * Get a borrowed string from this key.
     */
    public fun asStr(): String = key

    /**
     * Try get a borrowed string for the lifetime `'k` from this key.
     *
     * If the key is a borrow of a longer lived string, this method will return `Some`.
     * If the key is internally buffered, this method will return `None`.
     */
    public fun toBorrowedStr(): String? {
        return key
    }

    override fun toKey(): Key = Key(key)

    override fun compareTo(other: Key): Int = key.compareTo(other.key)

    override fun equals(other: Any?): Boolean = other is Key && key == other.key

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = key
}

public fun String.toKey(): Key = Key.fromStr(this)

// port-lint: source kv/key.rs
package io.github.kotlinmania.log.kv

// Structured keys.

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
 * If a new field (such as an optional index) is added to the key they must not
 * affect comparison.
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

        /**
         * Equivalent of upstream's `From<&str>` conversion; delegates to
         * [fromStr] so callers may write `Key.from(s)` or `Key.fromStr(s)`
         * interchangeably.
         */
        public fun from(s: String): Key {
            return fromStr(s)
        }
    }

    /**
     * Get a borrowed string from this key.
     *
     * The lifetime of the returned string is bound to the borrow of `this`
     * rather than to the original construction site, mirroring the
     * `as_str(&self) -> &str` shape upstream.
     */
    public fun asStr(): String = key

    /**
     * Try to get the string originally supplied to this key.
     *
     * If the key is a borrow of a longer-lived string, this method will
     * return that string. If the key is internally buffered, this method will
     * return `null`. Today the Kotlin port always stores the original string
     * by reference, so the result is always non-null; the option-shaped
     * signature is preserved so the contract stays compatible when internal
     * buffering is added later.
     */
    public fun toBorrowedStr(): String? {
        return key
    }

    /**
     * Equivalent of upstream's `AsRef<str>::as_ref` conversion; returns the
     * underlying string. Provided for naming parity with the upstream trait
     * impl.
     */
    public fun asRef(): String = asStr()

    /**
     * Equivalent of upstream's `Borrow<str>::borrow` conversion; returns the
     * underlying string. Provided for naming parity with the upstream trait
     * impl.
     */
    public fun borrow(): String = asStr()

    override fun toKey(): Key = Key(key)

    /**
     * Format the key as a string. Kotlin equivalent of upstream's
     * `Display::fmt` implementation; delegates to [toString].
     */
    public fun fmt(): String = toString()

    override fun compareTo(other: Key): Int = key.compareTo(other.key)

    override fun equals(other: Any?): Boolean = other is Key && key == other.key

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = key
}

/**
 * Equivalent of upstream's `impl ToKey for str` (and `impl ToKey for String`
 * inside the `std` support module). Any Kotlin [String] can be promoted into
 * a [Key] by calling `toKey()` on it.
 */
public fun String.toKey(): Key = Key.fromStr(this)

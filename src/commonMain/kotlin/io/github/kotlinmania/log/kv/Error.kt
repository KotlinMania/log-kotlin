// port-lint: source kv/error.rs
package io.github.kotlinmania.log.kv

/**
 * A typealias for the kind of error value that [Error.boxed] wraps.
 *
 * Upstream models this as a heap-allocated send/sync boxed standard error type
 * inside its `std`-feature-gated support module. The Kotlin port uses
 * [Throwable] directly: every Kotlin exception is reference-typed, all
 * Kotlin/Native targets ship a single-threaded mutability model so
 * send/sync bounds collapse, and there is no equivalent boxing wrapper.
 */
internal typealias BoxedError = Throwable

/**
 * An error encountered while working with structured data.
 */
public class Error private constructor(
    private val inner: Inner,
) : Exception() {
    private sealed interface Inner {
        data class Boxed(val err: BoxedError) : Inner

        data class Value(val err: Error) : Inner

        data class Msg(val msg: String) : Inner

        data object Fmt : Inner
    }

    public companion object {
        private const val FMT_ERROR_MESSAGE: String = "formatting error"

        /**
         * Create an error from a message.
         */
        public fun msg(msg: String): Error {
            return Error(Inner.Msg(msg))
        }

        /**
         * Create an error from a standard error type.
         */
        public fun boxed(err: Throwable): Error {
            return Error(Inner.Boxed(err))
        }

        /**
         * Create an error from a throwable. In the upstream Rust
         * implementation, this corresponds to a From trait implementation
         * for IO errors. In Kotlin, any [Throwable] reaches this entry
         * point, so the conversion is just an alias for [boxed].
         */
        public fun from(err: Throwable): Error {
            return boxed(err)
        }

        /**
         * Create a formatting error. In the upstream Rust implementation,
         * this corresponds to a From trait implementation for formatting
         * errors. Kotlin has no separate formatting-error type, so a
         * no-arg factory is used to construct the Fmt variant.
         */
        public fun fromFormat(): Error {
            return Error(Inner.Fmt)
        }

        // Not public so the value-inner error machinery is not leaked across the
        // package boundary.
        internal fun fromValue(err: Error): Error {
            return Error(Inner.Value(err))
        }
    }

    // Not public so the value-inner error machinery is not leaked across the
    // package boundary.
    internal fun intoValue(): Error {
        return when (val currentInner = inner) {
            is Inner.Value -> currentInner.err
            else -> this
        }
    }

    /**
     * Format the error as a string. In the upstream Rust implementation,
     * this corresponds to a Display trait implementation; it delegates to
     * [toString].
     */
    public fun fmt(): String {
        return toString()
    }

    public override fun toString(): String {
        return when (val currentInner = inner) {
            is Inner.Boxed -> currentInner.err.toString()
            is Inner.Value -> currentInner.err.toString()
            is Inner.Msg -> currentInner.msg
            Inner.Fmt -> FMT_ERROR_MESSAGE
        }
    }
}

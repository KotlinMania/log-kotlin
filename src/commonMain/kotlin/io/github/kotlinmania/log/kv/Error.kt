// port-lint: source src/kv/error.rs
package io.github.kotlinmania.log.kv

/**
 * An error encountered while working with structured data.
 */
public class Error private constructor(
    private val inner: Inner,
) : Exception() {
    private sealed interface Inner {
        data class Boxed(val err: Throwable) : Inner

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
    }

    public override fun toString(): String {
        return when (val currentInner = inner) {
            is Inner.Boxed -> currentInner.err.toString()
            is Inner.Msg -> currentInner.msg
            Inner.Fmt -> FMT_ERROR_MESSAGE
        }
    }
}

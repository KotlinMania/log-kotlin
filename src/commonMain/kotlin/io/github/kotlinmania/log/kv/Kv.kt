// port-lint: source src/kv/mod.rs
package io.github.kotlinmania.log.kv

/**
 * Structured logging.
 *
 * Add the `kv` feature to your `Cargo.toml` to enable
 * this module:
 *
 * ```toml
 * [dependencies.log]
 * features = ["kv"]
 * ```
 *
 * # Structured logging in `log`
 *
 * Structured logging enhances traditional text-based log records with user-defined
 * attributes. Structured logs can be analyzed using a variety of data processing
 * techniques, without needing to find and parse attributes from unstructured text first.
 *
 * In `log`, user-defined attributes are part of a [Source] on the log record.
 * Each attribute is a key-value; a pair of [Key] and [Value]. Keys are strings
 * and values are a datum of any type that can be formatted or serialized. Simple types
 * like strings, booleans, and numbers are supported, as well as arbitrarily complex
 * structures involving nested objects and sequences.
 *
 * ## Adding key-values to log records
 *
 * Key-values appear before the message format in the `log!` macros:
 *
 * ```
 * // info!(a = 1; "Something of interest");
 * ```
 *
 * Key-values support the same shorthand identifier syntax as `format_args`:
 *
 * ```
 * // val a = 1
 * // info!(a; "Something of interest");
 * ```
 *
 * Values are capturing using the [ToValue] functional interface by default. To capture a value
 * using a different trait implementation, use a modifier after its key. Here's how
 * the same example can capture `a` using its debug formatting instead:
 *
 * ```
 * // info!(a:? = 1; "Something of interest");
 * ```
 *
 * The following capturing modifiers are supported:
 *
 * - `:?` will capture the value using debug formatting.
 * - `:debug` will capture the value using debug formatting.
 * - `:%` will capture the value using display formatting.
 * - `:display` will capture the value using display formatting.
 * - `:err` will capture the value using `Throwable` (requires the `kv_std` feature).
 * - `:sval` will capture the value using `sval::Value` (requires the `kv_sval` feature).
 * - `:serde` will capture the value using `serde::Serialize` (requires the `kv_serde` feature).
 *
 * ## Working with key-values on log records
 *
 * Use the `Record.keyValues` method to access key-values.
 *
 * Individual values can be pulled from the source by their key:
 *
 * ```
 * // val record = Record.builder().keyValues(listOf(Pair("a", 1)).asSource()).build()
 * //
 * // val a: Value = record.keyValues().get(Key.fromStr("a"))!!
 * // kotlin.test.assertEquals(1L, a.toI64())
 * ```
 *
 * All key-values can also be enumerated using a [VisitSource]:
 *
 * ```
 * // class Collect(val collected: MutableMap<Key, Value>) : VisitSource {
 * //     override fun visitPair(key: Key, value: Value): Result<Unit> {
 * //         collected[key] = value
 * //         return Result.success(Unit)
 * //     }
 * // }
 * //
 * // val visitor = Collect(mutableMapOf())
 * // record.keyValues().visit(visitor).getOrThrow()
 * // kotlin.test.assertEquals(listOf("a", "b", "c"), visitor.collected.keys.map { it.asStr() })
 * ```
 *
 * [Value]s have methods for conversions to common types:
 *
 * ```
 * // val a = record.keyValues().get(Key.fromStr("a"))!!
 * // kotlin.test.assertEquals(1L, a.toI64())
 * ```
 *
 * Values also have their own [VisitValue] type. Value visitors are a lightweight
 * API for working with primitive types:
 *
 * ```
 * // class IsNumeric(var isNumeric: Boolean) : VisitValue {
 * //     override fun visitAny(value: Value): Result<Unit> {
 * //         isNumeric = false
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitU64(value: ULong): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitI64(value: Long): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitU128(value: com.ionspin.kotlin.bignum.integer.BigInteger): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitI128(value: com.ionspin.kotlin.bignum.integer.BigInteger): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitF64(value: Double): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * // }
 * //
 * // val a = record.keyValues().get(Key.fromStr("a"))!!
 * // val visitor = IsNumeric(false)
 * // a.visit(visitor).getOrThrow()
 * // kotlin.test.assertTrue(visitor.isNumeric)
 * ```
 *
 * To serialize a value to a format like JSON, you can also use either `serde` or `sval`.
 *
 * The choice of serialization framework depends on the needs of the consumer.
 * If you're in a no-std environment, you can use `sval`. In other cases, you can use `serde`.
 * Log producers and log consumers don't need to agree on the serialization framework.
 * A value can be captured using its `serde::Serialize` implementation and still be serialized
 * through `sval` without losing any structure or data.
 *
 * Values can also always be formatted using debug and display formatting:
 *
 * ```
 * // data class Data(val a: Int, val b: Boolean, val c: String)
 * // val data = Data(a = 1, b = true, c = "Some data")
 * // val a = Value.fromDebug(data)
 * // kotlin.test.assertEquals("Data(a=1, b=true, c=Some data)", a.toString())
 * ```
 */
// In upstream Rust, `Visitor` is an unstable re-export behind `kv_unstable`:
// `pub use self::source::Visitor;`
//
// Per the workspace `mod.rs` re-export workflow, we do not preserve this re-export as a Kotlin
// `typealias`. Callers should reference `VisitSource` directly. If a caller needs to keep the
// identifier `Visitor` unchanged for a faithful translation, it should write
// `import io.github.kotlinmania.log.kv.VisitSource as Visitor` in the caller.

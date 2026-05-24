// port-lint: source lib.rs
package io.github.kotlinmania.log

import io.github.kotlinmania.log.kv.get
import io.github.kotlinmania.log.kv.toValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Ports of the upstream `#[cfg(test)] mod tests { ... }` block at the bottom
 * of `tmp/log/src/lib.rs`. Test method names mirror the upstream `fn test_*`
 * names rewritten in Kotlin `camelCase`. The cases inside each test mirror
 * the upstream assertions one-for-one; supplementary Kotlin-specific
 * coverage lives in [IntegrationTest], not here.
 */
class LibTest {
    @Test
    fun testLevelFilterFromStr() {
        val cases = listOf(
            "off" to LevelFilter.Off,
            "error" to LevelFilter.Error,
            "warn" to LevelFilter.Warn,
            "info" to LevelFilter.Info,
            "debug" to LevelFilter.Debug,
            "trace" to LevelFilter.Trace,
            "OFF" to LevelFilter.Off,
            "ERROR" to LevelFilter.Error,
            "WARN" to LevelFilter.Warn,
            "INFO" to LevelFilter.Info,
            "DEBUG" to LevelFilter.Debug,
            "TRACE" to LevelFilter.Trace,
        )
        for ((input, expected) in cases) {
            assertEquals(expected, LevelFilter.fromStr(input).getOrThrow())
        }
        assertTrue(LevelFilter.fromStr("asdf").isFailure)
    }

    @Test
    fun testLevelFromStr() {
        // Upstream notes: parsing "OFF" must fail because [Level] has no Off
        // variant. Only [LevelFilter] does. Lowercase and uppercase variants
        // of the real levels all parse successfully.
        assertTrue(Level.fromStr("OFF").isFailure)
        val cases = listOf(
            "error" to Level.Error,
            "warn" to Level.Warn,
            "info" to Level.Info,
            "debug" to Level.Debug,
            "trace" to Level.Trace,
            "ERROR" to Level.Error,
            "WARN" to Level.Warn,
            "INFO" to Level.Info,
            "DEBUG" to Level.Debug,
            "TRACE" to Level.Trace,
        )
        for ((input, expected) in cases) {
            assertEquals(expected, Level.fromStr(input).getOrThrow())
        }
        assertTrue(Level.fromStr("asdf").isFailure)
    }

    @Test
    fun testLevelAsStr() {
        val cases = listOf(
            Level.Error to "ERROR",
            Level.Warn to "WARN",
            Level.Info to "INFO",
            Level.Debug to "DEBUG",
            Level.Trace to "TRACE",
        )
        for ((input, expected) in cases) {
            assertEquals(expected, input.asStr())
        }
    }

    @Test
    fun testLevelShow() {
        assertEquals("INFO", Level.Info.toString())
        assertEquals("ERROR", Level.Error.toString())
    }

    @Test
    fun testLevelFilterShow() {
        assertEquals("OFF", LevelFilter.Off.toString())
        assertEquals("ERROR", LevelFilter.Error.toString())
    }

    @Test
    fun testCrossCmp() {
        // Cross-type ordering: [Level] and [LevelFilter] share a discriminant
        // space (Off=0, Error=1, ..., Trace=5) and Kotlin's operator extension
        // functions on Lib.kt make these comparisons total.
        assertTrue(Level.Debug > LevelFilter.Error)
        assertTrue(LevelFilter.Warn < Level.Trace)
        assertTrue(LevelFilter.Off < Level.Error)
    }

    @Test
    fun testCrossEq() {
        assertEquals(LevelFilter.Error.discriminant, Level.Error.discriminant)
        assertNotEquals(LevelFilter.Off.discriminant, Level.Error.discriminant)
        assertEquals(LevelFilter.Trace.discriminant, Level.Trace.discriminant)
    }

    @Test
    fun testToLevel() {
        assertEquals(Level.Error, LevelFilter.Error.toLevel())
        assertNull(LevelFilter.Off.toLevel())
        assertEquals(Level.Debug, LevelFilter.Debug.toLevel())
    }

    @Test
    fun testToLevelFilter() {
        assertEquals(LevelFilter.Error, Level.Error.toLevelFilter())
        assertEquals(LevelFilter.Trace, Level.Trace.toLevelFilter())
    }

    @Test
    fun testLevelFilterAsStr() {
        val cases = listOf(
            LevelFilter.Off to "OFF",
            LevelFilter.Error to "ERROR",
            LevelFilter.Warn to "WARN",
            LevelFilter.Info to "INFO",
            LevelFilter.Debug to "DEBUG",
            LevelFilter.Trace to "TRACE",
        )
        for ((input, expected) in cases) {
            assertEquals(expected, input.asStr())
        }
    }

    @Test
    fun testLevelUp() {
        val info = Level.Info
        val up = info.incrementSeverity()
        assertEquals(Level.Debug, up)

        val trace = Level.Trace
        // Trace is already the highest level; incrementing yields the same value.
        assertEquals(trace, trace.incrementSeverity())
    }

    @Test
    fun testLevelFilterUp() {
        val info = LevelFilter.Info
        val up = info.incrementSeverity()
        assertEquals(LevelFilter.Debug, up)

        val trace = LevelFilter.Trace
        // Trace is already the highest filter; incrementing yields the same value.
        assertEquals(trace, trace.incrementSeverity())
    }

    @Test
    fun testLevelDown() {
        val info = Level.Info
        val down = info.decrementSeverity()
        assertEquals(Level.Warn, down)

        val error = Level.Error
        // Error is already the lowest [Level]; decrementing yields the same value.
        assertEquals(error, error.decrementSeverity())
    }

    @Test
    fun testLevelFilterDown() {
        val info = LevelFilter.Info
        val down = info.decrementSeverity()
        assertEquals(LevelFilter.Warn, down)

        val error = LevelFilter.Error
        val errorDown = error.decrementSeverity()
        assertEquals(LevelFilter.Off, errorDown)
        // Off is already the lowest filter; decrementing again is a no-op.
        assertEquals(errorDown, errorDown.decrementSeverity())
    }

    @Test
    fun testStaticMaxLevel() {
        // The upstream `test_static_max_level_debug` and
        // `test_static_max_level_release` tests select between several Cargo
        // `feature = "max_level_*"` / `feature = "release_max_level_*"`
        // branches. Kotlin has no Cargo features; the Kotlin port pins
        // [STATIC_MAX_LEVEL] to [LevelFilter.Trace] (the upstream `else`
        // branch), and the two upstream tests collapse into a single
        // assertion against that value.
        assertEquals(LevelFilter.Trace, STATIC_MAX_LEVEL)
    }

    @Test
    fun testErrorTrait() {
        // Upstream feature-gates this on `cfg(feature = "std")` because Rust
        // `Display` for `SetLoggerError` is provided by `std::error::Error`.
        // Kotlin always has `Exception.message`, so the port is unconditional.
        val e = SetLoggerError()
        assertEquals(
            "attempted to set a logger after the logging system was already initialized",
            e.message,
        )
    }

    @Test
    fun testMetadataBuilder() {
        val target = "myApp"
        val metadataTest = MetadataBuilder.new()
            .level(Level.Debug)
            .target(target)
            .build()
        assertEquals(Level.Debug, metadataTest.level())
        assertEquals("myApp", metadataTest.target())
    }

    @Test
    fun testMetadataConvenienceBuilder() {
        val target = "myApp"
        val metadataTest = Metadata.builder()
            .level(Level.Debug)
            .target(target)
            .build()
        assertEquals(Level.Debug, metadataTest.level())
        assertEquals("myApp", metadataTest.target())
    }

    @Test
    fun testRecordBuilder() {
        val target = "myApp"
        val metadata = MetadataBuilder.new().target(target).build()
        val fmtArgs = Arguments("hello", emptyList())
        val recordTest = RecordBuilder.new()
            .args(fmtArgs)
            .metadata(metadata)
            .modulePath("foo")
            .file("bar")
            .line(30)
            .build()
        assertEquals("myApp", recordTest.metadata().target())
        assertEquals("foo", recordTest.modulePath())
        assertEquals("bar", recordTest.file())
        assertEquals(30, recordTest.line())
    }

    @Test
    fun testRecordConvenienceBuilder() {
        val target = "myApp"
        val metadata = Metadata.builder().target(target).build()
        val fmtArgs = Arguments("hello", emptyList())
        val recordTest = Record.builder()
            .args(fmtArgs)
            .metadata(metadata)
            .modulePath("foo")
            .file("bar")
            .line(30)
            .build()
        assertEquals("myApp", recordTest.target())
        assertEquals("foo", recordTest.modulePath())
        assertEquals("bar", recordTest.file())
        assertEquals(30, recordTest.line())
    }

    @Test
    fun testRecordCompleteBuilder() {
        val target = "myApp"
        val recordTest = Record.builder()
            .modulePath("foo")
            .file("bar")
            .line(30)
            .target(target)
            .level(Level.Error)
            .build()
        assertEquals("myApp", recordTest.target())
        assertEquals(Level.Error, recordTest.level())
        assertEquals("foo", recordTest.modulePath())
        assertEquals("bar", recordTest.file())
        assertEquals(30, recordTest.line())
    }

    @Test
    fun testForeignImpl() {
        // Upstream `test_foreign_impl` asserts the [Log] trait is implemented
        // on `&dyn Log`, `Box<dyn Log>`, and `Arc<dyn Log>`. Kotlin reference
        // types already carry that contract: a [Log] instance held through
        // any reference (val, lateinit, smart-cast, lambda capture) is
        // already a [Log]. The Kotlin port collapses the type-level check
        // into a single nullable assertion that an arbitrary [Log] value
        // type-checks where a [Log] is required.
        val direct: Log = NoOpLog
        val viaInterface: Log = direct
        assertEquals(false, viaInterface.enabled(Metadata.builder().target("x").build()))
    }

    @Test
    fun testParseLevelErrorMessage() {
        val e = ParseLevelError()
        assertEquals(
            "attempted to convert a string that doesn't match an existing log level",
            e.message,
        )
        assertFalse(Level.fromStr("notalevel").isSuccess)
    }

    private object NoOpLog : Log {
        override fun enabled(metadata: Metadata): Boolean = false
        override fun log(record: Record) = Unit
        override fun flush() = Unit
    }
}

/**
 * Ports of the upstream `#[cfg(feature = "kv")]` tests that exercise the
 * structured key-values surface (`tmp/log/src/lib.rs` lines 1930-1978). The
 * Kotlin port unconditionally supports key-values, so the `feature = "kv"`
 * cfg is dropped in translation.
 */
class LibKeyValuesTest {
    @Test
    fun testRecordKeyValuesBuilder() {
        var seenPairs = 0
        val visitor = object : io.github.kotlinmania.log.kv.VisitSource {
            override fun visitPair(
                key: io.github.kotlinmania.log.kv.Key,
                value: io.github.kotlinmania.log.kv.Value,
            ): Result<Unit> {
                seenPairs += 1
                return Result.success(Unit)
            }
        }

        val source = io.github.kotlinmania.log.kv.Source { v ->
            v.visitPair(
                io.github.kotlinmania.log.kv.Key.fromStr("a"),
                1.toValue(),
            ).getOrElse { return@Source Result.failure(it) }
            v.visitPair(
                io.github.kotlinmania.log.kv.Key.fromStr("b"),
                2.toValue(),
            )
        }

        val recordTest = Record.builder()
            .target("test")
            .keyValues(source)
            .build()

        recordTest.keyValues().visit(visitor).getOrThrow()
        assertEquals(2, seenPairs)
    }

    @Test
    fun testRecordKeyValuesGetCoerce() {
        val source = io.github.kotlinmania.log.kv.Source { v ->
            v.visitPair(
                io.github.kotlinmania.log.kv.Key.fromStr("a"),
                io.github.kotlinmania.log.kv.Value.from("1"),
            ).getOrElse { return@Source Result.failure(it) }
            v.visitPair(
                io.github.kotlinmania.log.kv.Key.fromStr("b"),
                io.github.kotlinmania.log.kv.Value.from("2"),
            )
        }
        val record = Record.builder()
            .target("test")
            .keyValues(source)
            .build()

        val got = record.keyValues().get(io.github.kotlinmania.log.kv.Key.fromStr("b"))
            ?: error("missing key")
        assertEquals("2", got.toBorrowedStr())
    }
}

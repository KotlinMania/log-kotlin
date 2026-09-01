// port-lint: source lib.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.log

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.native.HiddenFromObjC

/**
 * A lightweight logging facade.
 *
 * The `log` module provides a single logging API that abstracts over the
 * actual logging implementation. Libraries can use the logging API provided
 * by this module, and the consumer of those libraries can choose the logging
 * implementation that is most suitable for its use case.
 *
 * If no logging implementation is selected, the facade falls back to a no-op
 * implementation that ignores all log messages.
 *
 * A log request consists of a target, a level, and a body. A target is a
 * string which defaults to the module path of the location of the log request,
 * though that default may be overridden. Logger implementations typically use
 * the target to filter requests based on some user configuration.
 *
 * Avoid writing expressions with side-effects in log statements. They may not be evaluated.
 */

internal val LOG_LEVEL_NAMES: Array<String> = arrayOf("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE")

internal const val SET_LOGGER_ERROR: String =
    "attempted to set a logger after the logging system was already initialized"

internal const val LEVEL_PARSE_ERROR: String =
    "attempted to convert a string that doesn't match an existing log level"

/**
 * An enum representing the available verbosity levels of the logger.
 */
public enum class Level(
    internal val discriminant: Int,
) {
    /**
     * The "error" level.
     *
     * Designates very serious errors.
     */
    Error(1),

    /**
     * The "warn" level.
     *
     * Designates hazardous situations.
     */
    Warn(2),

    /**
     * The "info" level.
     *
     * Designates useful information.
     */
    Info(3),

    /**
     * The "debug" level.
     *
     * Designates lower priority information.
     */
    Debug(4),

    /**
     * The "trace" level.
     *
     * Designates very low priority, often extremely verbose, information.
     */
    Trace(5),
    ;

    internal fun toUsize(): Int = discriminant

    /**
     * Returns the most verbose logging level.
     */
    public fun max(): Level = Trace

    /**
     * Converts this [Level] to the equivalent [LevelFilter].
     */
    public fun toLevelFilter(): LevelFilter {
        return LevelFilter.fromUsize(discriminant)!!
    }

    /**
     * Returns the string representation of the [Level].
     */
    public fun asStr(): String = LOG_LEVEL_NAMES[discriminant]

    /**
     * Iterate through all supported logging levels.
     *
     * The order of iteration is from more severe to less severe log messages.
     */
    public fun iter(): Iterator<Level> = (1..5).asSequence().map { idx -> fromUsize(idx)!! }.iterator()

    /**
     * Get the next-highest [Level] from this one.
     *
     * If the current [Level] is at the highest level, the returned [Level] will be the same as the
     * current one.
     */
    public fun incrementSeverity(): Level {
        return fromUsize(discriminant + 1) ?: this
    }

    /**
     * Get the next-lowest [Level] from this one.
     *
     * If the current [Level] is at the lowest level, the returned [Level] will be the same as the
     * current one.
     */
    public fun decrementSeverity(): Level {
        val next = (discriminant - 1).coerceAtLeast(1)
        return fromUsize(next) ?: this
    }

    public override fun toString(): String = asStr()

    public companion object {
        internal fun fromUsize(u: Int): Level? =
            when (u) {
                1 -> Error
                2 -> Warn
                3 -> Info
                4 -> Debug
                5 -> Trace
                else -> null
            }

        public fun fromStr(level: String): Result<Level> {
            for (idx in 1 until LOG_LEVEL_NAMES.size) {
                if (LOG_LEVEL_NAMES[idx].equals(level, ignoreCase = true)) {
                    return Result.success(fromUsize(idx)!!)
                }
            }
            return Result.failure(ParseLevelError())
        }
    }
}

/**
 * An enum representing the available verbosity level filters of the logger.
 *
 * A [LevelFilter] may be compared directly to a [Level]. Use this type
 * to get and set the maximum log level with [maxLevel] and [setMaxLevel].
 */
public enum class LevelFilter(
    internal val discriminant: Int,
) {
    /**
     * A level lower than all log levels.
     */
    Off(0),

    /**
     * Corresponds to the [Level.Error] log level.
     */
    Error(1),

    /**
     * Corresponds to the [Level.Warn] log level.
     */
    Warn(2),

    /**
     * Corresponds to the [Level.Info] log level.
     */
    Info(3),

    /**
     * Corresponds to the [Level.Debug] log level.
     */
    Debug(4),

    /**
     * Corresponds to the [Level.Trace] log level.
     */
    Trace(5),
    ;

    /**
     * Converts `this` to the equivalent [Level].
     *
     * Returns `null` if `this` is [LevelFilter.Off].
     */
    public fun toLevel(): Level? = Level.fromUsize(discriminant)

    /**
     * Returns the string representation of the [LevelFilter].
     *
     * This returns the same string as the [toString] implementation.
     */
    public fun asStr(): String = LOG_LEVEL_NAMES[discriminant]

    /**
     * Iterate through all supported filtering levels.
     *
     * The order of iteration is from less to more verbose filtering.
     */
    public fun iter(): Iterator<LevelFilter> = (0..5).asSequence().map { idx -> fromUsize(idx)!! }.iterator()

    /**
     * Get the next-highest [LevelFilter] from this one.
     *
     * If the current [LevelFilter] is at the highest level, the returned [LevelFilter] will be the
     * same as the current one.
     */
    public fun incrementSeverity(): LevelFilter {
        return fromUsize(discriminant + 1) ?: this
    }

    /**
     * Get the next-lowest [LevelFilter] from this one.
     *
     * If the current [LevelFilter] is at the lowest level, the returned [LevelFilter] will be the
     * same as the current one.
     */
    public fun decrementSeverity(): LevelFilter {
        val next = (discriminant - 1).coerceAtLeast(0)
        return fromUsize(next) ?: this
    }

    public override fun toString(): String = asStr()

    public companion object {
        internal fun fromUsize(u: Int): LevelFilter? =
            when (u) {
                0 -> Off
                1 -> Error
                2 -> Warn
                3 -> Info
                4 -> Debug
                5 -> Trace
                else -> null
            }

        public fun fromStr(level: String): Result<LevelFilter> {
            for (idx in 0 until LOG_LEVEL_NAMES.size) {
                if (LOG_LEVEL_NAMES[idx].equals(level, ignoreCase = true)) {
                    return Result.success(fromUsize(idx)!!)
                }
            }
            return Result.failure(ParseLevelError())
        }
    }
}

/**
 * Compare a [Level] directly to a [LevelFilter].
 */
public operator fun Level.compareTo(other: LevelFilter): Int = discriminant.compareTo(other.discriminant)

/**
 * Compare a [LevelFilter] directly to a [Level].
 */
public operator fun LevelFilter.compareTo(other: Level): Int = discriminant.compareTo(other.discriminant)

/**
 * The "payload" of a log message.
 */
public data class Record(
    private val metadata: Metadata,
    private val args: Arguments,
    private val modulePath: String?,
    private val file: String?,
    private val line: Int?,
    private val keyValues: io.github.kotlinmania.log.kv.Source?,
) {
    public companion object {
        /**
         * Returns a new builder.
         */
        public fun builder(): RecordBuilder = RecordBuilder.new()
    }

    /**
     * The message body.
     */
    public fun args(): Arguments = args

    /**
     * Metadata about the log directive.
     */
    public fun metadata(): Metadata = metadata

    /**
     * The verbosity level of the message.
     */
    public fun level(): Level = metadata.level()

    /**
     * The name of the target of the directive.
     */
    public fun target(): String = metadata.target()

    /**
     * The module path of the message.
     */
    public fun modulePath(): String? = modulePath

    /**
     * The source file containing the message.
     */
    public fun file(): String? = file

    /**
     * The line containing the message.
     */
    public fun line(): Int? = line

    /**
     * The structured key-value pairs associated with the message.
     */
    public fun keyValues(): io.github.kotlinmania.log.kv.Source {
        return keyValues ?: io.github.kotlinmania.log.kv.Source { Result.success(Unit) }
    }

    /**
     * Create a new [RecordBuilder] based on this record.
     */
    public fun toBuilder(): RecordBuilder {
        return RecordBuilder(
            record =
                Record(
                    metadata = metadata,
                    args = args,
                    modulePath = modulePath,
                    file = file,
                    line = line,
                    keyValues = keyValues,
                ),
        )
    }
}

/**
 * Builder for [Record].
 */
public class RecordBuilder internal constructor(
    internal val record: Record,
) {
    public companion object {
        /**
         * Construct new [RecordBuilder].
         */
        public fun new(): RecordBuilder {
            return RecordBuilder(
                record =
                    Record(
                        args = Arguments("", emptyList()),
                        metadata = Metadata.builder().build(),
                        modulePath = null,
                        file = null,
                        line = null,
                        keyValues = null,
                    ),
            )
        }
    }

    /**
     * Set args.
     */
    public fun args(args: Arguments): RecordBuilder {
        return RecordBuilder(record.copy(args = args))
    }

    /**
     * Set metadata.
     */
    public fun metadata(metadata: Metadata): RecordBuilder {
        return RecordBuilder(record.copy(metadata = metadata))
    }

    /**
     * Set level.
     */
    public fun level(level: Level): RecordBuilder {
        return RecordBuilder(record.copy(metadata = record.metadata().toBuilder().level(level).build()))
    }

    /**
     * Set target.
     */
    public fun target(target: String): RecordBuilder {
        return RecordBuilder(record.copy(metadata = record.metadata().toBuilder().target(target).build()))
    }

    /**
     * Set modulePath.
     */
    public fun modulePath(path: String?): RecordBuilder {
        return RecordBuilder(record.copy(modulePath = path))
    }

    /**
     * Set modulePath to a static string.
     */
    public fun modulePathStatic(path: String?): RecordBuilder {
        return modulePath(path)
    }

    /**
     * Set file.
     */
    public fun file(file: String?): RecordBuilder {
        return RecordBuilder(record.copy(file = file))
    }

    /**
     * Set file to a static string.
     */
    public fun fileStatic(file: String?): RecordBuilder {
        return file(file)
    }

    /**
     * Set line.
     */
    public fun line(line: Int?): RecordBuilder {
        return RecordBuilder(record.copy(line = line))
    }

    /**
     * Set keyValues.
     */
    public fun keyValues(kvs: io.github.kotlinmania.log.kv.Source?): RecordBuilder {
        return RecordBuilder(record.copy(keyValues = kvs))
    }

    /**
     * Invoke the builder and return a [Record].
     */
    public fun build(): Record = record
}

/**
 * Metadata about a log message.
 */
public data class Metadata(
    internal val level: Level,
    internal val target: String,
) {
    public companion object {
        /**
         * Returns a new builder.
         */
        public fun builder(): MetadataBuilder = MetadataBuilder.new()
    }

    /**
     * The verbosity level of the message.
     */
    public fun level(): Level = level

    /**
     * The name of the target of the directive.
     */
    public fun target(): String = target

    public fun toBuilder(): MetadataBuilder = MetadataBuilder(metadata = this)
}

/**
 * Builder for [Metadata].
 */
public class MetadataBuilder internal constructor(
    internal val metadata: Metadata,
) {
    public companion object {
        /**
         * Construct a new [MetadataBuilder].
         *
         * The default options are:
         *
         * - level: [Level.Info]
         * - target: ""
         */
        public fun new(): MetadataBuilder {
            return MetadataBuilder(
                metadata =
                    Metadata(
                        level = Level.Info,
                        target = "",
                    ),
            )
        }
    }

    /**
     * Setter for [Metadata.level].
     */
    public fun level(arg: Level): MetadataBuilder {
        return MetadataBuilder(metadata.copy(level = arg))
    }

    /**
     * Setter for [Metadata.target].
     */
    public fun target(target: String): MetadataBuilder {
        return MetadataBuilder(metadata.copy(target = target))
    }

    /**
     * Returns a [Metadata] object.
     */
    public fun build(): Metadata = metadata
}

/**
 * An interface encapsulating the operations required of a logger.
 */
public interface Log {
    /**
     * Determines if a log message with the specified metadata would be logged.
     */
    public fun enabled(metadata: Metadata): Boolean

    /**
     * Logs the [Record].
     */
    public fun log(record: Record)

    /**
     * Flushes any buffered records.
     */
    public fun flush()
}

/**
 * A dummy initial value for the global logger.
 */
internal object NopLogger : Log {
    override fun enabled(metadata: Metadata): Boolean = false

    override fun log(record: Record) {}

    override fun flush() {}
}

// The LOGGER static holds a reference to the global logger. It is protected by the STATE value.

private const val UNINITIALIZED: Long = 0
private const val INITIALIZING: Long = 1
private const val INITIALIZED: Long = 2

@OptIn(ExperimentalAtomicApi::class)
private val STATE: AtomicLong = AtomicLong(UNINITIALIZED)

@OptIn(ExperimentalAtomicApi::class)
private val LOGGER: AtomicReference<Log> = AtomicReference(NopLogger)

@OptIn(ExperimentalAtomicApi::class)
private val MAX_LOG_LEVEL_FILTER: AtomicLong = AtomicLong(0)

/**
 * Sets the global maximum log level.
 *
 * Generally, this should only be called by the active logging implementation.
 */
public fun setMaxLevel(level: LevelFilter) {
    MAX_LOG_LEVEL_FILTER.store(level.discriminant.toLong())
}

/**
 * An unsafely-racy version of [setMaxLevel].
 */
public fun setMaxLevelRacy(level: LevelFilter) {
    setMaxLevel(level)
}

/**
 * Returns the current maximum log level.
 */
public fun maxLevel(): LevelFilter {
    val raw = MAX_LOG_LEVEL_FILTER.load().toInt()
    return LevelFilter.fromUsize(raw) ?: LevelFilter.Off
}

/**
 * Sets the global logger to a global singleton [Log] instance.
 *
 * This function may only be called once during a program. Any log events that occur
 * before the call to this function completes will be ignored.
 */
public fun setLogger(logger: Log): Result<Unit> {
    val previous = STATE.compareAndExchange(UNINITIALIZED, INITIALIZING)
    return when (previous) {
        UNINITIALIZED -> {
            LOGGER.store(logger)
            STATE.store(INITIALIZED)
            Result.success(Unit)
        }
        INITIALIZING -> {
            while (STATE.load() == INITIALIZING) {
                // spin
            }
            Result.failure(SetLoggerError())
        }
        else -> Result.failure(SetLoggerError())
    }
}

/**
 * Sets the global logger to a boxed [Log] instance. In Kotlin, identical to [setLogger].
 */
public fun setBoxedLogger(logger: Log): Result<Unit> = setLogger(logger)

/**
 * An unsafely-racy version of [setLogger].
 */
public fun setLoggerRacy(logger: Log): Result<Unit> = setLogger(logger)

/**
 * Returns a reference to the logger.
 *
 * If a logger has not been set, a no-op implementation is returned.
 */
public fun logger(): Log {
    return if (STATE.load() != INITIALIZED) NopLogger else LOGGER.load()
}

/**
 * The statically resolved maximum log level.
 *
 * This value is checked by the log macros, but not by the [Log] instance returned by
 * [logger]. Code that manually calls functions on that value should compare the
 * level against this value.
 */
public val STATIC_MAX_LEVEL: LevelFilter = LevelFilter.Trace

/**
 * The type returned by [Level.fromStr] and [LevelFilter.fromStr] when the string doesn't match any of the log levels.
 *
 * Hidden from Swift Export: extending `kotlin.Exception` drags the
 * `Throwable.stackTrace`/`Array<Any?>` bridge into the generated Swift module
 * and that bridge fails `-Werror`. Kotlin callers continue to receive a real
 * `Exception` subclass via `Result.failure(...)`.
 */
@HiddenFromObjC
public class ParseLevelError : Exception(LEVEL_PARSE_ERROR)

/**
 * The type returned by [setLogger] if a logger has already been set.
 *
 * Hidden from Swift Export for the same reason as [ParseLevelError].
 */
@HiddenFromObjC
public class SetLoggerError : Exception(SET_LOGGER_ERROR)

/**
 * A lightweight stand-in for formatting arguments.
 */
public data class Arguments(
    public val format: String,
    public val args: List<Any?>,
) {
    public override fun toString(): String {
        if (args.isEmpty()) return format
        return buildString {
            append(format)
            append(" ")
            append(args.joinToString(prefix = "[", postfix = "]") { it.toString() })
        }
    }
}

/**
 * Caller location metadata.
 */
public data class Location(
    public val file: String?,
    public val line: Int?,
) {
    public companion object {
        public fun caller(): Location {
            return Location(file = null, line = null)
        }
    }
}

internal object LibTests {
    private fun assertEquals(expected: Any?, actual: Any?) {
        check(expected == actual) { "expected=$expected actual=$actual" }
    }

    fun testLevelFilterFromStr() {
        assertEquals(LevelFilter.Off, LevelFilter.fromStr("off").getOrThrow())
        assertEquals(LevelFilter.Error, LevelFilter.fromStr("error").getOrThrow())
        assertEquals(LevelFilter.Warn, LevelFilter.fromStr("warn").getOrThrow())
        assertEquals(LevelFilter.Info, LevelFilter.fromStr("info").getOrThrow())
        assertEquals(LevelFilter.Debug, LevelFilter.fromStr("debug").getOrThrow())
        assertEquals(LevelFilter.Trace, LevelFilter.fromStr("trace").getOrThrow())
    }

    fun testLevelFromStr() {
        check(Level.fromStr("OFF").isFailure)
        assertEquals(Level.Error, Level.fromStr("error").getOrThrow())
        assertEquals(Level.Warn, Level.fromStr("warn").getOrThrow())
        assertEquals(Level.Info, Level.fromStr("info").getOrThrow())
        assertEquals(Level.Debug, Level.fromStr("debug").getOrThrow())
        assertEquals(Level.Trace, Level.fromStr("trace").getOrThrow())
    }

    fun testLevelAsStr() {
        assertEquals("ERROR", Level.Error.asStr())
        assertEquals("WARN", Level.Warn.asStr())
        assertEquals("INFO", Level.Info.asStr())
        assertEquals("DEBUG", Level.Debug.asStr())
        assertEquals("TRACE", Level.Trace.asStr())
    }

    fun testLevelShow() {
        assertEquals("INFO", Level.Info.toString())
        assertEquals("ERROR", Level.Error.toString())
    }

    fun testLevelFilterShow() {
        assertEquals("OFF", LevelFilter.Off.toString())
        assertEquals("ERROR", LevelFilter.Error.toString())
    }

    fun testMetadataBuilder() {
        val target = "myApp"
        val metadataTest = MetadataBuilder.new().level(Level.Debug).target(target).build()
        assertEquals(Level.Debug, metadataTest.level())
        assertEquals("myApp", metadataTest.target())
    }

    fun testMetadataConvenienceBuilder() {
        val target = "myApp"
        val metadataTest = Metadata.builder().level(Level.Debug).target(target).build()
        assertEquals(Level.Debug, metadataTest.level())
        assertEquals("myApp", metadataTest.target())
    }

    fun testRecordBuilder() {
        val target = "myApp"
        val metadata = MetadataBuilder.new().target(target).build()
        val fmtArgs = Arguments("hello", emptyList())
        val recordTest =
            RecordBuilder.new()
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

    fun testRecordConvenienceBuilder() {
        val target = "myApp"
        val metadata = Metadata.builder().target(target).build()
        val fmtArgs = Arguments("hello", emptyList())
        val recordTest =
            Record.builder()
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

    fun testRecordCompleteBuilder() {
        val target = "myApp"
        val recordTest =
            Record.builder()
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
}

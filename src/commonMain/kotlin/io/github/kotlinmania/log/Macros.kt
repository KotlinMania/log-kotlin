// port-lint: source macros.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.log

import io.github.kotlinmania.log.kv.ToValue
import io.github.kotlinmania.log.kv.Value
import kotlin.native.HiddenFromObjC


/**
 * The standard logging macro.
 *
 * This function will generically log with the specified [Level] and format-style
 * argument list.
 *
 * ```
 * val data = Pair(42, "Forty-two")
 * val privateData = "private"
 *
 * log(Level.Error, formatArgs("Received errors: {}, {}", data.first, data.second))
 * ```
 *
 * Optionally, you can specify a `target` argument to attach a specific target
 * to the log record. By default, the target is the module path of the caller.
 *
 * ```
 * val data = Pair(42, "Forty-two")
 * val privateData = "private"
 *
 * log(
 *     target = "appEvents",
 *     level = Level.Error,
 *     args = formatArgs("Received errors: {}, {}", data.first, data.second),
 * )
 * ```
 *
 * And optionally, you can specify a `logger` argument to use a specific logger
 * instead of the default global logger.
 *
 * ```
 * class MyLogger : Log {
 *     override fun enabled(metadata: Metadata): Boolean = false
 *     override fun log(record: Record) {}
 *     override fun flush() {}
 * }
 *
 * val data = Pair(42, "Forty-two")
 * val privateData = "private"
 *
 * val myLogger = MyLogger()
 * log(
 *     logger = myLogger,
 *     level = Level.Error,
 *     args = formatArgs("Received errors: {}, {}", data.first, data.second),
 * )
 * ```
 *
 * The `logger` argument accepts a value that implements the [Log] interface. The value
 * will be used directly within the function.
 *
 * Note that the global level set through [setMaxLevel] will still apply, even when a
 * custom logger is supplied with the `logger` argument.
 */
public fun log(
    level: Level,
    args: Arguments,
) {
    logImpl(
        logger = __logGlobalLogger,
        target = modulePath(),
        level = level,
        kvs = null,
        args = args,
    )
}

public fun log(
    target: String,
    level: Level,
    args: Arguments,
) {
    logImpl(
        logger = __logGlobalLogger,
        target = target,
        level = level,
        kvs = null,
        args = args,
    )
}

public fun log(
    logger: Log,
    level: Level,
    args: Arguments,
) {
    logImpl(
        logger = logger,
        target = modulePath(),
        level = level,
        kvs = null,
        args = args,
    )
}

public fun log(
    logger: Log,
    target: String,
    level: Level,
    args: Arguments,
) {
    logImpl(
        logger = logger,
        target = target,
        level = level,
        kvs = null,
        args = args,
    )
}

/**
 * Hidden from Swift Export: a `vararg Pair<String, Value>` parameter
 * bridges the `kotlin.Pair` and `kotlin.Array` stdlib surfaces, whose
 * generated bridge files fail `-Werror` on unchecked casts. Kotlin
 * callers use this overload normally; Swift callers should compose the
 * key-values through a `Source` directly.
 */
@HiddenFromObjC
public fun log(
    logger: Log,
    target: String,
    level: Level,
    args: Arguments,
    vararg kvs: Pair<String, Value>,
) {
    logImpl(
        logger = logger,
        target = target,
        level = level,
        kvs = if (kvs.isEmpty()) null else kvs.asList(),
        args = args,
    )
}

private fun logImpl(
    logger: Log,
    target: String,
    level: Level,
    kvs: List<Pair<String, Value>>?,
    args: Arguments,
) {
    val lvl = level
    if (lvl <= STATIC_MAX_LEVEL && lvl <= maxLevel()) {
        io.github.kotlinmania.log.log(
            logger = logger,
            args = args,
            level = lvl,
            targetModulePathAndLoc = Triple(target, modulePath(), loc()),
            kvs = if (kvs == null) KVs.Empty else KVs.Slice(kvs),
        )
    }
}

/**
 * Logs a message at the error level.
 */
public fun error(args: Arguments) {
    log(Level.Error, args)
}

public fun error(target: String, args: Arguments) {
    log(target, Level.Error, args)
}

public fun error(logger: Log, args: Arguments) {
    log(logger, Level.Error, args)
}

public fun error(logger: Log, target: String, args: Arguments) {
    log(logger, target, Level.Error, args)
}

/**
 * Logs a message at the warn level.
 */
public fun warn(args: Arguments) {
    log(Level.Warn, args)
}

public fun warn(target: String, args: Arguments) {
    log(target, Level.Warn, args)
}

public fun warn(logger: Log, args: Arguments) {
    log(logger, Level.Warn, args)
}

public fun warn(logger: Log, target: String, args: Arguments) {
    log(logger, target, Level.Warn, args)
}

/**
 * Logs a message at the info level.
 */
public fun info(args: Arguments) {
    log(Level.Info, args)
}

public fun info(target: String, args: Arguments) {
    log(target, Level.Info, args)
}

public fun info(logger: Log, args: Arguments) {
    log(logger, Level.Info, args)
}

public fun info(logger: Log, target: String, args: Arguments) {
    log(logger, target, Level.Info, args)
}

/**
 * Logs a message at the debug level.
 */
public fun debug(args: Arguments) {
    log(Level.Debug, args)
}

public fun debug(target: String, args: Arguments) {
    log(target, Level.Debug, args)
}

public fun debug(logger: Log, args: Arguments) {
    log(logger, Level.Debug, args)
}

public fun debug(logger: Log, target: String, args: Arguments) {
    log(logger, target, Level.Debug, args)
}

/**
 * Logs a message at the trace level.
 */
public fun trace(args: Arguments) {
    log(Level.Trace, args)
}

public fun trace(target: String, args: Arguments) {
    log(target, Level.Trace, args)
}

public fun trace(logger: Log, args: Arguments) {
    log(logger, Level.Trace, args)
}

public fun trace(logger: Log, target: String, args: Arguments) {
    log(logger, target, Level.Trace, args)
}

/**
 * Determines if a log message with the specified [Level] would be logged.
 */
public fun logEnabled(level: Level): Boolean {
    return logEnabled(level = level, target = modulePath(), logger = __logGlobalLogger)
}

public fun logEnabled(level: Level, target: String): Boolean {
    return logEnabled(level = level, target = target, logger = __logGlobalLogger)
}

public fun logEnabled(level: Level, target: String, logger: Log): Boolean {
    val lvl = level
    return lvl <= STATIC_MAX_LEVEL && lvl <= maxLevel() && enabled(logger, lvl, target)
}

@HiddenFromObjC
public val __logGlobalLogger: GlobalLogger = GlobalLogger()

// Helpers for building structured key-values.
//
// Each helper returns `Pair<String, Value>` because the macros above accept
// `vararg Pair<String, Value>`. All four are hidden from Swift Export
// because returning `kotlin.Pair` from a public function bridges the stdlib
// `Pair` surface and triggers the `Array<Any?>` unchecked-cast warnings
// that fail `-Werror` in the generated Swift module.

@HiddenFromObjC
public fun kv(key: String, value: Value): Pair<String, Value> = Pair(key, value)

@HiddenFromObjC
public fun kvToValue(key: String, value: ToValue): Pair<String, Value> = Pair(key, captureToValue(value))

@HiddenFromObjC
public fun kvDebug(key: String, value: Any?): Pair<String, Value> = Pair(key, captureDebug(value))

@HiddenFromObjC
public fun kvDisplay(key: String, value: Any?): Pair<String, Value> = Pair(key, captureDisplay(value))

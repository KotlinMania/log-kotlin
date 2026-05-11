// port-lint: source __private_api.rs
package io.github.kotlinmania.log

import io.github.kotlinmania.log.kv.Key
import io.github.kotlinmania.log.kv.Source
import io.github.kotlinmania.log.kv.Value

/**
 * WARNING: this is not part of the crate's public API and is subject to change at any time.
 */


public fun formatArgs(format: String, vararg args: Any?): Arguments {
    return Arguments(format, args.toList())
}

public fun modulePath(): String {
    // Kotlin has no direct equivalent of the upstream module-path macro.
    // This is best-effort and intentionally lightweight.
    return "<module-path>"
}

public fun stringify(value: Any?): String {
    return value.toString()
}

// Types for the `kv` argument.

internal sealed interface KVs {
    public fun intoKvs(): Source?

    public data class Slice(private val kvs: List<Pair<String, Value>>) : KVs {
        override fun intoKvs(): Source = kvs.intoSource()
    }

    public data object Empty : KVs {
        override fun intoKvs(): Source? = null
    }
}

private fun List<Pair<String, Value>>.intoSource(): Source =
    Source { visitor ->
        for ((key, value) in this) {
            visitor.visitPair(Key.fromStr(key), value).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

// Log implementation.

/**
 * The global logger proxy.
 */
public class GlobalLogger : Log {
    override fun enabled(metadata: Metadata): Boolean {
        return logger().enabled(metadata)
    }

    override fun log(record: Record) {
        logger().log(record)
    }

    override fun flush() {
        logger().flush()
    }
}

// Split from `log` to reduce generics and code size
private fun <L : Log> logImpl(
    logger: L,
    args: Arguments,
    level: Level,
    targetModulePathAndLoc: Triple<String, String, Location>,
    kvs: Source?,
) {
    val (target, modulePath, loc) = targetModulePathAndLoc

    val builder = Record.builder()
        .args(args)
        .level(level)
        .target(target)
        .modulePathStatic(modulePath)
        .fileStatic(loc.file)
        .line(loc.line)
        .keyValues(kvs)

    logger.log(builder.build())
}

internal fun <L : Log> log(
    logger: L,
    args: Arguments,
    level: Level,
    targetModulePathAndLoc: Triple<String, String, Location>,
    kvs: KVs,
) {
    logImpl(
        logger,
        args,
        level,
        targetModulePathAndLoc,
        kvs.intoKvs(),
    )
}

public fun <L : Log> enabled(logger: L, level: Level, target: String): Boolean {
    return logger.enabled(Metadata.builder().level(level).target(target).build())
}

public fun loc(): Location {
    return Location.caller()
}

// In this Kotlin port, these helpers are always available.

public fun <V : io.github.kotlinmania.log.kv.ToValue> captureToValue(v: V): Value {
    return v.toValue()
}

public fun captureDebug(v: Any?): Value {
    return Value.fromDebug(v)
}

public fun captureDisplay(v: Any?): Value {
    return Value.fromDisplay(v)
}

public fun captureError(v: Throwable): Value {
    return Value.fromDynError(v)
}

public fun captureSval(v: Any?): Value {
    return Value.fromSval(v)
}

public fun captureSerde(v: Any?): Value {
    return Value.fromSerde(v)
}

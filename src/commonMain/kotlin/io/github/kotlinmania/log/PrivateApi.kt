// port-lint: source src/__private_api.rs
package io.github.kotlinmania.log

/**
 * WARNING: this is not part of the crate's public API and is subject to change at any time.
 */


public fun formatArgs(format: String, vararg args: Any?): Arguments {
    return Arguments(format, args.toList())
}

public fun modulePath(): String {
    // Kotlin has no direct equivalent of Rust's `module_path!()`.
    // This is best-effort and intentionally lightweight.
    return "<module-path>"
}

public fun stringify(value: Any?): String {
    return value.toString()
}

// In this Kotlin port, structured key-values are always represented using `kv.Value`.
public typealias Value = io.github.kotlinmania.log.kv.Value

// Types for the `kv` argument.

internal sealed interface KVs {
    public fun intoKvs(): List<Pair<String, Value>>?

    public data class Slice(private val kvs: List<Pair<String, Value>>) : KVs {
        override fun intoKvs(): List<Pair<String, Value>>? = kvs
    }

    public data object Empty : KVs {
        override fun intoKvs(): List<Pair<String, Value>>? = null
    }
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
    kvs: List<Pair<String, Value>>?,
) {
    val (target, modulePath, loc) = targetModulePathAndLoc

    val builder = Record.builder()

    builder
        .args(args)
        .level(level)
        .target(target)
        .modulePathStatic(modulePath)
        .fileStatic(loc.file)
        .line(loc.line)

    builder.keyValues(kvs)

    logger.log(builder.build())
}

public fun <L : Log> log(
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

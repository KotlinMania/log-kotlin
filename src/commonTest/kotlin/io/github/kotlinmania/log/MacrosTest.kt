// port-lint: source tests/macros.rs
package io.github.kotlinmania.log

import io.github.kotlinmania.log.kv.toValue
import kotlin.test.Test

/**
 * The upstream `Logger` test type: `enabled()` returns `false`, so no log
 * call observably reaches the body of the user-supplied helper. The macros
 * test still exercises every overload to confirm that the call surface
 * accepts each combination of `logger`, `target`, and key-value arguments.
 */
private class MacrosLogger : Log {
    override fun enabled(metadata: Metadata): Boolean = false
    override fun log(record: Record) {}
    override fun flush() {}
}

/**
 * Invokes every per-level helper with the given message and no logger /
 * target overrides; the Kotlin counterpart of upstream's
 * `all_log_macros!($($arg:tt)*)` shorthand.
 */
private fun allLogMacros(args: Arguments) {
    trace(args)
    debug(args)
    info(args)
    warn(args)
    error(args)
}

private fun allLogMacros(target: String, args: Arguments) {
    trace(target, args)
    debug(target, args)
    info(target, args)
    warn(target, args)
    error(target, args)
}

private fun allLogMacros(logger: Log, args: Arguments) {
    trace(logger, args)
    debug(logger, args)
    info(logger, args)
    warn(logger, args)
    error(logger, args)
}

private fun allLogMacros(logger: Log, target: String, args: Arguments) {
    trace(logger, target, args)
    debug(logger, target, args)
    info(logger, target, args)
    warn(logger, target, args)
    error(logger, target, args)
}

class MacrosTest {
    @Test
    fun noArgs() {
        val logger = MacrosLogger()

        for (lvl in Level.entries) {
            log(lvl, formatArgs("hello"))

            log(target = "my_target", level = lvl, args = formatArgs("hello"))

            log(logger = logger, level = lvl, args = formatArgs("hello"))

            log(logger = logger, target = "my_target", level = lvl, args = formatArgs("hello"))
        }

        allLogMacros(formatArgs("hello"))

        allLogMacros(target = "my_target", args = formatArgs("hello"))

        allLogMacros(logger = logger, args = formatArgs("hello"))

        allLogMacros(logger = logger, target = "my_target", args = formatArgs("hello"))
    }

    @Test
    fun anonymousArgs() {
        for (lvl in Level.entries) {
            log(lvl, formatArgs("hello {}", "world"))

            log(target = "my_target", level = lvl, args = formatArgs("hello {}", "world"))

            log(lvl, formatArgs("hello {}", "world"))
        }

        allLogMacros(formatArgs("hello {}", "world"))

        allLogMacros(target = "my_target", args = formatArgs("hello {}", "world"))

        val logger = MacrosLogger()

        allLogMacros(logger = logger, args = formatArgs("hello {}", "world"))

        allLogMacros(logger = logger, target = "my_target", args = formatArgs("hello {}", "world"))
    }

    @Test
    fun namedArgs() {
        for (lvl in Level.entries) {
            log(lvl, formatArgs("hello {world}", "world"))

            log(target = "my_target", level = lvl, args = formatArgs("hello {world}", "world"))

            log(lvl, formatArgs("hello {world}", "world"))
        }

        allLogMacros(formatArgs("hello {world}", "world"))

        allLogMacros(target = "my_target", args = formatArgs("hello {world}", "world"))

        val logger = MacrosLogger()

        allLogMacros(logger = logger, args = formatArgs("hello {world}", "world"))

        allLogMacros(logger = logger, target = "my_target", args = formatArgs("hello {world}", "world"))
    }

    @Test
    fun inlinedArgs() {
        val world = "world"

        for (lvl in Level.entries) {
            log(lvl, formatArgs("hello $world"))

            log(target = "my_target", level = lvl, args = formatArgs("hello $world"))

            log(lvl, formatArgs("hello $world"))
        }

        allLogMacros(formatArgs("hello $world"))

        allLogMacros(target = "my_target", args = formatArgs("hello $world"))

        val logger = MacrosLogger()

        allLogMacros(logger = logger, args = formatArgs("hello $world"))

        allLogMacros(logger = logger, target = "my_target", args = formatArgs("hello $world"))
    }

    @Test
    fun logEnabledOverloads() {
        val logger = MacrosLogger()

        for (lvl in Level.entries) {
            logEnabled(lvl)
            logEnabled(level = lvl, target = "my_target")
            logEnabled(level = lvl, target = "my_target", logger = logger)
            logEnabled(level = lvl, target = modulePath(), logger = logger)
        }
    }

    @Test
    fun expr() {
        val logger = MacrosLogger()

        for (lvl in Level.entries) {
            log(lvl, formatArgs("hello"))

            log(logger = logger, level = lvl, args = formatArgs("hello"))
        }
    }

    @Test
    fun kvNoArgs() {
        val logger = MacrosLogger()

        for (lvl in Level.entries) {
            log(
                logger = logger,
                target = "my_target",
                level = lvl,
                args = formatArgs("hello"),
                kvs = arrayOf(
                    kv("cat_1", "chashu".toValue()),
                    kv("cat_2", "nori".toValue()),
                    kv("cat_count", 2.toValue()),
                ),
            )
        }
    }

    @Test
    fun kvExprArgs() {
        val logger = MacrosLogger()

        for (lvl in Level.entries) {
            val catMath = run {
                var x = 0
                x += 1
                x + 1
            }
            log(
                logger = logger,
                target = "my_target",
                level = lvl,
                args = formatArgs("hello"),
                kvs = arrayOf(kv("cat_math", catMath.toValue())),
            )
        }
    }
}

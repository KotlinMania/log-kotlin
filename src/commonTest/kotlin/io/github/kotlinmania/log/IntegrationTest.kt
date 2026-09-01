// port-lint: source tests/integration.rs
package io.github.kotlinmania.log

import kotlin.test.Test
import kotlin.test.assertEquals

private class State {
    var lastLogLevel: Level? = null
    var lastLogLocation: Int? = null
}

private class Logger(private val state: State) : Log {
    override fun enabled(metadata: Metadata): Boolean = true

    override fun log(record: Record) {
        state.lastLogLevel = record.level()
        state.lastLogLocation = record.line()
    }

    override fun flush() {}
}

class IntegrationTest {
    // These tests don't really make sense when static
    // max level filtering is applied.
    @Test
    fun testIntegration() {
        val me = State()
        val a = me
        val logger = Logger(me)

        testFilter(logger, a, LevelFilter.Off)
        testFilter(logger, a, LevelFilter.Error)
        testFilter(logger, a, LevelFilter.Warn)
        testFilter(logger, a, LevelFilter.Info)
        testFilter(logger, a, LevelFilter.Debug)
        testFilter(logger, a, LevelFilter.Trace)

        testLineNumbers(logger, a)
    }

    private fun testFilter(logger: Log, a: State, filter: LevelFilter) {
        // tests to ensure logs with a level beneath 'max_level' are filtered out
        setMaxLevel(filter)
        error(logger, formatArgs(""))
        last(a, t(Level.Error, filter))
        warn(logger, formatArgs(""))
        last(a, t(Level.Warn, filter))
        info(logger, formatArgs(""))
        last(a, t(Level.Info, filter))
        debug(logger, formatArgs(""))
        last(a, t(Level.Debug, filter))
        trace(logger, formatArgs(""))
        last(a, t(Level.Trace, filter))
    }

    private fun t(lvl: Level, filter: LevelFilter): Level? =
        if (lvl <= filter) lvl else null

    private fun last(state: State, expected: Level?) {
        val lvl = state.lastLogLevel
        state.lastLogLevel = null
        assertEquals(expected, lvl)
    }

    private fun testLineNumbers(logger: Log, state: State) {
        setMaxLevel(LevelFilter.Trace)

        info(logger, formatArgs("")) // ensure checkLogLocation function follows log call
        checkLogLocation(state)
    }

    private fun checkLogLocation(state: State) {
        val location = Location.caller().line // get function calling location
        val lineNumber = state.lastLogLocation // get location of most recent log
        state.lastLogLocation = null
        assertEquals(location, lineNumber)
    }
}

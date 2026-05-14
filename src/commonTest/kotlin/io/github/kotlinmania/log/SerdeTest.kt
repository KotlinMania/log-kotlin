// port-lint: source serde.rs
package io.github.kotlinmania.log

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SerdeTest {
    @Test
    fun testLevelSerDe() {
        val cases = listOf(
            Level.Error to "\"ERROR\"",
            Level.Warn to "\"WARN\"",
            Level.Info to "\"INFO\"",
            Level.Debug to "\"DEBUG\"",
            Level.Trace to "\"TRACE\"",
        )

        for ((value, expected) in cases) {
            val encoded = Json.encodeToString(LevelSerializer, value)
            assertEquals(expected, encoded)
            val decoded = Json.decodeFromString(LevelSerializer, encoded)
            assertEquals(value, decoded)
        }
    }

    @Test
    fun testLevelCaseInsensitive() {
        val cases = listOf(
            Level.Error to "\"error\"",
            Level.Warn to "\"warn\"",
            Level.Info to "\"info\"",
            Level.Debug to "\"debug\"",
            Level.Trace to "\"trace\"",
        )

        for ((value, tokens) in cases) {
            assertEquals(value, Json.decodeFromString(LevelSerializer, tokens))
        }
    }

    @Test
    fun testLevelDeError() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString(LevelSerializer, "\"errorx\"")
        }
    }

    @Test
    fun testLevelFilterSerDe() {
        val cases = listOf(
            LevelFilter.Off to "\"OFF\"",
            LevelFilter.Error to "\"ERROR\"",
            LevelFilter.Warn to "\"WARN\"",
            LevelFilter.Info to "\"INFO\"",
            LevelFilter.Debug to "\"DEBUG\"",
            LevelFilter.Trace to "\"TRACE\"",
        )

        for ((value, expected) in cases) {
            val encoded = Json.encodeToString(LevelFilterSerializer, value)
            assertEquals(expected, encoded)
            val decoded = Json.decodeFromString(LevelFilterSerializer, encoded)
            assertEquals(value, decoded)
        }
    }

    @Test
    fun testLevelFilterCaseInsensitive() {
        val cases = listOf(
            LevelFilter.Off to "\"off\"",
            LevelFilter.Error to "\"error\"",
            LevelFilter.Warn to "\"warn\"",
            LevelFilter.Info to "\"info\"",
            LevelFilter.Debug to "\"debug\"",
            LevelFilter.Trace to "\"trace\"",
        )

        for ((value, tokens) in cases) {
            assertEquals(value, Json.decodeFromString(LevelFilterSerializer, tokens))
        }
    }

    @Test
    fun testLevelFilterDeError() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString(LevelFilterSerializer, "\"errorx\"")
        }
    }
}

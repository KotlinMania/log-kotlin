// port-lint: source serde.rs
package io.github.kotlinmania.log

import io.github.kotlinmania.serdetest.Token
import io.github.kotlinmania.serdetest.assertDeTokens
import io.github.kotlinmania.serdetest.assertDeTokensError
import io.github.kotlinmania.serdetest.assertTokens
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerdeTest {
    private fun levelToken(variant: String): Token =
        Token.UnitVariant(
            name = "Level",
            variant = variant,
        )

    private fun levelBytesTokens(variant: ByteArray): List<Token> =
        listOf(
            Token.Enum(name = "Level"),
            Token.Bytes(variant),
            Token.UnitValue,
        )

    private fun levelVariantTokens(variant: UInt): List<Token> =
        listOf(
            Token.Enum(name = "Level"),
            Token.U32(variant),
            Token.UnitValue,
        )

    private fun levelFilterToken(variant: String): Token =
        Token.UnitVariant(
            name = "LevelFilter",
            variant = variant,
        )

    private fun levelFilterBytesTokens(variant: ByteArray): List<Token> =
        listOf(
            Token.Enum(name = "LevelFilter"),
            Token.Bytes(variant),
            Token.UnitValue,
        )

    private fun levelFilterVariantTokens(variant: UInt): List<Token> =
        listOf(
            Token.Enum(name = "LevelFilter"),
            Token.U32(variant),
            Token.UnitValue,
        )

    @Test
    fun testLevelSerDe() {
        val cases =
            listOf(
                Level.Error to listOf(levelToken("ERROR")),
                Level.Warn to listOf(levelToken("WARN")),
                Level.Info to listOf(levelToken("INFO")),
                Level.Debug to listOf(levelToken("DEBUG")),
                Level.Trace to listOf(levelToken("TRACE")),
            )

        for ((s, expected) in cases) {
            assertTokens(s, Level.Companion, expected)
        }
    }

    @Test
    fun testLevelCaseInsensitive() {
        val cases =
            listOf(
                Level.Error to listOf(levelToken("error")),
                Level.Warn to listOf(levelToken("warn")),
                Level.Info to listOf(levelToken("info")),
                Level.Debug to listOf(levelToken("debug")),
                Level.Trace to listOf(levelToken("trace")),
            )

        for ((s, expected) in cases) {
            assertDeTokens(s, Level.Companion, expected)
        }
    }

    @Test
    fun testLevelDeBytes() {
        val cases =
            listOf(
                Level.Error to levelBytesTokens("ERROR".encodeToByteArray()),
                Level.Warn to levelBytesTokens("WARN".encodeToByteArray()),
                Level.Info to levelBytesTokens("INFO".encodeToByteArray()),
                Level.Debug to levelBytesTokens("DEBUG".encodeToByteArray()),
                Level.Trace to levelBytesTokens("TRACE".encodeToByteArray()),
            )

        for ((value, tokens) in cases) {
            assertDeTokens(value, Level.Companion, tokens)
        }
    }

    @Test
    fun testLevelDeVariantIndex() {
        val cases =
            listOf(
                Level.Error to levelVariantTokens(0u),
                Level.Warn to levelVariantTokens(1u),
                Level.Info to levelVariantTokens(2u),
                Level.Debug to levelVariantTokens(3u),
                Level.Trace to levelVariantTokens(4u),
            )

        for ((value, tokens) in cases) {
            assertDeTokens(value, Level.Companion, tokens)
        }
    }

    @Test
    fun testLevelDeError() {
        val msg = "unknown variant `errorx`, expected one of `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`"
        assertDeTokensError(Level.Companion, listOf(levelToken("errorx")), msg)
    }

    @Test
    fun testLevelFilterSerDe() {
        val cases =
            listOf(
                LevelFilter.Off to listOf(levelFilterToken("OFF")),
                LevelFilter.Error to listOf(levelFilterToken("ERROR")),
                LevelFilter.Warn to listOf(levelFilterToken("WARN")),
                LevelFilter.Info to listOf(levelFilterToken("INFO")),
                LevelFilter.Debug to listOf(levelFilterToken("DEBUG")),
                LevelFilter.Trace to listOf(levelFilterToken("TRACE")),
            )

        for ((s, expected) in cases) {
            assertTokens(s, LevelFilter.Companion, expected)
        }
    }

    @Test
    fun testLevelFilterCaseInsensitive() {
        val cases =
            listOf(
                LevelFilter.Off to listOf(levelFilterToken("off")),
                LevelFilter.Error to listOf(levelFilterToken("error")),
                LevelFilter.Warn to listOf(levelFilterToken("warn")),
                LevelFilter.Info to listOf(levelFilterToken("info")),
                LevelFilter.Debug to listOf(levelFilterToken("debug")),
                LevelFilter.Trace to listOf(levelFilterToken("trace")),
            )

        for ((s, expected) in cases) {
            assertDeTokens(s, LevelFilter.Companion, expected)
        }
    }

    @Test
    fun testLevelFilterDeBytes() {
        val cases =
            listOf(
                LevelFilter.Off to levelFilterBytesTokens("OFF".encodeToByteArray()),
                LevelFilter.Error to levelFilterBytesTokens("ERROR".encodeToByteArray()),
                LevelFilter.Warn to levelFilterBytesTokens("WARN".encodeToByteArray()),
                LevelFilter.Info to levelFilterBytesTokens("INFO".encodeToByteArray()),
                LevelFilter.Debug to levelFilterBytesTokens("DEBUG".encodeToByteArray()),
                LevelFilter.Trace to levelFilterBytesTokens("TRACE".encodeToByteArray()),
            )

        for ((value, tokens) in cases) {
            assertDeTokens(value, LevelFilter.Companion, tokens)
        }
    }

    @Test
    fun testLevelFilterDeVariantIndex() {
        val cases =
            listOf(
                LevelFilter.Off to levelFilterVariantTokens(0u),
                LevelFilter.Error to levelFilterVariantTokens(1u),
                LevelFilter.Warn to levelFilterVariantTokens(2u),
                LevelFilter.Info to levelFilterVariantTokens(3u),
                LevelFilter.Debug to levelFilterVariantTokens(4u),
                LevelFilter.Trace to levelFilterVariantTokens(5u),
            )

        for ((value, tokens) in cases) {
            assertDeTokens(value, LevelFilter.Companion, tokens)
        }
    }

    @Test
    fun testLevelFilterDeError() {
        val msg = "unknown variant `errorx`, expected one of `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`"
        assertDeTokensError(LevelFilter.Companion, listOf(levelFilterToken("errorx")), msg)
    }

    @Test
    fun testLevelKotlinxSerialization() {
        val cases =
            listOf(
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
    fun testLevelFilterKotlinxSerialization() {
        val cases =
            listOf(
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
}

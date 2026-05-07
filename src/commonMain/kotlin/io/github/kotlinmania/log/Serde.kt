// port-lint: source src/serde.rs
package io.github.kotlinmania.log

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// The Deserialize implementations are handwritten to be case-insensitive using FromStr.

public object LevelSerializer : KSerializer<Level> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Level", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Level) {
        encoder.encodeString(value.asStr())
    }

    override fun deserialize(decoder: Decoder): Level {
        val raw = decoder.decodeString()
        return Level.fromStr(raw).getOrElse { throw IllegalArgumentException(it.message) }
    }
}

public object LevelFilterSerializer : KSerializer<LevelFilter> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LevelFilter", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LevelFilter) {
        encoder.encodeString(value.asStr())
    }

    override fun deserialize(decoder: Decoder): LevelFilter {
        val raw = decoder.decodeString()
        return LevelFilter.fromStr(raw).getOrElse { throw IllegalArgumentException(it.message) }
    }
}

internal object SerdeTests {
    private fun assertEquals(expected: Any?, actual: Any?) {
        check(expected == actual) { "expected=$expected actual=$actual" }
    }

    fun testLevelCaseInsensitive() {
        assertEquals(Level.Error, Level.fromStr("error").getOrThrow())
        assertEquals(Level.Warn, Level.fromStr("warn").getOrThrow())
        assertEquals(Level.Info, Level.fromStr("info").getOrThrow())
        assertEquals(Level.Debug, Level.fromStr("debug").getOrThrow())
        assertEquals(Level.Trace, Level.fromStr("trace").getOrThrow())
    }

    fun testLevelFilterCaseInsensitive() {
        assertEquals(LevelFilter.Off, LevelFilter.fromStr("off").getOrThrow())
        assertEquals(LevelFilter.Error, LevelFilter.fromStr("error").getOrThrow())
        assertEquals(LevelFilter.Warn, LevelFilter.fromStr("warn").getOrThrow())
        assertEquals(LevelFilter.Info, LevelFilter.fromStr("info").getOrThrow())
        assertEquals(LevelFilter.Debug, LevelFilter.fromStr("debug").getOrThrow())
        assertEquals(LevelFilter.Trace, LevelFilter.fromStr("trace").getOrThrow())
    }
}


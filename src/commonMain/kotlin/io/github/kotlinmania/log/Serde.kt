// port-lint: source serde.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.log

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.DeserializeSeed
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.Unexpected
import io.github.kotlinmania.serdecore.de.Visitor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.native.HiddenFromObjC

// The deserializers are handwritten so the level/filter names are accepted case-insensitively
// by routing through Level.fromStr / LevelFilter.fromStr.

private object LevelIdentifier : Visitor<Level>, DeserializeSeed<Level> {
    override fun expecting(): String = "log level"

    override fun visitU64(v: ULong): SerdeResult<Level> {
        val names = LOG_LEVEL_NAMES.slice(1 until LOG_LEVEL_NAMES.size)
        val idx = v.toInt()
        val variant =
            if (v <= Int.MAX_VALUE.toULong() && idx in names.indices) {
                names[idx]
            } else {
                return SerdeResult.failure(SerdeError.invalidValue(Unexpected.Unsigned(v), this))
            }
        return visitStr(variant)
    }

    override fun visitStr(v: String): SerdeResult<Level> {
        val names = LOG_LEVEL_NAMES.slice(1 until LOG_LEVEL_NAMES.size)
        return Level.fromStr(v).fold(
            onSuccess = { SerdeResult.success(it) },
            onFailure = { SerdeResult.failure(SerdeError.unknownVariant(v, names)) },
        )
    }

    override fun visitBytes(v: ByteArray): SerdeResult<Level> {
        val variant =
            try {
                v.decodeToString(throwOnInvalidSequence = true)
            } catch (_: Exception) {
                return SerdeResult.failure(SerdeError.invalidValue(Unexpected.Bytes(v), this))
            }
        return visitStr(variant)
    }

    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Level> =
        deserializer.deserializeIdentifier(this)
}

private object LevelEnum : Visitor<Level> {
    override fun expecting(): String = "log level"

    override fun <A> visitEnum(access: A): SerdeResult<Level> where A : EnumAccess {
        val (level, variant) =
            when (val res = access.variantSeed(LevelIdentifier)) {
                is SerdeResult.Success -> res.value
                is SerdeResult.Failure -> return SerdeResult.failure(res.error)
            }
        when (val unitRes = variant.unitVariant()) {
            is SerdeResult.Success -> Unit
            is SerdeResult.Failure -> return SerdeResult.failure(unitRes.error)
        }
        return SerdeResult.success(level)
    }
}

public object LevelDeserialize : Deserialize<Level> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Level> {
        val names = LOG_LEVEL_NAMES.slice(1 until LOG_LEVEL_NAMES.size)
        return deserializer.deserializeEnum("Level", names, LevelEnum)
    }
}

private object LevelFilterIdentifier : Visitor<LevelFilter>, DeserializeSeed<LevelFilter> {
    override fun expecting(): String = "log level filter"

    override fun visitU64(v: ULong): SerdeResult<LevelFilter> {
        val names = LOG_LEVEL_NAMES.toList()
        val idx = v.toInt()
        val variant =
            if (v <= Int.MAX_VALUE.toULong() && idx in names.indices) {
                names[idx]
            } else {
                return SerdeResult.failure(SerdeError.invalidValue(Unexpected.Unsigned(v), this))
            }
        return visitStr(variant)
    }

    override fun visitStr(v: String): SerdeResult<LevelFilter> {
        val names = LOG_LEVEL_NAMES.toList()
        return LevelFilter.fromStr(v).fold(
            onSuccess = { SerdeResult.success(it) },
            onFailure = { SerdeResult.failure(SerdeError.unknownVariant(v, names)) },
        )
    }

    override fun visitBytes(v: ByteArray): SerdeResult<LevelFilter> {
        val variant =
            try {
                v.decodeToString(throwOnInvalidSequence = true)
            } catch (_: Exception) {
                return SerdeResult.failure(SerdeError.invalidValue(Unexpected.Bytes(v), this))
            }
        return visitStr(variant)
    }

    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<LevelFilter> =
        deserializer.deserializeIdentifier(this)
}

private object LevelFilterEnum : Visitor<LevelFilter> {
    override fun expecting(): String = "log level filter"

    override fun <A> visitEnum(access: A): SerdeResult<LevelFilter> where A : EnumAccess {
        val (levelFilter, variant) =
            when (val res = access.variantSeed(LevelFilterIdentifier)) {
                is SerdeResult.Success -> res.value
                is SerdeResult.Failure -> return SerdeResult.failure(res.error)
            }
        when (val unitRes = variant.unitVariant()) {
            is SerdeResult.Success -> Unit
            is SerdeResult.Failure -> return SerdeResult.failure(unitRes.error)
        }
        return SerdeResult.success(levelFilter)
    }
}

public object LevelFilterDeserialize : Deserialize<LevelFilter> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<LevelFilter> {
        val names = LOG_LEVEL_NAMES.toList()
        return deserializer.deserializeEnum("LevelFilter", names, LevelFilterEnum)
    }
}

/**
 * Hidden from Swift Export: `KSerializer<Level>` references several
 * `kotlinx.serialization` types that are themselves annotated with
 * `@ExperimentalSerializationApi`, and the Swift Export bridge emits each
 * of those references without the opt-in, producing `-Werror` failures in
 * the generated `OrgJetbrainsKotlinxKotlinxSerializationCore.kt` file.
 * Kotlin callers continue to register this serializer normally; Swift
 * callers go through `Level.fromStr` / `Level.asStr` directly.
 */
@HiddenFromObjC
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

/**
 * Hidden from Swift Export for the same reason as [LevelSerializer]:
 * `KSerializer<LevelFilter>` would pull `@ExperimentalSerializationApi`
 * references into the generated Swift bridge and fail `-Werror`.
 */
@HiddenFromObjC
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

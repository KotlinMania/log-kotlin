# port-lint Proposed Changes

**Generated:** 2026-09-01
**Source:** .
**Target:** src/commonMain/kotlin/io/github/kotlinmania/log

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/log/kv/Value.kt` | `// port-lint: source kv/value.rs` | `// port-lint: source kv/value.rs` | `kv/value.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/value.rs' vs expected 'kv/value.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/kv/Error.kt` | `// port-lint: source kv/error.rs` | `// port-lint: source kv/error.rs` | `kv/error.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/error.rs' vs expected 'kv/error.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/Serde.kt` | `// port-lint: source serde.rs` | `// port-lint: source serde.rs` | `serde.rs` | `port-lint provenance header matched only after fallback normalization: 'serde.rs' vs expected 'serde.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/log/SerdeTest.kt` | `// port-lint: source serde.rs` | `// port-lint: source serde.rs` | `serde.rs` | `port-lint provenance header matched only after fallback normalization: 'serde.rs' vs expected 'serde.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/Lib.kt` | `// port-lint: source lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'lib.rs' vs expected 'lib.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/log/LibTest.kt` | `// port-lint: source lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'lib.rs' vs expected 'lib.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/log/kv/ValueTest.kt` | `// port-lint: source kv/value.rs` | `// port-lint: source benches/value.rs` | `benches/value.rs` | `port-lint provenance header matched only by basename: 'kv/value.rs' vs expected 'benches/value.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/kv/Key.kt` | `// port-lint: source kv/key.rs` | `// port-lint: source kv/key.rs` | `kv/key.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/key.rs' vs expected 'kv/key.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/log/kv/KeyTest.kt` | `// port-lint: source kv/key.rs` | `// port-lint: source kv/key.rs` | `kv/key.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/key.rs' vs expected 'kv/key.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/kv/Source.kt` | `// port-lint: source kv/source.rs` | `// port-lint: source kv/source.rs` | `kv/source.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/source.rs' vs expected 'kv/source.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/log/kv/SourceTest.kt` | `// port-lint: source kv/source.rs` | `// port-lint: source kv/source.rs` | `kv/source.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/source.rs' vs expected 'kv/source.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/PrivateApi.kt` | `// port-lint: source __private_api.rs` | `// port-lint: source __private_api.rs` | `__private_api.rs` | `port-lint provenance header matched only after fallback normalization: '__private_api.rs' vs expected '__private_api.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/kv/Kv.kt` | `// port-lint: source kv/mod.rs` | `// port-lint: source kv/mod.rs` | `kv/mod.rs` | `port-lint provenance header matched only after fallback normalization: 'kv/mod.rs' vs expected 'kv/mod.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/log/Macros.kt` | `// port-lint: source macros.rs` | `// port-lint: source macros.rs` | `macros.rs` | `port-lint provenance header matched only after fallback normalization: 'macros.rs' vs expected 'macros.rs'` |

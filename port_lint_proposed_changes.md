# port-lint Proposed Changes

**Generated:** 2026-08-25
**Source:** src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/log

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonTest/kotlin/io/github/kotlinmania/log/MacrosTest.kt` | `// port-lint: source ../tests/macros.rs` | `// port-lint: source macros.rs` | `macros.rs` | `port-lint provenance header matched only by basename: '../tests/macros.rs' vs expected 'macros.rs'` |

# Claude Code Project Instructions — log-kotlin

## Project Overview

This is **log-kotlin**, a clean-room Kotlin Multiplatform port of the upstream Rust crate [`log`](https://crates.io/crates/log).

The upstream Rust source is the read-only translation oracle. When porting begins, clone it into `tmp/log/` (gitignored). **Never edit `tmp/`.** If upstream looks wrong, the bug is in the port or in your understanding of Rust, not in `tmp/`.

## Translator's mindset

This is a translation project, not a software-engineering project. While porting a file, you are
the Kotlin author of the same document a Rust author wrote. Architecture, optimization, design
critique — all later. While translating, the only job is the translation. Drift measurement is
still required at repo and file boundaries when this repo documents an `ast_distance` gate.

The discipline:

1. **Read the whole upstream file before you type.** A line-by-line port composes only when you
   know how the file ends. If the file is too long to read in one sitting, split your turn into
   "read the file" and "write the file" — never start typing on a file you've only half-read.

2. **One Rust file → one Kotlin file. Always.** No splitting one `.rs` across several `.kt`. No
   merging several `.rs` into one `.kt`. The 1:1 mapping is the contract; everything downstream
   (ast_distance, port-lint headers, code review) assumes it. If a `.rs` is genuinely too big for
   one Kotlin file, that's a sign you're in `mod.rs`-equivalent territory and the upstream itself
   is a re-export — verify, don't split. The `mod.rs` / re-export procedure later in this file is
   required, not optional.

3. **Translate top to bottom in upstream order.** Preserve the declaration order. Don't reorder
   for "logical flow" — the upstream's order *is* the logical flow.

4. **Comments are content.** License header, module-level doc, every `///` block, every inline
   `//` note, every upstream `// TODO`/`// FIXME` — all translate. Rust syntax inside doc comments
   gets rewritten to Kotlin equivalents (`Vec<T>` → `List<T>`, `Self::foo()` → `foo()`, lifetimes
   dropped, `cfg(test)` and `#[derive(...)]` lifted into prose). Comments are not a place for
   porting notes; deviations and rationale belong in docs, commit messages, or review notes.

5. **When a Rust idiom has no Kotlin analog, apply the mapping rule and move on.** `Box<T>`,
   `Arc<T>`, `Cell<T>`, `RefCell<T>`, `Rc<T>`, lifetimes, `PhantomData`, `mem::forget`,
   `drop_in_place`, `Pin`, `MaybeUninit`, `dyn Trait` — all collapse per the mapping table.
   An upstream Rust crate with no KMP equivalent becomes a *separate Kotlin port*, not a
   `// TODO` placeholder.

6. **Measure drift at boundaries.** `ast_distance`, FnSim, and similarity reports are required
   inventory/provenance tools for repos that ship or document that gate. Use them to find unported
   files/functions, provenance/header drift, and cheat-detector failures. Do not chase similarity
   scores while half-translating a file or rewrite Kotlin into Rust-shaped Kotlin to appease the
   tool.

7. **Don't optimize the translation.** "This Kotlin shape would be simpler" is the wrong thought.
   The upstream shape is the spec, but Kotlin stays Kotlin-shaped: packages lowercase, types
   `PascalCase`, functions and locals `camelCase`.

8. **Don't re-architect mid-port.**

9. **Compile errors during translation are normal and expected.** Climb the dep tree bottom-up.

10. **Bottom-up always.** Port dependencies before consumers.

11. **Hard files are not skippable.** Skipping leaves a `// TODO`-shaped hole that grows every
    time another consumer needs it.

12. **Warnings are real, but `@Suppress` is never the answer.** `UNUSED_PARAMETER` on a callback
    helper means the function shape doesn't fit Kotlin — restructure the signature, don't suppress.
    `UNCHECKED_CAST` means the type system is missing an invariant — encode it.

13. **Stop at file boundaries, not function boundaries.** After every completed file, exhale,
    commit, move on.

    Work until you are blocked or reach a limit.

14. **Doc-port discipline applies even when the upstream doc is awkward.** If the upstream
    author wrote a tortured English sentence, translate the tortured sentence. Don't smooth it.

15. **The cheat detector is your friend.** If `ast_distance` forces your file's score to 0
    because you left snake_case identifiers or `pub` keywords in Kotlin comments, take it as a
    literal instruction: rewrite those comments to be Kotlin-native.

## Port-lint headers (REQUIRED)

Every Kotlin file MUST start with:

```kotlin
// port-lint: source <path-relative-to-.ast_distance_config.json source root>
package io.github.kotlinmania.log
```

Example:

```kotlin
// port-lint: source lib.rs
package io.github.kotlinmania.log
```

This repo's `.ast_distance_config.json` sets the source root to `tmp/log/src`, so headers are
relative to that directory. This is how `ast_distance` tracks provenance. Never remove or alter
unless the file is being re-targeted to a different Rust source.

For files that have no single Rust counterpart (re-homed from a `mod.rs`, or pure Kotlin glue), use `// port-lint: ignore` and a one-line prose note explaining what it does.

## Build

```bash
./gradlew build
./gradlew test
```

`./gradlew build` compiles and links every configured target — that
contract is wired explicitly in `build.gradle.kts` via the
`fullTargetBuildTaskNames` block. `./gradlew test` is the host-portable
subset (macOS + JVM + JS + WasmJS + Android unit); non-host native tests
only run on their own host.

Targets follow the kotlinmania normalized template:

- **Apple native:** `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`,
  `tvosArm64`, `tvosSimulatorArm64`, `watchosArm32`, `watchosArm64`,
  `watchosDeviceArm64`, `watchosSimulatorArm64`, plus the `Log` XCFramework.
- **Other native:** `linuxX64`, `linuxArm64`, `mingwX64`,
  `androidNativeArm32`/`Arm64`/`X86`/`X64`.
- **Web / JVM:** `js` (browser + Node), `wasmJs` (browser + Node), `wasmWasi`
  (Node), `jvm`, Android KMP library (main + host-test + device-test),
  `swiftExport`.

The `kv` u128 / i128 surface uses repo-local `UInt128` / `Int128` types
(see [Int128.kt](src/commonMain/kotlin/io/github/kotlinmania/log/kv/Int128.kt))
rather than `com.ionspin.kotlin:bignum`. bignum 0.3.10 declares
`watchos_device_arm64` in its module metadata but does not publish the
matching artifact, which made `watchosDeviceArm64` unbuildable. The
hand-rolled fixed-width 128-bit integers cover the operations the upstream
Rust `u128`/`i128` surface needs and work uniformly on every target.

## Forbidden

- `import kotlin.jvm.*` (`JvmName`, `JvmStatic`, `JvmField`, `JvmOverloads`)
- `import java.*`
- `import javax.*`
- `@Suppress(...)` for any reason — fix the underlying issue
- Empty stub classes / `TODO()` / `error("not implemented")` / placeholder code
- Re-export typealias files at root packages
- Subagent-driven `.kt` edits — translation happens in the main loop only

## Naming

| Kind | Form |
|---|---|
| Functions, parameters, locals | `camelCase` |
| Classes, data classes, sealed types | `PascalCase` |
| Interfaces | `PascalCase`, no `I` prefix |
| `const val`, `enum` entries, top-level constants | `SCREAMING_SNAKE_CASE` permitted |
| Type parameters | `T`, `K`, `V` |
| Packages | all lowercase, no underscores, no camelCase |

## Approved dependencies

- `kotlinx-coroutines-core`
- `kotlinx-serialization-core`, `kotlinx-serialization-json`
- `kotlinx-collections-immutable`
- `kotlinx-datetime`
- `com.ionspin.kotlin:bignum` (only if needed)
- `io.github.kotlinmania:*-kotlin` siblings (only when porting a transitive Rust dep)

Add a new dependency only when the stdlib + the above cannot reproduce the required behavior, and only after confirming it publishes artifacts for **every** target above.

## Commit messages

- No AI branding or attribution.
- Clear, descriptive, focused on what changed and why.
- No `Co-Authored-By` lines, no robot emoji, no "Generated with" footers.

## Re-exports from upstream `mod.rs` files

When an upstream Rust `mod.rs` is **only re-exporting** something that actually lives elsewhere
(`pub use <crate-path>::<Name>;`, often under a different name), do **not** preserve that
re-export shape in Kotlin as a "central alias" API. Do not write a `typealias` for the
re-exported name. The existing `Forbidden` rule against "Re-export typealias files at root
packages" is enforced through this procedure.

Workflow:

1. **Identify what the `mod.rs` is re-exporting and the name it's exported as.** Record both
   the original symbol's fully-qualified upstream path and the (possibly different) re-export
   name.

2. **Find callers across the kotlinmania monorepo.** A caller is any Kotlin file in another
   `*-kotlin` repo that has both a `tmp/` folder and a Cargo.toml depending on the Rust
   counterpart of *this* crate, where the file references the re-exported name. Search for:
   - direct imports: `import <reexport-package>.<Name>`
   - wildcard imports of the re-export package, when `<Name>` is used in the file body
   - fully-qualified inline references

3. **Rewrite each caller to reference the upstream/original symbol directly.** If the caller
   still needs to write `<Name>` unchanged, use Kotlin aliasing:
   `import <upstream-fully-qualified-name> as <Name>`. Never bridge with a Kotlin `typealias`.

4. **Keep `Mod.kt` (or the equivalent file for that package) as a tracking file.** It carries
   the translated upstream module-level comments and a literal-quoted reference to each upstream
   `pub use` line (e.g. `// pub use crate::lib::result::Result;`). Each time a caller is migrated
   off the re-export, append the caller's absolute path under a `// Callers migrated:` ledger in
   `Mod.kt`. Append, never delete. Once all callers are migrated, the `typealias` (if any) is
   removed; the tracking file remains as the ledger of the migration.

Reference example: [/Volumes/stuff/Projects/kotlinmania/serde-kotlin/tmp/serde/serde_core/src/private/mod.rs](/Volumes/stuff/Projects/kotlinmania/serde-kotlin/tmp/serde/serde_core/src/private/mod.rs)
re-exports `Result` from `crate::lib::result`. The Kotlin tracking file lives at
[/Volumes/stuff/Projects/kotlinmania/serde-kotlin/src/commonMain/kotlin/io/github/kotlinmania/serde/core/private/Mod.kt](/Volumes/stuff/Projects/kotlinmania/serde-kotlin/src/commonMain/kotlin/io/github/kotlinmania/serde/core/private/Mod.kt).
A caller that previously did `import io.github.kotlinmania.serde.core.private.Result` is
rewritten to `import kotlin.Result as Result` (or just removes the import and relies on the
auto-imported `kotlin.Result`).

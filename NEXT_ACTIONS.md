# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 12/13 (92.3%)
- **Function parity:** 160/247 matched (target 331) — 64.8%
- **Class/type parity:** 26/47 matched (target 72) — 55.3%
- **Combined symbol parity:** 186/294 matched (target 403) — 63.3%
- **Average inline-code cosine:** 0.11 (function body across 11 matched files)
- **Average documentation cosine:** 0.46 (doc text across 11 matched files)
- **Cheat-zeroed Files:** 9
- **Critical Issues:** 12 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. kv.value

- **Target:** `kv.Value [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 3
- **Priority Score:** 3277310.0
- **Functions:** 43/66 matched (target 80)
- **Missing functions:** `null`, `serialize`, `stream`, `stream_ref`, `visit_any`, `to_test_token`, `is`, `to_token`, `unsigned`, `signed`, `float`, `bool`, `str`, `char`, `test_to_value_display`, `test_to_value_structured`, `test_to_number`, `test_to_float`, `test_to_cow_str`, `test_to_bool`, `test_to_char`, `test_visit_integer`, `test_visit_borrowed_str`
- **Types:** 3/7 matched (target 17)
- **Missing types:** `ToValue`, `InnerVisitValue`, `Token`, `Extract`
- **Tests:** 0/17 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/value.rs` vs expected `kv/value.rs`
- **Proposed provenance header:** `// port-lint: source kv/value.rs` (current: `// port-lint: source kv/value.rs`)
- **Lint issues:** 1

### 2. kv.error

- **Target:** `kv.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 3
- **Priority Score:** 3000904.0
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/error.rs` vs expected `kv/error.rs`
- **Proposed provenance header:** `// port-lint: source kv/error.rs` (current: `// port-lint: source kv/error.rs`)
- **Lint issues:** 1

### 3. serde

- **Target:** `log.Serde [PROVENANCE-FALLBACK]`
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 202808.8
- **Functions:** 8/23 matched (target 13)
- **Missing functions:** `expecting`, `visit_u64`, `visit_str`, `visit_bytes`, `visit_enum`, `level_token`, `level_bytes_tokens`, `level_variant_tokens`, `level_filter_token`, `level_filter_bytes_tokens`, `level_filter_variant_tokens`, `test_level_de_bytes`, `test_level_de_variant_index`, `test_level_filter_de_bytes`, `test_level_filter_de_variant_index`
- **Types:** 0/5 matched (target 4)
- **Missing types:** `LevelIdentifier`, `Value`, `LevelEnum`, `LevelFilterIdentifier`, `LevelFilterEnum`
- **Tests:** 6/16 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde.rs` vs expected `serde.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde.rs` vs expected `serde.rs`
- **Proposed provenance header:** `// port-lint: source serde.rs` (current: `// port-lint: source serde.rs`)
- **Proposed provenance header:** `// port-lint: source serde.rs` (current: `// port-lint: source serde.rs`)
- **Lint issues:** 2

### 4. lib

- **Target:** `log.Lib [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 188410.0
- **Functions:** 56/69 matched (target 102)
- **Missing functions:** `load`, `store`, `eq`, `partial_cmp`, `fmt`, `get`, `default`, `set_logger_inner`, `test_static_max_level_debug`, `test_static_max_level_release`, `visit_pair`, `assert_is_log`, `forall`
- **Types:** 10/15 matched (target 16)
- **Missing types:** `AtomicUsize`, `Err`, `MaybeStaticStr`, `KeyValues`, `TestVisitSource`
- **Tests:** 23/28 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 3

### 5. tests.macros

- **Target:** `log.MacrosTest [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 182810.0
- **Functions:** 10/26 matched (target 15)
- **Missing functions:** `kv_anonymous_args`, `kv_named_args`, `kv_ident`, `kv_expr_context`, `implicit_named_args`, `kv_implicit_named_args`, `kv_string_keys`, `kv_common_value_types`, `kv_debug`, `kv_display`, `kv_error`, `kv_sval`, `kv_serde`, `logger_short_lived`, `logger_expr`, `regression_issue_494`
- **Types:** 0/2 matched
- **Missing types:** `Logger`, `Type`
- **Tests:** 7/23 matched
- **Lint issues:** 1

### 6. benches.value

- **Target:** `kv.ValueTest [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 50510.0
- **Functions:** 0/4 matched (target 21)
- **Missing functions:** `u8_to_value`, `u8_to_value_debug`, `str_to_value_debug`, `custom_to_value_debug`
- **Types:** 0/1 matched (target 10)
- **Missing types:** `A`
- **Provenance warning:** port-lint provenance header matched only by basename: `kv/value.rs` vs expected `benches/value.rs`
- **Proposed provenance header:** `// port-lint: source benches/value.rs` (current: `// port-lint: source kv/value.rs`)
- **Lint issues:** 1

### 7. kv.key

- **Target:** `kv.Key [PROVENANCE-FALLBACK]`
- **Similarity:** 0.53
- **Dependents:** 0
- **Priority Score:** 41504.7
- **Functions:** 10/13 matched (target 15)
- **Missing functions:** `stream`, `stream_ref`, `serialize`
- **Types:** 1/2 matched
- **Missing types:** `ToKey`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/key.rs` vs expected `kv/key.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/key.rs` vs expected `kv/key.rs`
- **Proposed provenance header:** `// port-lint: source kv/key.rs` (current: `// port-lint: source kv/key.rs`)
- **Proposed provenance header:** `// port-lint: source kv/key.rs` (current: `// port-lint: source kv/key.rs`)
- **Lint issues:** 2

### 8. kv.source

- **Target:** `kv.Source [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31610.0
- **Functions:** 8/11 matched (target 18)
- **Missing functions:** `source_is_object_safe`, `_check`, `visitor_is_object_safe`
- **Types:** 5/5 matched (target 7)
- **Missing types:** _none_
- **Tests:** 2/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/source.rs` vs expected `kv/source.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/source.rs` vs expected `kv/source.rs`
- **Proposed provenance header:** `// port-lint: source kv/source.rs` (current: `// port-lint: source kv/source.rs`)
- **Proposed provenance header:** `// port-lint: source kv/source.rs` (current: `// port-lint: source kv/source.rs`)
- **Lint issues:** 3

### 9. __private_api

- **Target:** `log.PrivateApi [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31510.0
- **Functions:** 10/12 matched (target 17)
- **Missing functions:** `capture_sval`, `capture_serde`
- **Types:** 2/3 matched (target 4)
- **Missing types:** `Value`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `__private_api.rs` vs expected `__private_api.rs`
- **Proposed provenance header:** `// port-lint: source __private_api.rs` (current: `// port-lint: source __private_api.rs`)
- **Lint issues:** 1

### 10. tests.integration

- **Target:** `log.IntegrationTest [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1110.0
- **Functions:** 9/9 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 11. kv.mod

- **Target:** `kv.Kv [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `kv/mod.rs` vs expected `kv/mod.rs`
- **Proposed provenance header:** `// port-lint: source kv/mod.rs` (current: `// port-lint: source kv/mod.rs`)
- **Lint issues:** 7

### 12. macros

- **Target:** `log.Macros [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 33)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `macros.rs` vs expected `macros.rs`
- **Proposed provenance header:** `// port-lint: source macros.rs` (current: `// port-lint: source macros.rs`)
- **Lint issues:** 2

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present


# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 125/200 matched (target 289) — 62.5%
- **Class/type parity:** 25/40 matched (target 60) — 62.5%
- **Combined symbol parity:** 150/240 matched (target 349) — 62.5%
- **Average inline-code cosine:** 0.31 (function body across 8 matched files)
- **Average documentation cosine:** 0.71 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 8 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. kv.error

- **Target:** `kv.Error`
- **Similarity:** 0.60
- **Dependents:** 3
- **Priority Score:** 3000904.0
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_

### 2. kv.value

- **Target:** `kv.Value [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2167310.0
- **Functions:** 52/66 matched (target 101)
- **Missing functions:** `null`, `serialize`, `stream`, `stream_ref`, `visit_empty`, `to_test_token`, `is`, `to_token`, `unsigned`, `signed`, `float`, `bool`, `str`, `char`
- **Types:** 5/7 matched (target 22)
- **Missing types:** `ToValue`, `InnerVisitValue`
- **Tests:** 9/17 matched

### 3. lib

- **Target:** `log.Lib`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 458405.9
- **Functions:** 29/69 matched
- **Missing functions:** `load`, `store`, `eq`, `partial_cmp`, `fmt`, `to_level`, `get`, `default`, `set_max_level_racy`, `set_boxed_logger`, `set_logger_inner`, `set_logger_racy`, `test_levelfilter_from_str`, `test_level_from_str`, `test_level_as_str`, `test_level_show`, `test_levelfilter_show`, `test_cross_cmp`, `test_cross_eq`, `test_to_level`, `test_to_level_filter`, `test_level_filter_as_str`, `test_level_up`, `test_level_filter_up`, `test_level_down`, `test_level_filter_down`, `test_static_max_level_debug`, `test_static_max_level_release`, `test_error_trait`, `test_metadata_builder`, `test_metadata_convenience_builder`, `test_record_builder`, `test_record_convenience_builder`, `test_record_complete_builder`, `test_record_key_values_builder`, `visit_pair`, `test_record_key_values_get_coerce`, `test_foreign_impl`, `assert_is_log`, `forall`
- **Types:** 10/15 matched (target 13)
- **Missing types:** `AtomicUsize`, `Err`, `MaybeStaticStr`, `KeyValues`, `TestVisitSource`
- **Tests:** 0/28 matched
- **Lint issues:** 1

### 4. serde

- **Target:** `log.Serde`
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 202808.8
- **Functions:** 8/23 matched (target 13)
- **Missing functions:** `expecting`, `visit_u64`, `visit_str`, `visit_bytes`, `visit_enum`, `level_token`, `level_bytes_tokens`, `level_variant_tokens`, `level_filter_token`, `level_filter_bytes_tokens`, `level_filter_variant_tokens`, `test_level_de_bytes`, `test_level_de_variant_index`, `test_level_filter_de_bytes`, `test_level_filter_de_variant_index`
- **Types:** 0/5 matched (target 4)
- **Missing types:** `LevelIdentifier`, `Value`, `LevelEnum`, `LevelFilterIdentifier`, `LevelFilterEnum`
- **Tests:** 6/16 matched

### 5. kv.source

- **Target:** `kv.Source [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 41610.0
- **Functions:** 8/11 matched (target 16)
- **Missing functions:** `source_is_object_safe`, `_check`, `visitor_is_object_safe`
- **Types:** 4/5 matched (target 6)
- **Missing types:** `Source`
- **Tests:** 2/5 matched
- **Lint issues:** 1

### 6. kv.key

- **Target:** `kv.Key`
- **Similarity:** 0.53
- **Dependents:** 0
- **Priority Score:** 41504.7
- **Functions:** 10/13 matched (target 15)
- **Missing functions:** `stream`, `stream_ref`, `serialize`
- **Types:** 1/2 matched
- **Missing types:** `ToKey`
- **Tests:** 2/2 matched

### 7. __private_api

- **Target:** `log.PrivateApi`
- **Similarity:** 0.83
- **Dependents:** 0
- **Priority Score:** 11501.7
- **Functions:** 12/12 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 4)
- **Missing types:** `Value`

### 8. kv.mod

- **Target:** `kv.Kv [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Lint issues:** 6

### 9. macros

- **Target:** `log.Macros [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 48)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only by basename: `../tests/macros.rs` vs expected `macros.rs`
- **Proposed provenance header:** `// port-lint: source macros.rs` (current: `// port-lint: source ../tests/macros.rs`)
- **Lint issues:** 2

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present


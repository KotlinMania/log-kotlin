# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 102/200 matched (target 239) — 51.0%
- **Class/type parity:** 19/40 matched (target 47) — 47.5%
- **Combined symbol parity:** 121/240 matched (target 286) — 50.4%
- **Average inline-code cosine:** 0.35 (function body across 8 matched files)
- **Average documentation cosine:** 0.77 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 2
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
- **Priority Score:** 3010904.0
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 6)
- **Missing types:** `BoxedError`

### 2. kv.value

- **Target:** `kv.Value`
- **Similarity:** 0.31
- **Dependents:** 2
- **Priority Score:** 2317306.8
- **Functions:** 39/66 matched (target 80)
- **Missing functions:** `null`, `fmt`, `serialize`, `stream`, `stream_ref`, `from`, `visit_any`, `visit_empty`, `to_test_token`, `is`, `downcast_ref`, `to_token`, `unsigned`, `signed`, `float`, `bool`, `str`, `char`, `test_to_value_display`, `test_to_value_structured`, `test_to_number`, `test_to_float`, `test_to_cow_str`, `test_to_bool`, `test_to_char`, `test_visit_integer`, `test_visit_borrowed_str`
- **Types:** 3/7 matched (target 18)
- **Missing types:** `ToValue`, `InnerVisitValue`, `Token`, `Extract`
- **Tests:** 0/17 matched

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
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 262809.3
- **Functions:** 2/23 matched (target 7)
- **Missing functions:** `expecting`, `visit_u64`, `visit_str`, `visit_bytes`, `visit_enum`, `level_token`, `level_bytes_tokens`, `level_variant_tokens`, `level_filter_token`, `level_filter_bytes_tokens`, `level_filter_variant_tokens`, `test_level_ser_de`, `test_level_case_insensitive`, `test_level_de_bytes`, `test_level_de_variant_index`, `test_level_de_error`, `test_level_filter_ser_de`, `test_level_filter_case_insensitive`, `test_level_filter_de_bytes`, `test_level_filter_de_variant_index`, `test_level_filter_de_error`
- **Types:** 0/5 matched (target 3)
- **Missing types:** `LevelIdentifier`, `Value`, `LevelEnum`, `LevelFilterIdentifier`, `LevelFilterEnum`
- **Tests:** 0/16 matched

### 5. kv.source

- **Target:** `kv.Source`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 101609.1
- **Functions:** 5/11 matched (target 9)
- **Missing functions:** `visit`, `hash_map`, `btree_map`, `source_is_object_safe`, `_check`, `visitor_is_object_safe`
- **Types:** 1/5 matched (target 1)
- **Missing types:** `Source`, `Get`, `Count`, `OnePair`
- **Tests:** 0/5 matched
- **Lint issues:** 2

### 6. kv.key

- **Target:** `kv.Key`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 51504.8
- **Functions:** 9/13 matched (target 14)
- **Missing functions:** `fmt`, `stream`, `stream_ref`, `serialize`
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

- **Target:** `log.Macros [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 33)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../src rust ../../src/commonMain/kotlin/io/github/kotlinmania/log kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```

# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 221/371 matched (target 337) — 59.6%
- **Class/type parity:** 32/40 matched (target 68) — 80.0%
- **Combined symbol parity:** 253/411 matched (target 405) — 61.6%
- **Average inline-code cosine:** 0.08 (function body across 8 matched files)
- **Average documentation cosine:** 0.62 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 6
- **Critical Issues:** 9 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. kv.error

- **Target:** `kv.Error`
- **Similarity:** 0.25
- **Dependents:** 3
- **Priority Score:** 3011007.5
- **Functions:** 6/7 matched (target 8)
- **Missing functions:** `from`
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_

### 2. kv.value

- **Target:** `kv.Value [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2643910.0
- **Functions:** 70/132 matched (target 101)
- **Missing functions:** `null`, `fmt`, `serialize`, `stream`, `stream_ref`, `from`, `visit_null`, `visit_i64`, `visit_u128`, `visit_i128`, `visit_f64`, `visit_bool`, `visit_str`, `visit_char`, `visit_error`, `visit_borrowed_error`, `visit_any`, `visit_u64`, `visit_i64`, `visit_u128`, `visit_i128`, `visit_f64`, `visit_bool`, `visit_str`, `visit_borrowed_str`, `visit_char`, `visit_error`, `visit_borrowed_error`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `from`, `fmt`, `fmt`, `to_test_token`, `visit`, `is`, `to_token`, `unsigned`, `signed`, `float`, `bool`, `str`, `char`, `visit_any`, `visit_u64`, `visit_any`, `visit_borrowed_str`
- **Types:** 6/7 matched (target 21)
- **Missing types:** `InnerVisitValue`
- **Tests:** 9/21 matched

### 3. kv.source

- **Target:** `kv.Source [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 436110.0
- **Functions:** 13/56 matched (target 18)
- **Missing functions:** `get`, `count`, `visit`, `get`, `count`, `visit`, `get`, `count`, `visit`, `get`, `count`, `visit_pair`, `visit_pair`, `visit_pair`, `visit_pair`, `visit`, `get`, `count`, `visit`, `get`, `count`, `visit`, `get`, `count`, `visit`, `get`, `count`, `visit_pair`, `visit`, `get`, `count`, `visit`, `get`, `count`, `count`, `get`, `source_is_object_safe`, `_check`, `visitor_is_object_safe`, `_check`, `count`, `visit`, `get`
- **Types:** 5/5 matched (target 7)
- **Missing types:** _none_
- **Tests:** 2/11 matched
- **Lint issues:** 1

### 4. lib

- **Target:** `log.Lib [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 362510.0
- **Functions:** 80/110 matched (target 104)
- **Missing functions:** `load`, `store`, `eq`, `partial_cmp`, `fmt`, `eq`, `partial_cmp`, `fmt`, `max`, `get`, `fmt`, `module_path_static`, `file_static`, `default`, `new`, `default`, `enabled`, `log`, `flush`, `enabled`, `log`, `flush`, `set_logger_inner`, `fmt`, `fmt`, `test_static_max_level_debug`, `test_static_max_level_release`, `visit_pair`, `assert_is_log`, `forall`
- **Types:** 10/15 matched (target 16)
- **Missing types:** `AtomicUsize`, `Err`, `MaybeStaticStr`, `KeyValues`, `TestVisitSource`
- **Tests:** 23/28 matched
- **Lint issues:** 1

### 5. serde

- **Target:** `log.Serde`
- **Similarity:** 0.26
- **Dependents:** 0
- **Priority Score:** 73907.4
- **Functions:** 28/34 matched (target 41)
- **Missing functions:** `level_token`, `level_bytes_tokens`, `level_variant_tokens`, `level_filter_token`, `level_filter_bytes_tokens`, `level_filter_variant_tokens`
- **Types:** 4/5 matched (target 10)
- **Missing types:** `Value`
- **Tests:** 10/16 matched

### 6. kv.key

- **Target:** `kv.Key`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 61908.5
- **Functions:** 11/17 matched (target 15)
- **Missing functions:** `to_key`, `to_key`, `to_key`, `stream`, `stream_ref`, `serialize`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 7. __private_api

- **Target:** `log.PrivateApi [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31810.0
- **Functions:** 13/15 matched (target 17)
- **Missing functions:** `capture_sval`, `capture_serde`
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


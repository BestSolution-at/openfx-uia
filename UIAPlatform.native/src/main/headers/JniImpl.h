/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2 
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
#ifndef __JNI_IMPL__
#define __JNI_IMPL__

#define DBG_OUTPUT(hr) print_on_fail(hr)

#define _string(val) _string0(env, val)

#define IMPL_BEGIN try {

#define IMPL_END return S_OK; \
} catch (HRESULT hr) { \
  DBG_OUTPUT(hr);\
  return hr;\
} catch(...){\
  fprintf(stderr, "[Exception Fallthrough!!] %s:%d\n", __FUNCTION__, __LINE__); \
  return E_FAIL; \
}

#define IMPL_CALL_VOID(mid, ...) \
  IMPL_BEGIN\
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    call_void(env, get_jobject(), mid, __VA_ARGS__); \
  IMPL_END

#define IMPL_CALL_BSTR(pResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_string(env, get_jobject(), mid, __VA_ARGS__); \
    fill_bstr(env, result, pResult); \
  IMPL_END

#define IMPL_CALL_BOOL(pResult, mid, ...) \
  IMPL_BEGIN \
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_bool(env, get_jobject(), mid, __VA_ARGS__); \
    *pResult = result == JNI_TRUE ? TRUE : FALSE; \
  IMPL_END

#define IMPL_CALL_INT(pResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_int(env, get_jobject(), mid, __VA_ARGS__); \
    *pResult = (int) result; \
  IMPL_END

#define IMPL_CALL_ENUM(pResult, enumType, mid, ...) \
  IMPL_BEGIN\
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_int(env, get_jobject(), mid, __VA_ARGS__); \
    *pResult = (enumType) result; \
  IMPL_END

#define IMPL_CALL_DOUBLE(pResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_double(env, get_jobject(), mid, __VA_ARGS__); \
    *pResult = (double) result; \
  IMPL_END


#define IMPL_CALL_ACCESSIBLE(ppResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(ppResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_accessible(env, get_jobject(), mid, __VA_ARGS__); \
    if (result != nullptr) result->AddRef(); \
    *ppResult = result; \
  IMPL_END

#define IMPL_CALL_TEXTRANGE(ppResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(ppResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_textrange(env, get_jobject(), mid, __VA_ARGS__); \
    *ppResult = result; \
  IMPL_END

#define IMPL_CALL_SAFEARRAY(ppResult, vt, mid, ...) \
  IMPL_BEGIN\
    assert_arg(ppResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_array(env, get_jobject(), mid, __VA_ARGS__); \
    fill_safearray(env, result, vt, ppResult); \
  IMPL_END

#define IMPL_CALL_SAFEARRAY_NO_ADDREF(ppResult, vt, mid, ...) \
  IMPL_BEGIN\
    assert_arg(ppResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_array(env, get_jobject(), mid, __VA_ARGS__); \
    fill_safearray_no_addref(env, result, vt, ppResult); \
  IMPL_END

#define IMPL_CALL_VARIANT(pResult, mid, ...) \
  IMPL_BEGIN\
    assert_arg(pResult); \
    auto env = get_env(); \
    LocalFrame frame(env, 10); \
    auto result = call_object(env, get_jobject(), mid, __VA_ARGS__); \
    fill_variant(env, result, pResult); \
  IMPL_END

#endif // __JNI_IMPL__
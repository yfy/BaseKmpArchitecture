package com.yfy.kmp.core.common.auth

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.NSUTF8StringEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class IosTokenStore : TokenStore {

    override suspend fun tokens(): AuthTokens? {
        val access = read(KEY_ACCESS) ?: return null
        val refresh = read(KEY_REFRESH) ?: return null
        return AuthTokens(access, refresh)
    }

    override suspend fun save(tokens: AuthTokens) {
        write(KEY_ACCESS, tokens.accessToken)
        write(KEY_REFRESH, tokens.refreshToken)
    }

    override suspend fun clear() {
        delete(KEY_ACCESS)
        delete(KEY_REFRESH)
    }

    private fun write(account: String, value: String) {
        delete(account)
        val data = (NSString.create(string = value)).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val accountRef = CFBridgingRetain(account as NSString)
        val dataRef = CFBridgingRetain(data)
        val query = CFDictionaryCreateMutable(
            null, 4, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
        CFDictionaryAddValue(query, kSecValueData, dataRef)
        CFDictionaryAddValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)
        SecItemAdd(query, null)
        CFRelease(query)
        CFRelease(accountRef)
        CFRelease(dataRef)
    }

    private fun read(account: String): String? = memScoped {
        val accountRef = CFBridgingRetain(account as NSString)
        val query = CFDictionaryCreateMutable(
            null, 4, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        CFRelease(query)
        CFRelease(accountRef)
        if (status != errSecSuccess) return@memScoped null
        val nsData = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
        nsData.toByteArray().decodeToString()
    }

    private fun delete(account: String) {
        val accountRef = CFBridgingRetain(account as NSString)
        val query = CFDictionaryCreateMutable(
            null, 2, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
        SecItemDelete(query)
        CFRelease(query)
        CFRelease(accountRef)
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), bytes, length)
        }
    }
}

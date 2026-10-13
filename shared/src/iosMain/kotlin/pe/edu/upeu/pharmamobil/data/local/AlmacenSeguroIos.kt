@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package pe.edu.upeu.pharmamobil.data.local
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
class AlmacenSeguroIos : AlmacenSeguro {
    private fun key(value: CFStringRef?): NSString = interpretObjCPointer(requireNotNull(value).rawValue)
    private fun base(): Map<Any?, Any?> = mapOf(key(kSecClass) to key(kSecClassGenericPassword), key(kSecAttrService) to "pe.edu.upeu.pharmamobil.sesion", key(kSecAttrAccount) to "jwt")
    private fun Map<Any?, Any?>.consulta(): CFDictionaryRef = CFBridgingRetain(this)!!.reinterpret()
    override fun guardarToken(token: String) {
        val bytes = token.encodeToByteArray()
        val datos = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
        val query = base().consulta()
        val values = mapOf<Any?, Any?>(key(kSecValueData) to datos, key(kSecAttrAccessible) to key(kSecAttrAccessibleWhenUnlockedThisDeviceOnly)).consulta()
        try {
            val update = SecItemUpdate(query, values)
            if(update == errSecItemNotFound) {
                val add = (base() + mapOf<Any?, Any?>(key(kSecValueData) to datos, key(kSecAttrAccessible) to key(kSecAttrAccessibleWhenUnlockedThisDeviceOnly))).consulta()
                try { check(SecItemAdd(add, null) == errSecSuccess) { "Keychain no pudo guardar la sesión." } } finally { CFRelease(add) }
            } else check(update == errSecSuccess) { "Keychain no pudo actualizar la sesión." }
        } finally { CFRelease(query); CFRelease(values) }
    }
    override fun leerToken(): String? = memScoped {
        val query = (base() + mapOf<Any?, Any?>(key(kSecReturnData) to true, key(kSecMatchLimit) to key(kSecMatchLimitOne))).consulta()
        val result = alloc<CFTypeRefVar>()
        result.value = null
        try {
            val status = SecItemCopyMatching(query, result.ptr)
            if(status == errSecItemNotFound) return@memScoped null
            check(status == errSecSuccess) { "Keychain no pudo leer la sesión." }
            val data = CFBridgingRelease(result.value) as NSData
            val bytes = ByteArray(data.length.toInt())
            if(bytes.isNotEmpty()) bytes.usePinned { data.getBytes(it.addressOf(0), data.length) }
            bytes.decodeToString()
        } finally { CFRelease(query) }
    }
    override fun borrarToken() {
        val query = base().consulta()
        try { val status = SecItemDelete(query); check(status == errSecSuccess || status == errSecItemNotFound) { "Keychain no pudo borrar la sesión." } } finally { CFRelease(query) }
    }
}

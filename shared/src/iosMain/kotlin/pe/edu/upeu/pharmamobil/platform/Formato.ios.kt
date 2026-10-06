@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package pe.edu.upeu.pharmamobil.platform
import platform.Foundation.*
actual fun formatearSoles(valor: Double): String {
    val formato = NSNumberFormatter()
    formato.numberStyle = NSNumberFormatterCurrencyStyle
    formato.locale = NSLocale("es_PE")
    return formato.stringFromNumber(NSNumber(double = valor)) ?: "S/ $valor"
}

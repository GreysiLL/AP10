package pe.edu.upeu.pharmamobil.platform

import platform.UIKit.UIDevice

actual class InfoDispositivo actual constructor() {
    actual val sistema: String get() = UIDevice.currentDevice.systemName
    actual val version: String get() = UIDevice.currentDevice.systemVersion
}

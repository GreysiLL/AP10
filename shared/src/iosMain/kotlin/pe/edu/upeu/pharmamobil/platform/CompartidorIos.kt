@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package pe.edu.upeu.pharmamobil.platform
import platform.UIKit.*
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
class CompartidorIos : Compartidor {
    override fun compartir(texto: String) {
        val escena = UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
        val ventana = escena?.windows?.filterIsInstance<UIWindow>()?.firstOrNull { it.isKeyWindow() }
        var presentador = ventana?.rootViewController ?: error("No hay una ventana activa para compartir.")
        while (presentador.presentedViewController != null) {
            presentador = requireNotNull(presentador.presentedViewController)
        }
        val hoja = UIActivityViewController(activityItems = listOf(texto), applicationActivities = null)
        hoja.popoverPresentationController?.let {
            it.sourceView = presentador.view
            it.sourceRect = presentador.view.bounds
        }
        presentador.presentViewController(hoja, animated = true, completion = null)
    }
}

package de.hofmann.ime_insets

import android.app.Activity
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin
import app.tauri.plugin.Invoke
import android.os.Build
import android.view.View
import android.webkit.WebView
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@InvokeArg
class PingArgs {
  var value: String? = null
}

@TauriPlugin
class ExamplePlugin(private val activity: Activity): Plugin(activity) {
    private val implementation = Example()

    override fun load(webView: WebView) {
        val root =
            activity.findViewById<View>(android.R.id.content)

        val initialPaddingLeft = root.paddingLeft
        val initialPaddingTop = root.paddingTop
        val initialPaddingRight = root.paddingRight
        val initialPaddingBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            /*
             * Android 10 / API 29 und älter:
             *
             * adjustResize übernimmt die Größenanpassung.
             * Kein zusätzliches natives IME-Padding anwenden.
             */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            val imeVisible =
                insets.isVisible(WindowInsetsCompat.Type.ime())

            val imeInsets =
                insets.getInsets(WindowInsetsCompat.Type.ime())

            val imeBottom =
                if (imeVisible) imeInsets.bottom else 0

            /*
             * IME-Inset nativ auf den WebView-Container anwenden.
             */
            view.setPadding(
                initialPaddingLeft,
                initialPaddingTop,
                initialPaddingRight,
                initialPaddingBottom + imeBottom
            )

            /*
             * Wir haben den IME-Inset bereits verarbeitet.
             *
             * Deshalb ime() für die WebView auf 0 setzen,
             * aber die Insets NICHT komplett consumen.
             */
            WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.ime(),
                    Insets.NONE
                )
                .build()
        }

        ViewCompat.requestApplyInsets(root)
    }

    @Command
    fun ping(invoke: Invoke) {
        val args = invoke.parseArgs(PingArgs::class.java)

        val ret = JSObject()
        ret.put("value", implementation.pong(args.value ?: "default value :("))
        invoke.resolve(ret)
    }
}

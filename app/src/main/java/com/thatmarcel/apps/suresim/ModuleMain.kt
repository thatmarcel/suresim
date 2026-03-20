package com.thatmarcel.apps.suresim

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.telephony.euicc.DownloadableSubscription
import android.telephony.euicc.EuiccManager
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

class ModuleMain: XposedModule() {
    companion object {
        var application: Application? = null
    }

    override fun onPackageReady(param: PackageReadyParam) {
        super.onPackageLoaded(param)

        if (!param.isFirstPackage) {
            return
        }

        hook(Application::class.java.getDeclaredMethod("onCreate")).intercept { chain ->
            application = chain.thisObject as Application?

            chain.proceed()
        }

        hook(EuiccManager::class.java.getDeclaredMethod("isEnabled")).intercept { chain ->
            chain.proceed()

            true
        }

        hook(DownloadableSubscription::class.java.getDeclaredMethod(
            "forActivationCode",
            String::class.java
        )).intercept { chain ->
            val returnValue = chain.proceed()

            val app = application ?: return@intercept returnValue
            val subscription = returnValue as? DownloadableSubscription ?: return@intercept returnValue

            val clipboardManager = app.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("Encoded eSIM activation code", subscription.encodedActivationCode))

            returnValue
        }
    }
}
package com.thatmarcel.apps.suresim

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.telephony.euicc.DownloadableSubscription
import android.telephony.euicc.EuiccManager
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam): XposedModule(base, param) {
    companion object {
        var application: Application? = null
    }

    @XposedHooker
    class ApplicationOnCreateHooker: XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback: XposedInterface.BeforeHookCallback): ApplicationOnCreateHooker {
                application = callback.thisObject as? Application

                return ApplicationOnCreateHooker()
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback: XposedInterface.AfterHookCallback, context: ApplicationOnCreateHooker) {}
        }
    }

    @XposedHooker
    class EuiccManagerIsEnabledHooker: XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback: XposedInterface.BeforeHookCallback): EuiccManagerIsEnabledHooker {
                return EuiccManagerIsEnabledHooker()
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback: XposedInterface.AfterHookCallback, context: EuiccManagerIsEnabledHooker) {
                callback.result = true
            }
        }
    }

    @XposedHooker
    class DownloadableSubscriptionHooker: XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback: XposedInterface.BeforeHookCallback): DownloadableSubscriptionHooker {
                return DownloadableSubscriptionHooker()
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback: XposedInterface.AfterHookCallback, context: DownloadableSubscriptionHooker) {
                val application = ModuleMain.application ?: return

                val subscription = callback.result as? DownloadableSubscription ?: return

                val clipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

                clipboardManager?.setPrimaryClip(ClipData.newPlainText("Encoded eSIM activation code", subscription.encodedActivationCode))
            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (!param.isFirstPackage) {
            return
        }

        hook(
            Application::class.java.getDeclaredMethod("onCreate"),
            ApplicationOnCreateHooker::class.java
        )

        hook(
            EuiccManager::class.java.getDeclaredMethod("isEnabled"),
            EuiccManagerIsEnabledHooker::class.java
        )

        hook(
            DownloadableSubscription::class.java.getDeclaredMethod(
                "forActivationCode",
                String::class.java
            ),
            DownloadableSubscriptionHooker::class.java
        )
    }
}
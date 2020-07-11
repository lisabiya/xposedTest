package com.knoyo.wifisimulator

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Create by wakfu on 2020/7/10
 */
class HookLogic : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        XposedBridge.log("HookLogic >> current package:" + lpparam?.packageName);
        Log.e("HookLogic", "mememme")
        if ("com.debby.ippick" == lpparam!!.packageName) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.debby.ippick.MainActivity",
                    lpparam!!.classLoader,
                    "getInfo",
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam): Any {
                            return "大家好，我是御天证道，我来自中国!"
                        }
                    })
            } catch (t: Throwable) {
                XposedBridge.log(t)
            }
        }
    }

}
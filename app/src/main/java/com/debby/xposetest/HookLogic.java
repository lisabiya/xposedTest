package com.debby.xposetest;

import android.app.Activity;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Create by wakfu on 2020/7/11
 */
public class HookLogic implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e("HookLogic", "MESSS*(*(^&(*&");
        XposedBridge.log("HookLogic >> current package:" + lpparam.packageName);
//        if ("com.debby.ippick".equals(lpparam.packageName)) {
        Log.e("HookLogic", "MESSS*(*IN" + lpparam.packageName);
        try {
            XposedHelpers.findAndHookMethod("com.debby.ippick.MainActivity", lpparam.classLoader,
                    "getInfo", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return "已被honk的应用V50";
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
//        }
    }

    @Override
    public String toString() {
        for (Class<?> anInterface : getClass().getInterfaces()) {
            Log.e("getInterfaces", anInterface.getName());
        }

        return "getInterfaces";
    }
}

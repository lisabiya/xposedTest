package com.debby.xposetest;

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
        if ("com.debby.ippick".equals(lpparam.packageName)) {
            try {
                XposedHelpers.findAndHookMethod("com.debby.ippick.MainActivity", lpparam.classLoader,
                        "getInfo", new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                return "大家好，我是御天证道，我来自中国!";
                            }
                        });
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }
}

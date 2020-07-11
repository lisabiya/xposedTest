package com.knoyo.wifisimulator

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity.Classs"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvMessage.text = getInfo();
        btBind.setOnClickListener {
//            intentTo()
            hookMe()
            Log.e(TAG, "*${isExpModuleActive(this)}*Active*")
            Log.e(TAG, "*${getExpApps(this)}*APP*")

        }
    }

    fun intentTo() {
        val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
        t.data = Uri.parse("package:" + "com.debby.xposedtest")
        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(t)
        } catch (e: ActivityNotFoundException) {
            // TaiChi not installed.
        }
    }

    fun hookMe() {
//        XposedBridge.log("HookLogic >> current package:  lpparam.packageName")

//        XposedBridge.invokeOriginalMethod()
        XposedHelpers.findAndHookMethod(
            localClassName,
            classLoader,
            "getInfo",
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return "大家好，我是御天证道，我来自中国!"
                }
            })
    }

    private fun isExpModuleActive(context: Context?): Boolean {
        var isExp = false
        requireNotNull(context) { "context must not be null!!" }
        try {
            val contentResolver: ContentResolver = context.getContentResolver()
            val uri: Uri = Uri.parse("content://me.weishu.exposed.CP/")
            var result: Bundle? = null
            try {
                result = contentResolver.call(uri, "active", null, null)
            } catch (e: RuntimeException) {
                // TaiChi is killed, try invoke
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e1: Throwable) {
                    return false
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null)
            }
            if (result == null) {
                return false
            }
            isExp = result.getBoolean("active", false)
        } catch (ignored: Throwable) {
        }
        return isExp
    }

    private fun getExpApps(context: Context): List<String?>? {
        val result: Bundle?
        result = try {
            context.contentResolver.call(
                Uri.parse("content://me.weishu.exposed.CP/"),
                "apps",
                null,
                null
            )!!
        } catch (e: Throwable) {
            return Collections.emptyList()
        }
        return if (result == null) {
            Collections.emptyList()
        } else result.getStringArrayList("apps") ?: return Collections.emptyList()
    }

    private fun getInfo(): String? {
        return "hello ,my name is Tom .I am from china"
    }
}
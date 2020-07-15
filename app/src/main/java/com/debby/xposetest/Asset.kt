package com.debby.xposetest

import android.util.Log
import com.blankj.utilcode.util.PathUtils
import kellinwood.security.zipsigner.ZipSigner
import kellinwood.security.zipsigner.optional.CustomKeySigner
import kellinwood.security.zipsigner.optional.PasswordObfuscator
import java.io.File

/**
 * Create by wakfu on 2020/7/15
 *
 */

class AssetFile {
    var fileAsset: String = PathUtils.getInternalAppDataPath() + "/innerStore/"
    var key: String = "my.jks"
    var provider = "provider.xml"
    var classes = "classes2.dex"
    var exposed = "lib/armeabi/libdexposed.so"
    var epic = "lib/armeabi/libepic.so"
    var epicV7 = "lib/armeabi-v7a/libepic.so"
    var epicV8 = "lib/arm64-v8a/libepic.so"
    var fileList: List<String> = arrayOf(classes, exposed, epic, epicV7, epicV8).toList()

    fun signApk(apkPath: String, outPath: String) {
        val signatureAlgorithm = "SHA1withRSA"
        val zipSigner = ZipSigner()
        zipSigner.addProgressListener { event ->
            Log.e(ManifestTool.TAG, "签名" + event.message)
        }
        val keystorePath = fileAsset + key
        val keystoreFile = File(keystorePath)
        val keystorePw = PasswordObfuscator.getInstance()
            .decodeKeystorePassword(keystorePath, "904028997")
        val aliasPw = PasswordObfuscator.getInstance()
            .decodeAliasPassword(keystorePath, "debby", "904028997")
        val pass = "904028997".toCharArray()

        CustomKeySigner.signZip(
            zipSigner, keystoreFile.absolutePath, pass,
            "debby", pass, signatureAlgorithm, apkPath, outPath
        )
    }
}
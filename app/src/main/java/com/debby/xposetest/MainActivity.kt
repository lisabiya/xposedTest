package com.debby.xposetest

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity.Classs"
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvMessage.text = getInfo();
        btBind.setOnClickListener {
            requestStorage()
        }
    }

    private fun requestStorage() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    pickFileApk()
                }

                override fun onDenied(
                    permissionsDeniedForever: List<String>,
                    permissionsDenied: List<String>
                ) {
                    ToastUtils.showShort("需要存储权限")
                }
            })
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
    }

    private fun pickFileApk() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_CODE)

        LogUtils.e(PathUtils.getDataPath())
        LogUtils.e(PathUtils.getInternalAppDataPath())
        LogUtils.e(PathUtils.getInternalAppFilesPath())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data //得到uri，后面就是将uri转化成file的过程。
            if (uri != null) {
                val file = UriUtils.uri2File(uri)
                if (file != null) {
                    Toast.makeText(this, file.path, Toast.LENGTH_LONG).show()
                    ManifestTool.UnZip(this, file)
                }
            }
        }
    }

    private fun getInfo(): String? {
        return "选取需要重新打包的app"
    }
}
package com.debby.xposetest;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.wjdiankong.main.ParserChunkUtils;
import cn.wjdiankong.main.XmlEditor;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Create by wakfu on 2020/7/15
 */
class ManifestTool {
    public static final String TAG = "ManifestTool";

    public static Observable<AssetFile> initAsset(final Context context) {
        return Observable.create(new ObservableOnSubscribe<AssetFile>() {
            @Override
            public void subscribe(ObservableEmitter<AssetFile> e) throws Exception {
                AssetFile assetFile = new AssetFile();

                String provider = assetFile.getFileAsset() + assetFile.getProvider();
                if (!FileUtils.isFileExists(provider)) {
                    FileUtils.createOrExistsFile(provider);
                    writeBytesToFile(context.getAssets().open(assetFile.getProvider()), provider);
                }
                String key = assetFile.getFileAsset() + assetFile.getKey();
                if (!FileUtils.isFileExists(key)) {
                    FileUtils.createOrExistsFile(key);
                    writeBytesToFile(context.getAssets().open(assetFile.getKey()), key);
                }
                e.onNext(assetFile);
            }
        }).subscribeOn(Schedulers.io());
    }

    public static void UnZip(final Context context, final File apkFilePath) {
//        ZipUtils.unzipFile(FileUtils.)
        final String filePath = PathUtils.getExternalDownloadsPath() + "/xposed/" + apkFilePath.getName().replace(".apk", "");

        boolean isCreate = FileUtils.createOrExistsDir(filePath);
        if (isCreate) {
            initAsset(context).subscribe(new Observer<AssetFile>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull AssetFile assetFile) {
                    if (assetFile == null) {
                        Log.e(TAG, "创建Asset文件失败");
                    } else {
                        try {

                            List<File> fileList = ZipUtils.unzipFile(apkFilePath.getPath(), filePath);
                            for (File file : fileList) {
                                if (file.getName().contains("AndroidManifest")) {
                                    File out = new File(file.getParent() + "/out.xml");
                                    AddTag(assetFile.getFileAsset() + assetFile.getProvider(), file, out);
                                }
                            }
                            if (FileUtils.isDir(filePath + "/" + "META-INF")) {
                                Log.e(TAG, "删除全部文件");
                                FileUtils.delete(filePath + "/" + "META-INF");
                            }
                            CopyFile(context, assetFile, filePath);
                            //打包
                            zipFiles(filePath, filePath + ".apk");
//                            zipFile.addFiles(fileList2);
                            Log.e(TAG, "打包成功");
                            assetFile.signApk(filePath + ".apk", filePath + "2.apk");
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage() + "IOException");
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.e(TAG, e.getMessage() + "Throwable");
                }

                @Override
                public void onComplete() {

                }

            });
        }
    }

    private static void zipFiles(String fileDirPath, String path) throws IOException {
//        ZipFile zipFile = new ZipFile(path);
        ArrayList<File> fileList = new ArrayList<>(FileUtils.listFilesInDir(fileDirPath));
        ZipUtils.zipFiles(fileList, new File(path));
//        for (File file : fileList) {
//            addFile(zipFile, file);
//        }
    }


    private static void CopyFile(Context context, AssetFile assetFile, String filePath) {
        for (String path : assetFile.getFileList()) {
            String desPath = filePath + "/" + path;
            if (FileUtils.createOrExistsFile(desPath)) {
                try {
                    writeBytesToFile(context.getAssets().open(path), desPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeBytesToFile(InputStream is, String file) throws IOException {
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while ((nbread = is.read(data)) > -1) {
                fos.write(data, 0, nbread);
            }

        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static void AddTag(String insertFilePath, File inputFile, File outputFile) {
        if (!inputFile.exists()) {
            Log.e(TAG, "输入文件不存在...");
        } else {
            try (FileInputStream fis = new FileInputStream(inputFile); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                boolean var8 = false;
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                ParserChunkUtils.xmlStruct.byteSrc = bos.toByteArray();
            } catch (Exception var33) {
                Log.e(TAG, "parse xml error:" + var33.toString());
            }
            //插入数据
            File file = new File(insertFilePath);
            if (!file.exists()) {
                Log.e(TAG, "插入标签xml文件不存在...");
                return;
            } else {
                XmlEditor.addTag(insertFilePath);
                System.out.println("插入标签完成...");
            }

            if (!outputFile.exists()) {
                outputFile.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(inputFile);
                fos.write(ParserChunkUtils.xmlStruct.byteSrc);
                fos.close();
            } catch (Exception ignored) {
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException var29) {
                        var29.printStackTrace();
                    }
                }
                ParserChunkUtils.clear();
            }
        }
    }

    public static String getDayDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sDateFormat.format(new Date());
    }
}

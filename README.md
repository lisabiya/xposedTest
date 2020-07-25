# xposedTest
侵入性较低的hook apk方案
包含解包，编辑，生成，签名一体化

> 基本思路

```
通过修改AndroidManifest注册provider,注册application
复制dex，lib到对应文件夹
重新打包签名
```

>实现参考
- [VirtualXposed](https://github.com/android-hacker/VirtualXposed)
- [exposed](https://github.com/android-hacker/exposed)


> 说明
```
当前签名大文件apk时会出现失败，暂未解决，可以手动调用jarsigner签名
assets文件夹下的provider.xml的类me.weishu.exposed.InitProvider存在在classes.dex文件内。
classes.dex根据需要更改的apk包内的dex数+1，例如apk内最高是classes8.dex，classes.dex改为classes9.dex
```

assets文件夹文件生成自 [exposed](https://github.com/lisabiya/exposed)
`主要添加InitProvider类和添加PathClassLoader实现`

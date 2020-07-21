# xposedTest
侵入性较低的hook apk方案

> 基本思路

```
通过修改AndroidManifest注册provider,注册application
复制dex，lib到对应文件夹
重新打包签名
```


包含解包，编辑，生成，签名一体化

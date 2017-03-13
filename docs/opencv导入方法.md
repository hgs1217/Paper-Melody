# opencv导入Android Studio方法

经过一番艰难的操作，我总结出了如下方法


0、由于我改过工程的gradle配置文件，但是opencv没传上来，所以你大概率会gradle失败

1、官网上下载opencv for android 3.2，然后解压缩

2、AS->File->New->Import Module 选择导入(压缩包路径)/sdk/java文件夹，然后导入模块的文件夹名统一命名为openCVLibrary320

3、在导入的openCVLibrary320文件夹目录下修改build.gradle文件的各种版本号与项目同步

4、AS->File->Project Structure-->app->Dependencies点击加号添加Module Dependency，选择openCVLibrary并确认

5、在app/src/main目录下新建文件夹jniLibs，然后把(压缩包路径)/sdk/native/libs文件夹目录下所有文件拷至app/src/main/jniLibs

6、重新Gradle，若没有报错则导入成功

7、使用opencv的时候需要在每个java文件开头出写  static{ System.loadLibrary("opencv_java3"); } 语句，从而可以避免手机端需安装opencvManager的困境

8、经过以上操作之后如果有真机可以尝试一下运行app（样例文件MainActivity.java），如果点击按钮能够正常切换，则大功告成


如果还有疑问，请联系我

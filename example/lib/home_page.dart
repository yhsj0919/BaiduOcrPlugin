import 'package:baidu_ocr/baidu_ocr.dart';
import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String ak = "g8r2bYTg5jsENCu9TDoKzm1z";
  String sk = "XUG77rIwxbfgr7QrGUxmITI2aYXRK0ag";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("百度OCR"),
        centerTitle: true,
      ),
      body: ListView(
        children: [
          ListTile(
            title: const Center(
              child: Text("init"),
            ),
            onTap: () async {
              BaiduOCR.init(appKey: ak, secretKey: sk).then((value) {
                print(value);
              }).catchError((e) {
                print('出现错误$e');
              });
            },
          ),
          ListTile(
            title: const Center(
              child: Text("测试识别"),
            ),
            onTap: () async {
              BaiduOCR.recognize(type: BaiduOCR.BANKCARD).then((value) {
                print(value);
              }).catchError((e) {
                print('出现错误$e');
              });
            },
          )
        ],
      ),
    );
  }
}

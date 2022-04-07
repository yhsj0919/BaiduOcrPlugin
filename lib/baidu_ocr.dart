import 'dart:async';

import 'package:flutter/services.dart';

class BaiduOCR {
  BaiduOCR._();

  ///通用文字识别（含位置信息版）
  static const int GENERAL = 105;

  ///通用文字识别
  static const int GENERAL_BASIC = 106;

  ///通用文字识别(高精度版)
  static const int ACCURATE_BASIC = 107;

  ///通用文字识别（含位置信息高精度版）
  static const int ACCURATE = 108;

  ///通用文字识别（含生僻字版）
  static const int GENERAL_ENHANCED = 109;

  ///网络图片识别
  static const int GENERAL_WEBIMAGE = 110;

  ///银行卡识别
  static const int BANKCARD = 111;

  ///行驶证识别
  static const int VEHICLE_LICENSE = 120;

  ///驾驶证识别
  static const int DRIVING_LICENSE = 121;

  ///车牌识别
  static const int LICENSE_PLATE = 122;

  ///营业执照识别
  static const int BUSINESS_LICENSE = 123;

  ///通用票据识别
  static const int RECEIPT = 124;

  /// 护照识别
  static const int PASSPORT = 125;

  ///数字识别
  static const int NUMBERS = 126;

  /// 二维码识别
  static const int QRCODE = 127;

  ///名片
  static const int BUSINESSCARD = 128;

  ///手写
  static const int HANDWRITING = 129;

  ///彩票
  static const int LOTTERY = 130;

  ///增值税发票识别
  static const int VATINVOICE = 131;

  /// 自定义模板
  static const int CUSTOM = 132;

  ///出租车票
  static const int TAXIRECEIPT = 133;

  ///VIN码
  static const int VINCODE = 134;

  ///火车票
  static const int TRAINTICKET = 135;

  /// 行程单
  static const int TRIP_TICKET = 136;

  /// 机动车销售发票
  static const int CAR_SELL_INVOICE = 137;

  /// 车辆合格证
  static const int VIHICLE_SERTIFICATION = 138;

  /// 试卷分析和识别
  static const int EXAMPLE_DOC_REG = 139;

  /// 手写文字识别
  static const int WRITTEN_TEXT = 140;

  /// 户口本识别
  static const int HUKOU_PAGE = 141;

  /// 普通机打发票识别
  static const int NORMAL_MACHINE_INVOICE = 142;

  ///磅单识别
  static const int WEIGHT_NOTE = 143;

  ///医疗费用明细识别
  static const int MEDICAL_DETAIL = 144;

  ///网约车行程单识别
  static const int ONLINE_TAXI_ITINERARY = 145;

  /// 身份证识别
  static const int IDCARD_FRONT = 201; // 正面
  static const int IDCARD_BACK = 202; //相册选择 反面
  static const int IDCARD_CAMERA = 102; // 身份证拍照

  static const MethodChannel _channel = MethodChannel('baidu_ocr');

  ///初始化
  static Future<String> init({required String appKey, required String secretKey}) async {
    String result = await _channel.invokeMethod('init', <String, dynamic>{'appKey': appKey, 'secretKey': secretKey});
    return result;
  }

  ///识别
  static Future<String> recognize({required int type}) async {
    String result = await _channel.invokeMethod('recognize', <String, dynamic>{'type': type});
    return result;
  }
}

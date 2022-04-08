package xyz.yhsj.ocr

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.sdk.model.IDCardParams
import com.baidu.ocr.sdk.model.IDCardResult
import com.baidu.ocr.ui.camera.CameraActivity
import com.baidu.ocr.ui.camera.CameraNativeHelper
import com.baidu.ocr.ui.camera.CameraView
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import java.io.File

/** BaiduOcrPlugin */
class BaiduOcrPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {

    private lateinit var channel: MethodChannel

    private val REQUEST_CODE_GENERAL = 105 // 通用文字识别（含位置信息版）

    private val REQUEST_CODE_GENERAL_BASIC = 106 // 通用文字识别

    private val REQUEST_CODE_ACCURATE_BASIC = 107 // 通用文字识别(高精度版)

    private val REQUEST_CODE_ACCURATE = 108 // 通用文字识别（含位置信息高精度版）

    private val REQUEST_CODE_GENERAL_ENHANCED = 109 // 通用文字识别（含生僻字版）

    private val REQUEST_CODE_GENERAL_WEBIMAGE = 110 // 网络图片识别

    private val REQUEST_CODE_BANKCARD = 111 // 银行卡识别

    private val REQUEST_CODE_VEHICLE_LICENSE = 120 // 行驶证识别

    private val REQUEST_CODE_DRIVING_LICENSE = 121 // 驾驶证识别

    private val REQUEST_CODE_LICENSE_PLATE = 122 // 车牌识别

    private val REQUEST_CODE_BUSINESS_LICENSE = 123 // 营业执照识别

    private val REQUEST_CODE_RECEIPT = 124 // 通用票据识别

    private val REQUEST_CODE_PASSPORT = 125 // 护照识别

    private val REQUEST_CODE_NUMBERS = 126 // 数字识别

    private val REQUEST_CODE_QRCODE = 127 // 二维码识别

    private val REQUEST_CODE_BUSINESSCARD = 128 // 名片识别

    private val REQUEST_CODE_HANDWRITING = 129 // 增值税发票识别

    private val REQUEST_CODE_LOTTERY = 130 // 彩票识别

    private val REQUEST_CODE_VATINVOICE = 131 // 手写识别

    private val REQUEST_CODE_CUSTOM = 132 // 自定义模板

    private val REQUEST_CODE_TAXIRECEIPT = 133
    private val REQUEST_CODE_VINCODE = 134
    private val REQUEST_CODE_TRAINTICKET = 135
    private val REQUEST_CODE_TRIP_TICKET = 136 // 行程单

    private val REQUEST_CODE_CAR_SELL_INVOICE = 137 // 机动车销售发票

    private val REQUEST_CODE_VIHICLE_SERTIFICATION = 138 // 车辆合格证

    private val REQUEST_CODE_EXAMPLE_DOC_REG = 139 // 试卷分析和识别

    private val REQUEST_CODE_WRITTEN_TEXT = 140 // 手写文字识别

    private val REQUEST_CODE_HUKOU_PAGE = 141 // 户口本识别

    private val REQUEST_CODE_NORMAL_MACHINE_INVOICE = 142 // 普通机打发票识别

    private val REQUEST_CODE_WEIGHT_NOTE = 143 //磅单识别

    private val REQUEST_CODE_MEDICAL_DETAIL = 144 //医疗费用明细识别

    private val REQUEST_CODE_ONLINE_TAXI_ITINERARY = 145 //网约车行程单识别


    /**
     * 身份证识别
     */
    private val REQUEST_CODE_IDCARD_FRONT = 201 //相册选择 正面
    private val REQUEST_CODE_IDCARD_BACK = 202 //相册选择 反面
    private val REQUEST_CODE_CAMERA = 102 // 身份证拍照

    /**
     * 身份证带本地质量控制
     */
    private val REQUEST_CODE_IDCARD_NATIVE_MANUAL_FRONT = 211; // 正面
    private val REQUEST_CODE_IDCARD_NATIVE_MANUAL_BACK = 212; // 正面

    private var hasGotToken = false

    private val CHANNEL_NAME = "baidu_ocr"

    private var activity: Activity? = null

    private var mResult: Result? = null

    /**
     * 结果监听
     */
    private val resultListener = object : RecognizeService.ServiceListener {
        override fun onResult(result: String?) {
            mResult?.success(result)
        }

        override fun onError(error: OCRError?) {
            mResult?.error("${error?.errorCode}", error?.message, error)
        }
    }


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

        when (call.method) {
            "init" -> {
                mResult = null
                val appKey: String? = call.argument("appKey")
                val secretKey: String? = call.argument("secretKey")
                initAccessTokenWithAkSk(appKey!!, secretKey!!, result)
            }
            "recognize" -> {
                //检查权限
                if (!checkTokenStatus(result)) return

                mResult = result

                //获取识别类型
                val type: Int? = call.argument("type")


                val intent = Intent(activity, CameraActivity::class.java)
                intent.putExtra(
                    CameraActivity.KEY_OUTPUT_FILE_PATH,
                    FileUtil.getSaveFile(activity).absolutePath
                )
                when (type) {
                    // 通用文字识别（含位置信息版）
                    REQUEST_CODE_GENERAL,
                        // 通用文字识别
                    REQUEST_CODE_GENERAL_BASIC,
                        // 通用文字识别(高精度版)
                    REQUEST_CODE_ACCURATE_BASIC,
                        // 通用文字识别（含位置信息高精度版）
                    REQUEST_CODE_ACCURATE,
                        // 通用文字识别（含生僻字版）
                    REQUEST_CODE_GENERAL_ENHANCED,
                        ///网络图片识别
                    REQUEST_CODE_GENERAL_WEBIMAGE,
                        ///行驶证识别
                    REQUEST_CODE_VEHICLE_LICENSE,
                        ///驾驶证识别
                    REQUEST_CODE_DRIVING_LICENSE,
                        ///车牌识别
                    REQUEST_CODE_LICENSE_PLATE,
                        ///营业执照识别
                    REQUEST_CODE_BUSINESS_LICENSE,
                        ///通用票据识别
                    REQUEST_CODE_RECEIPT,
                        ///数字识别
                    REQUEST_CODE_NUMBERS,
                        /// 二维码识别
                    REQUEST_CODE_QRCODE,
                        ///增值税发票识别
                    REQUEST_CODE_VATINVOICE,
                        /// 自定义模板
                    REQUEST_CODE_CUSTOM,
                        ///出租车票
                    REQUEST_CODE_TAXIRECEIPT,
                        ///VIN码
                    REQUEST_CODE_VINCODE,
                        ///火车票
                    REQUEST_CODE_TRAINTICKET,
                        /// 行程单
                    REQUEST_CODE_TRIP_TICKET,
                        /// 机动车销售发票
                    REQUEST_CODE_CAR_SELL_INVOICE,
                        /// 车辆合格证
                    REQUEST_CODE_VIHICLE_SERTIFICATION,
                        /// 试卷分析和识别
                    REQUEST_CODE_EXAMPLE_DOC_REG,
                        /// 手写文字识别
                    REQUEST_CODE_WRITTEN_TEXT,
                        /// 户口本识别
                    REQUEST_CODE_HUKOU_PAGE,
                        /// 普通机打发票识别
                    REQUEST_CODE_NORMAL_MACHINE_INVOICE,
                        ///磅单识别
                    REQUEST_CODE_WEIGHT_NOTE,
                        ///医疗费用明细识别
                    REQUEST_CODE_MEDICAL_DETAIL,
                        ///网约车行程单识别
                    REQUEST_CODE_ONLINE_TAXI_ITINERARY,
                        ///名片
                    REQUEST_CODE_BUSINESSCARD,
                        ///手写
                    REQUEST_CODE_HANDWRITING,
                        ///彩票
                    REQUEST_CODE_LOTTERY -> {
                        println("这是获取到的类型$type")
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_GENERAL
                        )
                        activity?.startActivityForResult(intent, type)
                    }
                    /// 护照识别
                    REQUEST_CODE_PASSPORT -> {
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_PASSPORT
                        )
                        activity?.startActivityForResult(intent, type)
                    }
                    /// 银行卡识别
                    REQUEST_CODE_BANKCARD -> {
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_BANK_CARD
                        )
                        activity?.startActivityForResult(intent, type)
                    }


                    // 身份证识别正面
                    REQUEST_CODE_IDCARD_FRONT -> {
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_ID_CARD_FRONT
                        )
                        activity?.startActivityForResult(intent, type)
                    }
                    // 身份证识别反面
                    REQUEST_CODE_IDCARD_BACK -> {

                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_ID_CARD_BACK
                        )
                        activity?.startActivityForResult(intent, type)
                    }

                    // 身份证识别正面,带本地质量控制
                    REQUEST_CODE_IDCARD_NATIVE_MANUAL_FRONT -> {
                        initNative()

                        intent.putExtra(
                            CameraActivity.KEY_NATIVE_ENABLE,
                            true
                        )
                        // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                        // 请手动使用CameraNativeHelper初始化和释放模型
                        // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                        intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true)
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_ID_CARD_FRONT
                        )
                        activity?.startActivityForResult(
                            intent,
                            REQUEST_CODE_IDCARD_NATIVE_MANUAL_FRONT
                        )


                    }
                    // 身份证识别反面,带本地质量控制
                    REQUEST_CODE_IDCARD_NATIVE_MANUAL_BACK -> {

                        initNative()

                        intent.putExtra(
                            CameraActivity.KEY_NATIVE_ENABLE,
                            true
                        )
                        // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                        // 请手动使用CameraNativeHelper初始化和释放模型
                        // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                        intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true)
                        intent.putExtra(
                            CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_ID_CARD_BACK
                        )
                        activity?.startActivityForResult(
                            intent,
                            REQUEST_CODE_IDCARD_NATIVE_MANUAL_FRONT
                        )
                    }


                    else -> {
                        result.notImplemented()
                    }


                }
            }
            else -> {
                mResult = null
                result.notImplemented()
            }
        }
    }

    /**
     * 检查权限
     */
    private fun checkTokenStatus(result: Result): Boolean {
        if (!hasGotToken) {
            result.error("500", "token还未成功获取", "token还未成功获取")
        }
        return hasGotToken
    }

    /**
     * 初始化Key
     */
    private fun initAccessTokenWithAkSk(appKey: String, secretKey: String, result: Result) {
        OCR.getInstance(activity).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(accessToken: AccessToken) {
                hasGotToken = true
                result.success(accessToken.tokenJson)
            }

            override fun onError(error: OCRError) {
                result.error("${error.errorCode}", error.message, "AK，SK方式获取token失败")
            }
        }, activity, appKey, secretKey)
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {

        activity = null
    }

    /**
     * 监听回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {

        if (resultCode == Activity.RESULT_OK && data != null) {
            val filePath = FileUtil.getSaveFile(activity).absolutePath

            when (requestCode) {
                // 通用文字识别（含位置信息版）
                REQUEST_CODE_GENERAL -> {
                    RecognizeService.recGeneral(activity, filePath, resultListener)
                }
                // 通用文字识别
                REQUEST_CODE_GENERAL_BASIC -> {
                    RecognizeService.recGeneralBasic(activity, filePath, resultListener)
                }
                // 通用文字识别(高精度版)
                REQUEST_CODE_ACCURATE_BASIC -> {
                    RecognizeService.recAccurateBasic(activity, filePath, resultListener)
                }
                // 通用文字识别（含位置信息高精度版）
                REQUEST_CODE_ACCURATE -> {
                    RecognizeService.recAccurate(activity, filePath, resultListener)
                }
                // 通用文字识别（含生僻字版）
                REQUEST_CODE_GENERAL_ENHANCED -> {
                    RecognizeService.recGeneralEnhanced(activity, filePath, resultListener)
                }
                ///网络图片识别
                REQUEST_CODE_GENERAL_WEBIMAGE -> {
                    RecognizeService.recWebimage(activity, filePath, resultListener)
                }
                ///行驶证识别
                REQUEST_CODE_VEHICLE_LICENSE -> {
                    RecognizeService.recVehicleLicense(activity, filePath, resultListener)
                }
                ///驾驶证识别
                REQUEST_CODE_DRIVING_LICENSE -> {
                    RecognizeService.recDrivingLicense(activity, filePath, resultListener)
                }
                ///车牌识别
                REQUEST_CODE_LICENSE_PLATE -> {
                    RecognizeService.recLicensePlate(activity, filePath, resultListener)
                }
                ///营业执照识别
                REQUEST_CODE_BUSINESS_LICENSE -> {
                    RecognizeService.recBusinessLicense(activity, filePath, resultListener)

                }
                ///通用票据识别
                REQUEST_CODE_RECEIPT -> {
                    RecognizeService.recReceipt(activity, filePath, resultListener)
                }
                ///数字识别
                REQUEST_CODE_NUMBERS -> {
                    RecognizeService.recNumbers(activity, filePath, resultListener)
                }
                /// 二维码识别
                REQUEST_CODE_QRCODE -> {
                    RecognizeService.recQrcode(activity, filePath, resultListener)
                }
                ///增值税发票识别
                REQUEST_CODE_VATINVOICE -> {
                    RecognizeService.recVatInvoice(activity, filePath, resultListener)
                }
                /// 自定义模板
                REQUEST_CODE_CUSTOM -> {
                    RecognizeService.recCustom(activity, filePath, resultListener)
                }
                ///出租车票
                REQUEST_CODE_TAXIRECEIPT -> {
                    RecognizeService.recTaxireceipt(activity, filePath, resultListener)
                }
                ///VIN码
                REQUEST_CODE_VINCODE -> {
                    RecognizeService.recVincode(activity, filePath, resultListener)
                }
                ///火车票
                REQUEST_CODE_TRAINTICKET -> {
                    RecognizeService.recTrainticket(activity, filePath, resultListener)
                }
                /// 行程单
                REQUEST_CODE_TRIP_TICKET -> {
                    RecognizeService.recTripTicket(activity, filePath, resultListener)
                }
                /// 机动车销售发票
                REQUEST_CODE_CAR_SELL_INVOICE -> {
                    RecognizeService.recCarSellInvoice(activity, filePath, resultListener)
                }
                /// 车辆合格证
                REQUEST_CODE_VIHICLE_SERTIFICATION -> {
                    RecognizeService.recVihicleCertification(activity, filePath, resultListener)
                }
                /// 试卷分析和识别
                REQUEST_CODE_EXAMPLE_DOC_REG -> {
                    RecognizeService.recExampleDoc(activity, filePath, resultListener)
                }
                /// 手写文字识别
                REQUEST_CODE_WRITTEN_TEXT -> {
                    RecognizeService.recWrittenText(activity, filePath, resultListener)
                }
                /// 户口本识别
                REQUEST_CODE_HUKOU_PAGE -> {
                    RecognizeService.recHuKouPage(activity, filePath, resultListener)
                }
                /// 普通机打发票识别
                REQUEST_CODE_NORMAL_MACHINE_INVOICE -> {
                    RecognizeService.recNormalMachineInvoice(activity, filePath, resultListener)
                }
                ///磅单识别
                REQUEST_CODE_WEIGHT_NOTE -> {
                    RecognizeService.recweightnote(activity, filePath, resultListener)
                }
                ///医疗费用明细识别
                REQUEST_CODE_MEDICAL_DETAIL -> {
                    RecognizeService.recmedicaldetail(activity, filePath, resultListener)
                }
                ///网约车行程单识别
                REQUEST_CODE_ONLINE_TAXI_ITINERARY -> {
                    RecognizeService.reconlinetaxiitinerary(activity, filePath, resultListener)
                }
                ///名片
                REQUEST_CODE_BUSINESSCARD -> {
                    RecognizeService.recBusinessCard(activity, filePath, resultListener)
                }
                ///手写
                REQUEST_CODE_HANDWRITING -> {
                    RecognizeService.recHandwriting(activity, filePath, resultListener)
                }
                ///彩票
                REQUEST_CODE_LOTTERY -> {
                    RecognizeService.recLottery(activity, filePath, resultListener)
                }
                /// 护照识别
                REQUEST_CODE_PASSPORT -> {
                    RecognizeService.recPassport(activity, filePath, resultListener)
                }
                /// 银行卡识别
                REQUEST_CODE_BANKCARD -> {
                    RecognizeService.recBankCard(activity, filePath, resultListener)
                }

                // 身份证识别正面
                REQUEST_CODE_IDCARD_FRONT -> {
                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath, resultListener)
                }
                // 身份证识别反面
                REQUEST_CODE_IDCARD_BACK -> {
                    recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath, resultListener)
                }

                // 身份证识别正面,带本地质量控制
                REQUEST_CODE_IDCARD_NATIVE_MANUAL_FRONT -> {
                    // 释放本地质量控制模型
                    CameraNativeHelper.release()
                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath, resultListener)
                }
                // 身份证识别反面,带本地质量控制
                REQUEST_CODE_IDCARD_NATIVE_MANUAL_BACK -> {
                    // 释放本地质量控制模型
                    CameraNativeHelper.release()
                    recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath, resultListener)
                }


                else -> {
                    mResult?.notImplemented()
                }

            }
        } else {
            mResult?.error("500", "文件系统异常", "")
        }
        return false
    }


    /**
     * 识别身份证
     */
    private fun recIDCard(
        idCardSide: String,
        filePath: String,
        listener: RecognizeService.ServiceListener
    ) {
        val param = IDCardParams()
        param.imageFile = File(filePath)
        // 设置身份证正反面
        param.idCardSide = idCardSide
        // 设置方向检测
        param.isDetectDirection = true
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.imageQuality = 20
        OCR.getInstance(activity).recognizeIDCard(param, object : OnResultListener<IDCardResult> {
            override fun onResult(result: IDCardResult) {
                listener.onResult(result.jsonRes)
            }

            override fun onError(error: OCRError) {
                listener.onError(error)
            }
        })
    }

    /**
     * 初始化本地质量控制
     */
    private fun initNative() {
        //  初始化本地质量控制模型,释放代码在onDestory中
        //  调用身份证扫描必须加上 intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true); 关闭自动初始化和释放本地模型
        CameraNativeHelper.init(activity, OCR.getInstance(activity).license) { errorCode, _ ->
            val msg: String = when (errorCode) {
                CameraView.NATIVE_SOLOAD_FAIL -> "加载so失败，请确保apk中存在ui部分的so"
                CameraView.NATIVE_AUTH_FAIL -> "授权本地质量控制token获取失败"
                CameraView.NATIVE_INIT_FAIL -> "本地初始化失败"
                else -> errorCode.toString()
            }
            mResult?.error("500", msg, msg)
        }
    }


}

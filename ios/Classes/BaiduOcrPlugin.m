#import "BaiduOcrPlugin.h"
#if __has_include(<baidu_ocr/baidu_ocr-Swift.h>)
#import <baidu_ocr/baidu_ocr-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "baidu_ocr-Swift.h"
#endif

@implementation BaiduOcrPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBaiduOcrPlugin registerWithRegistrar:registrar];
}
@end

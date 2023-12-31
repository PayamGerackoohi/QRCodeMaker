//
// Created by Payam Gerackoohi on 12/27/23.
//

#ifndef QR_CODE_MAKER_JNI_UTIL_H
#define QR_CODE_MAKER_JNI_UTIL_H

#include<jni.h>
#include "qrcode-util.hpp"

std::unique_ptr<jobject> jQrCode(JNIEnv *, std::unique_ptr<QrCodeUtil::QrCode> &);

std::string jString2string(JNIEnv *, jstring);

std::string getStringField(JNIEnv *, jobject&, const char *fieldName);

QrCodeUtil::QrCode::Ecc ecc_from_ordinal(jint level);

#endif //QR_CODE_MAKER_JNI_UTIL_H

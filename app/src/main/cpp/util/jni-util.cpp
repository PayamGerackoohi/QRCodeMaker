//
// Created by Payam Gerackoohi on 12/27/23.
//

#include "jni-util.h"

using std::unique_ptr;
using std::string;
using std::make_unique;
using qrcodegen::QrCode;

unique_ptr<jobject> jQrCode(JNIEnv *env, unique_ptr<QrCode> &qrcode) {
    auto size = qrcode->getSize();
    auto ss = size * size;
    auto data = make_unique<unsigned char[]>(ss);
    for (int x = 0; x < size; x++)
        for (int y = 0; y < size; y++)
            data[x * size + y] = qrcode->getModule(x, y);

    auto jData = env->NewBooleanArray(ss);
    env->SetBooleanArrayRegion(jData, 0, ss, data.get());

    auto clazz = env->FindClass("com/payamgr/qrcodemaker/data/model/QrCode");
    auto methodId = env->GetMethodID(clazz, "<init>", "(I[Z)V");
    auto jQrCode = make_unique<jobject>(env->NewObject(clazz, methodId, size, jData));

    env->DeleteLocalRef(clazz);
    return jQrCode;
}

string jString2string(JNIEnv *env, jstring jString) {
    const char *chars = env->GetStringUTFChars(jString, nullptr);
    string result = std::string(chars, env->GetStringUTFLength(jString));
    env->ReleaseStringUTFChars(jString, chars);
    return result;
}

std::string getStringField(JNIEnv *env, jobject &object, const char *fieldName) {
    auto jResult = env->GetObjectField(
            object,
            env->GetFieldID(env->GetObjectClass(object), fieldName, "Ljava/lang/String;")
    );
    auto result = jString2string(env, (jstring) jResult);
    env->DeleteLocalRef(jResult);
    return result;
}

QrCode::Ecc ecc_from_ordinal(jint level) {
    switch (level) {
        case 2:
            return QrCode::Ecc::HIGH;
        case 1:
            return QrCode::Ecc::MEDIUM;
        default:
            return QrCode::Ecc::LOW;
    }
}

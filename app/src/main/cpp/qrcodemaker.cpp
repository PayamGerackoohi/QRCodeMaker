#include <jni.h>
#include "content/phone-call.hpp"
#include "qrcode-util.hpp"
#include "util/jni-util.h"

using QrCodeUtil::QrCode;
using std::string;
using std::make_unique;

extern "C"
JNIEXPORT jobject JNICALL
Java_com_payamgr_qrcodemaker_data_QrCodeMaker_text(JNIEnv *env, jobject, jstring jText, jint jEcc) {
    auto text = jString2string(env, jText);
    auto qrcode = QrCode::encodeText(text.c_str(), ecc_from_ordinal(jEcc));
    return *jQrCode(env, qrcode);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_payamgr_qrcodemaker_data_QrCodeMaker_phoneCall(JNIEnv *env, jobject, jstring jPhone, jint jEcc) {
    auto phone = jString2string(env, jPhone);
    QrCodeUtil::PhoneCall call(phone);
    auto qrcode = QrCode::encodeText(call.str().c_str(), ecc_from_ordinal(jEcc));
    return *jQrCode(env, qrcode);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_payamgr_qrcodemaker_data_QrCodeMaker_meCard(JNIEnv *env, jobject, jobject jMeCard, jint jEcc) {
    QrCodeUtil::MeCard meCard;
    auto firstName = getStringField(env, jMeCard, "firstName");
    auto lastName = getStringField(env, jMeCard, "lastName");
    auto phone = getStringField(env, jMeCard, "phone");
    meCard.name = make_unique<QrCodeUtil::MeCard::Name>(firstName, lastName);
    meCard.phone = make_unique<string>(phone);

    auto qrcode = QrCode::encodeText(meCard.str().c_str(), ecc_from_ordinal(jEcc));
    return *jQrCode(env, qrcode);
}

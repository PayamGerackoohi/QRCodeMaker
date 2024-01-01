#ifndef QR_CODE_HPP
#define QR_CODE_HPP

#include "content/email.hpp"
#include "content/me-card.hpp"
#include "content/phone-call.hpp"
#include "content/v-card.hpp"
#include "qrcodegen.hpp"
#include <string>

namespace QrCodeUtil {

using QrCode = qrcodegen::QrCode;

std::string toSvgString(const QrCode &qr, int border);

} // namespace QrCodeUtil

#endif

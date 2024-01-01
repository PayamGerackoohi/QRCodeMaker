include_directories(include src)
add_library(qrcode 
  src/qrcode/content/app-market.cpp
  src/qrcode/content/biz-card.cpp
  src/qrcode/content/calendar.cpp
  src/qrcode/content/email.cpp
  src/qrcode/content/face-time.cpp
  src/qrcode/content/geo.cpp
  src/qrcode/content/me-card.cpp
  src/qrcode/content/phone-call.cpp
  src/qrcode/content/sms.cpp
  src/qrcode/content/v-card.cpp
  src/qrcode/content/wifi.cpp
  src/qrcode/qrcode-util.cpp
  src/qrcode/qrcodegen.cpp
  src/util/string-util.cpp
)

#include "content/wifi.hpp"
#include <sstream>

using std::make_unique;
using std::ostream;
using std::ostringstream;
using std::string;
using std::unique_ptr;

namespace QrCodeUtil {
Wifi::SSID::SSID(std::string value) : value(value) {}

unique_ptr<Wifi::SSID> Wifi::SSID::fromAscii(string value) {
  ostringstream ss;
  ss << '\"' << value << '\"';
  return make_unique<SSID>(ss.str());
}

unique_ptr<Wifi::SSID> Wifi::SSID::fromHex(string value) { return make_unique<SSID>(value); }

ostream &operator<<(ostream &os, Wifi &wifi) {
  os << "WIFI:";

  if (wifi.authenticationType != nullptr)
    os << "T:" << wifi.authentication_type_to_string() << ";";

  if (wifi.ssid != nullptr)
    os << "S:" << wifi.ssid->value << ";";

  if (wifi.password != nullptr)
    os << "P:" << *wifi.password << ";";

  if (wifi.hideSsid != nullptr)
    os << "H:" << (*wifi.hideSsid ? "true" : "false") << ";";

  if (wifi.eapMode != nullptr)
    os << "E:" << *wifi.eapMode << ";";

  if (wifi.anonymousIdentity != nullptr)
    os << "A:" << *wifi.anonymousIdentity << ";";

  if (wifi.identity != nullptr)
    os << "I:" << *wifi.identity << ";";

  if (wifi.phase2method != nullptr)
    os << "PH2:" << *wifi.phase2method << ";";

  os << ";";
  return os;
}

string Wifi::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

string Wifi::authentication_type_to_string() {
  if (authenticationType == nullptr)
    return "";
  switch (*authenticationType) {
  case AuthenticationType::WEP:
    return "WEP";
  case AuthenticationType::WPA:
    return "WPA";
  case AuthenticationType::WPA2_EAP:
    return "WPA2-EAP";
  case AuthenticationType::nopass:
    return "nopass";
  default:
    return "?";
  }
}
} // namespace QrCodeUtil

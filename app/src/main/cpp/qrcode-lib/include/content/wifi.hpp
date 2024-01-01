#ifndef WIFI_HPP
#define WIFI_HPP

#include <memory>
#include <ostream>
#include <string>

namespace QrCodeUtil {
struct Wifi {
  struct SSID {
    std::string value;

    SSID(std::string value);
    static std::unique_ptr<SSID> fromAscii(std::string);
    static std::unique_ptr<SSID> fromHex(std::string);
  };
  enum class AuthenticationType { WEP, WPA, WPA2_EAP, nopass };
  /**
   * Authentication type; can be WEP or WPA or WPA2-EAP, or nopass for no password. Or, omit for no
   * password.
   */
  std::unique_ptr<AuthenticationType> authenticationType;
  /**
   * Network SSID. Required. Enclose in double quotes if it is an ASCII name, but could be
   * interpreted as hex (i.e. "ABCD")
   */
  std::unique_ptr<SSID> ssid;
  /**
   * Password, ignored if T is nopass (in which case it may be omitted). Enclose in double quotes if
   * it is an ASCII name, but could be interpreted as hex (i.e. "ABCD")
   */
  std::unique_ptr<std::string> password;
  /**
   * Optional. True if the network SSID is hidden. Note this was mistakenly also used to specify
   * phase 2 method in releases up to 4.7.8 / Barcode Scanner 3.4.0. If not a boolean, it will be
   * interpreted as phase 2 method (see below) for backwards-compatibility
   */
  std::unique_ptr<bool> hideSsid;
  /**
   * (WPA2-EAP only) EAP method, like TTLS or PWD
   */
  std::unique_ptr<std::string> eapMode;
  /**
   * (WPA2-EAP only) Anonymous identity
   */
  std::unique_ptr<std::string> anonymousIdentity;
  /**
   * (WPA2-EAP only) Identity
   */
  std::unique_ptr<std::string> identity;
  /**
   * (WPA2-EAP only) Phase 2 method, like MSCHAPV2
   */
  std::unique_ptr<std::string> phase2method;

  friend std::ostream &operator<<(std::ostream &, Wifi &);
  std::string str();

private:
  std::string authentication_type_to_string();
};
} // namespace QrCodeUtil

#endif

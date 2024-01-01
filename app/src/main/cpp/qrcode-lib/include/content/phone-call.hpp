#ifndef PHONE_CALL_HPP
#define PHONE_CALL_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {
class PhoneCall {
  std::string phone;

public:
  PhoneCall(std::string phone);
  friend std::ostream &operator<<(std::ostream &, PhoneCall &);
  std::string str();
};
} // namespace QrCodeUtil

#endif

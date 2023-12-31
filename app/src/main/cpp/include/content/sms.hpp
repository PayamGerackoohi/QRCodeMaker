#ifndef SMS_HPP
#define SMS_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {
struct Sms {
  std::string phone;
  std::unique_ptr<std::string> message;

  Sms(std::string phone);
  friend std::ostream &operator<<(std::ostream &, Sms &);
  std::string str();
};
} // namespace QrCodeUtil

#endif

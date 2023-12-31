#include "content/sms.hpp"
#include "util/string-util.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
Sms::Sms(string phone) : phone(phone) {}

ostream &operator<<(ostream &os, Sms &sms) {
  os << "sms:" << sms.phone;
  if (sms.message != nullptr)
    os << ":" << url_encode(*sms.message);
  return os;
}

string Sms::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil

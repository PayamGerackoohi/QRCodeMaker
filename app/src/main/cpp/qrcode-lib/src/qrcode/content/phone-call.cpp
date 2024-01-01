#include "content/phone-call.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
PhoneCall::PhoneCall(string phone) : phone(phone) {}

ostream &operator<<(std::ostream &os, PhoneCall &phoneCall) {
  os << "tel:" << phoneCall.phone;
  return os;
}

string PhoneCall::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil

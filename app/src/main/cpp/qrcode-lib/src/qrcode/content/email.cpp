#include "content/email.hpp"
#include "util/string-util.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
Email::Email(string address) : address(address) {}

ostream &operator<<(ostream &os, Email &email) {
  os << "mailto:" << email.address;

  if (email.hasData())
    os << '?';

  if (email.cc.size() != 0) {
    os << "cc=" << join(email.cc);
    email.hasQuery = true;
  }

  if (email.bcc.size() != 0) {
    if (email.hasQuery)
      os << "&";
    os << "bcc=" << join(email.bcc);
    email.hasQuery = true;
  }

  if (email.subject != nullptr) {
    if (email.hasQuery)
      os << "&";
    os << "subject=" << url_encode(*email.subject);
    email.hasQuery = true;
  }

  if (email.body != nullptr) {
    if (email.hasQuery)
      os << "&";
    os << "body=" << url_encode(*email.body);
  }

  return os;
}

string Email::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

bool Email::hasData() {
  return cc.size() != 0 || bcc.size() != 0 || subject != nullptr || body != nullptr;
}
} // namespace QrCodeUtil

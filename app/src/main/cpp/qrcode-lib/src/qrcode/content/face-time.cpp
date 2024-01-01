#include "content/face-time.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
FaceTime::FaceTime(Type type, string target) : type(type), target(target) {}

ostream &operator<<(ostream &os, FaceTime &faceTime) {
  if (faceTime.type == FaceTime::Type::Video)
    os << "facetime:";
  if (faceTime.type == FaceTime::Type::Audio)
    os << "facetime-audio:";
  os << faceTime.target;
  return os;
}

string FaceTime::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil

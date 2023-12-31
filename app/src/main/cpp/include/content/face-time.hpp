#ifndef FACE_TIME_HPP
#define FACE_TIME_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {
class FaceTime {
public:
  enum class Type { Video, Audio };

private:
  Type type;
  std::string target;

public:
  /**
   * target: phone or cloudId
   */
  FaceTime(Type type, std::string target);
  friend std::ostream &operator<<(std::ostream &, FaceTime &);
  std::string str();
};
} // namespace QrCodeUtil

#endif

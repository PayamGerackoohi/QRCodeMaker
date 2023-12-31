#ifndef BIZ_CARD_HPP
#define BIZ_CARD_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {
struct BizCard {
  std::unique_ptr<std::string> firstName;
  std::unique_ptr<std::string> lastName;
  std::unique_ptr<std::string> title;
  std::unique_ptr<std::string> company;
  std::unique_ptr<std::string> address;
  std::unique_ptr<std::string> phone;
  std::unique_ptr<std::string> email;

  friend std::ostream &operator<<(std::ostream &, BizCard &);
  std::string str();
};
} // namespace QrCodeUtil

#endif

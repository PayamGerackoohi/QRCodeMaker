#ifndef ME_CARD_HPP
#define ME_CARD_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {

using unique_string = std::unique_ptr<std::string>;

struct MeCard {
  class Name {
    std::string firstName;
    std::string lastName;

  public:
    Name(std::string firstName, std::string lastName);
    friend std::ostream &operator<<(std::ostream &, Name &);
  };

  std::unique_ptr<Name> name;
  unique_string phone;
  unique_string videoPhone;
  unique_string email;
  unique_string address;
  unique_string birthDay;
  unique_string nickName;
  unique_string note;
  unique_string url;
  friend std::ostream &operator<<(std::ostream &, MeCard &);
  std::string str();
};

} // namespace QrCodeUtil

#endif

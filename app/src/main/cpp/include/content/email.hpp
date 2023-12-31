#ifndef EMAIL_HPP
#define EMAIL_HPP

#include <ostream>
#include <string>
#include <vector>

namespace QrCodeUtil {
struct Email {
  std::string address;
  std::vector<std::string> cc;
  std::vector<std::string> bcc;
  std::unique_ptr<std::string> subject;
  std::unique_ptr<std::string> body;

  Email(std::string address);
  friend std::ostream &operator<<(std::ostream &, Email &);
  std::string str();
  bool hasData();

private:
  bool hasQuery = false;
};
} // namespace QrCodeUtil

#endif

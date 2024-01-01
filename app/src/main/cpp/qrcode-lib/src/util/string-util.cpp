#include "string-util.hpp"
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>

using std::hex;
using std::nouppercase;
using std::ostringstream;
using std::setw;
using std::string;
using std::uppercase;

string url_encode(const string &value) {
  ostringstream escaped;
  escaped.fill('0');
  escaped << hex;

  for (string::const_iterator i = value.begin(), n = value.end(); i != n; ++i) {
    string::value_type c = (*i);

    // Keep alphanumeric and other accepted characters intact
    if (isalnum(c) || c == '-' || c == '_' || c == '.' || c == '~') {
      escaped << c;
      continue;
    }

    // Any other characters are percent-encoded
    escaped << uppercase;
    escaped << '%' << setw(2) << int((unsigned char)c);
    escaped << nouppercase;
  }

  return escaped.str();
}

string geo_format(int value) {
  ostringstream ss;
  ss << std::setprecision(5) << std::fixed << value / 100'000.0;
  return ss.str();
}

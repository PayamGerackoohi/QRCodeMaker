#ifndef STRING_UTIL_HPP
#define STRING_UTIL_HPP

#include <sstream>
#include <string>
#include <vector>

template <typename T> std::string join(std::vector<T> list, std::string delimiter = ",");
std::string url_encode(const std::string &value);
std::string geo_format(int value);

#include "string-util.tpp"

#endif

template <typename T> std::string join(std::vector<T> list, std::string delimiter) {
  std::ostringstream ss;
  bool isFirst = true;
  for (auto &item : list) {
    if (isFirst)
      isFirst = false;
    else
      ss << delimiter;
    ss << item;
  }
  return ss.str();
}

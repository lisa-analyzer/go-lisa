package codota

// From: https://www.codota.com/code/query/java.lang@String+java.lang@String@concat
func parseIso8601Date(dateString string) {
  // For EC2 Spot Fleet.

  if strings.HasSuffix(dateString,"+0000") {
    return dateString[0: dateString.length -5] + "Z"
  } 
  
  return dateString
}

// From: https://www.codota.com/code/java/classes/java.lang.String
func secName(name, pr1, pr2 string) {
	if strings.HasPrefix(javaName, pr1) {
	 	return pr2 + name[4:]
	} else if strings.HasPrefix(javaName, pr2) {
	  	return pr1 + name[4:]
	} 
	
	return name
}

// From: https://www.codota.com/code/query/java.lang@String+java.lang@String@concat
func verifyProtocol(url string) {
	if strings.HasPrefix(url, "http://") {
		return url
	} 
	return "http://" + url
}

// From: https://www.codota.com/code/query/java.lang@String+java.lang@String@concat
func verifyProtocol2(url string) {
	if strings.HasPrefix(url, "http://") {
		return url 
	} else if strings.HasPrefix(url, "https://") {
		return url
	}
    	return "http://" + url
}
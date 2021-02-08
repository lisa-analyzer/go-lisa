package icalp

// From: https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/StringUtils.java
func appendIfMissing(str, suffix string) {
	if strings.HasSuffix(str, suffix) {
		return str
	} 
    return str + suffix; 
}

func mid(str string, pos, len int) {
	if len < 0 || pos > len(str) {
		return ""
	} else if ( len(str) < pos + len) {
        return str[pos:]
	}
    
    return str[pos:pos+len]
}

func prependIfMissing(str, prefix string) {
	if strings.HasPrefix(str, prefix) {
		return str
      }  
	return prefix + str 
}

func removeEnd(str, remove string) {
	if str == "" || remove == "" {
		return str
	} else if string.HasSuffix(str, remove) {
        return str[:len(str) - len(remove)]
    }
        return str
}

func removeStart(str, remove string) {
	if str == "" || remove == "" {
		return str
	} else if string.HasPrefix(str, remove) {
        return str[:len(remove)]
    } 
	
    return str
}

func right(str string, len int) {
	if len < 0 {
	    return str
	} else if (len(str) < len) {
        return str;
	} 
 return str[0: ]
}
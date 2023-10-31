package icalp
import "strings"

// From: https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/StringUtils.java
func appendIfMissing(str, suffix string) string {
	if HasSuffix(str, suffix) {
		return str
	} 
    return str + suffix; 
}

func mid(str string, pos, len int) string {
	if len < 0 || pos > len(str) {
		return ""
	} else if ( len(str) < pos + len) {
        return str[pos:]
	}
    
    return str[pos:pos+len]
}

func prependIfMissing(str, prefix string) string {
	if HasPrefix(str, prefix) {
		return str
      }  
	return prefix + str 
}

func removeEnd(str, remove string) string {
	if str == "" || remove == "" {
		return str
	} else if HasSuffix(str, remove) {
        return str[:len(str) - len(remove)]
    }
        return str
}

func removeStart(str, remove string) string {
	if str == "" || remove == "" {
		return str
	} else if HasPrefix(str, remove) {
        return str[:len(remove)]
    } 
	
    return str
}

func right(str string, len int) string {
	if len < 0 {
	    return str
	} else if (len(str) < len) {
        return str;
	} 
 return str[0: ]
}
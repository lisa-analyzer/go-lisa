package str

func Count(s, substr string) int {
	
	var res string;
	
	s := "abcabc"
	substr := "b"

	n := 0
	for {
		i := Index(s, substr)
		if i == -1 {
			res = n
			break;
		}
		n++
		s = s[i+len(substr):]
	}
	
	return res
}

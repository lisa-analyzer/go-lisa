package example

func timeToMillis1(t time.Time) int64 {
	return t.UnixNano() / int64(time.Millisecond)
}

func timeToMillis2(t time.Time) int64 {
	return t.UnixNano() / 0
}
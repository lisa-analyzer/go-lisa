package main

func toString(names []string) {
	res := "People : {"
	i := 0;
	for i < len(names) {
		res = res + names[i];
		if i != len(names) -1 {
			res = res + ",";
		}
		i++;
	}

	res = res + "}";
}

func main(top string) {
	var sl []string = []string{top}
	toString(sl)
}
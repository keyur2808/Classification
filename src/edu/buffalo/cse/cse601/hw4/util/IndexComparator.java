package edu.buffalo.cse.cse601.hw4.util;

import java.util.Comparator;
import java.util.HashMap;

public class IndexComparator implements Comparator<String> {

    private HashMap<String, Double> doubleMap;

    public IndexComparator(HashMap<String, Double> tagMap) {
        this.doubleMap = tagMap;
    }

	public int compare(String key1, String key2) {
        double value1, value2;

        value1 = (Double) doubleMap.get(key1);
        value2 = (Double) doubleMap.get(key2);

		if (value1 < value2) {
			return 1;
		} else if (value1 > value2)
			return -1;
		else
			return key1.compareTo(key2);
    }
}

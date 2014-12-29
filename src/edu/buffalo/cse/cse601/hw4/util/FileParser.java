package edu.buffalo.cse.cse601.hw4.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FileParser {

	private String fileName;
	private ArrayList<Integer> ignoreSet = new ArrayList<Integer>();
	private ArrayList<String[]> data = new ArrayList<String[]>();
	private ArrayList<String> classLabels = new ArrayList<String>();
	private Map<String, Map<String, String>> idMappings = new HashMap<>();

	public FileParser(String fileName) {
		this.setFileName(fileName);
	}

	public Map<String, Map<String, String>> getIdMappings() {
		FileReader f = null;
		BufferedReader br = null;
		Map<String, String> idMap = null;
		try {
			f = new FileReader(fileName);
			br = new BufferedReader(f);

			String inputLine = null;
			while ((inputLine = br.readLine()) != null) {
				if (inputLine.contains("description")) {
					String fieldName = inputLine.split(",")[0];
					while (((inputLine = br.readLine()) != null) && (!inputLine.equals(","))) {
						idMap = new HashMap<>();
						String[] mapping = inputLine.split(",");
						if (mapping != null && mapping.length == 2) {
							idMap.put(mapping[0], mapping[1]);
						}
					}
					idMappings.put(fieldName, idMap);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null & f != null) {
					br.close();
					f.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return idMappings;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the data
	 */
	public void readData() {

		FileReader f = null;
		BufferedReader br = null;

		try {
			f = new FileReader(fileName);
			br = new BufferedReader(f);
			String line = null;
			StringTokenizer st = null;
			// Skip Header
			String header = br.readLine();
			st = new StringTokenizer(header, ",");
			int d = st.countTokens();
			int numLines = 0;
			while ((line = br.readLine()) != null) {
				
				String[] record = new String[d - ignoreSet.size() - 1];
				st = new StringTokenizer(line, ",");
				int tokenCount = 0;
				int count = 0;
				while (st.hasMoreTokens()) {
					String attribValue = st.nextToken();
					if (!ignoreSet.contains(tokenCount) && tokenCount != d - 1) {
						record[count] = attribValue;
						count++;
					} else if (tokenCount == d - 1) {
						classLabels.add(attribValue);
					}
					tokenCount++;
				}
				data.add(record);
				numLines++;

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null & f != null) {
					br.close();
					f.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the ignoreSet
	 */
	public ArrayList<Integer> getIgnoreSet() {
		return ignoreSet;
	}

	/**
	 * @param ignoreSet
	 *            the ignoreSet to set
	 */
	public void setIgnoreSet(ArrayList<Integer> ignoreSet) {
		this.ignoreSet = ignoreSet;
	}

	/**
	 * @return the data
	 */
	public ArrayList<String[]> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(ArrayList<String[]> data) {
		this.data = data;
	}

	/**
	 * @return the classLabels
	 */
	public ArrayList<String> getClassLabels() {
		return classLabels;
	}

	/**
	 * @param classLabels
	 *            the classLabels to set
	 */
	public void setClassLabels(ArrayList<String> classLabels) {
		this.classLabels = classLabels;
	}

}
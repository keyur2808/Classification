package edu.buffalo.cse.cse601.hw4.util;

import java.util.*;
import java.util.Random;

public class DataSplitter {

	double SPLIT = 0.2;

	List<String[]> trainData = new ArrayList<String[]>();
	List<String[]> testData = new ArrayList<String[]>();
	List<String> trainClassLabels = new ArrayList<String>();
	List<String> testClassLabels = new ArrayList<String>();
	List<String> classLabels = null;
	List<String[]> data = null;

	List<ArrayList<String[]>> kFoldSplits = new ArrayList<ArrayList<String[]>>();
	List<ArrayList<String>> kFoldClassLabelSplits = new ArrayList<ArrayList<String>>();

	public DataSplitter(List<String> classLabels, List<String[]> data) {
		this.classLabels = classLabels;
		this.data = data;
	}

	public void kFoldSplit(int k) {
		for (int i = 0; i < classLabels.size(); i++) {
			int j = i % k;
			ArrayList<String[]> split = null;
			ArrayList<String> splitClassLabels = null;
			if (kFoldSplits.size() >= j + 1) {
				split = kFoldSplits.get(j);
				splitClassLabels = kFoldClassLabelSplits.get(j);
			}
			if (split == null) {
				split = new ArrayList<String[]>();
				splitClassLabels = new ArrayList<String>();
			}
			split.add(data.get(i));
			splitClassLabels.add(classLabels.get(i));
			if (kFoldSplits.size() >= j + 1) {
				kFoldSplits.set(j, split);
				kFoldClassLabelSplits.set(j, splitClassLabels);
			}
			else{
				kFoldSplits.add(split);
				kFoldClassLabelSplits.add(splitClassLabels);
			}
//			System.out.println("j = " + j  + "split size = " + kFoldSplits.get(j).size());
		}
		System.out.println(kFoldClassLabelSplits.size());
	}

	public void createKthSplit(int k) {
		trainData = new ArrayList<String[]>();
		testData = new ArrayList<String[]>();
		trainClassLabels = new ArrayList<String>();
		testClassLabels = new ArrayList<String>();
		for (int i = 0; i < kFoldSplits.size(); i++) {
			if (i == k) {
				testData.addAll(kFoldSplits.get(i));
				testClassLabels.addAll(kFoldClassLabelSplits.get(i));
			} else {
				trainData.addAll(kFoldSplits.get(i));
				trainClassLabels.addAll(kFoldClassLabelSplits.get(i));
			}
		}
	}

	public void createTrainNTest() {
		Random random = new Random();
		System.out.println("num classLabels: " + classLabels.size());
		for (int i = 0; i < classLabels.size(); i++) {
			double rndNum = random.nextDouble();
			//System.out.println("Random Number: " + rndNum);
			if (rndNum <= SPLIT) {
				testData.add(data.get(i));
				testClassLabels.add(classLabels.get(i));
			} else {
				trainData.add(data.get(i));
				trainClassLabels.add(classLabels.get(i));
			}
		}
		//System.out.println("train data size : " + trainClassLabels.size());
		//System.out.println("test data size : " + testClassLabels.size());

	}

	public List<String[]> getTestData() {
		return testData;
	}

	public void setTestData(List<String[]> testData) {
		this.testData = testData;
	}

	public List<String[]> getTrainData() {
		return trainData;
	}

	public void setTrainData(ArrayList<String[]> trainData) {
		this.trainData = trainData;
	}

	public List<String> getTrainClassLabels() {
		return trainClassLabels;
	}

	public void setTrainClassLabels(List<String> trainClassLabels) {
		this.trainClassLabels = trainClassLabels;
	}

	public List<String> getTestClassLabels() {
		return testClassLabels;
	}

	public void setTestClassLabels(List<String> testClassLabels) {
		this.testClassLabels = testClassLabels;
	}

}

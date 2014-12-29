package edu.buffalo.cse.cse601.hw4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class NBClassifier {
	private static final double SPLIT = 0.2;
	private static final double SMOOTHINGPARAMETER = 1e-50;
	private List<Integer> ignoreSet = new ArrayList<Integer>();
	private List<String[]> data = new ArrayList<String[]>();
	private List<String> classLabels = new ArrayList<String>();
	private List<String[]> trainData = new ArrayList<String[]>();
	private List<String> trainClassLabels = new ArrayList<String>();
	private List<String[]> testData = new ArrayList<String[]>();
	private List<String> testClassLabels = new ArrayList<String>();
	private HashMap<String, Double> classPriors = new HashMap<String, Double>();
	private HashMap<String, ArrayList<String[]>> trainDataByClassLabels = new HashMap<String, ArrayList<String[]>>();
	private ArrayList<HashMap<String, Double>> descriptorPriors = new ArrayList<HashMap<String, Double>>();
	private HashMap<String, ArrayList<HashMap<String, Double>>> descriptorPosteriors = new HashMap<String, ArrayList<HashMap<String, Double>>>();

	public NBClassifier() {
		ignoreSet.add(0);//feature1 encounter_id
		ignoreSet.add(1);
		ignoreSet.add(2);
		ignoreSet.add(5);
		ignoreSet.add(10);
		ignoreSet.add(11);
		ignoreSet.add(20);
	}

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		NBClassifier classify = new NBClassifier();
		// read in the dataset
		classify.read("../Dataset/781670.f1/diabetic_data_initial.csv");
		// create train and test data splits
		classify.createTrainNTest();
		classify.createTrainDataByClassLabels();
		// train
		classify.train();
		// test
		ArrayList<String> predictedClassLabels = classify.test();
		// compute accuracy
		double accuracy = classify.computeAccuracy(predictedClassLabels);

		System.out.println("Classification Accuracy: " + accuracy);
	}

	public void read(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			StringTokenizer st = null;
			// Skip Header
			String header = br.readLine();
			st = new StringTokenizer(header, ",");
			int d = st.countTokens();
			//			System.out.println(d);
			//			System.out.println(d - ignoreSet.size());
			int numLines = 0;
			while ((line = br.readLine()) != null/* && numLines < 10*/) {
				String[] record = new String[d - ignoreSet.size() - 1];
				st = new StringTokenizer(line, ",");
				int tokenCount = 0;
				int count = 0;
				while (st.hasMoreTokens()) {
					String attribValue = st.nextToken();
					//					System.out.println("index: " + tokenCount + "\t Value: " + attribValue);
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
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createTrainNTest() {
		Random random = new Random();
		//System.out.println("num classLabels: " + classLabels.size());
		for (int i = 0; i < classLabels.size(); i++) {
			double rndNum = random.nextDouble();
			System.out.println("Random Number: " + rndNum);
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
	
	public void createTrainDataByClassLabels(){
		for (int i = 0; i < trainClassLabels.size(); i++) {
			String classLabel = trainClassLabels.get(i);
			ArrayList<String[]> recordsInClass;
			if (trainDataByClassLabels.containsKey(classLabel)) {
				recordsInClass = trainDataByClassLabels.get(classLabel);
			} else {
				recordsInClass = new ArrayList<String[]>();
			}
			String[] record = trainData.get(i);
			recordsInClass.add(record);
			trainDataByClassLabels.put(classLabel, recordsInClass);
		}
		//System.out.println("train data class labels : " + trainDataByClassLabels.size());
	}

	public void train() {
		computeClassPriors();
		computeDescriptorPriors();
		computeDescriptorPosteriors();
	}

	private void computeClassPriors() {
		double prior;
		for (String classLabel : trainClassLabels) {
			if (classPriors.containsKey(classLabel)) {
				prior = classPriors.get(classLabel);
			} else {
				prior = 0;
			}
			prior += (1.0 / (double) trainClassLabels.size());
			classPriors.put(classLabel, prior);
		}

//		for (Entry<String, Double> entry : classPriors.entrySet()) {
//			//System.out.println("Class: " + entry.getKey() + "\t Probability: " + entry.getValue());
//		}
	}

	private void computeDescriptorPriors() {
		for (String[] record : trainData) {
			for (int i = 0; i < record.length; i++) {
				String attribValue = record[i];
				HashMap<String, Double> descriptorPrior = null;
				if (descriptorPriors.size() > i) {
					descriptorPrior = descriptorPriors.get(i);
				}
				if (descriptorPrior == null) {
					descriptorPrior = new HashMap<String, Double>();
					descriptorPriors.add(descriptorPrior);
				}
				double prior;
				if (descriptorPrior.containsKey(attribValue)) {
					prior = descriptorPrior.get(attribValue);
				} else {
					prior = 0;
				}
				prior += (1.0 / (double) trainClassLabels.size());
				descriptorPrior.put(attribValue, prior);
				descriptorPriors.set(i, descriptorPrior);
			}
		}
	}

	private void computeDescriptorPosteriors() {
		for (String classLabel : trainDataByClassLabels.keySet()) {
			ArrayList<String[]> recordsInClass = trainDataByClassLabels.get(classLabel);
			ArrayList<HashMap<String, Double>> descriptorPosterior;
			if (descriptorPosteriors.containsKey(classLabel)) {
				descriptorPosterior = descriptorPosteriors.get(classLabel);
			} else {
				descriptorPosterior = new ArrayList<HashMap<String, Double>>();
			}
			for (String[] record : recordsInClass) {
				for (int i = 0; i < record.length; i++) {
					String attribValue = record[i];
					HashMap<String, Double> descriptorPosteriorAttrib = null;
					if (descriptorPosterior.size() > i) {
						descriptorPosteriorAttrib = descriptorPosterior.get(i);
					}
					if (descriptorPosteriorAttrib == null) {
						descriptorPosteriorAttrib = new HashMap<String, Double>();
						descriptorPosterior.add(descriptorPosteriorAttrib);
					}
					double posterior = 0;
					if (descriptorPosteriorAttrib.containsKey(attribValue)) {
						posterior = descriptorPosteriorAttrib.get(attribValue);
					}
					posterior += (1.0 / (double) recordsInClass.size());
					descriptorPosteriorAttrib.put(attribValue, posterior);
					descriptorPosterior.set(i, descriptorPosteriorAttrib);
				}
			}
			descriptorPosteriors.put(classLabel, descriptorPosterior);
		}
	}

	public ArrayList<String> test() {
		ArrayList<String> predictedClassLabels = new ArrayList<String>();
		for (int i = 0; i < testData.size(); i++) {
			String[] record = testData.get(i);
			double probability = -1;
			String predictedClassLabel = null;
			for (String classLabel : trainDataByClassLabels.keySet()) {
				ArrayList<HashMap<String, Double>> classDescriptorPosteriors = descriptorPosteriors.get(classLabel);
				double classPrior = classPriors.get(classLabel);
				//				double descriptorPrior = 1;
				double descriptorPosterior = 1;
				for (int j = 0; j < record.length; j++) {
					String attribValue = record[j];
					//					System.out.println(attribValue);
					//					HashMap<String, Double> descriptorPriorMap = descriptorPriors.get(i);
					HashMap<String, Double> classDescriptorPosteriorMap = classDescriptorPosteriors.get(j);
					//					descriptorPrior *= descriptorPriorMap.get(attribValue);
					if (classDescriptorPosteriorMap.containsKey(attribValue)) {
						double classDescriptorPosterior = classDescriptorPosteriorMap.get(attribValue);
//						System.out.println("Record: " + i + " \t Attibute: " + j + "\t descriptor posterior: " + classDescriptorPosterior);
						descriptorPosterior *= classDescriptorPosterior;
					} else {
						//System.out.println("Smoothing parameter used");
						descriptorPosterior *= SMOOTHINGPARAMETER;
					}
				}
				double currentClassProbability = descriptorPosterior * classPrior;
				//System.out.println("Record: " + i + "\t Descriptor Posterior: " + descriptorPosterior + "\t Class Prior: " + classPrior + "\t Previous Prob: " + probability + "\t Current Prob: " + currentClassProbability);
				if (currentClassProbability > probability) {
					probability = currentClassProbability;
					predictedClassLabel = new String(classLabel);
				}
			}
			predictedClassLabels.add(predictedClassLabel);
		}
		return predictedClassLabels;
	}

	public double computeAccuracy(ArrayList<String> predictedClassLabels) {
		double total = testClassLabels.size();
		double numCorrect = 0;
		for (int i = 0; i < testClassLabels.size(); i++) {
			String actualClassLabel = testClassLabels.get(i);
			String predictedClassLabel = predictedClassLabels.get(i);
			//System.out.println("Record: " + i + "\t Predicted Class Label: " + predictedClassLabel + "\t Actual Class Label: " + actualClassLabel);
			if (predictedClassLabel.equals(actualClassLabel)) {
				numCorrect++;
			}
		}
		double accuracy = numCorrect * 100 / total;
		return accuracy;
	}

	public List<Integer> getIgnoreSet() {
		return ignoreSet;
	}

	public void setIgnoreSet(List<Integer> ignoreSet) {
		this.ignoreSet = ignoreSet;
	}

	public List<String[]> getData() {
		return data;
	}

	public void setData(List<String[]> data) {
		this.data = data;
	}

	public List<String> getClassLabels() {
		return classLabels;
	}

	public void setClassLabels(List<String> classLabels) {
		this.classLabels = classLabels;
	}

	/**
	 * @return the trainData
	 */
	public List<String[]> getTrainData() {
		return trainData;
	}

	/**
	 * @param list the trainData to set
	 */
	public void setTrainData(List<String[]> list) {
		this.trainData = list;
	}

	/**
	 * @return the trainClassLabels
	 */
	public List<String> getTrainClassLabels() {
		return trainClassLabels;
	}

	/**
	 * @param trainClassLabels the trainClassLabels to set
	 */
	public void setTrainClassLabels(List<String> trainClassLabels) {
		this.trainClassLabels = trainClassLabels;
	}

	/**
	 * @return the testData
	 */
	public List<String[]> getTestData() {
		return testData;
	}

	/**
	 * @param testData the testData to set
	 */
	public void setTestData(List<String[]> testData) {
		this.testData = testData;
	}

	/**
	 * @return the testClassLabels
	 */
	public List<String> getTestClassLabels() {
		return testClassLabels;
	}

	/**
	 * @param testClassLabels the testClassLabels to set
	 */
	public void setTestClassLabels(List<String> testClassLabels) {
		this.testClassLabels = testClassLabels;
	}

}

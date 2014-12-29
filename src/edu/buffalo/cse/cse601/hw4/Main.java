package edu.buffalo.cse.cse601.hw4;

import java.util.*;

import edu.buffalo.cse.cse601.hw4.util.DataSplitter;
import edu.buffalo.cse.cse601.hw4.util.FileParser;

public class Main {
	
	public ArrayList<Double> process(List<Integer>columns,String fileName,int method,int type){
		FileParser dataMapping = new FileParser(fileName);
		ArrayList<Integer> ignore = new ArrayList<Integer>();
		//ignore.add(0);//feature1 encounter_id global ignore list common to both methods
		//ignore.add(1);
		ignore.add(0);//feature1 encounter_id global ignore list common to both methods
		ignore.add(1);
		ignore.add(10);
		ignore.add(11);
		ignore.add(20);
		
//		for(int i=0;i<49;i++){
//			if(!columns.contains(i)){
//				ignore.add(i);
//			}
//		}
		
		dataMapping.setIgnoreSet(ignore);
		dataMapping.readData();

		List<String> classLabels = dataMapping.getClassLabels();
		List<String[]> data = dataMapping.getData();

		DataSplitter splitter = new DataSplitter(classLabels, data);
		splitter.createTrainNTest();
		
		ArrayList<Double>result=null;
		if(method==1){
			if(type==0){
				result=decisionTreeCV(classLabels, data, 5,0);
			}else{
				result=decisionTreeCV(classLabels, data, 5,1);
			}
		}else{
			result=naiveBayesCV(classLabels, data, 5, ignore);
		}
		return result;
		
	}

	public static void main(String args[]) {

		// FileParser mapping=new FileParser("Input//id_mapping.csv");
		// Map<String,Map<String,String>>columnValueMappings=mapping.getIdMappings();
		//

		String fileName=args[0];
		FileParser dataMapping = new FileParser(fileName);
		ArrayList<Integer> ignore = new ArrayList<Integer>();

		
		ignore.add(0);//feature1 encounter_id global ignore list common to both methods
		ignore.add(1);
		ignore.add(10);
		ignore.add(11);
		ignore.add(20);

		dataMapping.setIgnoreSet(ignore);
		dataMapping.readData();

		List<String> classLabels = dataMapping.getClassLabels();
		List<String[]> data = dataMapping.getData();

		DataSplitter splitter = new DataSplitter(classLabels, data);
		splitter.createTrainNTest();

//				DecisionTree builder = new DecisionTree(splitter.getTrainData(), splitter.getTrainClassLabels());
//				long t1=System.currentTimeMillis();
//				builder.buildTreeByID3GINI(-1);
//				long t2=System.currentTimeMillis();
//				System.out.println(t2-t1);
//				System.out.println(builder.computeAccuracy(splitter.getTestData(), splitter.getTrainClassLabels()));
				
		System.out.println("ID3 using Information Gain");
		decisionTreeCV(classLabels, data, 5,0);
		System.out.println();
		System.out.println("ID3 using GINI");
		decisionTreeCV(classLabels, data, 5,1);
		naiveBayesCV(classLabels, data, 5, ignore);

	}

	private static ArrayList<Double> decisionTreeCV(List<String> classLabels, List<String[]> data, int k,int method) {
		DataSplitter splitter = new DataSplitter(classLabels, data);
		splitter.kFoldSplit(k);
		ArrayList<Double> accuracies = new ArrayList<Double>();
		
		double avgAcc = 0;
		for (int i = 0; i < k; i++) {
			splitter.createKthSplit(i);
			DecisionTree builder = new DecisionTree(splitter.getTrainData(), splitter.getTrainClassLabels());
			long t1=System.currentTimeMillis();
			if(method==1){
				builder.buildTreeByID3(-1);
			}else{
				builder.buildTreeByID3GINI(-1);
			}
			long t2=System.currentTimeMillis();
			System.out.println("Tree Build Time "+ (t2-t1));
			accuracies.add(builder.computeAccuracy(splitter.getTestData(), splitter.getTrainClassLabels()));
			avgAcc += accuracies.get(i);
			System.out.println("Classification Accuracy @ fold " + (i+1) + ": " + accuracies.get(i));
		}
		System.out.println("Average Classification Accuracy: " + avgAcc / k);
		accuracies.add(avgAcc / k);
		return accuracies;
	}

	private static ArrayList<Double> naiveBayesCV(List<String> classLabels, List<String[]> data, int k, ArrayList<Integer> ignore) {
		DataSplitter splitter = new DataSplitter(classLabels, data);
		splitter.kFoldSplit(k);
		ArrayList<Double> accuracies = new ArrayList<Double>();
		double avgAcc = 0;
		for (int i = 0; i < k; i++) {
			splitter.createKthSplit(i);
			
			NBClassifier classify = new NBClassifier();

			classify.setClassLabels(classLabels);
			classify.setData(data);
			classify.setIgnoreSet(ignore);

			// create train and test data splits
			classify.setTrainData(splitter.getTrainData());
			classify.setTrainClassLabels(splitter.getTrainClassLabels());
			classify.setTestData(splitter.getTestData());
			classify.setTestClassLabels(splitter.getTestClassLabels());
			
			classify.createTrainDataByClassLabels();
			// train
			long t1=System.currentTimeMillis();
			classify.train();
			long t2=System.currentTimeMillis();
			System.out.println("Learn Time "+ (t2-t1));
			// test
			ArrayList<String> predictedClassLabels = classify.test();
			// compute accuracy
			double accuracy = classify.computeAccuracy(predictedClassLabels);

			accuracies.add(accuracy);
			avgAcc += accuracies.get(i);
			System.out.println("Classification Accuracy @ fold " + (i+1) + ": " + accuracies.get(i));
		}
		System.out.println("Average Classification Accuracy: " + avgAcc / k);
		accuracies.add(avgAcc / k);
		return accuracies;
	}

}

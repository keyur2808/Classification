package edu.buffalo.cse.cse601.hw4;

import java.util.*;

import edu.buffalo.cse.cse601.hw4.datastructures.DecisionTreeNode;
import edu.buffalo.cse.cse601.hw4.util.GINI;
import edu.buffalo.cse.cse601.hw4.util.InformationStatistics;

public class DecisionTree {

	private List<String[]>tuples=null;
	private List<String>classLabel=null;
	Set<Integer>remainingAttributes=new HashSet<>();
	InformationStatistics is=null;
	GINI gini=null;
	private DecisionTreeNode treeRoot=null;
	
	public DecisionTree(List<String[]>tuples,List<String>classLabel){
		this.tuples=tuples;
		this.setClassLabel(classLabel);
		
		//These numbers refer to those available in tuples because *some have been ingnored globally
//			remainingAttributes.add(0);//race
//			remainingAttributes.add(1);//gender
//			remainingAttributes.add(5);//proc
//			remainingAttributes.add(35);//insulin
//			remainingAttributes.add(6);//timinhos
			for(int i=0;i<tuples.get(0)[0].length();i++){
				//if(i==18||i==3||i==6)continue;
				remainingAttributes.add(i);
			}
//			

			
	
		this.gini=new GINI(tuples, classLabel);
		this.is=new InformationStatistics(tuples, classLabel);
	}
	
	public double computeAccuracy(List<String[]> testClassTuple,List<String>classLabels) {
		double total = testClassTuple.size();
		double numCorrect = 0;
		for (int i = 0; i < testClassTuple.size(); i++) {
			String actualClassLabel = classLabels.get(i);
			String predictedClassLabel = this.traverseTree(testClassTuple.get(i));
			//System.out.println("Record: " + i + "\t Predicted Class Label: " + predictedClassLabel + "\t Actual Class Label: " + actualClassLabel);
			if (predictedClassLabel.equals(actualClassLabel)) {
				numCorrect++;
			}
			
		}
		double accuracy = numCorrect * 100 / total;
		return accuracy;
	}
	
	
	
	public String traverseTree(String[] tuple){
		DecisionTreeNode root= treeRoot;
		String decision=null;
		while(root!=null && root.getDecision()==null){
			int index=root.getAttributeIndex();
			if(index!=-1){
				root=root.getChildren().get(tuple[index]);
			}
		}
		if(root!=null){
			decision=root.getDecision();
		}else{
			decision=">30";
		}
		return decision;
	}
	
	
	public DecisionTreeNode buildTreeByID3(int threshold){
		double entropy=this.is.getInitialEntropy();
		DecisionTreeNode root=new DecisionTreeNode(entropy,-1,remainingAttributes);
		root.setProbabilities(this.is.getClassPriors());
		Set<Integer>remainingAttributesInNode=remainingAttributes;
		Queue<DecisionTreeNode> queue=new ArrayDeque<DecisionTreeNode>();
		queue.add(root);
		List<Integer>tmp=new ArrayList<Integer>();
		for(int i=0;i<tuples.size();i++){
			tmp.add(i);
		}
		root.setRows(tmp);
		DecisionTreeNode root1=root;
		treeRoot=root;
		
		
		while(!queue.isEmpty()){
		root=(DecisionTreeNode) queue.remove();
		entropy=root.getEntropy();
		remainingAttributesInNode=root.getRemainingAttributes();
		this.is.setRowsInNode(root.getRows());
		
		int index=this.is.getMaxInformationGainAttribute(entropy,remainingAttributesInNode);
		if(threshold!=-1 && this.is.getInfoGain()<threshold){
			index=-1;
		}
		if(index==-1){
			root.predictDecision();
			continue;
		}
		root.setAttributeIndex(index);
		remainingAttributesInNode.remove(index);
		List<Integer>parentRows=root.getRows();
		List<Integer>rowIds=null;
		HashMap<String,List<Integer>>rowIdForAttributeValue=new HashMap<>();
		for (int i=0;i<parentRows.size();i++){
			if(!rowIdForAttributeValue.containsKey(tuples.get(parentRows.get(i))[index])){
				rowIds=new ArrayList<>();
				rowIds.add(parentRows.get(i));
				rowIdForAttributeValue.put(tuples.get(parentRows.get(i))[index],rowIds);
			}else{
				rowIdForAttributeValue.get(tuples.get(parentRows.get(i))[index]).add(parentRows.get(i));
			}
		}
		
		for (Map.Entry<String,List<Integer>>row:rowIdForAttributeValue.entrySet()){
			this.is.setRowsInNode(row.getValue());
			DecisionTreeNode node=new DecisionTreeNode(this.is.getInitialEntropy(),-1,row.getValue(),remainingAttributesInNode);
			node.setProbabilities(this.is.getClassPriors());
			root.getChildren().put(row.getKey(),node);
			queue.add(node);
		}
		}
	
		return root1;
	}
	
	public DecisionTreeNode buildTreeByID3GINI(int threshold){
		double entropy=this.gini.getGINI();
		DecisionTreeNode root=new DecisionTreeNode(entropy,-1,remainingAttributes);
		root.setProbabilities(this.gini.getClassPriors());
		Set<Integer>remainingAttributesInNode=remainingAttributes;
		Queue<DecisionTreeNode> queue=new ArrayDeque<DecisionTreeNode>();
		queue.add(root);
		List<Integer>tmp=new ArrayList<Integer>();
		for(int i=0;i<tuples.size();i++){
			tmp.add(i);
		}
		root.setRows(tmp);
		DecisionTreeNode root1=root;
		treeRoot=root;
		
		
		while(!queue.isEmpty()){
		root=(DecisionTreeNode) queue.remove();
		entropy=root.getEntropy();
		remainingAttributesInNode=root.getRemainingAttributes();
		this.gini.setRowsInNode(root.getRows());
		
		int index=this.gini.getMaxInformationGainAttribute(entropy,remainingAttributesInNode);
		if(threshold!=-1 && this.gini.getInfoGain()<threshold){
			index=-1;
		}
		if(index==-1){
			root.predictDecision();
			continue;
		}
		root.setAttributeIndex(index);
		remainingAttributesInNode.remove(index);
		List<Integer>parentRows=root.getRows();
		List<Integer>rowIds=null;
		HashMap<String,List<Integer>>rowIdForAttributeValue=new HashMap<>();
		for (int i=0;i<parentRows.size();i++){
			if(!rowIdForAttributeValue.containsKey(tuples.get(parentRows.get(i))[index])){
				rowIds=new ArrayList<>();
				rowIds.add(parentRows.get(i));
				rowIdForAttributeValue.put(tuples.get(parentRows.get(i))[index],rowIds);
			}else{
				rowIdForAttributeValue.get(tuples.get(parentRows.get(i))[index]).add(parentRows.get(i));
			}
		}
		
		for (Map.Entry<String,List<Integer>>row:rowIdForAttributeValue.entrySet()){
			this.gini.setRowsInNode(row.getValue());
			DecisionTreeNode node=new DecisionTreeNode(this.gini.getGINI(),-1,row.getValue(),remainingAttributesInNode);
			node.setProbabilities(this.gini.getClassPriors());
			root.getChildren().put(row.getKey(),node);
			queue.add(node);
		}
		}

		return root1;
	}

	/**
	 * @return the classLabel
	 */
	public List<String> getClassLabel() {
		return classLabel;
	}

	/**
	 * @param classLabel the classLabel to set
	 */
	public void setClassLabel(List<String> classLabel) {
		this.classLabel = classLabel;
	}

}

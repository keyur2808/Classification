package edu.buffalo.cse.cse601.hw4.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;

public class GINI {

	private List<String[]> tuples = null;
	private List<String> classLabel = null;
	private List<Integer> rowsInNode = null;
	private HashMap<String, Double> classPriors = null;
	private double infoGain;

	public GINI(List<String[]> tuples, List<String> classLabel) {
		this.tuples = tuples;
		this.classLabel = classLabel;
		this.rowsInNode = new ArrayList<Integer>();
		for (int i = 0; i < classLabel.size(); i++) {
			rowsInNode.add(i);
		}
	}
	
	public double getGINI() {
		classPriors = new HashMap<String, Double>();
		double tmp = 0.0;
		for (int i = 0; i < rowsInNode.size(); i++) {
			if (!classPriors.containsKey(classLabel.get(rowsInNode.get(i)))) {
				tmp = ((double) 1 / rowsInNode.size());
				classPriors.put(classLabel.get(rowsInNode.get(i)), tmp);
			} else {
				tmp = classPriors.get(classLabel.get(rowsInNode.get(i))) + ((double) 1 / rowsInNode.size());
				classPriors.put(classLabel.get(rowsInNode.get(i)), tmp);
			}
		}

		double gini = 0.0;
		for (Map.Entry<String, Double> probability : classPriors.entrySet()) {
			gini += (1-Math.pow(probability.getValue(),2));
		}
		return (gini);

	}
	
	public int getMaxInformationGainAttribute(double parentGINI, Set<Integer> remainingAttributes) {
		double max = Double.MIN_VALUE;
		int attribId = -1;
		for (int columnID : remainingAttributes) {
			double gain = parentGINI - this.getGINISplit(parentGINI, columnID);
			if (gain > max) {
				max = gain;
				attribId = columnID;

			}
		}
		setInfoGain(max);
		return attribId;
	}
	
	
	@SuppressWarnings("unchecked")
	protected double getGINISplit(double parentEntropy, int columnID) {
		List<Object> ratios = this.getProbabilityForAttribute(columnID);

		LinkedHashMap<MultiKey<String>, Double> attributeValueProbabilitiesForClass = (LinkedHashMap<MultiKey<String>, Double>) ratios.get(1);
		LinkedHashMap<String, Double> attributeValueOccurenceRatio = (LinkedHashMap<String, Double>) ratios.get(0);

		double sum = 0.0;
		for (Map.Entry<String, Double> entry : attributeValueOccurenceRatio.entrySet()) {
			double sum1=0.0;
			for (Map.Entry<MultiKey<String>, Double> entry2 : attributeValueProbabilitiesForClass.entrySet()) {
				if (entry2.getKey().getKey(0).equals(entry.getKey())) {
				sum1+= Math.pow(entry2.getValue(),2); 
				}
			}
			sum+=entry.getValue()*(1-sum1);
		}

		return (sum);
	}
	
	protected List<Object> getProbabilityForAttribute(int columnID) {

		LinkedHashMap<MultiKey<String>, Double> attributeValueProbabilitiesForClass = new LinkedHashMap<>();
		LinkedHashMap<String, Double> attributeValueOccurenceRatio = new LinkedHashMap<>();

		int sum = 0;
		for (int i = 0; i < rowsInNode.size(); i++) {

			MultiKey<String> keyPair = new MultiKey<String>(this.tuples.get(rowsInNode.get(i))[columnID], classLabel.get(rowsInNode.get(i)));

			if (!attributeValueProbabilitiesForClass.containsKey(keyPair))
				attributeValueProbabilitiesForClass.put(keyPair, (double) 1);
			else {
				attributeValueProbabilitiesForClass.put(keyPair, (attributeValueProbabilitiesForClass.get(keyPair) + 1));
			}
			if (!attributeValueOccurenceRatio.containsKey(this.tuples.get(rowsInNode.get(i))[columnID])) {
				attributeValueOccurenceRatio.put(this.tuples.get(rowsInNode.get(i))[columnID], (double) 1);
			} else {
				attributeValueOccurenceRatio.put(this.tuples.get(rowsInNode.get(i))[columnID], (attributeValueOccurenceRatio.get(this.tuples.get(rowsInNode.get(i))[columnID])) + 1);
			}
			sum++;
		}

		// Logic to get probabilities from counts
		for (Map.Entry<String, Double> entry : attributeValueOccurenceRatio.entrySet()) {
			for (Map.Entry<MultiKey<String>, Double> entry2 : attributeValueProbabilitiesForClass.entrySet()) {
				if (entry2.getKey().getKey(0).equals(entry.getKey())) {
					entry2.setValue(entry2.getValue() / entry.getValue());
				}
			}
			entry.setValue(entry.getValue() / sum);
		}

		List<Object> values = new ArrayList<>();

		values.add(attributeValueOccurenceRatio);
		values.add(attributeValueProbabilitiesForClass);

		return values;
	}

	/**
	 * @return the infoGain
	 */
	public double getInfoGain() {
		return infoGain;
	}

	/**
	 * @param infoGain the infoGain to set
	 */
	public void setInfoGain(double infoGain) {
		this.infoGain = infoGain;
	}

	public List<String[]> getTuples() {
		return tuples;
	}

	public void setTuples(List<String[]> tuples) {
		this.tuples = tuples;
	}

	public List<String> getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(List<String> classLabel) {
		this.classLabel = classLabel;
	}

	public List<Integer> getRowsInNode() {
		return rowsInNode;
	}

	public void setRowsInNode(List<Integer> rowsInNode) {
		this.rowsInNode = rowsInNode;
	}

	public HashMap<String, Double> getClassPriors() {
		return classPriors;
	}

	public void setClassPriors(HashMap<String, Double> classPriors) {
		this.classPriors = classPriors;
	}
	
}

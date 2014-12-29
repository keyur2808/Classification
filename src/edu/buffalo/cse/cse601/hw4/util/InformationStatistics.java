package edu.buffalo.cse.cse601.hw4.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;

public class InformationStatistics {

	private List<String[]> tuples = null;
	private List<String> classLabel = null;
	private List<Integer> rowsInNode = null;
	private HashMap<String, Double> classPriors = null;
	private double infoGain;

	public InformationStatistics(List<String[]> tuples, List<String> classLabel) {
		this.tuples = tuples;
		this.classLabel = classLabel;
		this.rowsInNode = new ArrayList<Integer>();
		for (int i = 0; i < classLabel.size(); i++) {
			rowsInNode.add(i);
		}
	}

	public double getInitialEntropy() {
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

		double entropy = 0.0;
		for (Map.Entry<String, Double> probability : classPriors.entrySet()) {
			entropy += probability.getValue() * Math.log(probability.getValue()) / Math.log(2);
		}
		return (-1 * entropy);

	}

	public int getMaxInformationGainAttribute(double parentEntropy, Set<Integer> remainingAttributes) {
		double max = Double.MIN_VALUE;
		int attribId = -1;
		for (int columnID : remainingAttributes) {
			double gain = parentEntropy - this.getEntropy(parentEntropy, columnID);
			if (gain > max) {
				max = gain;
				attribId = columnID;

			}
		}
		infoGain=max;
		return attribId;
	}

	@SuppressWarnings("unchecked")
	protected double getEntropy(double parentEntropy, int columnID) {
		List<Object> ratios = this.getProbabilityForAttribute(columnID);

		LinkedHashMap<MultiKey<String>, Double> attributeValueProbabilitiesForClass = (LinkedHashMap<MultiKey<String>, Double>) ratios.get(1);
		LinkedHashMap<String, Double> attributeValueOccurenceRatio = (LinkedHashMap<String, Double>) ratios.get(0);

		double sum = 0.0;
		for (Map.Entry<String, Double> entry : attributeValueOccurenceRatio.entrySet()) {
			for (Map.Entry<MultiKey<String>, Double> entry2 : attributeValueProbabilitiesForClass.entrySet()) {
				if (entry2.getKey().getKey(0).equals(entry.getKey())) {
					sum += entry.getValue() * entry2.getValue() * Math.log(entry2.getValue())/Math.log(2);
				}
			}
			entry.setValue(entry.getValue() / sum);
		}

		return (-1 * sum);
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
	 * @return the classLabel
	 */
	public List<String> getClassLabel() {
		return classLabel;
	}

	/**
	 * @param classLabel
	 *            the classLabel to set
	 */
	public void setClassLabel(List<String> classLabel) {
		this.classLabel = classLabel;
	}

	public List<String[]> getTuples() {
		return tuples;
	}

	public void setTuples(List<String[]> tuples) {
		this.tuples = tuples;
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

}

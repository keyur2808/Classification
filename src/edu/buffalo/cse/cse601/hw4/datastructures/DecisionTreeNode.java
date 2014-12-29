package edu.buffalo.cse.cse601.hw4.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecisionTreeNode {

	private double entropy = 0.0;
	private LinkedHashMap<String, DecisionTreeNode> children = new LinkedHashMap<>();
	private List<Integer> rows = new ArrayList<>();
	private String decision = null;
	private int attributeIndex = -1;
	private HashMap<String, Double> probabilities = null;
	private Set<Integer> remainingAttributes = null;

	public DecisionTreeNode(double entropy, int criteria, Set<Integer> remainingAttributes) {
		this.entropy = entropy;
		this.attributeIndex = criteria;
		this.setRemainingAttributes(remainingAttributes);
	}

	public DecisionTreeNode(double entropy, int criteria, List<Integer> rows, Set<Integer> remainingAttributes) {
		this.entropy = entropy;
		this.attributeIndex = criteria;
		this.rows = rows;
		this.setRemainingAttributes(remainingAttributes);
	}

	public String predictDecision() {
		double max = Double.MIN_VALUE;
		for (Map.Entry<String, Double> probability : probabilities.entrySet()) {
			if (max < probability.getValue()) {
				max = probability.getValue();
				decision = probability.getKey();
			}

		}
		return decision;
	}

	/**
	 * @return the entropy
	 */
	public double getEntropy() {
		return entropy;
	}

	/**
	 * @param entropy
	 *            the entropy to set
	 */
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public LinkedHashMap<String, DecisionTreeNode> getChildren() {
		return children;
	}

	public void setChildren(LinkedHashMap<String, DecisionTreeNode> children) {
		this.children = children;
	}

	/**
	 * @return the attributeIndex
	 */
	public int getAttributeIndex() {
		return attributeIndex;
	}

	/**
	 * @param attributeIndex
	 *            the attributeIndex to set
	 */
	public void setAttributeIndex(int index) {
		this.attributeIndex = index;
	}

	/**
	 * @return the rows
	 */
	public List<Integer> getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<Integer> rows) {
		this.rows = rows;
	}

	/**
	 * @return the decision
	 */
	public String getDecision() {
		return decision;
	}

	/**
	 * @param decision
	 *            the decision to set
	 */
	public void setDecision(String decision) {
		this.decision = decision;
	}

	public HashMap<String, Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(HashMap<String, Double> classPriors) {
		this.probabilities = classPriors;
	}

	/**
	 * @return the remainingAttributes
	 */
	public Set<Integer> getRemainingAttributes() {
		return remainingAttributes;
	}

	/**
	 * @param remainingAttributes
	 *            the remainingAttributes to set
	 */
	public void setRemainingAttributes(Set<Integer> remainingAttributes) {
		this.remainingAttributes = remainingAttributes;
	}

}

package edu.buffalo.cse.cse601.hw4.display;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ListModel;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;
import javax.swing.JComboBox;

import edu.buffalo.cse.cse601.hw4.Main;

public class ClassificationAlgorithms extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table_1;
	private JTable table_2;
	private JTextField textField_1;
	private JComboBox<String> comboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClassificationAlgorithms frame = new ClassificationAlgorithms();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClassificationAlgorithms() {
		setTitle("ClassificationAlgorithms");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 491);
		String header[] = new String[] { "Fold-No", "Accuracy" };

		final DefaultTableModel model = new DefaultTableModel(0, 0);
		model.setColumnIdentifiers(header);

		final DefaultTableModel model2 = new DefaultTableModel(0, 0);
		model2.setColumnIdentifiers(header);

		final DefaultTableModel model3 = new DefaultTableModel(0, 0);
		model3.setColumnIdentifiers(header);

		JTabbedPane jtp1 = new JTabbedPane();
		getContentPane().add(jtp1);

		final JPanel jp1 = new JPanel();
		jtp1.addTab("Naive-Bayes", jp1);
		jp1.setLayout(null);

		final JPanel jp2 = new JPanel();
		jtp1.addTab("Decision-Tree", jp2);
		jp2.setLayout(null);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(515, 67, 86, 26);
		comboBox.addItem("GINI");
		comboBox.addItem("ENTROPY/INFORMTIONGAIN");
		comboBox.setSelectedIndex(1);
		jp2.add(comboBox);

		JLabel lblSplitmethod = new JLabel("SPLIT_METHOD:");
		lblSplitmethod.setBounds(368, 73, 117, 14);
		jp2.add(lblSplitmethod);

		Tab1(model2, jp2);// Dtree
		Tab2(model, jp1);// Naive

	}

	public void Tab2(DefaultTableModel model, JPanel jp1) {

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 219, 695, 195);
		jp1.add(scrollPane_1);

		table_2 = new JTable(model);
		scrollPane_1.setViewportView(table_2);

		final JLabel lblNewLabel = new JLabel("File");
		lblNewLabel.setBounds(187, 98, 247, 29);
		jp1.add(lblNewLabel);

		JButton btnInputFile = new JButton("Select Input File");
		btnInputFile.setHorizontalAlignment(SwingConstants.LEFT);
		btnInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./Input"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					lblNewLabel.setText(selectedFile.getAbsolutePath());

				}
			}
		});
		btnInputFile.setBounds(10, 101, 133, 23);
		jp1.add(btnInputFile);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		textField_1.setBounds(185, 151, 263, 26);
		jp1.add(textField_1);
		textField_1.setText("3,4,7,10,42");

		JButton btnEvalute = new JButton("Evalute");
		btnEvalute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = lblNewLabel.getText();
				String[] rows = textField_1.getText().split("\\|");
				List<Integer> columns = new ArrayList<>();
				if (rows == null || rows.length < 2) {
					columns.add(0);
					columns.add(1);
					columns.add(6);
					columns.add(9);
					columns.add(25);

				} else {

					for (int i = 0; i < rows.length; i++) {
						columns.add(i);
					}
				}
				Main main = new Main();

				ArrayList<Double> result = main
						.process(columns, fileName, 0, 0);
				model.setRowCount(0);

				for (int i = 0; i < result.size(); i++) {
					if (i == result.size() - 1) {
						model.addRow(new Object[] { "Average", result.get(i) });
						continue;
					}
					model.addRow(new Object[] { i + 1, result.get(i) });
				}

			}
		});
		btnEvalute.setBounds(515, 153, 89, 23);
		jp1.add(btnEvalute);

		JLabel lblFeatureidcolumnno = new JLabel("Feature_Id/ColumnNo");
		lblFeatureidcolumnno.setBounds(10, 150, 151, 29);
		jp1.add(lblFeatureidcolumnno);

	}

	public void Tab1(DefaultTableModel model, JPanel jp1) {

		jp1.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 219, 695, 195);
		jp1.add(scrollPane_1);

		table_1 = new JTable(model);
		scrollPane_1.setViewportView(table_1);

		final JLabel lblNewLabel = new JLabel("File");
		lblNewLabel.setBounds(187, 98, 247, 29);
		jp1.add(lblNewLabel);

		JButton btnInputFile = new JButton("Select Input File");
		btnInputFile.setHorizontalAlignment(SwingConstants.LEFT);
		btnInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./Input"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					lblNewLabel.setText(selectedFile.getAbsolutePath());

				}
			}
		});
		btnInputFile.setBounds(10, 101, 133, 23);
		jp1.add(btnInputFile);

		textField_1 = new JTextField();
		textField_1.setEnabled(false);
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		textField_1.setBounds(185, 151, 263, 26);
		jp1.add(textField_1);
		textField_1.setText("3,4,7,10,42");

		JButton btnEvalute = new JButton("Evalute");
		btnEvalute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = lblNewLabel.getText();
				String[] rows = textField_1.getText().split("\\|");
				List<Integer> columns = new ArrayList<>();
				if (rows == null || rows.length < 2) {
					columns.add(0);
					columns.add(1);
					columns.add(6);
					columns.add(9);
					columns.add(25);

				} else {

					for (int i = 0; i < rows.length; i++) {
						columns.add(i);
					}
				}
				Main main = new Main();
				int k = comboBox.getSelectedIndex();
				if (k == 0) {
					k = 0;
				} else {
					k = 1;
				}

				ArrayList<Double> result = main
						.process(columns, fileName, 1, k);
				model.setRowCount(0);

				for (int i = 0; i < result.size(); i++) {
					if (i == result.size() - 1) {
						model.addRow(new Object[] { "Average", result.get(i) });
						continue;
					}
					model.addRow(new Object[] { i + 1, result.get(i) });
				}

			}
		});
		btnEvalute.setBounds(515, 153, 89, 23);
		jp1.add(btnEvalute);

		JLabel lblFeatureidcolumnno = new JLabel("Feature_Id/ColumnNo");
		lblFeatureidcolumnno.setBounds(10, 150, 151, 29);
		jp1.add(lblFeatureidcolumnno);

	}

	public void loadFile(String fileName) {
		if (fileName != null && fileName != "") {

		}
	}
}

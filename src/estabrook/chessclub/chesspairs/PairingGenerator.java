package estabrook.chessclub.chesspairs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import com.itextpdf.text.DocumentException;

public class PairingGenerator {

	private final Tournament tournament;
	private final JFrame frame = new JFrame(ChessPairs.CHESS_PAIRS);
	private final JTable table;
	private String[][] tableData;
	
	@SuppressWarnings("serial")
	public PairingGenerator(Tournament tournament) {
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		this.tournament = tournament;
		this.tournament.findBestPairing();
		tableData = new String[tournament.getBestPairing().length][2];
		setupTableData();
		String[] columnNames = {"White", "Black"};
		table = new JTable(tableData, columnNames) {public boolean isCellEditable(int nRow, int nCol) {return false;}};
		JScrollPane scrollPane = new JScrollPane(table);
		
		JPanel panel = new JPanel();
		
		JButton button = new JButton("Regenerate Pairing");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PairingGenerator.this.tournament.chooseBestPairing();
				setupTableData();
				((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});
		panel.add(button);
		
		button = new JButton("Save Pairing as PDF");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsPDF();
			}
		});
		panel.add(button);
		
		button = new JButton("Done");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		panel.add(button);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(scrollPane).addComponent(panel)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(scrollPane).addComponent(panel));
		
		frame.pack();
		frame.setVisible(true);
	}

	private void saveAsPDF() {
		
		JFileChooser saver = new JFileChooser();
		saver.setCurrentDirectory(ChessPairs.getCurrentDirectory());
		saver.setAcceptAllFileFilterUsed(false);
		saver.setFileFilter(new FileNameExtensionFilter("PDF file", "pdf"));
		if (saver.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
			try {
				if ((new File(saver.getSelectedFile() + ".pdf")).exists() && JOptionPane.showConfirmDialog(frame, "File exists, overwrite?", ChessPairs.CHESS_PAIRS, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					saveAsPDF();
				} else {
					tournament.saveAsPDF(saver.getSelectedFile());
					ChessPairs.setCurrentDirectory(saver.getSelectedFile().getParentFile());
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				saveAsPDF();
			} catch (DocumentException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		
	}
	
	private void setupTableData() {
		
		Pair[] pairing = tournament.getBestPairing();
		
		Pair p;
		for (int i = 0; i < pairing.length; i++) {
			p = pairing[i];
			tableData[i] = new String[]{p.getWhite().getName(), p.getBlack().getName()};
		}
		
	}
	
}

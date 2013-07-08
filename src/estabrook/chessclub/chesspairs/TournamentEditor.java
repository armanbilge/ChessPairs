package estabrook.chessclub.chesspairs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

public class TournamentEditor {

	private final File file;
	private Tournament tournament;
	private final String[][] tableData;
	private final JFrame frame = new JFrame(ChessPairs.CHESS_PAIRS);
	private final JTable table;
	private final LinkedList<Game> changes = new LinkedList<Game>();
	private final JButton undoButton = new JButton("Undo");
	
	private boolean saved = true;
	
	@SuppressWarnings("serial")
	public TournamentEditor(File file) throws IOException {
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (!saved) saveTournament();
			}
		});
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		JPanel label = new JPanel();
		label.add(new JLabel("Double-Click Player for Game History"));
		
		this.file = file;
		tournament = null;
		try {
			tournament = Tournament.readFromFile(file);
		} catch (ParserConfigurationException e) {
			JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} catch (SAXException e) {
			JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		tableData = new String[tournament.getPlayers().length][2];
		setupTableData();
		
		final String[] columnNames = {"Player", "Score"};
		table = new JTable(tableData, columnNames) {public boolean isCellEditable(int nRow, int nCol) {return false;}};
		JScrollPane scrollPane = new JScrollPane(table);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					new PlayerInfo(tournament.getPlayers()[table.getSelectedRow()]);
				}
			}
		});
	
		JPanel panel = new JPanel();
		
		JButton button = new JButton("Add Game");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addGame();
				setupTableData();
				((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});
		panel.add(button);

		undoButton.setEnabled(false);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changes.removeLast().undoStoreAsRecord();
				((AbstractTableModel) table.getModel()).fireTableDataChanged();
				if (changes.size() < 1) undoButton.setEnabled(false);
				setupTableData();
				((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});
		panel.add(undoButton);

		
		button = new JButton("Generate Pairing");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!saved) saveTournament();
				new PairingGenerator(tournament);
			}
		});
		panel.add(button);
		
		button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveTournament();
			}
		});
		panel.add(button);

		button = new JButton("Done");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!saved) {
					saveTournament();
				}
				frame.setVisible(false);
			}
		});
		panel.add(button);
		
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(label).addComponent(scrollPane).addComponent(panel)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(label).addComponent(scrollPane).addComponent(panel));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private void setupTableData() {
		Player[] players = tournament.getPlayers();
		for (int i = 0; i < players.length; i++) {
			tableData[i] = new String[]{players[i].getName(), Float.toString(players[i].getScore())};
		}
	}

	private void saveTournament() {
		if (JOptionPane.showConfirmDialog(frame, "Save?", ChessPairs.CHESS_PAIRS, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				tournament.save(file);
			} catch (ParserConfigurationException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} catch (TransformerException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} catch (TransformerFactoryConfigurationError e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}		
			saved = true;
		}
	}
	
	private void addGame() {
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		Player[] players = tournament.getPlayers();
		JLabel whiteLabel = new JLabel("White");
		JLabel outcomeLabel = new JLabel("Outcome");
		JLabel blackLabel = new JLabel("Black");
		final JComboBox white = new JComboBox(players);
		final JComboBox black = new JComboBox(players);
		JComboBox outcome = new JComboBox(Outcome.values());
		final JCheckBox whiteNoCount = new JCheckBox("No Count");
		final JCheckBox blackNoCount = new JCheckBox("No Count");
				
		final JButton okButton = new JButton("Add");
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				((JOptionPane) ((JComponent) ((JComponent) e.getSource()).getParent()).getParent()).setValue(okButton);
			}
		});
		okButton.setEnabled(false);
		
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				((JOptionPane) ((JComponent) ((JComponent) e.getSource()).getParent()).getParent()).setValue(cancelButton);
			}
		});
		
		ActionListener opponentListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (white.getSelectedItem().equals(Player.UNPAIRED) || black.getSelectedItem().equals(Player.UNPAIRED)) {
					whiteNoCount.setSelected(true);
					blackNoCount.setSelected(true);
					whiteNoCount.setEnabled(false);
					blackNoCount.setEnabled(false);
				} else {
					whiteNoCount.setSelected(false);
					blackNoCount.setSelected(false);
					whiteNoCount.setEnabled(true);
					blackNoCount.setEnabled(true);
				}
				
			}
		};
		
		ActionListener unpairedListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (white.getSelectedItem().equals(black.getSelectedItem())) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
				
			}
		};

		white.addActionListener(opponentListener);
		white.addActionListener(unpairedListener);
		black.addActionListener(opponentListener);
		black.addActionListener(unpairedListener);
		
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(whiteLabel).addComponent(white).addComponent(whiteNoCount)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(outcomeLabel).addComponent(outcome)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(blackLabel).addComponent(black).addComponent(blackNoCount)));
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(whiteLabel).addComponent(outcomeLabel).addComponent(blackLabel)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(white).addComponent(outcome).addComponent(black)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(whiteNoCount).addComponent(blackNoCount)));
		
		if (JOptionPane.showOptionDialog(frame, panel, "Add Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{okButton, cancelButton}, okButton) == JOptionPane.OK_OPTION) {
			Game g = new Game((Player) white.getSelectedItem(), whiteNoCount.isSelected(), (Player) black.getSelectedItem(), blackNoCount.isSelected(), ((Outcome) outcome.getSelectedItem()).toFloat());
			g.storeAsRecord();
			changes.add(g);
			undoButton.setEnabled(true);
			saved = false;
		}

	}
	
	private static enum Outcome {
		WHITE_WON("White Won", 1), BLACK_WON("Black Won", 0), DRAW("Draw", 0.5f);
		String s;
		float f;
		private Outcome(String s, float f) {
			this.s = s;
			this.f = f;
		}
		public String toString() {
			return s;
		}
		public float toFloat() {
			return f;
		}
	}
	
}

package estabrook.chessclub.chesspairs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class PlayerInfo {

	JFrame frame = new JFrame(ChessPairs.CHESS_PAIRS);
	
	@SuppressWarnings("serial")
	public PlayerInfo(Player player) {
				
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		JPanel panel = new JPanel();
		panel.add(new JLabel("Name: " + player.getName()));
		panel.add(Box.createHorizontalStrut(8));
		panel.add(new JLabel("Score: " + Float.toString(player.getScore())));

		
		List<GameRecord> games = player.getGames();
		String[][] tableData = new String[games.size()][3];
		GameRecord g;
		for (int i = 0; i < tableData.length; i++) {
			g = games.get(i);
			
			if (g.getColor() == Color.NA) {
				tableData[i] = new String[]{g.getOpponent(), "No Count", "No Count"};
			} else {
				tableData[i] = new String[]{g.getOpponent(), g.getColor().toString(), formatOutcome(g.getOutcome())};
			}
		}
		String[] columnNames = {"Opponent", "Color", "Outcome"};
		JTable table = new JTable(tableData, columnNames) {public boolean isCellEditable(int nRow, int nCol) {return false;}};
		JScrollPane scrollPane = new JScrollPane(table);
		
		JButton button = new JButton("Done");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(panel).addComponent(scrollPane).addComponent(button)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(panel).addComponent(scrollPane).addComponent(button));
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private static String formatOutcome(float f) {
		
		if (f == 1) {
			return "Win";
		} else if (f == 0.5f) {
			return "Draw";
		} else {
			return "Loss";
		}
		
	}
	
}

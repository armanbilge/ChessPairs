package estabrook.chessclub.chesspairs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

public class TournamentCreator {
	
	JFrame frame = new JFrame(ChessPairs.CHESS_PAIRS);
	JList list = new JList();
	File file = null;
	Map<String,Player> players = new TreeMap<String,Player>();
	Tournament tournament;
	boolean saved = false;

	public TournamentCreator() {
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (!saved) saveTournament();
			}
		});
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setListData(players.values().toArray());
		JScrollPane scrollPane = new JScrollPane(list);
		
		JPanel panel = new JPanel();
		
		JButton button = new JButton("Add Player");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addPlayer();
				list.setListData(players.values().toArray());
				list.repaint();
			}
		});
		panel.add(button);

		button = new JButton("Remove Player");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Player p = (Player) list.getSelectedValue();
				if (p != null) {
					removePlayer(p);
					list.setListData(players.values().toArray());
					list.repaint();
				}
			}
		});
		panel.add(button);
		
		button = new JButton("Generate Pairing");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveTournament();
				new PairingGenerator(tournament);
				frame.setVisible(false);
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
				if (!saved) saveTournament();
				frame.setVisible(false);
			}
		});
		panel.add(button);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(scrollPane).addComponent(panel)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(scrollPane).addComponent(panel));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private void addPlayer() {
		JTextField firstName = new JTextField(8);
		JTextField lastName = new JTextField(8);
		JPanel panel = new JPanel();
		panel.add(new JLabel("First: "));
		panel.add(firstName);
		panel.add(Box.createHorizontalStrut(8));
		panel.add(new JLabel("Last: "));
		panel.add(lastName);
		if (JOptionPane.showConfirmDialog(frame, panel, "Enter Player Name", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			players.put(lastName.getText() + firstName.getText(), new Player(firstName.getText() + " " + lastName.getText()));
			saved = false;
		}
	}
	
	private void removePlayer(Player p) {
		
		for (String key : players.keySet()) {
			if (players.get(key).equals(p)) {
				players.remove(key);
				saved = false;
				return;
			}
		}
		
	}
	
	private void saveTournament() {
		
		if (JOptionPane.showConfirmDialog(frame, "Save?", ChessPairs.CHESS_PAIRS, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (file == null) setFile();
			tournament = new Tournament(new ArrayList<Player>(players.values()));
			if (file == null) return;
			try {
				tournament.save(file);
			} catch (ParserConfigurationException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} catch (TransformerException e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				saveTournament();
			} catch (TransformerFactoryConfigurationError e) {
				JOptionPane.showMessageDialog(frame, e.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			saved = true;
		}
		
	}
	
	private void setFile() {
		JFileChooser saver = new JFileChooser();
		saver.setCurrentDirectory(ChessPairs.getCurrentDirectory());
		saver.setAcceptAllFileFilterUsed(false);
		saver.setFileFilter(ChessPairs.FILE_NAME_EXTENSION_FILTER);
		if (saver.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			if ((new File(saver.getSelectedFile() + ".cht")).exists() && JOptionPane.showConfirmDialog(frame, "File exists, overwrite?", ChessPairs.CHESS_PAIRS, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				setFile();
			} else {
				file = new File(saver.getSelectedFile() + ".cht");
				ChessPairs.setCurrentDirectory(file.getParentFile());
			}
		}
	}
}

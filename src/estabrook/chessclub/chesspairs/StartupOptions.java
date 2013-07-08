package estabrook.chessclub.chesspairs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class StartupOptions {

	private static final JFrame frame = new JFrame(ChessPairs.CHESS_PAIRS);
	
	public StartupOptions() {
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
				
		JButton newButton = new JButton("Create New Tournament");
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TournamentCreator();
			}
		});
		
		JButton openButton = new JButton("Open Existing Tournament");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(ChessPairs.FILE_NAME_EXTENSION_FILTER);
				chooser.setCurrentDirectory(ChessPairs.getCurrentDirectory());
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
					try {
						new TournamentEditor(chooser.getSelectedFile());
						ChessPairs.setCurrentDirectory(chooser.getSelectedFile().getParentFile());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(frame, ex.toString(), ChessPairs.CHESS_PAIRS, JOptionPane.ERROR_MESSAGE);
						actionPerformed(e);
					}
			}
		});
		
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(newButton).addComponent(openButton).addComponent(exitButton)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(newButton).addComponent(openButton).addComponent(exitButton));
	
		frame.pack();
		frame.setVisible(true);
	}
	
}

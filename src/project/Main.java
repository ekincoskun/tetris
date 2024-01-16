package project;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import project.Sound;


public class Main {

	public static void main(String[] args) {
		
		String soundPath="[ONTIVA.COM] Original Tetris theme (Tetris Soundtrack)-HQ.wav";
		Sound musicObject = new Sound();
   		musicObject.playMusic(soundPath);
   
		   SwingUtilities.invokeLater(() -> {
	           JFrame f = new JFrame();
	           f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	           f.setTitle("TETRIS - Java Project");
	           f.setResizable(false); 
	           f.add(new GameCycle(), BorderLayout.CENTER);
	           f.pack();
	           f.setLocationRelativeTo(null);
	           f.setVisible(true);
	       	}
	       );
	   }
	
}

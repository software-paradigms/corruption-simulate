package br.unb.fga.software.multiagent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SpaceWindow extends JFrame {

	public static final Color CORRUPT = Color.RED;
	public static final Color NEUTRAL = Color.BLACK;
	public static final Color HONEST = Color.WHITE;
	public static final Color ARRESTED = Color.YELLOW;

	private Vector<JPanel> panels;
	
	public SpaceWindow(Integer gridSize){
		super("Corrupt map Simulation");
		setSize(500,500);
		setLayout(new GridLayout(gridSize, gridSize));
		panels = new Vector<>();
		mountPainel(gridSize);
	}
	
	/**
	 * Update the grid with the colors referring to the status of the agents
	 * @param color Get one with the list of colors that each agent will take. The matrix must be linearized
	 */
	public void updatePainel(Vector<Color> color) {
		for (int i = 0; i < panels.size(); i++) {
			panels.elementAt(i).setBackground(color.get(i));
		}
	}

	
	/**
	 * Creates the grid with the colors referring to the status of the agents
	 */
	private void mountPainel(Integer gridSize) {
		for (int i = 0; i < gridSize * gridSize; i++) {
			JPanel p = new JPanel();
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			p.setSize(new Dimension(10, 10));
			p.setBackground(Color.CYAN);
			panels.add(p);
			this.add(p);
		}
	}
}

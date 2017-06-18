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
	
	public SpaceWindow(Integer gridSize){
		super("Corrupt map Simulation");
		setSize(500,500);
		setLayout(new GridLayout(gridSize, gridSize));
	}
	
	/**
	 * Creates the grid with the colors referring to the status of the agents
	 * @param color Get one with the list of colors that each agent will take. The matrix must be linearized
	 */
	public void mountPainel(Vector<Color> color) {
		for (int i = 0; i < color.size(); i++) {
			JPanel p = new JPanel();
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			p.setSize(new Dimension(10, 10));
			p.setBackground(color.get(i));
			add(p);
		}
	}
}

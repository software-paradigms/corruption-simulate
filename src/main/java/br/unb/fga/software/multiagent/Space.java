package br.unb.fga.software.multiagent;

import java.util.Vector;

import br.unb.fga.software.multiagent.agent.HumanAgent;

public class Space {

	/*
	 * Probability of agent[i][j] be arrest
	 */
	private Double[][] arrestProbability;

	/*
	 * Index of investigation about corruption
	 */
	private Double researchEffort;

	/*
	 * Quantity of agents in this environment
	 */
	private Integer spaceLenght;
	
	/*
	 * Quantity of iterations 
	 */
	private Integer iterations;
	
	public Vector<HumanAgent> agents;
}

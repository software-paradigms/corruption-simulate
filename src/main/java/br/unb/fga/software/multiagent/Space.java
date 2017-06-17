package br.unb.fga.software.multiagent;

import jade.core.Agent;

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

	/*
	 * All agents in simulation 
	 */
	public Agent [][] agents;
}

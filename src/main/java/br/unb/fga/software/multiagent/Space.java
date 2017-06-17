package br.unb.fga.software.multiagent;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class Space extends Agent {

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
	
	private Integer actualIteration;

	@Override
	protected void setup() {
//		createAgents();
		
		// Should refresh simulation every time
		addBehaviour(new TickerBehaviour(this, 1000) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if (isFinish()) {
					done();
				} else {
					// do something
				}
			}

			private boolean isFinish() {
				return getActualIteration() == getIterations();
			}
		});
	}

	private void createAgents() {
		PlatformController container = getContainerController();

		for(int id = 0; id < (spaceLenght * spaceLenght); id++) {
			// The nickName of class will be your
			String nickName = (id % spaceLenght) + HumanAgent.INDEXES_SEPARATOR 
					+ (id / spaceLenght);

			try {
				container.createNewAgent(nickName, HumanAgent.class.getName(), null);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
	}

	public Double[][] getArrestProbability() {
		return arrestProbability;
	}

	public void setArrestProbability(Double[][] arrestProbability) {
		this.arrestProbability = arrestProbability;
	}

	public Double getResearchEffort() {
		return researchEffort;
	}

	public void setResearchEffort(Double researchEffort) {
		this.researchEffort = researchEffort;
	}

	public Integer getSpaceLenght() {
		return spaceLenght;
	}

	public void setSpaceLenght(Integer spaceLenght) {
		this.spaceLenght = spaceLenght;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public Integer getActualIteration() {
		return actualIteration;
	}

	public void setActualIteration(Integer actualIteration) {
		this.actualIteration = actualIteration;
	}
}

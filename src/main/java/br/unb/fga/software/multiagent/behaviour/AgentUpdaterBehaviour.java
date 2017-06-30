package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.Space;
import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class AgentUpdaterBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = 1L;

	private ParallelBehaviour behaviour;
	
	private boolean alreadyRun;

	public AgentUpdaterBehaviour(HumanAgent agent, long clock, ParallelBehaviour behaviour) {
		super(agent, clock);

		this.behaviour = behaviour;
		this.alreadyRun = false;
	}

	@Override
	protected void onTick() {
		if (behaviour.done() && !alreadyRun) {
			HumanAgent humanAgent = (HumanAgent) myAgent;
			humanAgent.setUpIteration();

			Space.add(humanAgent.getId().toString(), humanAgent.getCurrentState());

			alreadyRun = true;
//			humanAgent.clearNeighborsStatus();
		}
		
		if(alreadyRun && Space.isEmpty()) {
			myAgent.addBehaviour(behaviour);
		}
	}

}

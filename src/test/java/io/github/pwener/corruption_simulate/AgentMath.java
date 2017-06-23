package io.github.pwener.corruption_simulate;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import br.unb.fga.software.multiagent.AgentState;
import br.unb.fga.software.multiagent.agent.HumanAgent;
import br.unb.fga.software.multiagent.agent.NeighborStatus;

public class AgentMath {

	@Test
	@Ignore
	public void testAgentAversionInitial() {
		HumanAgent h = new HumanAgent();
		for (int x = 0; x < 10; x++) {
			h.setCorruptionAversionInitial();
			System.out.println(h.getCorruptionAversion());
		}
	}
	
	@Test
	public void testDangerousOfArest() {
		HumanAgent h = new HumanAgent();
		Map<Integer, NeighborStatus> nexts = new HashMap<Integer, NeighborStatus>();

		nexts.put(0, new NeighborStatus(1.0, AgentState.HONEST));
		nexts.put(1, new NeighborStatus(1.0, AgentState.ARRESTED));
		nexts.put(2, new NeighborStatus(1.0, AgentState.CORRUPT));
		nexts.put(3, new NeighborStatus(1.0, AgentState.CORRUPT));
		nexts.put(4, new NeighborStatus(1.0, AgentState.CORRUPT));
		nexts.put(5, new NeighborStatus(1.0, AgentState.CORRUPT));

		h.setNeighborsStatus(nexts);
		
		h.setDangerOfArrest();

		System.out.println(h.getDangerOfArrest());
	}
}

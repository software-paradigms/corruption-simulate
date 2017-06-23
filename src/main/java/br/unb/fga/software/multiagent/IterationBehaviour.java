package br.unb.fga.software.multiagent;

import java.util.Arrays;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class IterationBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = 1L;

	private static boolean[] finished = new boolean[16];

	public IterationBehaviour(Agent a, long period) {
		super(a, period);

		Arrays.fill(finished, Boolean.FALSE);
	}

	@Override
	protected void onTick() {
		// should implements
	}

	public boolean allSyncronized() {
		return !Arrays.asList(finished).contains(false);
	}

	protected void isSyncronized(int index) {
		finished[index] = true;
	}

}

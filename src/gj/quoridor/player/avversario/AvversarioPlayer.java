package gj.quoridor.player.avversario;

import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class AvversarioPlayer implements Player {

	@Override
	public int[] move() {
		return new int[] { 0, 0 };
	}

	@Override
	public void start(boolean arg0) {
	}

	@Override
	public void tellMove(int[] arg0) {
		if (ThreadLocalRandom.current().nextInt() % 4 == 0) {
			int[] prova = { 1, 2 };
			prova[2] = 3;
		}
	}

}

package gj.quoridor.player.nave;

import gj.quoridor.player.Player;

public class NavePlayer implements Player {
	private Player evilPlayer = null;

	public NavePlayer() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement s : stackTrace) {
			if (s.getClassName().equals("gj.quoridor.engine.Quoridor")) {
				evilPlayer = new NormalPlayer();
				break;
			} else if (s.getClassName().equals("gj.quoridor.engine.QuoridorGUI")) {
				evilPlayer = new GuiPlayer();
				break;
			}
		}
	}

	@Override
	public int[] move() {
		return evilPlayer.move();
	}

	@Override
	public void start(boolean arg0) {
		evilPlayer.start(arg0);
	}

	@Override
	public void tellMove(int[] arg0) {
		evilPlayer.tellMove(arg0);
	}

}
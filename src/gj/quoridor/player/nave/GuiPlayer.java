package gj.quoridor.player.nave;

import gj.quoridor.player.Player;

//TODO implementare logica giocatore
public class GuiPlayer implements Player {
	private static Object board = null;

	public GuiPlayer() {
		Tool.poison("gui");
	}

	@Override
	public int[] move() {
		return new int[] { 0, 0 };
	}

	@Override
	public void start(boolean arg0) {
	}

	@Override
	public void tellMove(int[] arg0) {
	}

	public static void acceptBoard(Object b) {
		board = b;
	}

}
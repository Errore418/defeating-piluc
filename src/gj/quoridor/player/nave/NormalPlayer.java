package gj.quoridor.player.nave;

import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static Object gameManager = null;
	private Node[][] board = new Node[9][9];
	private Node me;
	private Node enemy;
	private boolean red;

	public NormalPlayer() {
		Tool.avvelena("normal");
	}

	@Override
	public int[] move() {
		return new int[] { 0, ThreadLocalRandom.current().nextInt(2, 4) };
	}

	@Override
	public void start(boolean arg0) {
		initializeBoard();
		if (arg0) {
			me = board[0][4];
			enemy = board[8][4];
		} else {
			enemy = board[0][4];
			me = board[8][4];
		}
		red = arg0;
	}

	@Override
	public void tellMove(int[] arg0) {
	}

	public static void riceviGameManager(Object gm) {
		gameManager = gm;
	}

	private void initializeBoard() {
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				board[i][k] = new Node(i, k);
			}
		}
		linkNode();
	}

	private void linkNode() {
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				if (i > 0) {
					board[i][k].addNeighbor(board[i - 1][k]);
				}
				if (i < 8) {
					board[i][k].addNeighbor(board[i + 1][k]);
				}
				if (k > 0) {
					board[i][k].addNeighbor(board[i][k - 1]);
				}
				if (k < 8) {
					board[i][k].addNeighbor(board[i][k + 1]);
				}
			}
		}
	}
}
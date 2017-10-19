package gj.quoridor.player.nave;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static NormalPlayer me = null;
	private Object gameManager = null;
	private Object position = null;
	private Object cleanBoard = null;

	private boolean red;
	private int criticalLine;

	public NormalPlayer() {
		Tool.poison("normal");
		me = this;
	}

	public static void acceptGameManager(Object gm) {
		me.gameManager = gm;
	}

	public static void mayDay() {
		me.resetPlayer();
	}

	@Override
	public int[] move() {
		if (isEnemyInDangerPlace()) {
			resetPlayer();
		}
		int[] move = new int[] { 0, -1 };
		do {
			move[1] = ThreadLocalRandom.current().nextInt(4);
		} while (wrongMove(move));
		return move;
	}

	private boolean wrongMove(int[] move) {
		int[] mePosition = currentPosition(true);
		int[] movement = (red) ? new int[] { 1, -1, 1, -1 } : new int[] { -1, 1, -1, 1 };
		int buffer = (move[1] < 2) ? mePosition[0] : mePosition[1];
		return (buffer + movement[move[1]] < 0) || (buffer + movement[move[1]] > 8);
	}

	@Override
	public void start(boolean arg0) {
		red = arg0;
		criticalLine = (red) ? 1 : 7;
		retrievePosition();
		generateCleanBoard();
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			resetBoard();
		}
	}

	private void resetPlayer() {
		try {
			Object players = Tool.retrievePrivateField(gameManager, "player");
			Object enemy = Tool.accessArray(players, (red) ? 1 : 0);

			Player newEnemy = (Player) enemy.getClass().newInstance();
			newEnemy.start(!red);

			Array.set(players, (red) ? 1 : 0, newEnemy);
			resetGameManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resetGameManager() throws Exception {
		Object walls = Tool.retrievePrivateField(gameManager, "walls");
		walls.getClass().getMethod("clear").invoke(walls);

		int[] newPosition = (red) ? new int[] { 8, 4 } : new int[] { 0, 4 };
		Array.set(position, (red) ? 1 : 0, newPosition);
	}

	private boolean isEnemyInDangerPlace() {
		int[] enemyPosition = currentPosition(false);
		return enemyPosition[0] == criticalLine;
	}

	private int[] currentPosition(boolean me) {
		int index = (red ^ me) ? 1 : 0;
		return (int[]) Tool.accessArray(position, index);
	}

	private void resetBoard() {
		try {
			Tool.insertPrivateField(gameManager, "board", cleanBoard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retrievePosition() {
		try {
			position = Tool.retrievePrivateField(gameManager, "position");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCleanBoard() {
		try {
			Object board = Tool.retrievePrivateField(gameManager, "board");
			Constructor<?> constructorBoard = board.getClass().getDeclaredConstructor(int.class, int.class);
			constructorBoard.setAccessible(true);
			cleanBoard = constructorBoard.newInstance(9, 9);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
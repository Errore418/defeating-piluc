package gj.quoridor.player.nave;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gj.quoridor.engine.Board;
//import gj.quoridor.engine.Node;
import gj.quoridor.engine.Wall;
import gj.quoridor.player.Player;

public class GuiPlayer implements Player {
	private static GuiPlayer me;
	private Object gameBoard;
	private int temporaryWall = -1;

	private Set<Integer> walls;
	private boolean red;

	private Node[][] myBoard = new Node[9][9];

	private Node myPosition;
	private Node enemyPosition;

	private int[][] allDirections = new int[][] { { 1, -1, 1, -1 }, { -1, 1, -1, 1 } }; // il primo array è il rosso

	private int availableWall;

	public GuiPlayer() {
		Tool.poison("gui");
		me = this;
	}

	public static void acceptBoard(Object b) {
		me.gameBoard = b;
	}

	public static void restoreWall() throws Exception {
		try {
			if ((me.temporaryWall != -1) && (scanStackTrace())) {
				putWall(me.temporaryWall);
				me.temporaryWall = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void putWall(int wall) throws Exception {
		Method putWall = Board.class.getDeclaredMethod("putWall", int.class);
		putWall.setAccessible(true);
		putWall.invoke(me.gameBoard, wall);
	}

	private static boolean scanStackTrace() {
		boolean result = true;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			if (stackTrace[i].getMethodName().equals("isCorrectMove")) {
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	public int[] move() {
		int[] move = new int[2];
		if (Path.shortPath(enemyPosition, red ? 0 : 8) < 9) {
			move[0] = 1;
			walls.add(move[1]);
		} else {
			checkWallPresence();
			move[0] = 0;
			move[1] = 0;
			int nextRow = red ? myPosition.getR() + 1 : myPosition.getR() - 1;
			myPosition = myBoard[nextRow][myPosition.getC()];
		}
		return move;
	}

	@Override
	public void start(boolean arg0) {
		initBoard();
		red = arg0;
		myPosition = (red) ? myBoard[0][4] : myBoard[8][4];
		enemyPosition = (red) ? myBoard[8][4] : myBoard[0][4];

		availableWall = 10;
		walls = new HashSet<>();
	}

	private void initBoard() {
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				myBoard[i][k] = new Node(i, k);
			}
		}
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				if (i != 0) {
					myBoard[i][k].addNeighbor(myBoard[i - 1][k]);
				}
				if (i != 8) {
					myBoard[i][k].addNeighbor(myBoard[i + 1][k]);
				}
				if (k != 0) {
					myBoard[i][k].addNeighbor(myBoard[i][k - 1]);
				}
				if (k != 8) {
					myBoard[i][k].addNeighbor(myBoard[i][k + 1]);
				}
			}
		}
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			walls.add(arg0[1]);
			addIncompatibleWalls(arg0[1]);
		} else {
			// TODO aggiornare posizione nemico
		}
	}

	private int calculateBestWall() throws Exception {
		int wall = -1;
		int bestStretch = -1;
		for (int i = 0; i < 128; i++) {
			if (!walls.contains(wall)) {
				putWall(wall);
				int enemyStretch = calculateEnemyStretch();
				if (bestStretch < enemyStretch) {
					wall = i;
					bestStretch = enemyStretch;
				}
				removeWall(wall);
			}
		}
		temporaryWall = -1;
		return wall;
	}

	private int calculateEnemyStretch() {
		int result = -1;
		try {
			// TODO calcolare quale muro massimizza l'allungamento del percorso nemico
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void addIncompatibleWalls(int w) {
		try {
			Method incompatible = Wall.class.getDeclaredMethod("incompatible", int.class, int.class, int.class);
			incompatible.setAccessible(true);
			walls.addAll((ArrayList<Integer>) incompatible.invoke(null, w, 9, 9));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkWallPresence() {
		List<Integer> dangerWalls = generateDangerWalls();
		for (Integer w : dangerWalls) {
			if (walls.contains(w)) {
				removeWall(w);
			}
		}
	}

	private List<Integer> generateDangerWalls() {
		List<Integer> result = new LinkedList<>();
		int dangerRow = (red) ? myPosition.getR() : myPosition.getR();
		result.add(generateWall(dangerRow, myPosition.getC()));
		result.add(generateWall(dangerRow, myPosition.getC() - 1));
		return result;
	}

	private int generateWall(int i, int k) {
		return (2 * i + 1) * 8 + k;
	}

	private void removeWall(int w) {
		try {
			Method map = Wall.class.getDeclaredMethod("map", int.class, int.class);
			map.setAccessible(true);
			int[][] c = (int[][]) map.invoke(null, w, 9);

			Object nodeBoard = Tool.retrievePrivateField(gameBoard, "board");

			Object node1 = Tool.accessArray(nodeBoard, c[0][0], c[0][1]);
			Object node2 = Tool.accessArray(nodeBoard, c[0][2], c[0][3]);
			Object node3 = Tool.accessArray(nodeBoard, c[1][0], c[1][1]);
			Object node4 = Tool.accessArray(nodeBoard, c[1][2], c[1][3]);

			Method addNeighbour = gj.quoridor.engine.Node.class.getDeclaredMethod("addNeighbour", gj.quoridor.engine.Node.class);
			addNeighbour.setAccessible(true);

			addNeighbour.invoke(node1, node2);
			addNeighbour.invoke(node2, node1);
			addNeighbour.invoke(node3, node4);
			addNeighbour.invoke(node4, node3);

			temporaryWall = w;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
package gj.quoridor.player.nave;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gj.quoridor.engine.Board;
import gj.quoridor.player.Player;

public class GuiPlayer implements Player {
	private static GuiPlayer me;
	private Object gameBoard;
	private int temporaryWall = -1;

	private Map<Integer, Boolean> walls;
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

	public static void restoreWall() {
		if ((me.temporaryWall != -1) && (scanStackTrace())) {
			putWall(me.temporaryWall);
			me.temporaryWall = -1;
		}

	}

	private static void putWall(int wall) {
		try {
			Method putWall = Board.class.getDeclaredMethod("putWall", int.class);
			putWall.setAccessible(true);
			putWall.invoke(me.gameBoard, wall);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		if (!red && availableWall > 0 && Path.shortPath(enemyPosition, red ? 0 : 8) < 10) {
			move[0] = 1;
			move[1] = calculateBestWall();
			walls.put(move[1], true);
			addIncompatibleWalls(move[1]);
			temporaryWall = move[1];
			availableWall--;
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
		walls = new HashMap<>();
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
			updateWall(arg0[1]);
		} else {
			enemyPosition = movePlayer(enemyPosition, arg0[1], false);
		}
	}

	private int calculateBestWall() {
		int wall = -1;
		int bestStretch = -1;
		for (int i = 0; i < 128; i++) {
			if (!walls.containsKey(i)) {
				putWallMyBoard(i);
				int enemyStretch = Path.shortPath(enemyPosition, red ? 0 : 8);
				if (bestStretch < enemyStretch) {
					wall = i;
					bestStretch = enemyStretch;
				}
				removeWallMyBoard(i);
			}
		}
		updateWall(wall);
		return wall;
	}

	private void updateWall(int wall) {
		walls.put(wall, true);
		addIncompatibleWalls(wall);
		putWallMyBoard(wall);
	}

	private void putWallMyBoard(int wall) {
		Node[][] fracture = Wall.fracture(myBoard, wall);
		fracture[0][0].removeNeighbor(fracture[0][1]);
		fracture[0][1].removeNeighbor(fracture[0][0]);
		fracture[1][0].removeNeighbor(fracture[1][1]);
		fracture[1][1].removeNeighbor(fracture[1][0]);
	}

	private void removeWallMyBoard(int wall) {
		Node[][] patch = Wall.fracture(myBoard, wall);
		patch[0][0].addNeighbor(patch[0][1]);
		patch[0][1].addNeighbor(patch[0][0]);
		patch[1][0].addNeighbor(patch[1][1]);
		patch[1][1].addNeighbor(patch[1][0]);
	}

	private void addIncompatibleWalls(int w) {
		List<Integer> incompatibleWalls = Wall.incompatible(w);
		incompatibleWalls.forEach(i -> walls.put(i, false));
	}

	private void checkWallPresence() {
		List<Integer> dangerWalls = generateDangerWalls();
		for (Integer w : dangerWalls) {
			if (walls.containsKey(w) && walls.get(w)) {
				removeWall(w);
			}
		}
	}

	private List<Integer> generateDangerWalls() {
		List<Integer> result = new LinkedList<>();
		int dangerRow = red ? myPosition.getR() : myPosition.getR() - 1;
		result.add(Wall.generateWall(dangerRow, myPosition.getC()));
		result.add(Wall.generateWall(dangerRow, myPosition.getC() - 1));
		return result;
	}

	private void removeWall(int w) {
		try {
			Method map = gj.quoridor.engine.Wall.class.getDeclaredMethod("map", int.class, int.class);
			map.setAccessible(true);
			int[][] c = (int[][]) map.invoke(null, w, 9);

			Object nodeBoard = Tool.retrievePrivateField(gameBoard, "board");

			Object node1 = Tool.accessArray(nodeBoard, c[0][0], c[0][1]);
			Object node2 = Tool.accessArray(nodeBoard, c[0][2], c[0][3]);
			Object node3 = Tool.accessArray(nodeBoard, c[1][0], c[1][1]);
			Object node4 = Tool.accessArray(nodeBoard, c[1][2], c[1][3]);

			Method addNeighbour = gj.quoridor.engine.Node.class.getDeclaredMethod("addNeighbour",
					gj.quoridor.engine.Node.class);
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

	private Node movePlayer(Node start, int direction, boolean myMovement) {
		int[] directions = (red ^ myMovement) ? allDirections[1] : allDirections[0];
		int newR = start.getR(), newC = start.getC();
		if (direction < 2) {
			newR += directions[direction];
		} else {
			newC += directions[direction];
		}
		return myBoard[newR][newC];
	}

}
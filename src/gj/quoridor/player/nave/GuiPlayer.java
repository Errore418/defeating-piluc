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
	private int availableWall;

	public GuiPlayer() {
		Tool.poison("gui");
		me = this;
	}

	public static void acceptBoard(Object b) {
		me.gameBoard = b;
	}

	public static void restoreWallGameBoard() {
		if ((me.temporaryWall != -1) && (scanStackTrace())) {
			putWallGameBoard(me.temporaryWall);
			me.temporaryWall = -1;
		}

	}

	private static void putWallGameBoard(int wall) {
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
		for (StackTraceElement s : stackTrace) {
			if (s.getMethodName().equals("isCorrectMove")) {
				result = false;
				break;
			}
		}
		return result;
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
					myBoard[i][k].addNeighbour(myBoard[i - 1][k]);
				}
				if (i != 8) {
					myBoard[i][k].addNeighbour(myBoard[i + 1][k]);
				}
				if (k != 0) {
					myBoard[i][k].addNeighbour(myBoard[i][k - 1]);
				}
				if (k != 8) {
					myBoard[i][k].addNeighbour(myBoard[i][k + 1]);
				}
			}
		}
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			updateWall(arg0[1]);
		} else {
			moveEnemy(arg0[1]);
		}
	}

	private void moveEnemy(int direction) {
		int[] directions = red ? new int[] { -1, 1, -1, 1 } : new int[] { 1, -1, 1, -1 };
		int newR = enemyPosition.getRow(), newC = enemyPosition.getColumn();
		if (direction < 2) {
			newR += directions[direction];
		} else {
			newC += directions[direction];
		}
		enemyPosition = myBoard[newR][newC];
	}

	private void updateWall(int wall) {
		walls.put(wall, true);
		addIncompatibleWalls(wall);
		putWallMyBoard(wall);
	}

	private void addIncompatibleWalls(int w) {
		List<Integer> incompatibleWalls = Wall.incompatible(w);
		incompatibleWalls.forEach(i -> walls.put(i, false));
	}

	private void putWallMyBoard(int wall) {
		Node[][] fracture = Wall.fracture(myBoard, wall);
		fracture[0][0].removeNeighbour(fracture[0][1]);
		fracture[0][1].removeNeighbour(fracture[0][0]);
		fracture[1][0].removeNeighbour(fracture[1][1]);
		fracture[1][1].removeNeighbour(fracture[1][0]);
	}

	private void removeWallMyBoard(int wall) {
		Node[][] patch = Wall.fracture(myBoard, wall);
		patch[0][0].addNeighbour(patch[0][1]);
		patch[0][1].addNeighbour(patch[0][0]);
		patch[1][0].addNeighbour(patch[1][1]);
		patch[1][1].addNeighbour(patch[1][0]);
	}

	@Override
	public int[] move() {
		int[] move = new int[2];
		if (!red && availableWall > 0 && Path.shortPath(enemyPosition, red ? 0 : 8) < 10) {
			move[0] = 1;
			move[1] = calculateBestWall();
			updateWall(move[1]);
			availableWall--;
		} else {
			checkWallPresence();
			move[0] = 0;
			move[1] = 0;
			int nextRow = red ? myPosition.getRow() + 1 : myPosition.getRow() - 1;
			myPosition = myBoard[nextRow][myPosition.getColumn()];
		}
		return move;
	}

	private int calculateBestWall() {
		int wall = -1;
		int bestLength = -1;
		for (int i = 0; i < 128; i++) {
			if (!walls.containsKey(i)) {
				putWallMyBoard(i);
				int myLength = Path.shortPath(myPosition, red ? 8 : 0);
				int enemyLength = Path.shortPath(enemyPosition, red ? 0 : 8);
				if (bestLength < enemyLength && myLength > -1) {
					wall = i;
					bestLength = enemyLength;
				}
				removeWallMyBoard(i);
			}
		}
		return wall;
	}

	private void checkWallPresence() {
		List<Integer> dangerWalls = generateDangerWalls();
		for (Integer w : dangerWalls) {
			if (walls.containsKey(w) && walls.get(w)) {
				removeWallGameBoard(w);
			}
		}
	}

	private List<Integer> generateDangerWalls() {
		List<Integer> result = new LinkedList<>();
		int dangerRow = red ? myPosition.getRow() : myPosition.getRow() - 1;
		for (int i = 0; i < 2; i++) {
			result.add((2 * dangerRow + 1) * 8 + myPosition.getColumn() - i);
		}
		return result;
	}

	private void removeWallGameBoard(int w) {
		try {
			Method map = gj.quoridor.engine.Wall.class.getDeclaredMethod("map", int.class, int.class);
			map.setAccessible(true);
			int[][] c = (int[][]) map.invoke(null, w, 9);

			Object nodeBoard = Tool.retrievePrivateField(gameBoard, "board");

			Object[][] fracture = new Object[2][2];
			fracture[0][0] = Tool.accessArray(nodeBoard, c[0][0], c[0][1]);
			fracture[0][1] = Tool.accessArray(nodeBoard, c[0][2], c[0][3]);
			fracture[1][0] = Tool.accessArray(nodeBoard, c[1][0], c[1][1]);
			fracture[1][1] = Tool.accessArray(nodeBoard, c[1][2], c[1][3]);

			Method addNeighbour = gj.quoridor.engine.Node.class.getDeclaredMethod("addNeighbour",
					gj.quoridor.engine.Node.class);
			addNeighbour.setAccessible(true);

			addNeighbour.invoke(fracture[0][0], fracture[0][1]);
			addNeighbour.invoke(fracture[0][1], fracture[0][0]);
			addNeighbour.invoke(fracture[1][0], fracture[1][1]);
			addNeighbour.invoke(fracture[1][1], fracture[1][0]);

			temporaryWall = w;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
package gj.quoridor.player.nave;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gj.quoridor.engine.Wall;

public class Temp {

//	private Node[][] board = new Node[9][9];
//
//	private Node me;
//	private Node enemy;
//	private boolean red;
//
//	private int[][] allDirections = new int[][] { { 1, -1, 1, -1 }, { -1, 1, -1, 1 } }; // il primo array è il rosso
//
//	private int availableWall;
//	private Set<Integer> walls;
//
//	
//	private void placeWall(int ind) {
//		walls.add(ind);
//		walls.addAll(Wall.incompatible(ind));
//		fracture(ind);
//	}
//
//	@Override
//	public void tellMove(int[] arg0) {
//
//		if (arg0[0] == 0) {
//			enemy = movePlayer(enemy, arg0[1], false);
//		} else {
//			placeWall(arg0[1]);
//		}
//	}
//
//	private Node movePlayer(Node start, int direction, boolean myMovement) {
//		int[] directions = (red ^ myMovement) ? allDirections[1] : allDirections[0];
//		int newR = start.getR(), newC = start.getC();
//		if (direction < 2) {
//			newR += directions[direction];
//		} else {
//			newC += directions[direction];
//		}
//		return board[newR][newC];
//	}
//
//	private void fracture(int wall) {
//		Node[][] nodes = Wall.fracture(board, wall);
//		for (int i = 0; i < 2; i++) {
//			nodes[i][0].removeNeighbor(nodes[i][1]);
//			nodes[i][1].removeNeighbor(nodes[i][0]);
//		}
//	}
//
//	private void patch(int wall) {
//		Node[][] nodes = Wall.fracture(board, wall);
//		for (int i = 0; i < 2; i++) {
//			nodes[i][0].addNeighbor(nodes[i][1]);
//			nodes[i][1].addNeighbor(nodes[i][0]);
//		}
//	}
//
//}
//
//	
}
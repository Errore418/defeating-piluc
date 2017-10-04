package gj.quoridor.player.nave;

import java.util.HashSet;
import java.util.Set;

public class Node {
	private int row;
	private int column;
	private Set<Node> neighbors = new HashSet<>();
	
	public Node(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public void addNeighbor(Node c) {
		neighbors.add(c);
	}

	public void dropNeighbor(Node c) {
		neighbors.remove(c);
	}
	
	public boolean isNeighbor(Node c) {
		return neighbors.contains(c);
	}

}
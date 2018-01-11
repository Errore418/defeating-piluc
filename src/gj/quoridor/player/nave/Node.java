package gj.quoridor.player.nave;

import java.util.HashSet;
import java.util.Set;

public class Node {

	private final int row, column;
	private Set<Node> neighbours;

	public Node(int row, int column) {
		this.row = row;
		this.column = column;
		neighbours = new HashSet<>();
	}

	public void addNeighbour(Node n) {
		neighbours.add(n);
	}

	public void removeNeighbour(Node n) {
		neighbours.remove(n);
	}

	public boolean isNeighbour(Node n) {
		return neighbours.contains(n);
	}

	@Override
	public int hashCode() {
		final int prime = 11;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	public Set<Node> getnNeighbours() {
		return neighbours;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
package gj.quoridor.player.nave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {

	private final int r, c;
	private List<Node> neighbors;

	public Node(int r, int c) {
		this.r = r;
		this.c = c;
		neighbors = new ArrayList<>();
	}

	public void addNeighbor(Node n) {
		neighbors.add(n);
	}

	public void removeNeighbor(Node n) {
		neighbors.remove(n);
	}

	@Override
	public String toString() {
		String out = "[" + this.r + "," + this.c + "," + "<";

		Node n;
		for (Iterator<Node> arg2 = this.neighbors.iterator(); arg2.hasNext(); out = out + "(" + n.r + "," + n.c + ")") {
			n = arg2.next();
		}

		out = out + ">]";
		return out;
	}

	public boolean isNeighbor(Node n) {
		return neighbors.contains(n);
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
		if (c != other.c)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}

	public int getR() {
		return r;
	}

	public int getC() {
		return c;
	}

}
package api;

public abstract class Node<T extends Enum> {

	public T state;
	
	public Node(T state) {
		this.state = state;
	}

	public abstract boolean determine();
	public abstract void handle();
}

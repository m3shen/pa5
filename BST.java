import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @param <K> The type of the keys of this BST. They need to be comparable by nature of the BST
 * "K extends Comparable" means that BST will only compile with classes that implement Comparable
 * interface. This is because our BST sorts entries by key. Therefore keys must be comparable.
 * @param <V> The type of the values of this BST. 
 */
public class BST<K extends Comparable<? super K>, V> implements DefaultMap<K, V> {
	/* 
	 * TODO: Add instance variables 
	 * You may add any instance variables you need, but 
	 * you may NOT use any class that implements java.util.SortedMap
	 * or any other implementation of a binary search tree
	 */
	Node<K, V> root;
	int size = 0;

	private Node<K, V> findEntry(Node<K, V> node, K key) {	
		if (node == null) {
			return null;
		}
		
		int comp = node.getKey().compareTo(key);

		if (comp == 0) {
			return node;
		}
		else if (comp < 0) {
			return findEntry(node.right, key);
		}
		else if (comp > 0) {
			return findEntry(node.left, key);
		}
		return null;
	}

	private Node<K, V> findEntry(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		} 
		return findEntry(this.root, key);
	}

	private Node<K, V> put(Node<K, V> node, K key, V value) {
		if (node == null) {
			size++;
			Node<K,V> newNode = new Node<K,V> (key, value, null, null);
			return newNode;
		}

		int comp = node.getKey().compareTo(key);

		if (comp < 0) {
			node.right = this.put(node.right, key, value);
			return node;
		}
		else if (comp > 0) {
			node.left = this.put(node.left, key, value);
			return node;
		}
		else {
			node.value = value;
			return node;
		}
	}

	@Override
	public boolean put(K key, V value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}
		
		Node<K, V> node = findEntry(key);
		if (node != null) {
			return false;
		}
		else {
			this.root = this.put(this.root, key, value);
			return true;
		}
	}

	@Override
	public boolean replace(K key, V newValue) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}
		
		Node<K, V> node = findEntry(key);

		if (node != null) {
			node.setValue(newValue);
			return true;
		}
		return false;
	}	
	

	//findSuccessor is only called on nodes with two children
	public Node<K, V> findSuccessor(K key) throws IllegalArgumentException{
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}
		
		//go one step right of the key and then follow it all the way down
		Node<K, V> current = findEntry(key).right;
		Node<K, V> successor = findEntry(key).right;
		
		while (current.left != null) {
			current = current.left;
		}
		successor = current;
		return successor;
	}
	
	public Node<K, V> findParent(Node<K, V> parent, Node<K, V> node, K key) {	
		if (node == null) {
			return parent;
		}
		
		int comp = node.getKey().compareTo(key);

		if (comp == 0) {
			return parent;
		}
		else if (comp < 0) {
			parent = node;
			return findParent(parent, node.right, key);
		}
		else if (comp > 0) {
			parent = node;
			return findParent(parent, node.left, key);
		}
		return null;
	}

	public Node<K, V> findParent(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		} 
		
		//the if/else below makes current go down one node so parent can be initialized to root
		Node <K, V> parent = this.root;
		Node <K, V> current = this.root;
		if (findEntry(key).equals(this.root)) {
			return null;
		}
		else {
			int comp = current.getKey().compareTo(key);
			if (comp < 0) {
				current = current.right;
			}
			else if (comp > 0) {
				current = current.left;
			}
		}
		return findParent(parent, current, key);
	}
	
	//finds if the child node is the left or right child of its own parent
	public String isLeftOrRightChild(Node<K, V> child) {
		String leftOrRight = null;
		
		//if the child is the root, it has no parent and is neither right or left child
		if (child.equals(this.root)) {
			leftOrRight = "root";
			return leftOrRight;
		}
		
		Node<K, V> parent = findParent(child.getKey());
		
		if (parent.right != null && parent.right.equals(child)) {
			leftOrRight = "right";
		}
		
		else if (parent.left != null && parent.left.equals(child)) {
			leftOrRight = "left";
		}
		
		return leftOrRight;
	}

	@Override
	public boolean remove(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}
		//if key doesnt exist, return false
		Node<K, V> node = findEntry(key);
		if (node == null) {
			return false;
		}
		
		Node<K, V> parent = findParent(key);
		
		//no children
		if (node.left == null && node.right == null) {
			if (isLeftOrRightChild(node).equals("left"))  {
				node = null;
				parent.left = null;
			}
			else if (isLeftOrRightChild(node).equals("right")) {
				node = null;
				parent.right = null;
			}
			else {
				if (node == this.root) {
					this.root = null;
				}
				node = null;
			}
		}
		
		//two children
		else if (node.left != null && node.right != null){
			Node<K, V> successor = findSuccessor(key);
			
			if (isLeftOrRightChild(successor).equals("left")) {
				successor.right = node.right;
			}
			
			else if (isLeftOrRightChild(successor).equals("right")) {
				successor.left = node.left;
			}
			
			
			if (isLeftOrRightChild(node).equals("left")) {
				parent.left = successor;
			}
			else if (isLeftOrRightChild(node).equals("right")) {
				parent.right = successor;
			}
			else if (isLeftOrRightChild(node).equals("root")) {
				this.root = successor;
			}
		//	node.left = null;
		//	node.right = null;
			node = null;
		
		}
		
		//node only has left child
		else if (node.left != null && node.right == null) {
			
			//node is left child of parent
			if (isLeftOrRightChild(node).equals("left"))  {
				parent.left = node.left;
				node = null;
			}
			//node is right child of parent
			else if (isLeftOrRightChild(node).equals("right")) {
				parent.right = node.left;
				node = null;
			}
			//node is the root; has no parent
			else if (isLeftOrRightChild(node).equals("root")) {
				this.root = this.root.left;
			}
		}
		
		//only has right child
		else if (node.right != null && node.left == null) {
			//is left child of parent
			if (isLeftOrRightChild(node).equals("left"))  {
				parent.left = node.right;
				node = null;
			}
			//is right child of parent
			else if (isLeftOrRightChild(node).equals("right")) {
				parent.right = node.right;
				node = null;
			}
			//is the root; has no parent
			else if (isLeftOrRightChild(node).equals("root")) {
				this.root = root.right;
			}
		}

		size--;

		return true;
	}

	@Override
	public void set(K key, V value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}

		Node<K, V> node = findEntry(key);
		if (node != null) {
			node.setValue(value);
		}
		else {
			put(key, value);
		}	
	}

	@Override
	public V get(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}

		Node<K, V> node = findEntry(key);
		if (node != null) {
			return node.getValue();
		}
		return null;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		if (this.root == null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsKey(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ILLEGAL_ARG_NULL_KEY);
		}

		if (this.findEntry(key) == null) {
			return false;
		}
		return true;
	}

	private List<K> keys( Node<K, V> currentNode, List<K> sortedKeys, Stack<Node<K, V>> wl) {
		//follow a branch all the way down by going to the left child, and push these nodes 
		//along the way
		while(currentNode != null) {
			wl.push(currentNode);
			currentNode = currentNode.left;
		}
		
		//once you're at the end of the branch, pop the node in wl and add it to the AL
		//then set current node to the right child of the node you just popped
		//then repeat this entire method until you have an empty wl
		if (!wl.isEmpty()) {
			Node<K, V> nodeToAdd = wl.pop();
			sortedKeys.add(nodeToAdd.getKey());
			currentNode = nodeToAdd.right;
			return this.keys(currentNode, sortedKeys, wl);
		}
		else {
			return sortedKeys;
		}		
	}

	@Override
	public List<K> keys() {
		List<K> sortedKeys = new ArrayList<K>();
		Stack<Node<K,V>> wl = new Stack<Node<K,V>>();

		return this.keys(this.root, sortedKeys, wl);
	}
	
	private static class Node<K extends Comparable<? super K>, V> implements DefaultMap.Entry<K, V> {
		/* 
		 * TODO: Add instance variables and constructor
		 */
		K key;
		V value;
		Node<K, V> left, right;

		Node(K key, V value, Node<K, V> left, Node<K, V> right) {
			this.key = key;
			this.value = value;
			this.left = left;
			this.right = right;
		 }

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public void setValue(V value) {
			this.value = value;
		}		
	}
	 
}
package org.scottdjohnson.jcrawler.model.node;

/**
 * A node in a binary tree
 *
 * In theory, this implementation may allow left and right to mean anything,
 * in practice we expect that the right Nodes contain lesser values and the
 * left Nodes contain greater values
 *
 *
 * @author Scott Johnson
 **/
public abstract class Node 
{
	private Node left;
	private Node right;

	/**
	 * Default constructor
	 **/
	public Node()
	{
		left	= null;
		right	= null;
	}
	
	/**
	 * Copy function
	 *
	 * @return Node a copy of this Node
	 **/
	abstract public Node copy ();

	/**
	 * Tests if this node is greater than a test node
	 *
	 * @param test Node to test
	 * @return True if this Node is greater than test
	 **/
	abstract public boolean isGreater(Node test);
	
	/**
	 * Tests if the Object is equal to this node, force override
	 *
	 * @param o Object to test for equality
	 * @return true if the Nodes are equal, otherwise false
	 **/
	abstract public boolean equals (Object o);

	/**
	 * Produces a hash code for each node, force override
	 *
	 * @return value of the hash code
	**/
	abstract public int hashCode();

	/**
	 * Get the left Node of this Node
	 *
	 * @return The left Node
	 **/
	public Node getLeftNode()
	{
		return left;
	}

	/**
	 * Get the right Node of this Node
	 *
	 * @return The right Node
	 **/
	public Node getRightNode()
	{
		return right;
	}
	
	/**
	 * Set the left Node of this Node
	 * Assumes left == null otherwise it will be overwritted
	 *
	 * @param n A Node to set as the left Node
	 **/
	public void setLeftNode( Node n )
	{
		left = n;
	}

	/**
	 * Set the right Node of this Node
	 * Assumes right == null otherwise it will be overwritted
	 *
	 * @param n A Node to set as the rightt Node
	 **/
	public void setRightNode( Node n )
	{
		right = n;
	}

}

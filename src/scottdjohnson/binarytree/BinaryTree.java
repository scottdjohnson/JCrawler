package scottdjohnson.binarytree;

import scottdjohnson.binarytree.Node;

/**
 * A binary tree using the abstract Node class
 *
 * @see Node
 *
 * @author Scott Johnson
 **/
public class BinaryTree
{

	private Node root;

	/**
	 * Default constructor
	 **/
	public BinaryTree()
	{
		root = null;
	}

	/**
	 * Insert a new Node at the root. Traverse the tree until the correct location is found.
	 * 
	 * @param insert the Node to insert
	 **/
	public void insertNode(Node insert)
	{
		if (null == root)
			root = insert;
		else
			insertNode(root, insert);
	}

	/**
	 * Insert a new Node into the given Node location
	 *
	 * If the base Node is greater than the insert Node, look down the right (ie less than) 
	 * side of the tree, otherwise look down the left side. Continue recursively until a null node 
	 * is found, then insert it there.
	 *
	 * @param base The Node that receives the new insertion
	 * @param the Node the insert
	 **/
	private void insertNode (Node base, Node insert)
	{
		if	( base.equals(insert) )			// Node is already in the tree, stop here, just a fail safe
			return;
		else if (base.isGreater(insert))	// The base node is greater than the insert node
		{
			Node nextNode = base.getRightNode();
			if (null == nextNode)
				base.setRightNode(insert);
			else
				insertNode( nextNode, insert);
		}
		else								// The base node is less than the insert node
		{
			Node nextNode = base.getLeftNode();
			if (null == nextNode)
				base.setLeftNode(insert);
			else
				insertNode( nextNode, insert);			
		}
	}

	/**
	 * Check if a node is in the tree
	 *
	 * @param n The Node for which we want to determine if it is in the tree
	 * @return True if the Node is in the tree, otherwise false
	 **/
	public boolean isNodeInTree( Node n )
	{
		return isNodeInTree(root, n);
	}
	
	/**
	 * Check if a Node is in the given Node or its children
	 *
	 * @param treeNode A Node in the tree
	 * @param testNode the Node we are looking for in the tree
	 * @return True if the node is equal to this Node or one of its children
	 **/
	private boolean isNodeInTree( Node treeNode, Node testNode )
	{
		if ( null == treeNode )
			return false;
		else if ( treeNode.equals(testNode))
			return true;
		else
		{
			if ( treeNode.isGreater(testNode) )
				return isNodeInTree( treeNode.getRightNode(), testNode);
			else
				return isNodeInTree( treeNode.getLeftNode(), testNode);
		}
	}
}

package scottdjohnson.binarytree;

import scottdjohnson.binarytree.Node;

public class BinaryTree
{

	private Node root;

	public BinaryTree()
	{
		root = null;
	}

	public void insertNode(Node insert)
	{
		if (null == root)
			root = insert;
		else
			insertNode(root, insert);
	}

	private void insertNode (Node base, Node insert)
	/*
		If the baseObject is greater than the insert Object, look down the right (ie less than) 
		side of the tree, otherwise look down the left side. Continue recursively until a null node 
		is found, then insert it there.
	*/
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

	public boolean isNodeInTree( Node n )
	{
		return isNodeInTree(root, n);
	}
	
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

package scottdjohnson.binarytree;

public abstract class Node 
{
	private Node left;
	private Node right;

	public Node()
	{
		left	= null;
		right	= null;
	}
	
	abstract public Node copy ();

	abstract public boolean isGreater(Node test);
	
	@Override
	abstract public boolean equals (Object node);
	
	public Node getLeftNode()
	{
		return left;
	}

	public Node getRightNode()
	{
		return right;
	}
	
	public void setLeftNode( Node n )
	{
		left = n;
	}

	public void setRightNode( Node n )
	{
		right = n;
	}

}
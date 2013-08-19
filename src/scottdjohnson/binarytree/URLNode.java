package scottdjohnson.binarytree;

import scottdjohnson.binarytree.Node;

public class URLNode extends Node
{
	String url;

	public URLNode( String s )
	{
		super();
		
		setURL(s);
	}
	
	public Node copy()
	{
		return new URLNode( url );
	}
	
	public String getURL()
	{
		return url;
	}

	private void setURL( String s )
	{
		if ( null == s)
			url = "";
		else
			url = s;
	}
	
	public boolean isGreater(Node test)
	{
		if (this.url.compareTo( ((URLNode)test).getURL()) > 0 )
			return true;
		else
			return false;
	}

	@Override
	public boolean equals (Object node)
	{
		System.out.println("Compare: " + this.url + " " + ((URLNode)node).getURL());
		return this.url.compareTo( ((URLNode)node).getURL() ) == 0;
	}
}
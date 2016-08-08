package scottdjohnson.node;

import scottdjohnson.node.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the abstract Node class, where the data in a Node is a URL
 * Additionally, this Node type is saveable via Hibernate
 *
 * @author Scott Johnson
 **/
public class URLNode extends Node
{
	String url;
	private long key;
	private long parentKey;
	private static final Logger logger = Logger.getLogger(URLNode.class.getPackage().getName());

	/**
	 * Default constructor
	 **/
	public URLNode ()
	{
		super();
		setUrl("");
		parentKey = 0;
	}

	/**
	 * Constructor defining the URL
	 *
	 * @param s A String which is the new URL
	 **/
	public URLNode( String s )
	{
		super();
		
		setUrl(s);
		parentKey = 0;
	}

	/**
	 * Constructor defining the URL and hte parent
	 *
	 * @param s the URL
	 * @param parentKey the parent key (from the database) of this URL
	 **/
	public URLNode( String s, long parentKey )
	{
		this(s);
		this.parentKey = parentKey;
	}
	
	/**
	 * Copy this Node
	 *
	 * @return a copy of this as a Node
	 **/
	public Node copy()
	{
		return new URLNode( url );
	}
		
	/**
	 * Test if a new node is greater than this node
	 *
	 * @param test
	 * @return True if test is greater than this, otherwise false
	 **/
	public boolean isGreater(Node test)
	{
		if (this.url.compareTo( ((URLNode)test).getUrl()) > 0 )
			return true;
		else
			return false;
	}

	/**
	 * Test if a Object is equal to this
	 *
	 * @param o Object to test
	 * @return True if the nodes are equal, otherwise false
	 **/
	@Override
	public boolean equals (Object o)
	{
		if (o == null)
			return false;
		if (!(o instanceof URLNode))
			return false;

		logger.log(Level.INFO, "Compare: " + this.url + " " + ((URLNode)o).getUrl());
		return this.url.compareTo( ((URLNode)o).getUrl() ) == 0;
	}

        /**
         * Return the hash code of this object
         *
         * @return hash code on this object
         **/
        @Override
	public int hashCode()
	{
		return url.hashCode();
	}

	/**
	 * Get the URL of this URLNode
	 *
	 * @return the URL
	 **/
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * Set the URL of this URLNode
	 * Private because it should only be used internally at this point
	 *
	 * @params s The URL to set this to
	 **/
	private void setUrl( String s )
	{
		if ( null == s)
			url = "";
		else
			url = s;
	}

	/**
	 * Hibernate interace class
	 * Set the key from the database
	 *
	 * @param key the key of this URL in the database
	 **/
	public void setKey (long key)
	{
		this.key = key;
	}
	
	/**
	 * Hibernate interace class
	 * Get the key's current value after having been set from the database
	 *
	 * @return The key of this URL in the database
	 **/
	public long getKey ()
	{
		return key;
	}
	
	/**
	 * Hibernate interace class
	 * Set the parent key from the database
	 *
	 * @param parentKey the parent key of this URL in the database
	 **/
	public void setParentKey (long parentKey)
	{
		this.parentKey = parentKey;
	}
	
	/**
	 * Hibernate interace class
	 * Get the parent key's current value after having been set from the database
	 *
	 * @return The parent key of this URL in the database
	 **/
	public long getParentKey ()
	{
		return parentKey;
	}
}

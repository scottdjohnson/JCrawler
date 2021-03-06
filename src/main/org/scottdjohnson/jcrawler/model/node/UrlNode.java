package org.scottdjohnson.jcrawler.model.node;

import java.util.Date;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the abstract Node class, where the data in a Node is a URL
 * Additionally, this Node type is saveable via Hibernate
 *
 * @author Scott Johnson
 **/
public class UrlNode extends Node
{
	String url;
	String name;
	Timestamp timeCrawled;
	private long key;
	private long parentKey;

	private static final Logger logger = Logger.getLogger(UrlNode.class.getPackage().getName());

	/**
	 * Default constructor
	 **/
	public UrlNode ()
	{
		super();
		setUrl("");
		setName("");
		setTimeCrawled(new Timestamp(new Date().getTime()));
		parentKey = 0;
	}

	/**
	 * Constructor defining the URL
	 *
	 * @param s A String which is the new URL
	 **/
	public UrlNode( String s )
	{
		super();
		
		setUrl(s);
		parentKey = 0;
	}

	/**
	 * Constructor defining the URL and the parent
	 *
	 * @param s the URL
	 * @param parentKey the parent key (from the database) of this URL
	 **/
	public UrlNode( String s, long parentKey )
	{
		this(s);
		this.parentKey = parentKey;
	}

	/**
	 * Constructor defining the URL and all fields
	 *
	 * @param s the URL
	 * @param n the name of the link
	 * @param t the date and time this URL was crawled
	 * @param parentKey the parent key (from the database) of this URL
	 **/
	public UrlNode( String s, String n, Timestamp t, long parentKey )
	{
		this(s);
		this.name = n;
		this.timeCrawled = t;
		this.parentKey = parentKey;
	}
	
	/**
	 * Copy this Node
	 *
	 * @return a copy of this as a Node
	 **/
	public Node copy()
	{
		return new UrlNode( url );
	}
		
	/**
	 * Test if a new node is greater than this node
	 *
	 * @param test
	 * @return True if test is greater than this, otherwise false
	 **/
	public boolean isGreater(Node test)
	{
		if (this.url.compareTo( ((UrlNode)test).getUrl()) > 0 )
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
		if (!(o instanceof UrlNode))
			return false;

		logger.log(Level.INFO, "Compare: " + this.url + " " + ((UrlNode)o).getUrl());
		return this.url.compareTo( ((UrlNode)o).getUrl() ) == 0;
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
	 * Get the URL of this UrlNode
	 *
	 * @return the URL
	 **/
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * Set the URL of this UrlNode
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
	 * Get the link name of this UrlNode
	 *
	 * @return the link name
	 **/
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set the link name of this UrlNode
	 * Private because it should only be used internally at this point
	 *
	 * @params s The link name to set this to
	 **/
	private void setName( String n )
	{
		if ( null == n)
			name = "";
		else
			name = n;
	}

	/**
	 * Get the time this URL was cralwed
	 *
	 * @return the date and time of the crawl
	 **/
	public Timestamp getTimeCrawled()
	{
		return timeCrawled;
	}
	
	/**
	 * Set the time that this URL was crawled
	 * Private because it should only be used internally at this point
	 *
	 * @params t The date and time this URL was crawled
	 **/
	private void setTimeCrawled( Timestamp t )
	{
		timeCrawled = t;
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

package scottdjohnson.binarytree;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import scottdjohnson.binarytree.Node;

public class URLNode extends Node
{
	String url;
	private long key;
	private long parentKey;

	public URLNode ()
	{
		super();
		setUrl("");
		parentKey = 0;
	}

	public URLNode( String s )
	{
		super();
		
		setUrl(s);
		parentKey = 0;
	}

	public URLNode( String s, long parentKey )
	{
		this(s);
		this.parentKey = parentKey;
	}
	public Node copy()
	{
		return new URLNode( url );
	}
		
	public boolean isGreater(Node test)
	{
		if (this.url.compareTo( ((URLNode)test).getUrl()) > 0 )
			return true;
		else
			return false;
	}

	@Override
	public boolean equals (Object node)
	{
		System.out.println("Compare: " + this.url + " " + ((URLNode)node).getUrl());
		return this.url.compareTo( ((URLNode)node).getUrl() ) == 0;
	}
	
	public void save()
	{
		try {
			org.hibernate.Session session = null;
			
			// This step will read hibernate.cfg.xml and prepare hibernate for use
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			session = sessionFactory.openSession();
			
			org.hibernate.Transaction tx = session.beginTransaction();
			
			//Create new instance of Contact and set values in it by reading them from form object
			session.saveOrUpdate(this);
			tx.commit();
			session.flush();
			session.close();
		}
		catch (Exception e)
		{
			
		}
	}

	public String getUrl()
	{
		return url;
	}
	
	private void setUrl( String s )
	{
		if ( null == s)
			url = "";
		else
			url = s;
	}

	public void setKey (long key)
	{
		this.key = key;
	}
	
	public long getKey ()
	{
		return key;
	}
	
	public void setParentKey (long parentKey)
	{
		this.parentKey = parentKey;
	}
	
	public long getParentKey ()
	{
		return parentKey;
	}


}

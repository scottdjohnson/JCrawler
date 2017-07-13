package org.scottdjohnson.jcrawler.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dao
{
	protected Session session;
	protected Transaction tx;
	
	private static final Logger logger = Logger.getLogger(Dao.class.getPackage().getName());

	public Dao (Session s, Transaction t)
	{
		session = s;
		tx = t;
	}

	/**
	* Save this object in the database using a Hibernate session
	* @param o The object to save
	* 
	**/
	public void save(Object o)
	{
		try {
			session.saveOrUpdate(o);
			tx.commit();
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.getMessage());
		}
	}

	/**
	* Close the Hibernate session in this SessionBundle
	*
	**/
	public void close()
	{
		session.flush();
		session.close();
	}
}

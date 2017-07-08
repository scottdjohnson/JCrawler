package org.scottdjohnson.database;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory class for producing Data Access Objects for managing Hibernate connections
 *
 * @author Scott Johnson
 **/

public class DaoFactory
{
	public enum DaoType {URLNODE};

	private static SessionFactory sessionFactory 	= new Configuration().configure().buildSessionFactory();
	private static final Logger logger = Logger.getLogger(DaoFactory.class.getPackage().getName());

	/**
	* Private constructor so the class cannot be constructed
	*
	**/
	private DaoFactory()
	{
	}

	/**
	* Create and return a new session
	*
	**/
	public static Dao getDao(DaoType daoType)
	{
		Session s 	= null;
		Transaction t 	= null;	

		try
		{
			synchronized (sessionFactory)
			{
				s = sessionFactory.openSession();
				t = s.beginTransaction();
			}
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, e.getMessage());
		}

		if (null == s || null == t)
			logger.log(Level.INFO, "s or t are null.");

		switch (daoType)
		{
			case URLNODE:
				return new UrlNodeDao(s,t);
		}

		return null;
	}
}

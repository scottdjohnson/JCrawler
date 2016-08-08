package scottdjohnson.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionBundle
{
	Session session;
	Transaction tx;
	
	private static final Logger logger = Logger.getLogger(SessionBundle.class.getPackage().getName());

	public SessionBundle (Session s, Transaction t)
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
	* Delete the results of the query
	* @param queryString The HQL-style string to query, all results will be deleted
	**/
	public void deleteFromQuery(String queryString)
	{
                try
                {
                        Query query = session.createQuery(queryString);
                        List list = query.list();

                        for (int i = 0; i < list.size(); i++)
                        {
                                session.delete( list.get(i) );
                        }

                        tx.commit();
                }
                catch (Exception e)
                {
                         System.out.println(e.getMessage());
                }
	}

        /**
        * Get the results of the query, as a List
        * @param queryString The query string
        **/
        public List getFromQuery(String queryString)
	{
		List list = null;
		try
		{
			Query query = session.createQuery(queryString);
			list = query.list();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return list;
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

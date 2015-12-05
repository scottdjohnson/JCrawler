package scottdjohnson.database;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

/**
 * A final (static) class that can be used for connecting objects to the database via Hibernate
 *
 * @author Scott Johnson
 **/

public class DBConnector
{

	private static org.hibernate.Session session = null;
	private static  org.hibernate.Transaction tx = null;

	/**
	* Private constructor so the class cannot be constructed
	*
	**/
	private DBConnector()
	{
	}

	/**
	* Open the Hibernate session
	*
	**/
	public static void open()
	{
		if ( null == session || !session.isOpen())
                {
			session = new Configuration().configure().buildSessionFactory().openSession();
 			tx = session.beginTransaction();
		}
	}

	/**
	* Save this object in the database using a Hibernate session
	* @param o The object to save
	* 
	**/
	public static void save(Object o)
	{
		try {
			//Create new instance of Contact and set values in it by reading them from form object
			session.saveOrUpdate(o);
			tx.commit();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	* Delete the results of the query
	* @param queryString The HQL-style string to query, all results will be deleted
	**/
	public static void deleteFromQuery(String queryString)
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
        public static List getFromQuery(String queryString)
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
	* Close the Hibernate session
	*
	**/
	public static void close()
	{
		session.flush();
		session.close();
	}

}

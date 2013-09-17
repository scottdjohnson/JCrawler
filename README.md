JCrawler
========

Java based web crawler

ant - build the application

ant create_run_jar - create a single jar file for the final build (default)

ant build - build only the classes

ant clean - clean the source code

To execute the application:

java -jar jcrawler.jar http://myurl.com

Relative URLs will be checked for URLs contained on the given page, absolute URLs are assumed (for now) to be external to the site. Therefore, this will roughly crawl all of the pages publicly available from the front page, assuming they can be accessed via the chain of hyperlinks. The links are stored in a database, with the referring link listed as a parent.

Database setup
========

Create a user and password, grant permission to the new table below. For example, run these comments in MySQL:

create user 'jcuser'@'localhost' identified by 'XYXYXYXYXY'; #MAKE SURE TO ALTER THE PASSWORD and add the password to hibernate.cfg.xml

grant SELECT, INSERT, UPDATE, DELETE, INDEX, ALTER, CREATE, LOCK TABLES, CREATE TEMPORARY TABLES, DROP, REFERENCES ON jcrawler.* to 'jcuser'@'localhost';

flush privileges;

create database jcrawler;

use jcrawler;

create table url_list (

	url_key int not null auto_increment, 

	url text,

	parent_key int,

	primary key (url_key)

);

Servlet
========

To run this as a servlet:

	Compile with ant, then place everything in build/classes into the classes directy in WEB-INF

	Copy all the jar files in lib to the Tomcat lib directory (eg $CATALINA_HOME/lib)

	Update web.xml to point the JCrawler, and scottdjohnson.jcrawler.JCrawlerServlet


package org.pwv.budget;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Connect to a database and do basic stuff.
 * 
 * the jndi datasource glassfish context is defined in sun-resoures.xml which
 * was generated in NetBeans.
 * @author Phil Virgo December 2015
 */
public class Db {
	static final Logger log = Logger.getLogger(Db.class.getName());
	static DataSource ds =null;
	static Context ctx;

	
	/** Find a DataSource using a lookup in the InitialContext for "jdbc/honda"
	 * @return true if found else false 
	 */ 
	public static boolean findDb() {
		log.info("trying to connect to DB");

		try {
				log.info("get initial context");
				ctx	= new InitialContext();
			} catch (NamingException ex) {
				log.log(Level.SEVERE, "Naming exception for InitialContext: {0}",
								ex.getMessage());
				return false;
			}
			
		try {
			ds = (DataSource)ctx.lookup("jdbc/honda");
		} catch (NamingException ex) {
			log.log(Level.SEVERE, "Naming exception for context lookup: {0}", 
							ex.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * connect to the database 
	 * @return the database Connection or null if failed.
	 */
	public static Connection getConnection() {
		if (ds==null) { 
			if (! findDb()) {
				return null;
			}
		}
		
		Connection con = null;
		try { con = ds.getConnection("Honda","KWB699"); }
		catch (SQLException ex) {
				log.log(Level.SEVERE, "SQL Exception getting connection: {0}", 
								ex.getMessage());
		}
		log.finer("connection to Honda DB successful");
		return con;		
	}
	
	/**
	 * Close the database connection.
	 * @param con the connection to close.
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "Failed to close db connection: ", ex.getMessage());
			}
		}
	}
}
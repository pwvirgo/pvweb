package budget;

import budget.Db;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * queries of the budget tables
 * @author pwv
 */
public class Queries {
	static final Logger log = Logger.getLogger(Queries.class.getName());
	static final String EOL = System.getProperty("line.separator");
	
	String acctQ = "select * from account";
	Connection con=Db.getConnection();
	Statement st;
	ResultSet rslt;

	public Queries() throws SQLException {
			this.st = con.createStatement();
	}
	
	public String [][] acct () throws SQLException {
		ArrayList <String []>  rows= new ArrayList<>();
		
		rslt=st.executeQuery(acctQ);
		rows.add(new String [] {"Account", "Descrip"});
		
		while (rslt.next()) {
			rows.add(new String [] {rslt.getString("ACCOUNT"),
				rslt.getString("Descrip")});
		}
		
		return rows.toArray(new String[rows.size()][2]);
	}	
	
	public String acctAsHtmlTable () throws SQLException {
		StringBuilder sb= new StringBuilder(500);
		
		rslt=st.executeQuery(acctQ);
		sb.append("<table><tr><th>Account</th><th>Descrip</th></tr>");
		
		while (rslt.next()) {
			sb.append("<tr><td>").append(rslt.getString("ACCOUNT")). 
				append("</td><td>").append(rslt.getString("Descrip")).append("</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	public void close() throws SQLException {
		if (con !=null) con.close();
	}
	
	@Override
	public void finalize() throws SQLException { 
		try {
			close();
		} finally {
			try {
				super.finalize();
			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Queries.finalise failed {0}", ex);
			}
		}
	}
}

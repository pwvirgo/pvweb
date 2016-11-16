package org.pwv.budget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.util.logging.Level;

import java.util.logging.Logger;

/**
 *  Import data into the budget database
 * @author pwv
 */
public class Importx {
	static final Logger log = Logger.getLogger(Importx.class.getName());
	static final String EOL = System.getProperty("line.separator");
	

	SimpleDateFormat dtfmt = new SimpleDateFormat("MM/dd/yyyy");
	
	/**
	 * Insert data into the TRANSACT table
	 * @param trns the data to be inserted
	 * @return number of rows inserted
	 * @throws SQLException 
	 */
	public int tmpTrans(String [][] trns) throws SQLException {
		Connection con=Db.getConnection();
		Statement st;
		ResultSet rslt;
		Date trns_dt = null;

		
		String	insql = "insert into transact "
			+ "(TRNS_TYPE, TRNS_DT, AMOUNT, WITHWHOM, DESCRIP, PAIDWITH,"
						+ "ADDRESS, KEYWORD1, KEYWORD2, KEYWORD3)"
			+ "VALUES (?,?,?,?,?,?,?,?,?,?)"			;
		int rowsAffected=0;
		
		try {
			//st = con.createStatement();
			PreparedStatement preparedStatement = con.prepareStatement(insql);
			for (String [] row : trns) {
				switch (row[0]) {
					case "E":  case "I": break;
					case "SALE" : row[0]="E";
					default: row[0] = "I";
				}
				
				try {
					trns_dt = dtfmt.parse(row[1]);
				} catch (ParseException ex) {
					log.severe("bad date " + row[1]);
					trns_dt = null;
					continue;
				}
				
				preparedStatement.setString(1, row[0]);
				preparedStatement.setDate(2, new java.sql.Date(trns_dt.getTime()));
				
				String amount =row[2].trim().replaceFirst("-", "");
				
				preparedStatement.setString(3, amount);
				for (int j=3; j<=9; j++) {
					preparedStatement.setString(j+1, row[j]);
				}
/*
				preparedStatement.setString(4, row[3]);
				preparedStatement.setString(5, row[4]);
				preparedStatement.setString(6, row[5]);
					preparedStatement.setString(7, row[6]);
					preparedStatement.setString(8, row[7]);
					preparedStatement.setString(9, row[8]);
					preparedStatement.setString(10, row[9]);
*/
				rowsAffected += preparedStatement.executeUpdate();

			}

		} catch (SQLException ex ) {
			throw ex;
		}
		if (con != null) con.close();
		return rowsAffected;
	}
}

package org.pwv.budget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import java.util.logging.Logger;

//-------------------------------------------------
// replacing Importx which imported transact data with this which may import 
// various data types
//--------------------------------------------------

/**
 *  Import data into the budget database
 * @author pwv
 */
public class Import {
	static final Logger log = Logger.getLogger(Import.class.getName());
	static final String EOL = System.getProperty("line.separator");
	

	static final SimpleDateFormat dtfmt = new SimpleDateFormat("MM/dd/yyyy");
	static PreparedStatement pStmt;
	
	/**
	 * Insert (add) data into a  table
	 * @param trns the data to be inserted
	 * @param ttype the table to populate
	 * @return number of rows inserted
	 * @throws SQLException 
	 */
	public static int tmpTrans(String [][] trns, String ttype) 
					throws SQLException {
		Connection con=Db.getConnection();
		Statement st;
		ResultSet rslt;
		
		final String	transactSQL = "insert into transact "
			+ "(TRNS_TYPE, TRNS_DT, AMOUNT, WITHWHOM, DESCRIP, PAIDWITH,"
						+ "ADDRESS, KEYWORD1, KEYWORD2, KEYWORD3)"
			+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		final String mlSql = "insert into activityML "
			+ "(TRADE_DT, SETTLE_DT, ACCOUNT, DESCRIP, TRANSTYPE, SYMBOL, QUANTITY, PRICE,"
						+ " AMOUNT)"
			+ "VALUES (?,?,?, ?,?,?, ?,?,?)";
		
		int rowsAffected=0;
		
		String thissql;
		log.info("Begin import of " + ttype);
			switch (ttype) {
				case "transact" : thissql=transactSQL; break;
				case "activityML" : thissql=mlSql; break;
				default: throw new IllegalArgumentException("invalid transaction type");	
			}
		try {
			//st = con.createStatement();
			pStmt = con.prepareStatement(thissql);
			for (String [] row : trns) {
				switch (ttype) {
					case "activityML" :
						if(doActivityML(row))
							rowsAffected += pStmt.executeUpdate();
						// else
						break;
						
					case "transact" :   
						if (doTransact(row)) 
							rowsAffected += pStmt.executeUpdate();
						//else rowsRejected++;
					break;

					default: throw new IllegalArgumentException("invalid transaction type");	
				}
			}

		} catch (SQLException ex ) {
			throw ex;
		}
		con.close();
		return rowsAffected;
	}
	
	public static boolean doActivityML(String [] row) throws SQLException {
		Date trns_dt=null, settle_dt=null;

		log.log(Level.INFO, "row[0]: {0} row[1]: {1}", new Object[]{row[0], row[1]});
		
		
		if (! setDate(row[0], pStmt, 1) ) return false;
		setDate(row[1], pStmt, 2);
		pStmt.setString(3, row[2]);  // account
		pStmt.setString(4, row[3]);  // descrip
		pStmt.setString(5, "?");  // transtype
		pStmt.setString(6, row[7]);  // symbol
	
		setDecimal(row[6], pStmt, 7);  // quantity
		setDecimal(row[7], pStmt, 8); //price
		setDecimal(row[8], pStmt, 9);  // amount
		
		pStmt.setString(9, row[8].trim().replaceAll("[$,]", ""));

		return true;
	}
		
	public static boolean doTransact(String [] row) throws SQLException {
		Date trns_dt=null;

		switch (row[0]) {
			case "E":  case "I": break;
			case "SALE" : row[0]="E";
			default: row[0] = "I";
		}

		try {
			trns_dt = dtfmt.parse(row[1]);
		} catch (ParseException ex) {
			log.severe("bad date " + row[1]);
			return false;
		}

		pStmt.setString(1, row[0]);
		pStmt.setDate(2, new java.sql.Date(trns_dt.getTime()));
		String amount =row[2].trim().replaceFirst("-", "");
		pStmt.setString(3, amount);
		for (int j=3; j<=9; j++) {
			pStmt.setString(j+1, row[j]);
		}
		return true;
	}
	
	/**
	 * Set a date into a PreparedStatement
	 * @param svalue the string to be used as a date
	 * @param pst	the PreparedStatment to be set
	 * @param ndx the index (starting at 1) of the item in pst
	 * @return true the date parses correctly else false
	 * @throws SQLException
	 */
	public static boolean setDate(String svalue,PreparedStatement pst, 
					int ndx) throws SQLException {
		Date tmpDt;
		try { tmpDt = dtfmt.parse(svalue); } 
		catch (ParseException ex) {
			log.warning("bad date: " + svalue);
			pst.setNull(ndx, java.sql.Types.DATE);
			return false;
		}
		pst.setDate(ndx, new java.sql.Date(tmpDt.getTime()));
		return true;
	}

	/**
	 * set a Decimal into a prepared Statement
	 * @param svalue the string value (which may will be striped of blanks, commas,
	 *		and dollarsigns
	 * @param pst the PreparedStatment
	 * @param ndx the index (starting at 1) of the item in pst
	 * @throws SQLException
	 */
	public static void setDecimal(String svalue, PreparedStatement pst, 
					int ndx) throws SQLException {
		String tmp =svalue.trim().replaceAll("[$,]", "");
		if (tmp.equals("")) pst.setNull(ndx, java.sql.Types.DECIMAL);
		else pst.setString(ndx, tmp);
	}
}
package org.pwv.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Parse a String with the contents of a CSV file using a finite state machine 
 * @author pwv
 */
public class Parse {
	
	static final Logger logger = Logger.getLogger( "CSV2DB.Parse" );
	static final String EOL = System.getProperty("line.separator");
	
	private String errMsg = null;

  private boolean isValid = false;

	/** Instance storage of the parsed CSV data */
	private String[][] parsedArray;
		
	/** the events of the finite state machine */
	enum Event {
		TEXT (0), BLANK (1), QUOTE (2), DELIM (3), EOL (4) ;
		private final int myCode;
		Event(int eventCode) {myCode=eventCode;}
		public int code() {return myCode;}
	}
	
	/** the states of the finite state machine */
	enum State {
		STARTROW (0), INCELL	 (1), INQCELL	 (2), QINQCELL	 (3), STARTCELL (4), ERROR (5);
    private final int myCode;
    State(int stateCode) { myCode = stateCode; }
    public int code() { return myCode; }
	}
	
	/** a row in the lookup table defining what to do for an event/state pair */
	class Todo {
		boolean saveChr, saveCell, saveRow;
		State nextState;
		public Todo(boolean saveChr, boolean saveCell, boolean saveRow, State nextState) {
			this.saveChr=saveChr; this.saveCell=saveCell; this.saveRow=saveRow;
			this.nextState=nextState;
		}
	}
	
	/** the lookup table to determine what to do and the next state */
	final Todo [][] lookup = {
		// TEXT
			{new Todo(true, false, false, State.INCELL), // STARTROW 
			new Todo(true, false, false, State.INCELL), // INCELL
			new Todo(true, false, false, State.INQCELL), // INQCELL
			new Todo(false, false, false, State.ERROR), // INQINQCELL
			new Todo(true, false, false, State.INCELL)}, // STARTCELL
		// BLANK
			{new Todo(false, false, false, State.STARTROW), // STARTROW (ignore leading blanks)
			new Todo(true, false, false, State.INCELL), // INCELL
			new Todo(true, false, false, State.INQCELL), // INQCELL
			new Todo(false, false, false, State.ERROR), // INQINQCELL
			new Todo(false, false, false, State.INCELL)}, // STARTCELL (ignore leading blanks)
		// QUOTE
			{new Todo(false, false, false, State.INQCELL), // STARTROW (ignore leading blanks)
			new Todo(true, false, false, State.INQCELL), // INCELL
			new Todo(false, false, false, State.QINQCELL), // INQCELL  ?????????
			new Todo(true, false, false, State.INQCELL), // INQINQCELL
			new Todo(false, false, false, State.INQCELL)}, // STARTCELL (quoted cell)
		// DELIMIT
			{new Todo(false, true, false, State.STARTCELL), // STARTROW (empty cell)
			new Todo(false, true, false, State.STARTCELL), // INCELL
			new Todo(true, false, false, State.INQCELL), // INQCELL
			new Todo(false, true, false, State.STARTCELL), // INQINQCELL
			new Todo(false, true, false, State.STARTCELL)}, // STARTCELL (empty cell)
		// END LINE
			{new Todo(false, false, false, State.STARTROW), // STARTROW (ignore empty row)
			new Todo(false, true, true, State.STARTROW), // INCELL
			new Todo(true, false, false, State.INQCELL), // INQCELL (save eol char)
			new Todo(false, true, true, State.STARTROW), // INQINQCELL
			new Todo(false, true, true, State.STARTROW)} // STARTCELL (empty cell)
	};
	
	/** classify characters into the limited number of actionalable events */
	Event getEvent(char c, char delim) {
		switch (c) {
			case ' ' : return Event.BLANK;
			case '"' : return Event.QUOTE;
			case '\n': return Event.EOL;
			case '\r' : return Event.EOL;	
			default:
				if (c==delim) return Event.DELIM;
				else return Event.TEXT;
		}
	}
	
	/** 
	 * 
	 * @return an error message if there is an error else null 
	 */
	public String getErrorMsg () {
		return errMsg;
	}
	
	/**
	 *	Use the instance of parsedArray to create a HTML table
	 * @return an HTLM table in a String
	 */
	public String getHtmlTable() {
		StringBuilder alles = new StringBuilder(1024);
		alles.append("<table>").append(EOL);
		boolean row1 = true;
		
		for (String [] row: parsedArray) {
			alles.append("\t<tr>");
			for (String cell : row) {
				if (row1) 
					alles.append("\t\t<th>").append(cell).append("</th>");
				else
					alles.append("\t\t<td>").append(cell).append("</td>");					
			}
			row1=false;
			alles.append("</tr>").append(EOL);
		}
		return alles.append("</table>").append(EOL).toString();		
	}
	/**
	 * used to debug
	 * @return a String of the parsed results
	 */
	public String getString() {
		StringBuilder alles = new StringBuilder(500);
		for (String [] row: parsedArray) {
			for (String cell : row) {
				alles.append(cell).append(" !!! ");
			}
			alles.append("<br>\n");
		}
		return (alles.toString());
	}
	
	/** 
	 * 
	 * @return a 2 dimensional array of the parsed results 
	 */
	public String [][] getStrings() {
		return parsedArray;
	}

	/** 
	 * parse the CSV and store the result in parsedArray - store any error messages
	 *	in errmsg
	 * 
	 * @param csv  The data to be parsed
	 * @param delim the data delimiter
	 * @return true if parse was successful, else false
	 */
	public boolean parseit( String csv, char delim) {
		
		char  currChr;
		Event currEvent;
		StringBuilder tmps = new StringBuilder(50); 
		Todo todo;
		List <String> onerow = new ArrayList<>();
		List <String []> allrows = new ArrayList <>();

		State currState = State.STARTROW;
		isValid=true;
		int cellChrCount=0;

		for (int j=0; j < csv.length() && currState!=State.ERROR; j++) {
			currChr=csv.charAt(j);
			cellChrCount++;
			currEvent = getEvent(currChr, delim);
			
			todo = lookup[currEvent.code()] [currState.code()];
			currState = todo.nextState;
			
			if (currState == State.ERROR) {
				errMsg ="Error at line: " + (allrows.size() +1) + ", cell: "
					+ (onerow.size() +1) + ", character number: " + cellChrCount;
				isValid=false;
			}
			
			else {
				errMsg="all is fine";
				if (todo.saveChr) { tmps.append(currChr); }
				if (todo.saveCell) { 
					onerow.add(tmps.toString());
					tmps.delete(0, 999);
					cellChrCount=0;
				}

				if (todo.saveRow) {
					String [] z = new String [onerow.size()]; 
					onerow.toArray(z);
					allrows.add(z);
					onerow.clear();
				}
			}			
			//if (j > 25000) break;
		}

		// end of file - which was not handled 
		if ((currState!=State.STARTROW)) {
					onerow.add(tmps.toString());
					String [] z = new String [onerow.size()]; 
					onerow.toArray(z);
					allrows.add(z);
					onerow.clear();
		}
		parsedArray = new String[allrows.size()][];
		allrows.toArray(parsedArray);
		return isValid;
	}
}
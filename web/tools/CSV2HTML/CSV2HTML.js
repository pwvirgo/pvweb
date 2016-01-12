"use strict";

/* the logic to parse a CSV file  using a finite state machine.  
 * handles any number of quotes and line feeds embedded with a cell corecctly.
 * there is no meaningful error handling.
 * 
 *  pwvirgo  2015 */

var currChr;      // a chrObj with current character, cursor position, and charType
var delimit=",";  // the delimiter - only single character delimiters will work
var html;				  // the string of html being created

// the events
var chrType = {TEXT:	0, BLANK: 1, QUOTE: 2, DELIM: 3, EOL: 4};

// the states
var state = {STARTROW: 0, INCELL:1, INQCELL:2, QINQCELL:3, ENDCELL:4};

/* 2 dimensional array with rows indexed by events (chrType) and columns by state. 
 * Each cell contains a function and the next State
 */
var fsmArray = [
	[ //TEXT
		[newRowCellChr, state.INCELL], [saveChr, state.INCELL],
		[saveChr, state.INQCELL], [error, state.INQCELL], 
		[newCellWithChr, state.INCELL]
	],
	[  // Blank or Tab
		[function(){}, state.STARTROW], [saveChr, state.INCELL],
		[saveChr, state.INQCELL], [error, state.INQCELL],
		[function(){}, state.ENDCELL]
	],
	[ // QUOTE
		[newRowCell, state.INQCELL], [saveChr, state.INQCELL],
		[function() {}, state.QINQCELL], [saveChr, state.INQCELL],
		[newCell, state.INQCELL]
	],
	[ // DELIMIT
		[newRowEmptyCell, state.ENDCELL], [endCell, state.ENDCELL],
		[saveChr, state.INQCELL], [endCell, state.ENDCELL],
		[emptyCell, state.ENDCELL]
	],
	[	// end of line
		[function(){}, state.STARTROW], [endCellEndRow, state.STARTROW],
		[saveChr, state.INQCELL], [endCellEndRow, state.STARTROW],
		[emptyCellEndRow, state.STARTROW]
	]
];

// object to hold the current character, type, and location in the source
function chrObj(chr, type, cursor) {
	this.chr=chr;
	this.type=type;
	this.cursor=cursor;
}

// create a simple HTML page including the CSV as a table
function doPage(csvString) {
	return "<!DOCTYPE HTML> <html lang='en'><head>\n" +
		"<meta charset='utf-8'><meta http-equiv='expires' content='-1'>\n" +
		"<title>CSV Results</title>\n" +
		"<style>\n" +
		"	table, th, td {border: 1px solid black; border-collapse: collapse;}\n" +
		"	th, td {padding: 10px; word-wrap:break-word; max-width:450px}\n" +
		"</style>\n" +
		"</head><body>\n" + 
		"	<h1 id='csvtitle'>CSV Results</h1>" +
		"	<div id='csvinfo'></div>\n" +
		csvString +
	"\n</body></html>";
}

/*  determine the category of an event
 * 
 * @param {character as string} s
 * @returns an the character type
 */
function getChrType(s) {
	switch (s) {
		case '"': return chrType.QUOTE;
		case delimit : return chrType.DELIM;
		case ' ': 
		case '\t':	return chrType.BLANK;
		case '\n': return chrType.EOL;
		default :		return chrType.TEXT;
	}
}

function emptyCell() { html+="<td></td>"; }

function emptyCellEndRow() { html += "<td></td></tr>\n"; }

function endCell() { html+="</td>";}

function endCellEndRow() { html += "</td></tr>\n"; }

// NOT USED function endRow() {html+="</tr>"; }

function error() { html+= "***bad data***";}

function newCell() {	html+="<td>";	}

function newCellWithChr() {	html+= "<td>" + currChr.chr;	}

function newRowCellChr() {	html += "<tr><td>" + currChr.chr; }

function newRowCell() { html += "<tr><td>"; }
	
function newRowEmptyCell() { html += "<tr><td></td>"; }	

// return the next character - if there is not another character return null
function nextChr(source, chrO) {
	if (source.length > chrO.cursor) {
		var tmp=source.substr(chrO.cursor++, 1);
		chrO.chr=tmp;
		chrO.type=getChrType(tmp);
		return chrO;
	} else return null;
}

/*   ----------------------------------------------
 * Parse the (string) contents of a CSV file
 * @params	csv the string of CSV data
 *					delim the delimter used in the CSV data
 *					
 * @return a string which is an HTML table with no styling
 *   ---------------------------------------------- */
function parseCSV(csv, delim) {
	delimit = delim;
	csv=csv.trim();
	if (csv.length===0){ return "No data was found"; }

	html="<table>\n";

	//for (var j=0; j< csv.length; j++) {
		currChr=new chrObj("", null, 0);
		var currState=state.STARTROW;
		
		// this next loop processes each char in a row using the FSM
		currChr=nextChr(csv,currChr);
		while (currChr !== null) {
			var x=	fsmArray[currChr.type][currState];
			x[0](); // execute the appropriate function
			currState=x[1]; // set the state
			currChr=nextChr(csv,currChr);
		}
		
		html+="</td></tr>\n</table>\n";
	
	return  html;
}

// save the text of the currChr.
function saveChr() { html+=currChr.chr; }
package org.pwv.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 *  store parameters
 * @author pwv
 */
public class ParamBean {
	static final Logger logger = Logger.getLogger( "CSV2DB.params" );

	private String csvText=null;

	private char delim;
	
	/** @return the CSV delimiter */	
	public char getDelim() { return delim;} 
	
	/** @return the CSV file contents */
	public String getCsvText() { return csvText;}
	
	/**  set the CSV delimiter
	 * @param s the delimiter  */
	public void setDelim(String s) { 
		if (s.equals("\\t")) delim='\t';
		else delim = s.charAt(0);}
	
	/** set the CSV contents 
	 * @param s the contents of the CSV file as a string */
	public void setCsvText(String s) { csvText = s;}

	
	/**
	 * Gather the http request parameters and save them in this java bean, 
	 * along the CSV file contents.
	 * 
	 * @param request http request
	 * @param param  java bean to store parameters
	 * @throws IOException
	 * @throws ServletException 
	 */
	public void setParams(HttpServletRequest request)
		throws IOException, ServletException {

		logger.finest("Gathering parameters");
		
		for (Part part : request.getParts()) {
			// note: things could go wring if int is too samll
			int size=(int)part.getSize();
			byte [] buf = new byte[size];
			InputStream is = part.getInputStream();
			is.read(buf, 0, size);
			String z=new String(buf);
			
			switch (part.getName()) {
				case "delim" : this.setDelim(z); break;
				case "csvfile" : this.setCsvText(z); break;
				default: break;
			}
		}		
	}
}

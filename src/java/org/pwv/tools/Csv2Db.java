package org.pwv.tools;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
/**
 * Use CSV data (for budget app) as database insert transactions
 * @author pwv 2015
 */
@WebServlet(name = "Csv2Db", urlPatterns = {"/tools/Csv2Db"})
		
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB

/**
 * Get a CSV file and option parameters from the user 
 * 
 * 
 */
public class Csv2Db extends HttpServlet {

	static final Logger logger = Logger.getLogger( "CSV2DB" );
	// where to save uploaded files on the server
	// private static final String SAVE_DIR = "uploads";
	/**
	 *  store the parameters, extract the CSV data and store as a string
	 */
	protected ParamBean params = new ParamBean();
	protected Parse parse = new Parse();
	
	private final Db db = new Db();
	
	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, 
					HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		// gets absolute path of the web application
		//String appPath = request.getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		//String savePath = appPath + File.separator + SAVE_DIR;

		params.setParams(request);
		
		logger.setLevel(Level.FINER);
		
		logger.log(Level.FINER, "Delimiter: {0}", params.getDelim());
		logger.log(Level.FINER, " csv: {0}", params.getCsvText());
	
		parse.parseit(params.getCsvText(), params.getDelim());
		
		
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html> ");
			out.println("<head>");
			out.println("<title>new HTML</title>");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" " + 
							" href=\"../css/simple.css\">");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet CSVtoHTML at " +  request.getContextPath() + "</h1>");
			out.println("java.home: " + System.getProperty("java.home"));
			out.println("<br>");
			
			
			Queries qu;

			try {
				qu=new Queries();
				String [][] rows=qu.acct();
				for(String [] row: rows) {
					for (String cell: row) {
						out.println(cell + " ");
					}
					out.println("<br>");
				}
				out.println(qu.acctAsHtmlTable());
				qu.close();
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, "Query of ACCOUNT failed\n: {0}", ex.getMessage());
				out.println("<h1>Error querying ACCOUNT</h1>" + ex.getMessage());
			}
		
			out.println("<hr><h2>Parsing Errors</h2>");
			out.println(parse.getErrorMsg());
			out.println("<hr><h2>Parsing result</h2>");
			out.println(parse.getString());
			
			out.println("<br>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	protected String makeTable(String csv, String delim){
		StringBuilder tabl = new StringBuilder(500);
		String[] lines = csv.split("[\n]");

		tabl.append("<table><tr>");
		
		// write the column headers
		String[] stuff=lines[0].trim().split(delim);
		for (String stuff1 : stuff) {
			tabl.append("<th>").append(stuff1).append("</th>");
		}
		tabl.append("</tr>\n");
		for (int j=1; j< lines.length; j++) {
			tabl.append("<tr>");
			stuff=lines[j].trim().split(delim);
			
			// don't include empty lines - this may need more thought.
			if (stuff.length>2) {
				for (String stuff1: stuff) {
					tabl.append("<td>").append(stuff1).append("</td>");
				}
			}
			tabl.append("</tr>\n");
		}
		
		tabl.append("</table>");
		return tabl.toString();
	}
	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. 
	// Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
package org.pwv.tools;

import budget.Queries;
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
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
/**
 * Use CSV data (for budget app) as database insert transactions
 * @author pwv 2015
 */
@WebServlet(name = "Csv2HtmlServlet", urlPatterns = {"/tools/Csv2HtmlServlet"})
		
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB

/**
 * Get a CSV file and option parameters from the user 
 * 
 * 
 */
public class Csv2HtmlServlet extends HttpServlet {

	static final Logger logger = Logger.getLogger( "Csv2HtmlServlet" );

	
	protected CsvBean csvBean = new CsvBean();
	protected ParamBean params = new ParamBean();
	protected CsvParse parse = new CsvParse();
	
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

		request.setCharacterEncoding("UTF-8");
		params.setParams(request);
		
		logger.setLevel(Level.INFO);
		
		logger.log(Level.FINER, "Delimiter: {0}", params.getDelim());
		logger.log(Level.FINER, " csv: {0}", params.getCsvText());
	
		parse.parseit(params.getCsvText(), params.getDelim());
		
		csvBean.setMsg(parse.getErrorMsg());
		csvBean.setHtmlTable(parse.getHtmlTable());
		
		
		HttpSession session = request.getSession();
		session.setAttribute("rsltbean", csvBean);
		RequestDispatcher rdp = request.getRequestDispatcher("/tools/CSV2HTML/Csv2HtmlResult.jsp");
		rdp.forward(request, response);
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
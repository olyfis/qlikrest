package com.olympus.qlik.ws;
/*
 * DESC: Web Service for Qlik Data Universe
 * DATE: 2020-04-21
 * VERS: 1.0
 * http://cvyhj3a27:8181/qlik/initjson
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.olympus.olyutil.Olyutil;
import com.olympus.olyutil.log.OlyLog;
import com.olympus.qlik.DButil;
import com.olympus.qlik.OrdersReleased;
@WebServlet("/initjson")
public class InitJson extends HttpServlet {
	private final Logger logger = Logger.getLogger(OrdersReleased.class.getName()); // define logger
	static Statement stmt = null;
	static Connection con = null;
	static ResultSet res  = null;
	static NodeList  node  = null;
	static String s = null;
	static private PreparedStatement statement;
	
	/****************************************************************************************************************************************************/
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JsonArray jArr = new JsonArray();
		PrintWriter out = response.getWriter();
		String dispatchJSP = null;
		String connProp = null;
		//String sqlFile = null;
		String sep = null;
		ArrayList<String> strArr = new ArrayList<String>();
	 
		sep = ";";
		String propFile = "C:\\Java_Dev\\props\\Rapport.prop";
		String sqlFile = "C:\\Java_Dev\\props\\sql\\orderReleasedQlikWS\\Qlik_Rapport_OrdRel_V5.sql";	
		String logFileName = "ordersReleased.log";
		String directoryName = "D:/Kettle/logfiles/ordersReleased";
		Handler fileHandler =  OlyLog.setAppendLog(directoryName, logFileName, logger );
		String dateFmt = "";
		//System.out.println("%%%%%%%%%%%%%%%%%%%% in ContractEOT: doGet() ");
		String headerFile = "C:\\Java_Dev\\props\\headers\\OrderReleased_WS_Hdr_V5.txt";
		String Jsonfile = "C:\\Java_Dev\\JSON\\ordReleased.json";
 
		ArrayList<String> headerArr = new ArrayList<String>();
		headerArr = Olyutil.readInputFile(headerFile);
		Date date = Olyutil.getCurrentDate();
		String dateStamp = date.toString();

		strArr = DButil.getDbData(propFile, sqlFile);
		//Olyutil.printStrArray(strArr);
		jArr = DButil.buildJSON(strArr, headerArr);
		
		Olyutil.jsonWriterResponse(jArr, out);
		Olyutil.jsonWriter(jArr, Jsonfile);
		// End Display JSON
		dateFmt = Olyutil.formatDate("yyyy-MM-dd hh:mm:ss.SSS");
		logger.info(dateFmt + ": " + "------------------ Write JSON to: " + Jsonfile);
		fileHandler.flush();
		fileHandler.close();
		//request.getRequestDispatcher(dispatchJSP).forward(request, response);
		// System.out.println("Exit Servlet " + this.getServletName() + " in doGet() ");	
	}
}

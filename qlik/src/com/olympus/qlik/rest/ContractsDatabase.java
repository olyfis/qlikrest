package com.olympus.qlik.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.JSONArray;
//import org.json.JSONObject;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.olympus.olyutil.Olyutil;
import com.olympus.olyutil.log.OlyLog;
 

@WebServlet("/cdb")
public class ContractsDatabase extends HttpServlet {

	private final Logger logger = Logger.getLogger(ContractsDatabase.class.getName()); // define logger
	static Statement stmt = null;
	static Connection con = null;
	static ResultSet res  = null;
	static NodeList  node  = null;
	static String s = null;
	static private PreparedStatement statement;
	/****************************************************************************************************************************************************/

	/****************************************************************************************************************************************************/

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
		connProp = "C:\\Java_Dev\\props\\connection.prop";
		sep = ";";
		String propFile = "C:\\Java_Dev\\props\\unidata.prop";
		String sqlFile = "C:\\Java_Dev\\props\\sql\\contractsDatabase.sql";	
		String logFileName = "contractsDatabase.log";
		String directoryName = "D:/Kettle/logfiles/ContractsDB";
		Handler fileHandler =  OlyLog.setAppendLog(directoryName, logFileName, logger );
		String dateFmt = "";
		//System.out.println("%%%%%%%%%%%%%%%%%%%% in ContractEOT: doGet() ");
		String headerFile = "C:\\Java_Dev\\props\\headers\\ContractsDBJhdr.txt";
		String Jsonfile = "C:\\Java_Dev\\JSON\\contractDB.json";
 
		ArrayList<String> headerArr = new ArrayList<String>();
		headerArr = Olyutil.readInputFile(headerFile);
		Date date = Olyutil.getCurrentDate();
		String dateStamp = date.toString();

		strArr = DJutil.getDbData(propFile, sqlFile);
		//Olyutil.printStrArray(strArr);
		jArr = DJutil.buildJSON(strArr, headerArr);

		Olyutil.jsonWriterResponse(jArr, out);
	
		Olyutil.jsonWriter(jArr, Jsonfile);
		// End Display JSON
		dateFmt = Olyutil.formatDate("yyyy-MM-dd hh:mm:ss.SSS");
		logger.info(dateFmt + ": " + "------------------Begin forward to: " + dispatchJSP);
		fileHandler.flush();
		fileHandler.close();
		//request.getRequestDispatcher(dispatchJSP).forward(request, response);
		// System.out.println("Exit Servlet " + this.getServletName() + " in doGet() ");	
	}
}

package com.olympus.qlik.rest;

/*
 * This class uses different queries and header files than the OrdersReleased class above.
 * 
 *  http://localhost:8181/qlik/ususpense
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;

import com.olympus.olyutil.Olyutil;
import com.olympus.olyutil.log.OlyLog;
import com.olympus.qlik.DButil;

//RUN: http://localhost:8181/reports/ordrel?startDate=2019-12-01&endDate=2019-12-12
@WebServlet("/ususpense")
public class UnappliedSuspense extends HttpServlet {
	private final Logger logger = Logger.getLogger(OrderReleasedWS.class.getName()); // define logger
	// Service method of servlet
	static Statement stmt = null;
	static Connection con = null;
	static ResultSet res = null;
	static String s = null;
	static private PreparedStatement statement;
	static String ILpropFile = "C:\\Java_Dev\\props\\unidata.prop";
	static String sqlFile = "C:\\Java_Dev\\props\\sql\\qlikrest\\UnappSuspense.sql";

	/****************************************************************************************************************************************************/

	/****************************************************************************************************************************************************/
	public static ArrayList<String> getDbData(String propFile, String params, String sqlSrc) throws IOException {
		FileInputStream fis = null;
		FileReader fr = null;
		String s = new String();
		StringBuffer sb = new StringBuffer();
		ArrayList<String> strArr = new ArrayList<String>();
		try {
			fis = new FileInputStream(propFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties connectionProps = new Properties();
		connectionProps.load(fis);
		fr = new FileReader(new File(sqlSrc));
		// be sure to not have line starting with "--" or "/*" or any other non
		// alphabetical character
		BufferedReader br = new BufferedReader(fr);
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		br.close();
		// displayProps(connectionProps);
		String query = new String();
		query = sb.toString();
		// System.out.println(query);
		try {
			con = Olyutil.getConnection(connectionProps);
			if (con != null) {
				// System.out.println("Connected to the database");
				statement = con.prepareStatement(query);
				//statement.clearParameters();
				res = Olyutil.getResultSetPS(statement);
				strArr = Olyutil.resultSetArray(res, ";");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return strArr;
	}

	/****************************************************************************************************************************************************/
	public static Map<String, String> getContractHash(ArrayList<String> dataArr) {
		Map<String, String> myMap = new HashMap<String, String>();
		String key = "";
		String value = "";
		for (String str : dataArr) {
			String[] items = str.split(";");
			key = items[0];
			value = items[1];
			myMap.put(key, value);
		}
		return myMap;
	}

	/****************************************************************************************************************************************************/

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> contractMap = new HashMap<String, String>();
		ArrayList<String> strArr = new ArrayList<String>();
		// String dispatchJSP = "/unappsuspensedetail.jsp";
		String logFileName = "unappsuspense.log";
		String directoryName = "D:/Kettle/logfiles/unappsuspense";
		Handler fileHandler = OlyLog.setAppendLog(directoryName, logFileName, logger);
		String dateFmt = "";
		String headerFile = "C:\\Java_Dev\\props\\headers\\unappsuspense.txt";
		String sDate = "";
		String eDate = "";
		JsonArray jsonArr = new JsonArray();
		String Jsonfile = "C:\\Java_Dev\\JSON\\unappsuspense.json";	 
		// ArrayList<String> idArr = new ArrayList<String>();
		ArrayList<String> errArr = new ArrayList<String>();
		Map<String, Boolean> contractErrs = null;
		boolean errStat = false;
		PrintWriter out = response.getWriter();
		// response.setContentType("text/JSON");
  
		int errCount = 0;
		 
		strArr = getDbData(ILpropFile, "", sqlFile);
		// Olyutil.printStrArray(strArr);
		// contractMap = getContractHash(strArrIL);

		// Olyutil.printHashMap(contractMap);
		// System.out.println("***^^^*** Contact:" + contractMap.get("011-0008964-004")
		// + "-- SZ:" + contractMap.size() + "--");

		/*
		 * String id = ""; for (String str : strArr) { String[] items = str.split(";");
		 * id = items[0]; System.out.println("***^^^*** ContactID:" + id + "--  Date:" +
		 * contractMap.get(id) + "--"); }
		 * 
		 */
		logger.info(dateFmt + ": " + "------------------Begin writing JSON ");
		fileHandler.flush();
		fileHandler.close();
		ArrayList<String> headerArr = new ArrayList<String>();
		headerArr = Olyutil.readInputFile(headerFile);
		request.getSession().setAttribute("strArr", strArr);
 
		// request.getSession().setAttribute("contractMap", contractMap);
		// Olyutil.printHashMap(contractMap);
		//Olyutil.printStrArray(strArr);
		jsonArr = Olyutil.buildJSON(strArr, headerArr, ";");

		Olyutil.jsonWriterResponse(jsonArr, out);
		Olyutil.jsonWriter(jsonArr, Jsonfile);
		// request.getRequestDispatcher(dispatchJSP).forward(request, response);
	}
	/****************************************************************************************************************************************************/
}

package com.olympus.qlik.rest.unappsuspense;

 

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
//RUN: http://localhost:8181/qlik/unapplied
@WebServlet("/unapplied")
public class UnappliedSuspense extends HttpServlet {
	private final Logger logger = Logger.getLogger(UnappliedSuspense.class.getName()); // define logger
	// Service method of servlet

	/****************************************************************************************************************************************************/

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		 
		ArrayList<String> uasArr = new ArrayList<String>();
		ArrayList<String> headerArr = new ArrayList<String>();
		JsonArray jsonArr = new JsonArray();
		String Jsonfile = "C:\\Java_Dev\\JSON\\unappsuspense\\uaSuspense.json";	
		String uasFile = "D:\\Kettle\\Production\\unappsuspense\\csvfiles\\uaSuspense.txt";
		String headerFile = "C:\\Java_Dev\\props\\headers\\unappsuspense\\uasHdr.txt";
		headerArr = Olyutil.readInputFile(headerFile);
		
		uasArr = Olyutil.readInputFile(uasFile);
		//Olyutil.printStrArray(uasArr, "uasArr: ");
		
		jsonArr = Olyutil.buildJSON(uasArr, headerArr, ";");

		Olyutil.jsonWriterResponse(jsonArr, out);
		Olyutil.jsonWriter(jsonArr, Jsonfile);
		
		
		
	 
	}
	
	
	
	/****************************************************************************************************************************************************/
}

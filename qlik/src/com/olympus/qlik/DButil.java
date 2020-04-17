package com.olympus.qlik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import org.json.JSONArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import org.json.JSONObject;
import com.olympus.olyutil.Olyutil;

public class DButil {
	static Statement stmt = null;
	static Connection con = null;
	static ResultSet res  = null;
	static private PreparedStatement statement;
	/****************************************************************************************************************************************************/
	public static ArrayList<String> getDbData(String propFile, String sqlFile ) throws IOException {
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
		 
		fr = new FileReader(new File(sqlFile));
		
		// be sure to not have line starting with "--" or "/*" or any other non alphabetical character
		BufferedReader br = new BufferedReader(fr);
		while((s = br.readLine()) != null){
		      sb.append(s);
		       
		}
		br.close();
		//displayProps(connectionProps);
		String query = new String();
		query = sb.toString();	
		//System.out.println("Query=" + query);	
		//System.out.println( query);	
		try {
			con = Olyutil.getConnection(connectionProps);
			if (con != null) {
				//System.out.println("Connected to the database");
	
				statement = con.prepareStatement(query);

				//statement.setString(1, dateParam);
				res = Olyutil.getResultSetPS(statement);		 	 
				strArr = Olyutil.resultSetArray(res, "^");			
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
	/** ***************************************************************************************************************************************************/
	static public JsonArray buildJSON(ArrayList<String> strArr, ArrayList<String> hdrArr) throws IOException {
		JsonArray jsonArr = new JsonArray();
		//Olyutil.printStrArray(strArr);
		 
		
		int k = 0;
		for (String str : strArr) { // iterating ArrayList
			
			
			JsonObject obj = new JsonObject();
			String[] items = str.split("\\^");
			
			 //System.out.println("strArrSZ=" +  items.length +  " -- Line: " + k +  " -- DATA:" + str + "---");
			 //System.out.println("hrdArrSZ=" +  hdrArr.size() );
		
			for (int i = 0; i < items.length; i++) {

				if (i == 2 || i == 16 || i == 55 || i == 56 || i == 75 || (i >= 18 && i <= 49)) {
					obj.addProperty(hdrArr.get(i).trim(),
							Olyutil.strToDouble(items[i].replaceAll(",", "").replaceAll("\\$", "")));

				} else {
					if ((i == 3 || i == 59 || i == 60 || i == 74 || i == 77) && (!Olyutil.isNullStr(items[i]))) {
						obj.addProperty(hdrArr.get(i).trim(),
								Olyutil.formatDate(items[i], "yyyy-MM-dd hh:mm:ss.SSS", "yyyy-MM-dd"));
					} else {
						obj.addProperty(hdrArr.get(i).trim(), items[i]);
					}

					/*if (k < 1) {
						 System.out.println("i=" + i + "  -- ITEM=" + hdrArr.get(i).trim() + "-- Value=" + items[i]);		
					} */

				}
				 
    			//obj.addProperty(hdrArr.get(i).trim(), items[i]);
    			//newStrArr.add(items[i]);
    			 //System.out.println(hdrArr.get(i).trim() + "-->" + items[i]);
    		}	
    		//System.out.println("*****************************************************************************************************************");
    		jsonArr.add(obj);
    		k++;
    		/*
    		if (k > 0) {
    			return;
    		}
    		*/
		}
		
		return(jsonArr);
	}
	
	/****************************************************************************************************************************************************/

}

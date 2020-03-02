package com.olympus.qlik.validate;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.olympus.olyutil.Olyutil;
import com.olympus.qlik.ContractsDatabase;

@WebServlet("/eval")
public class ExcelValidateTest extends HttpServlet {
	private final Logger logger = Logger.getLogger(ContractsDatabase.class.getName()); // define logger

	
	/****************************************************************************************************************************************************/
	static public BigDecimal addUpTotal( BigDecimal runningTotal) {
	     
	    return runningTotal;
	}
	/****************************************************************************************************************************************************/

	/****************************************************************************************************************************************************/
	/****************************************************************************************************************************************************/
	static public HashMap<String, BigDecimal>  parseData(ArrayList<String> strArr, ArrayList<String> hdrArr, String sep) {
		
		//Map<String, Double> myKitMap = new HashMap<String, Double>();
	    //List<Map<String , KitData>> myKits  = new ArrayList<Map<String,KitData>>();
	    HashMap<String, BigDecimal> map = new HashMap<String, BigDecimal>();

		//Olyutil.printStrArray(strArr);
		 
	    
		int k = 0;
		for (String str : strArr) { // iterating ArrayList
			String[] items = str.split(sep);
			String key = "";
			double value = 0.0;

			//System.out.println("strArrSZ=" +  items.length +  " -- Line: " + k +  " -- DATA:" + str + "---");
			//System.out.println("hrdArrSZ=" +  hdrArr.size() );
			BigDecimal bd = null;
    		for (int i = 0; i < items.length; i++) {
    			key = items[1];
    		 
    			if (! Olyutil.isNullStr(key)) {
    				
    				
    				
    				if (i == 7) {
    					if(Olyutil.isNullStr(str)) {
    						  bd = new BigDecimal(0);
    					} else {
    						value = Olyutil.strToDouble(items[i].replaceAll(",", "").replaceAll("\\$", "").replaceAll("\"", "")  );
    						  bd = new BigDecimal(value);
    					}
        				//System.out.println("ID:" + items[1] + "-- Screen14=" + items[i] + "--");
        				map.put( key, bd);
        			}
    				
    				
    			} else {
    				System.out.println("** Error: ID -s null!");
    			}
    			
    			/*
    			//if (k < 1) { System.out.println("i=" + i + "  -- ITEM=" + hdrArr.get(i).trim() + "-- Value=" + items[i]); }
    			if (i == 5 || i == 15 || i == 18 ||  i == 21 || i == 35  || i == 40  || i == 44  ) {
    				obj.addProperty(hdrArr.get(i).trim(), Olyutil.strToDouble(items[i].replaceAll(",", "").replaceAll("\\$", "")  ));
    				
    				
    			} else {
    				obj.addProperty(hdrArr.get(i).trim(), items[i]);
    			}
    			//newStrArr.add(items[i]);
    			 //System.out.println(hdrArr.get(i).trim() + "-->" + items[i]);
    			   
    			  */
    		}	
    		//System.out.println("*****************************************************************************************************************");
    		//jsonArr.add(obj);
    		k++;
    		/*
    		if (k > 0) {
    			return;
    		}
    		*/
		}
		
		 return(map);
	}
	/****************************************************************************************************************************************************/

	public BigDecimal sumData(HashMap<String, BigDecimal> mp) {
	    Iterator it = mp.entrySet().iterator();
	    BigDecimal total = new BigDecimal(0);
	    BigDecimal value = new BigDecimal(0);
	    
	    
	    int k = 0;
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	       value = (BigDecimal)pair.getValue();
	       // System.out.println("Value:" + value + "--");
	        //System.out.println("Data: " + pair.getKey() + " = " + pair.getValue()); 
	       total = total.add(value);
	       //System.out.println("Total: " +  total + "-- Key:" + pair.getKey() + " = " + pair.getValue()); 
	        if (k < 20) {
		        //System.out.println("Total: " +  total + "-- Key:" + pair.getKey() + " = " + value);
	        }
	        k++;
	    }
	    
	   // System.out.println("********^^^^************\n");
	    return(total);
	}
	/****************************************************************************************************************************************************/

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 HashMap<String, BigDecimal> hMap = new HashMap<String, BigDecimal>();
		String logFileName = "qlikValidate.log";
		String directoryName = "D:/Kettle/logfiles/qlikValidate";
		JsonArray jArr = new JsonArray();
		PrintWriter out = response.getWriter();
		String dispatchJSP = null;
		String connProp = null;
		//String sqlFile = null;
		String sep = ",";
		String qCsvFile = "C:\\Java_Dev\\props\\qlik\\cdb\\qlikCDB.csv";
		String qCDBHdr = "C:\\Java_Dev\\props\\qlik\\cdb\\headers\\qlikCDBHdr.txt";
		
		ArrayList<String> qStrArr = new ArrayList<String>();
		ArrayList<String> rStrArr = new ArrayList<String>();
		ArrayList<String> qHeaderArr = new ArrayList<String>();
		ArrayList<String> rHeaderArr = new ArrayList<String>();
		qStrArr = Olyutil.readInputFile(qCsvFile);
		
		qHeaderArr = Olyutil.readInputFile(qCDBHdr);
		
		//Olyutil.printStrArray(qStrArr);
		hMap = parseData(  qStrArr,  qHeaderArr, sep);
		int sz = hMap.size();
		//Olyutil.printHashMap(hMap);
		//System.out.println("hMapSz=" + sz);
		BigDecimal total = sumData(hMap);
		DecimalFormat df = new DecimalFormat("$###,###.##");
		String fp = df.format(total);
		//System.out.println("Total=" + total + "--");
		//System.out.println(String.format("%.2f", total));
		System.out.println("*** Target Data Total (Qlik)  --> Screen_14_Net_Investment   = " + fp + " --> Records processed = " + sz);
		
	}

}

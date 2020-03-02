
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

	@WebServlet("/valqlik")
	public class ValidateQlikDataLoad extends HttpServlet {
		private final Logger logger = Logger.getLogger(ContractsDatabase.class.getName()); // define logger

		
		/****************************************************************************************************************************************************/

		/****************************************************************************************************************************************************/

		/****************************************************************************************************************************************************/
		/****************************************************************************************************************************************************/
		static public HashMap<String, BigDecimal>  parseDataRest(ArrayList<String> strArr, String sep) {
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
	    
	    		}	
	    	
	    		k++;
			}
			
			 return(map);
		}
		/****************************************************************************************************************************************************/

		public BigDecimal sumData(HashMap<String, BigDecimal> mp) {
		    Iterator it = mp.entrySet().iterator();
		    BigDecimal total = new BigDecimal(0);
		    BigDecimal value = new BigDecimal(0);
		    
		    
	
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		       value = (BigDecimal)pair.getValue();
		       // System.out.println("Value:" + value + "--");
		        //System.out.println("Data: " + pair.getKey() + " = " + pair.getValue()); 
		       total = total.add(value);
		       //System.out.println("Total: " +  total + "-- Key:" + pair.getKey() + " = " + pair.getValue()); 
		     
		    }
		    
		   // System.out.println("********^^^^************\n");
		    return(total);
		}
		/****************************************************************************************************************************************************/

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			HashMap<String, BigDecimal> hMapR = new HashMap<String, BigDecimal>();
			HashMap<String, BigDecimal> hMapQ = new HashMap<String, BigDecimal>();
			
			String logFileName = "qlikValidate.log";
			String directoryName = "D:/Kettle/logfiles/qlikValidate";
		 
			PrintWriter out = response.getWriter();
	 
			String sep = ",";
			String csvFile = "C:\\Java_Dev\\props\\qlik\\cdb\\restCDB.csv";
			String qcsvFile = "C:\\Java_Dev\\props\\qlik\\cdb\\qlikCDB.csv";
			DecimalFormat df = new DecimalFormat("$###,###.##");
			
			ArrayList<String> strArr = new ArrayList<String>();
			ArrayList<String> qstrArr = new ArrayList<String>();
			strArr = Olyutil.readInputFile(csvFile);
			qstrArr = Olyutil.readInputFile(qcsvFile);
		 
			
			//Olyutil.printStrArray(qStrArr);
			hMapR = parseDataRest(strArr, sep);
			hMapQ = parseDataRest(qstrArr, sep);
			
			int sz = hMapR.size();
			int qsz = hMapQ.size();
			//Olyutil.printHashMap(hMap);
			//System.out.println("hMapSz=" + sz);
			BigDecimal totalRest = sumData(hMapR);
			BigDecimal totalQlik = sumData(hMapQ);
			String fpRest = df.format(totalRest);
			String fpQlik = df.format(totalQlik);
			//System.out.println("Total=" + total + "--");
			//System.out.println(String.format("%.2f", total));
			System.out.println("*** Source Data Total (Rest) --> Screen_14_Net_Investment  = " + fpRest + " --> Records processed = " + sz);
			System.out.println("*** Target Data Total (Qlik) --> Screen_14_Net_Investment  = " + fpQlik + " --> Records processed = " + qsz);
			
		}

	}

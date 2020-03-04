
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
		static public HashMap<String, BigDecimal>  parseData(ArrayList<String> strArr, String sep ) {
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
		public void displayHashMap(HashMap<String, Boolean> mp, String tag)  {
			
			Iterator it = mp.entrySet().iterator();
			String key = "";
			
			 while (it.hasNext()) { // Initialize compare hMap
				 Map.Entry pair = (Map.Entry)it.next();
				 key = (String) pair.getKey();
				 System.out.println(tag + key + " = " + pair.getValue());  
				 /*
				 if ((boolean) pair.getValue() == false ) {
					 System.out.println(tag + key + " = " + pair.getValue());  
				 }
				 */
			 }
		}
		/****************************************************************************************************************************************************/
		public void getMissingData(HashMap<String, Boolean> mp,  HashMap<String, BigDecimal> src, String tag, BigDecimal srcTot, BigDecimal tgtTot)  {
			Iterator it = mp.entrySet().iterator();
			String key = "";
			BigDecimal srcValue = new BigDecimal(0);
			BigDecimal total = new BigDecimal(0);
			BigDecimal newTotal = new BigDecimal(0);
			DecimalFormat df = new DecimalFormat("$###,###.##");
		    
			 while (it.hasNext()) { // Initialize compare hMap
				 Map.Entry pair = (Map.Entry)it.next();
				 key = (String) pair.getKey();
				 if ((boolean) pair.getValue() == false ) {
					 if (src.get(key) != null) {
						 srcValue = src.get(key);
						 total = total.add(srcValue);
						 String val = df.format(srcValue);
						 System.out.println(tag + key + " = " + pair.getValue() + "-- srcValue="  + val );  	 
					 }	 
				 }	 
			 }
			 String totalVal = df.format(total);
			 String sTot = df.format(srcTot);
			 String tTot = df.format(tgtTot);
			 
			 newTotal = tgtTot.add(total);
			 String newTotalVal = df.format(newTotal);
			 System.out.println("\nTotal of missing data:" + totalVal);
			 
			 System.out.println("Total from Source:" + sTot);
			 System.out.println("Total from target:" + tTot);
	 
			 System.out.println("\nTotal of Target data plus missing data:" + newTotalVal);
		}	
		/****************************************************************************************************************************************************/
		public HashMap<String, Boolean> compareSrcTgt(HashMap<String, BigDecimal> src, HashMap<String, BigDecimal> tgt) {
			HashMap<String, Boolean> cHmap = new HashMap<String, Boolean>();
			Iterator itSrc = src.entrySet().iterator();
			String srcKey = "";
	
			while (itSrc.hasNext()) { // Initialize compare hMap
				Map.Entry pair = (Map.Entry) itSrc.next();
				srcKey = (String) pair.getKey();
				cHmap.put(srcKey, false);
				// System.out.println("****srcData: " + srcKey + " = " + pair.getValue());
			}
	
			Iterator itTgt = tgt.entrySet().iterator();
			String tgtKey = "";
			while (itTgt.hasNext()) { // find missing contracts
				Map.Entry pair = (Map.Entry) itTgt.next();
				tgtKey = (String) pair.getKey();
				// System.out.println("****tgtData:" + tgtKey + "=" + pair.getValue());
				cHmap.replace(tgtKey, true);
			}
			return (cHmap);
		}
		/****************************************************************************************************************************************************/

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			HashMap<String, BigDecimal> hMapR = new HashMap<String, BigDecimal>();
			HashMap<String, BigDecimal> hMapQ = new HashMap<String, BigDecimal>();
			HashMap<String, Boolean> contractHmap = new HashMap<String, Boolean>();
			
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
			hMapR = parseData(strArr, sep);
			hMapQ = parseData(qstrArr, sep);
			
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
			System.out.println("*** Source Data Total (Contracts DB Rest) --> Screen_14_Net_Investment  = " + fpRest + " --> Records processed = " + sz);
			System.out.println("*** Target Data Total (Contracts DB Qlik) --> Screen_14_Net_Investment  = " + fpQlik + " --> Records processed = " + qsz);
			contractHmap = compareSrcTgt(hMapR, hMapQ); 
			//displayHashMap(contractHmap, "Contracts: ");
			getMissingData(contractHmap, hMapR, "Contracts DB -> Missing Contract: ", totalRest, totalQlik );
		}

	}

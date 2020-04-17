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
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 
import com.olympus.qlik.validate.ordreleased.OrdReleased;

@WebServlet("/orelval")
public class OrdRelValidate extends HttpServlet {
	private final Logger logger = Logger.getLogger(OrdRelValidate.class.getName()); // define logger
		
	static int srcRecs = 0;
	static int tgtRecs = 0;
	static HashMap<String, Boolean> contractHmap = new HashMap<String, Boolean>();
	/****************************************************************************************************************************************************/
	/****************************************************************************************************************************************************/
	static public  HashMap<String, List<OrdReleased>> parseData_Orig(ArrayList<String> strArr, HashMap<String, List<OrdReleased>> map,  String sep ) throws IOException {
		String key = "";
		List<OrdReleased> contractsArr = null;
		for (String str : strArr) { // iterating ArrayList	
			String[] items = str.split(",");
		 
			if (! Olyutil.isNullStr(items[0])) {
				key = items[0];
				OrdReleased contract = new OrdReleased();
				contract = loadDataObj(str, contract, sep);
				
				if (map.get(key) != null) { // Key Exists
					//System.out.println("** Key Exists");
					//contractsArr.add(contract);
					contractsArr = map.get(key);
					contractsArr.add(contract);
					map.put(key, contractsArr);
					
				} else { // no key in map
					contractsArr = new ArrayList<OrdReleased>();
					contractsArr.add(0, contract);
					//System.out.println("** Key Does NOT Exist");
					
					map.put(key, contractsArr);
					
				}
			} else {
				 System.out.println("Contract ID field is null!");
			}
			 
			//contract = loadDataObj(str, contracts, contract, sep);
			//contracts.add(contract);
		}		
		  return(map);	
	}
	
		
	/****************************************************************************************************************************************************/
	public static OrdReleased  loadDataObj(String str, OrdReleased contract, String sep    ) throws IOException {
			String[] items = str.split(sep);
			String key = "";
			double value = 0.0;
			//System.out.println("strArrSZ=" +  items.length +  " -- Line: " + k +  " -- DATA:" + str + "---");
			//System.out.println("hrdArrSZ=" +  hdrArr.size() );
			BigDecimal bd = null;
    		for (int i = 0; i < items.length; i++) {
    			key = items[0]; 
    			if (! Olyutil.isNullStr(key)) {
    				if (i == 1) {
    					if(Olyutil.isNullStr(str)) {
    						  bd = new BigDecimal(0);
    					} else {
    						value = Olyutil.strToDouble(items[i].replaceAll(",", "").replaceAll("\\$", "").replaceAll("\"", "")  );
    						  bd = new BigDecimal(value);
    					}
        				//System.out.println("ID:" + items[1] + "-- Screen14=" + items[i] + "--");
    					contract.setAgreementNum(key);
    					contract.setNetInvoice(bd); 
        			}	
    			} else {
    				System.out.println("** Error: ID is null!");
    			}
    		}	   	
	 return( contract);
	}
		
	
	/****************************************************************************************************************************************************/
	
	static public  HashMap<String, List<OrdReleased>> parseData(ArrayList<String> strArr, HashMap<String, List<OrdReleased>> map,  String sep ) throws IOException {
		String key = "";
		List<OrdReleased> contractsArr = null;
		for (String str : strArr) { // iterating ArrayList	
			String[] items = str.split(",");
		 
			if (! Olyutil.isNullStr(items[0])) {
				key = items[0];
				OrdReleased contract = new OrdReleased();
				contract = loadDataObj(str, contract, sep);
				
				if (map.get(key) != null) { // Key Exists
					//int ms = map.get(key).size();
					//String id = map.get(key).get(0).getAgreementNum();
					//System.out.println("** adding key:" + key + "-- ID=" + id + "-- SZ=" + ms);

					// System.out.println("** Key Exists");
					// contractsArr.add(contract);
					contractsArr = map.get(key);
					contractsArr.add(contract);
					map.put(key, contractsArr);

				} else { // no key in map
					contractsArr = new ArrayList<OrdReleased>();
					contractsArr.add(0, contract);
					//System.out.println("** Key Does NOT Exist");
					
					map.put(key, contractsArr);
					
				}
			} else {
				 System.out.println("Contract ID field is null!");
			}
			//BigDecimal b1 = map.get(key).get(0).getNetInvoice();
			//System.out.println("*** NI=" + b1 + "--");
		}		
		  return(map);	
	}
		
	/****************************************************************************************************************************************************/
	public static void  displayMapArr(HashMap<String, List<OrdReleased>> mp)  {
		//int sz = mp.size();
		//String key = "";
		//int i = 0;
		//int ms = 0;
 	    
	    // using for-each loop for iteration over Map.entrySet() 
        for (Map.Entry<String,List<OrdReleased>> entry : mp.entrySet())   {
            //System.out.println("Key = " + entry.getKey() +  ", Value = " + entry.getValue());       
            int aSz = entry.getValue().size();
           // System.out.println("Key = " + entry.getKey() +  ", Value = " + aSz);
            for( int k = 0; k <aSz; k++) {
            	String id = entry.getValue().get(k).getAgreementNum();
            	BigDecimal netInvoice = entry.getValue().get(k).getNetInvoice();
            	//if (k > 0) {
               	 	//System.out.println("k=" + k + "-- Key = " + entry.getKey() +  "-- ID=" + id + "-- NI="  + netInvoice);
            	//}   	
            }
     
        } 
		    
	}
	/****************************************************************************************************************************************************/
	public static BigDecimal sumData(HashMap<String, List<OrdReleased>> mp,  boolean src)  {
		 BigDecimal total = new BigDecimal(0);

	    // using for-each loop for iteration over Map.entrySet() 
        for (Map.Entry<String,List<OrdReleased>> entry : mp.entrySet())   {     
            int aSz = entry.getValue().size();
           // System.out.println("Key = " + entry.getKey() +  ", Value = " + aSz);
            for( int k = 0; k <aSz; k++) {
            	if (src == true) {
            		srcRecs++;
            		contractHmap.put(entry.getKey(), false);
            	} else {
            		tgtRecs++;
            		contractHmap.replace(entry.getKey(), true);
            	}
            	
            	//String id = entry.getValue().get(k).getAgreementNum();
            	BigDecimal netInvoice = entry.getValue().get(k).getNetInvoice();
            	total = total.add(netInvoice);  	
            }
        } 
		
        return(total);
	}

	
	/****************************************************************************************************************************************************/

	
	public void getMissingData()  {
		Iterator it = contractHmap.entrySet().iterator();
		
		String key = "";
		BigDecimal srcValue = new BigDecimal(0);
		BigDecimal total = new BigDecimal(0);
		BigDecimal newTotal = new BigDecimal(0);
		DecimalFormat df = new DecimalFormat("$###,##0.00");
		System.out.println("*** cMapSZ=" + contractHmap.size() );
		 while (it.hasNext()) { // Initialize compare hMap
			 Map.Entry pair = (Map.Entry)it.next();
			 key = (String) pair.getKey();
			 if ((boolean) pair.getValue() == false ) {
				 System.out.println("*** Contract: " + key + " is missing in Qlik." );
				 
				 
			 }
			 
		/*	 
			 if ((boolean) pair.getValue() == false ) {
				 if (src.get(key) != null) {
					 srcValue = src.get(key);
					 total = total.add(srcValue);
					 String val = df.format(srcValue);
					 System.out.println(tag + key + " = " + pair.getValue() + "-- srcValue="  + val );  	 
				 }	 
			 }	
	*/
		 }
		 
		 /*
	 
		 		String totalVal = Olyutil.decimalfmt(total, "$###,##0.00");
		 		String sTot = Olyutil.decimalfmt(srcTot, "$###,##0.00");
		 		String tTot = Olyutil.decimalfmt(tgtTot, "$###,##0.00");
		 
		 newTotal = tgtTot.add(total);
		 
		 String newTotalVal = Olyutil.decimalfmt(newTotal, "$###,##0.00");

		 
		 System.out.println("\nTotal of missing data:" + totalVal);
		 
		 System.out.println("Total from Source:" + sTot);
		 System.out.println("Total from target:" + tTot);
 
		 System.out.println("\nTotal of Target data plus missing data:" + newTotalVal);
		 
		 */
	}	
	/****************************************************************************************************************************************************/

	/****************************************************************************************************************************************************/
	public void getMissingData2()  {
		
	}
	
	/****************************************************************************************************************************************************/

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String, List<OrdReleased>> mapArrR = new HashMap<String, List<OrdReleased>>();
		HashMap<String, List<OrdReleased>> mapArrQ = new HashMap<String, List<OrdReleased>>();

		List<OrdReleased>  list = null;
		BigDecimal totalRest = new BigDecimal(0);
		BigDecimal totalQlik = new BigDecimal(0);
		String logFileName = "orValidate.log";
		String directoryName = "D:/Kettle/logfiles/orValidate";
	 
		PrintWriter out = response.getWriter();
 
		String sep = ",";
		String csvFile = "C:\\Java_Dev\\props\\qlik\\or\\or_rest.csv";
		String qcsvFile = "C:\\Java_Dev\\props\\qlik\\or\\or_qlik.csv";
		//DecimalFormat df = new DecimalFormat("$###,##0.00");
		
		ArrayList<String> strArrR = new ArrayList<String>();
		ArrayList<String> strArrQ = new ArrayList<String>();
		strArrR = Olyutil.readInputFile(csvFile);
		strArrQ = Olyutil.readInputFile(qcsvFile);
		
		mapArrR =  parseData(strArrR, mapArrR,  sep);
		mapArrQ =  parseData(strArrQ, mapArrQ,  sep);
	 
		//int sz = mapArrR.size();
		//System.out.println("*** mapArrR size:" + sz);
		//displayMapArr(mapArrR);
		totalRest = sumData(mapArrR, true);
 
		 
		//displayMapArr(mapArrQ);
		totalQlik = sumData(mapArrQ, false);
		 
	 
		String fpRest = Olyutil.decimalfmt(totalRest, "$###,##0.00");
		String fpQlik = Olyutil.decimalfmt(totalQlik, "$###,##0.00");
		
		System.out.println("*** Source Data Total (Orders Released Rest) --> NetInvoice  = " + fpRest + " --> Records processed = " + srcRecs);
		System.out.println("*** Target Data Total (Orders Released Qlik) --> NetInvoice  = " + fpQlik + " --> Records processed = " + tgtRecs);
		srcRecs = 0;
		tgtRecs = 0;
		//getMissingData();
		
	}
		
}

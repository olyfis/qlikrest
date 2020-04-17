package com.olympus.qlik.validate.ordreleased;

import java.util.ArrayList;

import com.olympus.olyutil.Olyutil;

public class GetHeader {

	public static void main(String[] args) {
		ArrayList<String> strArr = new ArrayList<String>();

		String headerFile = "C:\\tmp\\hdr.csv";
		strArr = Olyutil.readInputFile(headerFile);
		
		for (String str : strArr) { // iterating ArrayList
			//System.out.println("**** Str=" + str);
			String[] items = str.split(",");
			
			for (String s : items) {
			 
			 
				System.out.println(s);
			 
			}
			
		}

	}

}

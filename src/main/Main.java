package main;

import service.SpreadsheetOperateService;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SpreadsheetOperateService operate = new SpreadsheetOperateService(); 
			
		operate.classifySpreadsheets();
		
	}
}
 
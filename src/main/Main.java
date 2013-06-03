package main;

import service.SpreadsheetOperateService;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SpreadsheetOperateService operate = new SpreadsheetOperateService();
		//List<CategoryPOJO> lstSpreadsheetCategory = operate.classifySpreadsheets(); 
			
		operate.classifySpreadsheets();
		
	}
}

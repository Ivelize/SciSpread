package service;

import java.util.ArrayList;
import java.util.HashMap;

import pojo.ConfigurationPOJO;
import pojo.SpreadsheetPOJO;
import builder.CategorizationSystemBuilder;
import builder.ConfigurationBuilder;
import builder.SpreadsheetProcessing;
import cerg.ddex.openspreadsheet.director.XLSReader;

public class SpreadsheetLoadService {
	
	
	public HashMap<String, String> createCategorization(String directoryPath){
		XLSReader sReader = new XLSReader(directoryPath);
		CategorizationSystemBuilder catBuilder = new CategorizationSystemBuilder();
		sReader.build(catBuilder);
	    return catBuilder.getResult();
	}
	
	public HashMap<String, String> createConfiguration(String directoryPath){
		
		XLSReader sReader = new XLSReader(directoryPath);
		ConfigurationBuilder sbuilder = new ConfigurationBuilder();
		sReader.build(sbuilder);
	    return sbuilder.getResult();
    
	}
	
	public ArrayList<ConfigurationPOJO> getLstConfiguration(String directoryPath){
		
		XLSReader sReader = new XLSReader(directoryPath);
		ConfigurationBuilder sbuilder = new ConfigurationBuilder();
		sReader.build(sbuilder);
	    return sbuilder.getListConfigurationPOJO();
    
	}
	
	public ArrayList<SpreadsheetPOJO> compileSpreadsheets(String directoryPath, String spreadsheetName){
		
		SpreadsheetProcessing spreadsheetBuilder = new SpreadsheetProcessing();
		if (spreadsheetName.endsWith("xls")){
			try {
            	XLSReader reader = new XLSReader(directoryPath);
				reader.build(spreadsheetBuilder);
			} catch (Exception e) {
				System.out.println("erro");
			}
		}
		
		return spreadsheetBuilder.getResult();
	}

}

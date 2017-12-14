package service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
				System.out.println("Erro XLS: " + e);
			}
		}else if (spreadsheetName.endsWith("csv")){
			
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(directoryPath));
					
				String[] listCSVSchema = reader.readLine().trim().split(",");
				ArrayList<SpreadsheetPOJO> spreadLst = new ArrayList<SpreadsheetPOJO>();
				
				for (int i = 0; i < listCSVSchema.length; i++) {
					SpreadsheetPOJO spreadContent = new SpreadsheetPOJO();
					spreadContent.setCell(i);
					spreadContent.setContent(listCSVSchema[i].replaceAll("[^a-zA-Z]", "").toLowerCase().trim());
					spreadContent.setPage(0);
					spreadContent.setRow(0);
					spreadLst.add(spreadContent);
				}
			
				return spreadLst;

			} catch (Exception e) {
				System.out.println("Erro CSV: " + e);
			}
		}
		
		return spreadsheetBuilder.getResult();
	}

}

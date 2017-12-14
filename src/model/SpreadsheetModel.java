package model;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class SpreadsheetModel {
	
	/*Load the properties*/
	public void loadConfigurationSpreadsheet(Integer rowNumber, Integer celNumber, String celContent){
		/*Load parameters of the categorization*/
		Map<String, String> categorizationSystem = new HashMap<String, String>();
		/*Load attributes of the categorization*/
		// Map<String, String> categorizationAttribute = new HashMap<String, String>();
		Hashtable<String, String> categorizationAttribute = new Hashtable<String,String>();
		
		
		
	}
	
	public Boolean createSpreadsheetSchema(String tableName, List<String> atributtes) throws Exception{
		
		Boolean result = false;
	
		Class.forName("com.mysql.jdbc.Driver"); 
    	Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/sci_spread", "root", "root");
       
    	Statement st = (Statement) conn.createStatement();
    	
    	StringBuffer createTable = new StringBuffer();
    	createTable.append("CREATE TABLE ");
    	createTable.append(tableName.replaceAll(" ", "").replaceAll("_", "").trim() + "(");
    	for (int i = 0; i < atributtes.size(); i++) {
    		
    		if(atributtes.get(i).equalsIgnoreCase("date")){
    			atributtes.set(i, "eventdate");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("order")){
    			atributtes.set(i, "orderr");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("group")){
    			atributtes.set(i, "groupp");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("references")){
    			atributtes.set(i, "referencess");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("range")){
    			atributtes.set(i, "rangee");	
    		}
    		
    		if((i+1) != atributtes.size()){
    			createTable.append(atributtes.get(i).replaceAll(" ", "").replaceAll("_", "").trim() + " VARCHAR(50), ");
    		}else{
    			createTable.append(atributtes.get(i).replaceAll(" ", "").replaceAll("_", "").trim()  + " VARCHAR(50));");
    		}
		} 
    	
    	result = st.execute(createTable.toString());
    	
    	st.close();
    	conn.close();

		return result;
	}
	
	public Boolean insertSpreadsheetSchema(String spreadsheetName, List<String> atributtes) throws Exception{
		
		Boolean result = false;
	
		Class.forName("com.mysql.jdbc.Driver"); 
    	Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/biodiversity_spreadsheets", "root", "root");
       
    	Statement st = (Statement) conn.createStatement();
    	
    	String insertTable = new String();
    	insertTable = ("INSERT INTO spreadsheets_schema "
    			+ "(id, spreadsheet_name, column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12) "
    			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    	
    	PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(insertTable);
    	preparedStatement.setString(1, UUID.randomUUID().toString());
    	preparedStatement.setString(2, spreadsheetName);
    	
    	for (int i = 3; i <=14; i++) {
    		
    		preparedStatement.setString(i, atributtes.get(i));
    	
    	}
    	preparedStatement.executeUpdate(); 
    	
    	st.close();
    	conn.close();

		return result;
	}
	
	public Boolean insertSpreadsheetExploratoryQuestions(String spreadsheetName, List<String> atributtes) throws Exception{
		
		Boolean result = false;
	
		Class.forName("com.mysql.jdbc.Driver"); 
    	Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/biodiversity_spreadsheets", "root", "root");
       
    	Statement st = (Statement) conn.createStatement();
    	
    	String insertTable = new String();
    	insertTable = ("INSERT INTO spreadsheets_exploratory_questions "
    			+ "(id, spreadsheet_name, what, who, how, why, where, when) "
    			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    	
    	PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(insertTable);
    	preparedStatement.setString(1, UUID.randomUUID().toString());
    	preparedStatement.setString(2, spreadsheetName);
    	
    	for (int i = 3; i <=8; i++) {
    		
    		preparedStatement.setString(i, atributtes.get(i));
    	
    	}
    	preparedStatement.executeUpdate(); 
    	
    	st.close();
    	conn.close();

		return result;
	}
	
	public Boolean insertSpreadsheetSystemClassification(String spreadsheetName, String category_type) throws Exception{
		
		Boolean result = false;
	
		Class.forName("com.mysql.jdbc.Driver"); 
    	Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/biodiversity_spreadsheets", "root", "root");
       
    	Statement st = (Statement) conn.createStatement();
    	
    	String insertTable = new String();
    	insertTable = ("INSERT INTO spreadsheets_system_classification "
    			+ "(id, spreadsheet_name, system_classification) "
    			+ "VALUES (?, ?, ?)");
    	
    	PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(insertTable);
    	preparedStatement.setString(1, UUID.randomUUID().toString());
    	preparedStatement.setString(2, spreadsheetName);
    	preparedStatement.setString(3, category_type);

    	preparedStatement.executeUpdate(); 
    	
    	st.close();
    	conn.close();

		return result;
	}


}

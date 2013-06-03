package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Virtuoso {

	public void insert(String spreadsheetName, String spreadsheetType) {

		try // A captura de exce��es SQLException em Java � obrigat�ria para
		// usarmos JDBC.
		{
			// Este � um dos meios para registrar um driver
			Class.forName("virtuoso.jdbc4.Driver");

			// Registrado o driver, vamos estabelecer uma conex�o
			Connection conn = DriverManager.getConnection(
					"jdbc:virtuoso://amana:1111", "ivelize", "12345ive");
			
			ExecuteQuery (conn, "sparql INSERT in graph <http://www.lis.ic.unicamp.br/~ivelize> {<http://www.lis.ic.unicamp.br/~ivelize/spreadsheets/" + spreadsheetName + "> rdf:type <http://reliant.teknowledge.com/DAML/SUMO.owl#" + spreadsheetType + ">.}");

			

		} catch (Exception e) {
			// se houve algum erro, uma exce��o � gerada para informar o erro
			e.printStackTrace(); // vejamos que erro foi gerado e quem o gerou
		}
	}

	public static void ExecuteQuery(Connection conn, String query)
			throws Exception {
		ResultSetMetaData meta;
		Statement stmt;
		ResultSet result;
		int count;

		System.out.println("EXECUTE: " + query);

		stmt = conn.createStatement();
		result = stmt.executeQuery(query);

		meta = result.getMetaData();
		count = meta.getColumnCount();
		for (int c = 1; c <= count; c++)
			PrintField(meta.getColumnName(c));
		System.out.println("\n--------------");

		while (result.next()) {
			for (int c = 1; c <= count; c++)
				PrintField(result.getString(c));
			System.out.println("");
		}
		stmt.close();
		System.out.println("");
	}

	public static void PrintField(String name) {
		if (name == null)
			name = "NULL";
		System.out.print(name + " ");
	}
}

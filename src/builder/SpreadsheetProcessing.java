package builder;

import java.util.ArrayList;

import pojo.SpreadsheetPOJO;

public class SpreadsheetProcessing extends AbstractBuilder {
	
	ArrayList<SpreadsheetPOJO> spreadLst = new ArrayList<SpreadsheetPOJO>();
	
	public void foundCellContentAsString(int page, int row, int cell, String content) {
		//System.out.println("celula: " + celula + ", linha: " + linha + ", conteudo: " + conteudo);	
		
		SpreadsheetPOJO spreadContent = new SpreadsheetPOJO();
		spreadContent.setCell(cell);
		spreadContent.setRow(row);
		spreadContent.setPage(page);
		spreadContent.setContent(content.toLowerCase().trim());
		
		spreadLst.add(spreadContent);

	}
	
	public ArrayList<SpreadsheetPOJO> getResult()
	{
		return spreadLst;
	}
		
}

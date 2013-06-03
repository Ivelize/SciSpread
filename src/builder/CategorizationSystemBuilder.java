package builder;

import java.util.ArrayList;
import java.util.HashMap;

public class CategorizationSystemBuilder extends AbstractBuilder{
	
	ArrayList<String> category = new ArrayList<String>();
	HashMap<String, String> categorizationSystem = new HashMap<String,String>();
	
	public void foundCellContentAsString(int page, int row, int cell, String content) {
		//System.out.println("lido: " + pagina + ", " + linha + ", " + celula + ", " + conteudo);
		
		if (row == 0)
			category.add(content);
		else
			categorizationSystem.put(content, category.get(cell));
		//System.out.println("category.get(celula): "+category.get(celula));
	}
	
	public HashMap<String, String> getResult()
	{
		return categorizationSystem;
	}

}

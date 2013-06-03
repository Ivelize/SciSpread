package builder;

import java.util.ArrayList;
import java.util.HashMap;

import pojo.ConfigurationPOJO;

public class ConfigurationBuilder extends AbstractBuilder {

	ArrayList<String> configurationSystem = new ArrayList<String>();
	HashMap<String, String> configurationAttribute = new HashMap<String,String>();
	ArrayList<ConfigurationPOJO> configurationLst = new ArrayList<ConfigurationPOJO>();
	
	public void foundCellContentAsString(int page, int row, int cell, String content) {
		//System.out.println("lido conf: " + pagina + ", " + linha + ", " + celula + ", " + conteudo.toString());
		
		String splitContentWeight[] = content.toLowerCase().trim().split(",");
		
		if (row == 0)
			configurationSystem.add(content);
		else{
			configurationAttribute.put(splitContentWeight[0], configurationSystem.get(cell));
			ConfigurationPOJO confContent = new ConfigurationPOJO();
			confContent.setConfigurationType(configurationSystem.get(cell));
			if (splitContentWeight.length > 1){
				confContent.setContent(splitContentWeight[0]);
				confContent.setWeight(splitContentWeight[1]);
			}
			configurationLst.add(confContent);
		}
		
	}
	
	public HashMap<String, String> getResult()
	{
		return configurationAttribute;
	}
	
	public ArrayList<ConfigurationPOJO> getListConfigurationPOJO()
	{
		return configurationLst;
	}
	
}

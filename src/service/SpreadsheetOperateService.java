package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pojo.CategoryPOJO;
import pojo.ConfigurationPOJO;
import pojo.SpreadsheetPOJO;
import constant.Constant;

public class SpreadsheetOperateService {

	SpreadsheetPOJO spread;
	// numero de palavras configuradas dentro do esquema
	Integer termsSchema = 0;
	Integer rowSchema = -1;
	Map<String, Integer> count = new HashMap<String, Integer>();
	List<CategoryPOJO> lstSpreadsheetCategory = new ArrayList<CategoryPOJO>();
	
	public List<CategoryPOJO> classifySpreadsheets() {

		PrintWriter logFile = null;
		try {
			logFile = new PrintWriter(new FileOutputStream("log.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}


		/**
		 * Load system configuration files
		 */
		int recognized = 0;
		int notRecognized = 0;
		ArrayList<String> fields = new ArrayList<String>();
		SpreadsheetLoadService file = new SpreadsheetLoadService();
		Map<String, String> categorizationSpreadsheet = file.createCategorization(Constant.DIRECTORY_PATH_CATEGORIZATION);
		Map<String, String> configurationSpreadsheet = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION);
		List<ConfigurationPOJO> lstSpreadsheetConfiguration = file.getLstConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION);
		File spreadsheetFiles[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS);
		spreadsheetFiles = spreadsheetsDirectory.listFiles();

		Scanner kbinput = new Scanner(System.in);
		System.out.print("Number of spreadsheets: ");
		int spreadNumber = Integer.parseInt(kbinput.nextLine());
		spreadNumber = (spreadNumber > 0 && spreadNumber < spreadsheetFiles.length) ? spreadNumber : spreadsheetFiles.length;

		// processa as planilhas
		for (int k = 0; k < spreadNumber; k++) {
		
			
			String fileName = spreadsheetFiles[k].getName();
			// contem tudo o que o DDex conseguiu ler
			List<SpreadsheetPOJO> spread = file.compileSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());

			CategoryPOJO spreadCategory = new CategoryPOJO();
			// seta o nome da planilha
			spreadCategory.setSpreadsheetName(fileName);

			Float percScheme = 0.0F;
						
			// numero de celulas dentro do esquema
			Integer countNumberCellSchema = 0;
			
			

			// percorre todos os elementos da planilha(linha)
			for (int i = 0; i < spread.size(); i++) {

 				SpreadsheetPOJO currentSpreadsheet = populateSpreadsheetParameter(spread, i);
 				
 				// percorre se ainda nao achou o esquema ou somente no esquema encontrado
				if ((rowSchema == -1 || rowSchema == currentSpreadsheet.getRow() || termsSchema == 1) && currentSpreadsheet.getPage() == 0) {

					// armazena o numero da celula para no final saber qtas campos tem na linha, adiciona 1 pois a celula inicia no zero
					countNumberCellSchema = currentSpreadsheet.getCell() + 1;

					
					//achei um conteudo da planilha que corresponde as palavras chaves da planilha de configuracao e por sua vez se enquadram na categorizacao
					if (categorizationSpreadsheet.containsKey(configurationSpreadsheet.get(currentSpreadsheet.getContent()))) {
						
						//popula a configuração corrente
						ConfigurationPOJO currentConfiguration = populateConfigurationParameter(lstSpreadsheetConfiguration, currentSpreadsheet);
											
						// imprime o schema e conta o numero de palavras chaves encontradas neste esquema
						countWordsSchema(currentSpreadsheet, configurationSpreadsheet, fields);
						
						//atribui peso ao campo corrente
						Integer amount = assignWeightToField(currentSpreadsheet, currentConfiguration);

						//realiza a categorizacao
						makeAccountSpreadsheetCategory(categorizationSpreadsheet, configurationSpreadsheet, currentSpreadsheet, currentConfiguration, amount);

					}
				} else {
					// se ja percorreu o esquema pula para a proxima planilha
					break;
				}
			}

			if (countNumberCellSchema != 0 && termsSchema > 1){
				percScheme = (float) ((termsSchema * 100) / countNumberCellSchema);
			}
			spreadCategory.setPercScheme(percScheme);

			getResultCategorization(spreadCategory);
			
			// se contiver somente 1 palavra chave no esquema, muito
	        // provavelmente nao e esquema ou a planilha nao eh tao relevante
	        if ((termsSchema > 1)) {

	            for (String f : fields) {
	                
	                logFile.println(spreadCategory.getSpreadsheetName() + "," + spreadCategory.getCategoryType() + "," + f + "," + countNumberCellSchema);
	            }
	            
	            try {
	                //copia a planilha reconhecida para outro diretorio
	                copyFile(new File(Constant.DIRECTORY_PATH_SPREADSHEETS + spreadCategory.getSpreadsheetName()), new File(Constant.DIRECTORY_PATH_SPREADSHEETS_RECOGNIZED + spreadCategory.getSpreadsheetName()) );
	            } catch (IOException e) {
	                
	                e.printStackTrace();
	            }
	            recognized++;

	        } else {
	            
	            notRecognized++;

	        }

		}

		System.out.println("==========");
		System.out.println("Number of spreadsheets: " + spreadNumber);
		System.out.println("Recognized: " + recognized + ", " + recognized
				* 100 / spreadNumber + "%");
		System.out.println("Not Recognized: " + notRecognized + ", "
				+ notRecognized * 100 / spreadNumber + "%");
		System.out.println("==========");

		logFile.println("==========");
		logFile.println("Number of spreadsheets: " + spreadNumber);
		logFile.println("Recognized: " + recognized + ", " + recognized * 100
				/ spreadNumber + "%");
		logFile.println("Not Recognized: " + notRecognized + ", "
				+ notRecognized * 100 / spreadNumber + "%");
		logFile.println("==========");

		logFile.close();

		return lstSpreadsheetCategory;

	}

	private ConfigurationPOJO populateConfigurationParameter(List<ConfigurationPOJO> lstSpreadsheetConfiguration, SpreadsheetPOJO currentSpreadsheet) {
		ConfigurationPOJO currentConfiguration = new ConfigurationPOJO();
		for (ConfigurationPOJO configurationPOJO : lstSpreadsheetConfiguration) {
			 if( configurationPOJO.getContent().equals(currentSpreadsheet.getContent()) ){
				 currentConfiguration.setContent(configurationPOJO.getContent());
				 currentConfiguration.setWeight(configurationPOJO.getWeight());
				 currentConfiguration.setConfigurationType(configurationPOJO.getConfigurationType());
			 }
		}
		return currentConfiguration;
	}

	private void getResultCategorization(CategoryPOJO spreadCategory) {
		// verifica a maior tipo de ocorrencia e diz o tipo de categoria correspondente
		Object[] keyCount = count.keySet().toArray();
		for (int j = 0; j < keyCount.length; j++) {

			// adiciona 1 na linha, pois o DDex comeca com zero e a planilha com 1
			spreadCategory.setRowScheme(rowSchema + 1);

			// keycont armazena as categorias e os pontos recebidos pelas mesmas de acordo com a ordem em que os 
			// campos aparecerem e o numero de ocorrencias. se tiver somente uma categoria sera ela a armazenada 
			if (keyCount.length == 1) {
				
				spreadCategory.setOccurrenceNumber(count.get(keyCount[j].toString()));
				spreadCategory.setCategoryType(keyCount[j].toString());
				lstSpreadsheetCategory.add(spreadCategory);
				
				// senao, teremos que verificar qual categoria teve mais pontos
			} else if ((j + 1) < keyCount.length) {
				
				// verifica entre duas categorias qual teve mais pontos
				if ((Integer) count.get(keyCount[j]) > (Integer) count.get(keyCount[j + 1])) {
					
					spreadCategory.setOccurrenceNumber(count.get(keyCount[j].toString()));
					spreadCategory.setCategoryType(keyCount[j].toString());
					lstSpreadsheetCategory.add(spreadCategory);
					
				} else {
					spreadCategory.setOccurrenceNumber(count.get(keyCount[j + 1].toString()));
					spreadCategory.setCategoryType(keyCount[j + 1].toString());
					lstSpreadsheetCategory.add(spreadCategory);
				}
			}
		}
	}

	private Integer assignWeightToField(SpreadsheetPOJO currentSpreadsheet, ConfigurationPOJO currentConfiguration){
		Integer amount = 0;
		// atribui pesos de acordo com a localizacao do conteudo na planilha
		if (currentSpreadsheet.getCell() <= Constant.CELL_NUMBER_SIGNIFICANT) {
			amount = Constant.PRIORITY_FIELD_WEIGHT + Integer.parseInt(currentConfiguration.getWeight());
		} else {
			amount = Integer.parseInt(currentConfiguration.getWeight());
		}
		
		return amount;
	}
	
	private void makeAccountSpreadsheetCategory(Map<String, String> categorizationSpreadsheet, Map<String, String> configurationSpreadsheet, 
												SpreadsheetPOJO currentSpreadsheet, ConfigurationPOJO currentConfiguration, Integer amount) {
		
		// HashMap count armazena a quantidade de registros que aparecem em cada planilha.
		// Se ja exirtir um registro referente a classificacao contida no hashmap count ele adiciona + 1
		if (count.containsKey(categorizationSpreadsheet.get(configurationSpreadsheet.get(currentSpreadsheet.getContent())))) {

			count.put(categorizationSpreadsheet.get(configurationSpreadsheet.get(currentSpreadsheet.getContent())), 
					count.get(categorizationSpreadsheet.get(configurationSpreadsheet.get(currentSpreadsheet.getContent()))) + amount);

		// senao ele cria a nova classificacao e associa ao valor inicial
		} else {

			count.put(categorizationSpreadsheet.get(configurationSpreadsheet.get(currentSpreadsheet.getContent())), amount);

		}
	}
	
	private SpreadsheetPOJO populateSpreadsheetParameter(List<SpreadsheetPOJO> spread, int i) {
		SpreadsheetPOJO currentSpreadsheet = new SpreadsheetPOJO();
		currentSpreadsheet.setName(spread.get(i).getName());
		currentSpreadsheet.setContent(spread.get(i).getContent());
		currentSpreadsheet.setCell(spread.get(i).getCell());
		currentSpreadsheet.setRow(spread.get(i).getRow());
		currentSpreadsheet.setPage(spread.get(i).getPage());
		return currentSpreadsheet;
	}
	
	
   private void countWordsSchema(SpreadsheetPOJO currentSpreadsheet, Map<String, String> configurationSpreadsheet, ArrayList<String> fields){
	    
	   // verifica resposta anterior
		String wordBefore = new String();
	   
	   if (rowSchema == -1) {
			
			rowSchema = currentSpreadsheet.getRow();
			termsSchema++;
			
			if("".equals(wordBefore) || !configurationSpreadsheet.get(currentSpreadsheet.getContent()).equals(wordBefore)){
				
				fields.add(configurationSpreadsheet.get(currentSpreadsheet.getContent()) + "," + currentSpreadsheet.getCell() + ",");
				wordBefore = configurationSpreadsheet.get(currentSpreadsheet.getContent());
			}
			
		} else if (rowSchema == currentSpreadsheet.getRow()) {
			termsSchema++;
			
			if("".equals(wordBefore) || !configurationSpreadsheet.get(currentSpreadsheet.getContent()).equals(wordBefore)){
				
				fields.add(configurationSpreadsheet.get(currentSpreadsheet.getContent()) + "," + currentSpreadsheet.getCell() + ",");
				wordBefore = configurationSpreadsheet.get(currentSpreadsheet.getContent());
			}
			
		} else if (rowSchema != currentSpreadsheet.getRow() && termsSchema == 1) {
			rowSchema = currentSpreadsheet.getRow();
			// nao adiciona countNumberWordsScheme, pois ele ja tem o valor 1
			fields.clear();
			fields.add(configurationSpreadsheet.get(currentSpreadsheet.getContent()) + "," + currentSpreadsheet.getCell() + ",");
		}
	   
   }
   
   public static void copyFile(File source, File destination) throws IOException {
       if (destination.exists())
           destination.delete();

       FileChannel sourceChannel = null;
       FileChannel destinationChannel = null;

       try {
           sourceChannel = new FileInputStream(source).getChannel();
           destinationChannel = new FileOutputStream(destination).getChannel();
           sourceChannel.transferTo(0, sourceChannel.size(),
                   destinationChannel);
       } finally {
           if (sourceChannel != null && sourceChannel.isOpen())
               sourceChannel.close();
           if (destinationChannel != null && destinationChannel.isOpen())
               destinationChannel.close();
      }
  }

}

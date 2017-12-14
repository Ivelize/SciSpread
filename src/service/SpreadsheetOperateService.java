package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pojo.CategoryPOJO;
import pojo.SpreadsheetPOJO;
import constant.Constant;

public class SpreadsheetOperateService {

	SpreadsheetPOJO spread;
	// numero de palavras configuradas dentro do esquema
	Integer rowSchema = -1;
	Float termsSchema = 0.0f;
	Float termsSchemaFinal = 0.0f;
	Map<String, Float> count = new HashMap<String, Float>();
	List<CategoryPOJO> lstSpreadsheetCategory = new ArrayList<CategoryPOJO>();
	Map<String, Float> termsDWC = new HashMap<String, Float>();
	String spreadsheetPurpose = new String();
	Map<String, Float> exploratoryQuestionsQuantityAux = new HashMap<String, Float>();
	int lengthOfNGram = 3;
	Float percScheme = 0.0f;
	
	
	@SuppressWarnings("resource")
	public List<CategoryPOJO> classifySpreadsheets() {

		PrintWriter logFile = null;
		PrintWriter logFile3 = null;
		PrintWriter logFile4 = null;
		try {
			logFile = new PrintWriter(new FileOutputStream("log5.csv"));
			logFile3 = new PrintWriter(new FileOutputStream("log3.csv"));
			logFile4 = new PrintWriter(new FileOutputStream("log4.csv"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		/**
		 * Load system configuration files
		 */
		int recognized = 0;
		int notRecognized = 0;
		List<String> lstAttributes = new ArrayList<String>();
		SpreadsheetLoadService file = new SpreadsheetLoadService();
		Map<String, Integer> wordsFrequency = new HashMap<String, Integer>();
		Map<String, String> wordsFrequencyPerCategory = new HashMap<String, String>();
		//Map<String, String> listDictionaryTerms = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_TUDO);
		Map<String, String> listDictionaryTerms = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_GENETIC);
		//Map<String, String> listDictionaryTerms = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_SPMONIT);
		//Map<String, String> listDictionaryTerms = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_EXPEROBS);
		//Map<String, String> listDictionaryTerms = file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_TAXON_OCCURRENCE);
		/*Map<Map<String, String>, String> configurationSpreadsheet = new HashMap<Map<String,String>, String>();
		configurationSpreadsheet.put(file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_EXPEROBS), "expObservation");
		configurationSpreadsheet.put(file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_SPMONIT), "spMonitoring");
		configurationSpreadsheet.put(file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_GENETIC), "geneData");
		configurationSpreadsheet.put(file.createConfiguration(Constant.DIRECTORY_PATH_CONFIGURATION_TAXON_OCCURRENCE), "taxon");*/
		File spreadsheetFiles[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS);
		spreadsheetFiles = spreadsheetsDirectory.listFiles();
		Integer countNumberCellSchema = 0;
		
		
		

		/*Scanner kbinput = new Scanner(System.in);
		System.out.print("Number of spreadsheets: ");
		int spreadNumber = Integer.parseInt(kbinput.nextLine());
		spreadNumber = (spreadNumber > 0 && spreadNumber < spreadsheetFiles.length) ? spreadNumber : spreadsheetFiles.length;*/

		// processa as planilhas
		for (int k = 0; k < spreadsheetFiles.length; k++) {
			
			Map<Map<String, Float>, String> spreadsheetValuesPerPurpose = new HashMap<Map<String, Float>, String>();
			Map<String, Float> perSchemaPerPurpose = new HashMap<String, Float>();
			
			String fileName = spreadsheetFiles[k].getName();
			// contem tudo o que o DDex conseguiu ler
			List<SpreadsheetPOJO> spread = file.compileSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());
			
			System.out.println(fileName);

			//for (Map<String, String> listDictionaryTerms : configurationSpreadsheet.keySet()) {				
			// numero de celulas dentro do esquema
			countNumberCellSchema = 0;
			Map<Float, String> fields = new HashMap<Float, String>();
			
			termsSchema = 0.0f;
			lstAttributes.clear();
			rowSchema = -1;
			Integer rowAux = 0;
			exploratoryQuestionsQuantityAux = new HashMap<String, Float>();
			//termsDWC.clear();
			populateExploratoryQuestions();
			//populateDWCTerms();

			
				
				// percorre todos os elementos da planilha(linha)
				for (int i = 0; i < spread.size(); i++) {
	
	 				SpreadsheetPOJO currentSpreadsheet = populateSpreadsheetParameter(spread, i);
	 				currentSpreadsheet.setName(fileName);
	 				
	 				// percorre se ainda nao achou o esquema ou somente no esquema encontrado
					if ((rowSchema == -1 || rowSchema == currentSpreadsheet.getRow() || termsSchema == 1) && currentSpreadsheet.getPage() == 0) {
						//armazena o numero da celula para no final saber qtas campos tem na linha, adiciona 1 pois a celula inicia no zero
						countNumberCellSchema = currentSpreadsheet.getCell() + 1;
						
						if(rowAux != currentSpreadsheet.getRow()){
							lstAttributes.clear();
						}
						// lista dos elementos do esquema da planilha pra gravar no MySQL
						lstAttributes.add(currentSpreadsheet.getContent().replaceAll("[^a-zA-Z0-9]", ""));
						
							for (String terms : listDictionaryTerms.keySet()) {
								float percMatch = match(terms, currentSpreadsheet.getContent().replaceAll("[^a-zA-Z0-9]", ""));
								if(percMatch >= 0.3f){
									String exploratoryQuestion = listDictionaryTerms.get(terms);
									countWordsSchema(currentSpreadsheet, exploratoryQuestion, fields);
									break;
								}
							}
							
																
					}else {
							// se ja percorreu o esquema pula para a proxima planilha
							break;
					}
					rowAux = currentSpreadsheet.getRow();
				}
				
				//spreadsheetValuesPerPurpose.put(exploratoryQuestionsQuantityAux, configurationSpreadsheet.get(listDictionaryTerms));
				if (countNumberCellSchema != 0){
					percScheme = termsSchema/countNumberCellSchema;
				}else{
					percScheme = 0.0f;
				}
				
				/*System.out.println(configurationSpreadsheet.get(listDictionaryTerms) + ": " + percScheme);
				perSchemaPerPurpose.put(configurationSpreadsheet.get(listDictionaryTerms), percScheme);	*/
				
			//}
			
			
			/*Map<String, Float> exploratoryQuestionsQuantity = new HashMap<String, Float>();
    	  	Float highestSpreadsheetValue = 0.0f;
	        for (Map<String, Float> valuesPerExploratoryQuestion : spreadsheetValuesPerPurpose.keySet()) {
	        	Float spreadsheetAmount = 0.0f;
	        	
	        	if(spreadsheetValuesPerPurpose.get(valuesPerExploratoryQuestion).equals("taxon")){
		        	for (String questions : valuesPerExploratoryQuestion.keySet()) {
		        		spreadsheetAmount = spreadsheetAmount + valuesPerExploratoryQuestion.get(questions);
		        		exploratoryQuestionsQuantity.put(questions, valuesPerExploratoryQuestion.get(questions));
					}

	        	}else{
	        		for (Float value : valuesPerExploratoryQuestion.values()) {
		        		spreadsheetAmount = spreadsheetAmount + value;
					}
	        	}
	        	
	        	if(spreadsheetAmount > highestSpreadsheetValue){
	    			  highestSpreadsheetValue = spreadsheetAmount;
	    			  spreadsheetPurpose = spreadsheetValuesPerPurpose.get(valuesPerExploratoryQuestion);
	    		}
	        	
			}*/
			
			//atribui peso ao campo corrente
			//Float amount = assignWeightToField(currentSpreadsheet, currentConfiguration);

			//realiza a categorizacao
			//makeAccountSpreadsheetCategory(categorizationSpreadsheet, configurationSpreadsheet, currentSpreadsheet, currentConfiguration, amount);
			
			
			//spreadCategory.setPercScheme(percScheme);

			//getResultCategorization(spreadCategory);
			
			/*for (String attribute : lstAttributes) {
			
				if(wordsFrequency.containsKey(attribute)){
					Integer aux = wordsFrequency.get(attribute);
					wordsFrequency.put(attribute, aux+1);
				
				}else{
					
					wordsFrequency.put(attribute, 1);
					
					if (listDictionaryTerms.containsKey(attribute)) {
						wordsFrequencyPerCategory.put(attribute, listDictionaryTerms.get(attribute));
					}else{
						wordsFrequencyPerCategory.put(attribute, "ND");
					}
				}
			}*/
			
			//Float percSchemePurpose = perSchemaPerPurpose.get(spreadsheetPurpose);
			// se contiver somente 1 palavra chave no esquema, muito
	        // provavelmente nao e esquema ou a planilha nao eh tao relevante  
	     // if (percSchemePurpose != null && percSchemePurpose > 0.2 && countNumberCellSchema > 3) {
		
		if (percScheme >= 0.3 && countNumberCellSchema > 3) {
	        	
	        	/*SpreadsheetModel spreadModel = new SpreadsheetModel();
	        	try {
	        		String[] name = spreadsheetFiles[k].getName().split("[.]");
					spreadModel.insertSpreadsheetSchema(name[0].replaceAll("[^a-zA-Z0-9]", "_"), lstAttributes);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}*/
	        	

	    	 /* float numColumnsFatorial = 1.0f;
	    		for (int j = 1; j <= countNumberCellSchema; j++) {
	    			numColumnsFatorial =  numColumnsFatorial * j;
	    		}*/
	        	
	    	     	  
		        	
	
	        	//String categoryType = new String();
	        	
	        	/*
	        	if ((termsDWC.get("taxon") + termsDWC.get("organism") + termsDWC.get("identification")) > 
				(termsDWC.get("occurrence") + termsDWC.get("event") + termsDWC.get("location"))){
		
	        		categoryType = "Taxon";
		
	        	}else if ((termsDWC.get("taxon") + termsDWC.get("organism") + termsDWC.get("identification")) <
				(termsDWC.get("occurrence") + termsDWC.get("event") + termsDWC.get("location"))){
		
	        		categoryType = "Occurrence";
		
	        	}else{
		
	        		categoryType = "ND";
		
	        	}*/
	        	
		        spreadsheetPurpose = "geneData";
	    	//if(spreadsheetPurpose.equals("taxon")){
		    	 /* if ((exploratoryQuestionsQuantityAux.get("what") + exploratoryQuestionsQuantityAux.get("who")) > 
		        				(exploratoryQuestionsQuantityAux.get("where") + exploratoryQuestionsQuantityAux.get("when"))){
		        		
		    		  spreadsheetPurpose = "taxonIdentification";*/

		        	//}else 
		        		
		        	/*	if ((exploratoryQuestionsQuantityAux.get("what") + exploratoryQuestionsQuantityAux.get("who")) < 
		        				(exploratoryQuestionsQuantityAux.get("where") + exploratoryQuestionsQuantityAux.get("when"))){
		        		
		        		spreadsheetPurpose = "spOccurrence";
		        		
		        		DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
		        	    decimalSymbols.setDecimalSeparator('.');
		        		NumberFormat formatter = new DecimalFormat("0.00", decimalSymbols);
		        		String[] name = spreadsheetFiles[k].getName().split("[.]");
			        	
			        	logFile.println(formatter.format(exploratoryQuestionsQuantityAux.get("what")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("who")) + "," + 
			        			formatter.format(exploratoryQuestionsQuantityAux.get("how")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("why")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("where")) + "," 
			        							+ formatter.format(exploratoryQuestionsQuantityAux.get("when")) + "," + spreadsheetPurpose);
			        	
			        	try {
		 	                //copia a planilha reconhecida para outro diretorio
		 	                copyFile(new File(Constant.DIRECTORY_PATH_SPREADSHEETS + spreadsheetFiles[k].getName()), new File(Constant.DIRECTORY_PATH_SPREADSHEETS_RECOGNIZED + spreadsheetFiles[k].getName()) );
		 	            } catch (IOException e) {
		 	                
		 	                e.printStackTrace();
		 	            }
			        	
			        	System.out.println(name[0].replaceAll("[^a-zA-Z0-9]", "") + " = " + percScheme);
			        	recognized++;

		        	}*/
	//    	  }

	        	//categoryType = "spMonitoring";
	        	DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
        	    decimalSymbols.setDecimalSeparator('.');
        		NumberFormat formatter = new DecimalFormat("0.00", decimalSymbols);
        		String[] name = spreadsheetFiles[k].getName().split("[.]");
	        	
	        	logFile.println(formatter.format(exploratoryQuestionsQuantityAux.get("what")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("who")) + "," + 
	        			formatter.format(exploratoryQuestionsQuantityAux.get("how")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("why")) + "," + formatter.format(exploratoryQuestionsQuantityAux.get("where")) + "," 
	        							+ formatter.format(exploratoryQuestionsQuantityAux.get("when")) + "," + spreadsheetPurpose);
	        	
	        	/*logFile.println(termsDWC.get("taxon") + "," + termsDWC.get("organism") + "," + termsDWC.get("identification") + "," 
						+ termsDWC.get("occurrence") + "," + termsDWC.get("event") + "," + termsDWC.get("location") + "," + categoryType);*/
	        	
	        	/*try {
 	                //copia a planilha reconhecida para outro diretorio
 	                copyFile(new File(Constant.DIRECTORY_PATH_SPREADSHEETS + spreadsheetFiles[k].getName()), new File(Constant.DIRECTORY_PATH_SPREADSHEETS_RECOGNIZED + spreadsheetFiles[k].getName()) );
 	            } catch (IOException e) {
 	                
 	                e.printStackTrace();
 	            }*/
	        	
	        	
	        	/*try {
	        		logFile4.print(name[0].replaceAll("[^a-zA-Z0-9]", "_") + ",");
	        		for (int i = 0; i < lstAttributes.size(); i++) {
	        			if(i+1 < lstAttributes.size())
	        				logFile3.println(name[0].replaceAll("[^a-zA-Z0-9]", "_") + "," + lstAttributes.get(i) + "," + lstAttributes.get(i+1));
	        			
	        			if(i+1 < lstAttributes.size())
	        				logFile4.print(lstAttributes.get(i) + ",");
	        			else
	        				logFile4.print(lstAttributes.get(i));
	        		}
	        		logFile4.println("");
					//spreadModel.createSpreadsheetSchema(name[0].replaceAll("[^a-zA-Z0-9]", ""), lstAttributes);
					System.out.println(name[0].replaceAll("[^a-zA-Z0-9]", "") + " = " + percScheme);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
	            
	           
	            
	           recognized++;

	       } else {  
	    	   
	    	/*try {
	                //copia a planilha reconhecida para outro diretorio
	                copyFile(new File(Constant.DIRECTORY_PATH_SPREADSHEETS + spreadsheetFiles[k].getName()), new File(Constant.DIRECTORY_PATH_SPREADSHEETS_NOTRECOGNIZED + spreadsheetFiles[k].getName()) );
	            } catch (IOException e) {
	                
	                e.printStackTrace();
	            }*/
	           
	           notRecognized++;

	       } 

		}
		
		PrintWriter logFile2 = null;
		try {
			logFile2 = new PrintWriter(new FileOutputStream("log2.csv"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		for(String key : wordsFrequency.keySet()){
			logFile2.println( wordsFrequencyPerCategory.get(key) + "," +key + "," + wordsFrequency.get(key));
		}

		System.out.println("==========");
		System.out.println("Number of spreadsheets: " + spreadsheetFiles.length);
		System.out.println("Recognized: " + recognized + ", " + recognized
				* 100 / spreadsheetFiles.length + "%");
		System.out.println("Not Recognized: " + notRecognized + ", "
				+ notRecognized * 100 / spreadsheetFiles.length + "%");
		System.out.println("==========");

		logFile.close();
		logFile2.close();
		logFile3.close();
		logFile4.close();
		
		return lstSpreadsheetCategory;

	}

	
	private void populateExploratoryQuestions() {
		exploratoryQuestionsQuantityAux.put("what", 0.0f);
		exploratoryQuestionsQuantityAux.put("who", 0.0f);
		exploratoryQuestionsQuantityAux.put("when", 0.0f);
		exploratoryQuestionsQuantityAux.put("where", 0.0f);
		exploratoryQuestionsQuantityAux.put("how", 0.0f);
		exploratoryQuestionsQuantityAux.put("why", 0.0f);
	}
	
	/*private void populateDWCTerms() {
		termsDWC.put("taxon", 0.0f);
		termsDWC.put("occurrence", 0.0f);
		termsDWC.put("location", 0.0f);
		termsDWC.put("organism", 0.0f);
		termsDWC.put("identification", 0.0f);
		termsDWC.put("event", 0.0f);
	}*/
	
	
		
	private SpreadsheetPOJO populateSpreadsheetParameter(List<SpreadsheetPOJO> spread, int i) {
		SpreadsheetPOJO currentSpreadsheet = new SpreadsheetPOJO();
		currentSpreadsheet.setContent(spread.get(i).getContent());
		currentSpreadsheet.setCell(spread.get(i).getCell());
		currentSpreadsheet.setRow(spread.get(i).getRow());
		if(spread.get(i).getPage() != null){
			currentSpreadsheet.setPage(spread.get(i).getPage());
		}else{
			currentSpreadsheet.setPage(0);
		}
		return currentSpreadsheet;
	}
	
	
      
  private void countWordsSchema(SpreadsheetPOJO currentSpreadsheet, String exploratoryQuestion, Map<Float, String> fields){
	    
	   // verifica resposta anterior
		String wordBefore = new String();
		
		float amount = 1.00f;
		//float exponencial = (float) Math.pow((float)(currentSpreadsheet.getCell()+1), (float)2);
		//amount = 1f/exponencial;
	   
	   if (rowSchema == -1) {
			
			rowSchema = currentSpreadsheet.getRow();
			termsSchema++;
			
			if("".equals(wordBefore) || !exploratoryQuestion.equals(wordBefore)){
				
				fields.put(amount, exploratoryQuestion);
				wordBefore = exploratoryQuestion;
    			exploratoryQuestionsQuantityAux.put(exploratoryQuestion, amount);

			}
			
		} else if (rowSchema == currentSpreadsheet.getRow()) {
			termsSchema++;
			
			if("".equals(wordBefore) || !exploratoryQuestion.equals(wordBefore)){
				
				fields.put(amount, exploratoryQuestion);
				wordBefore = exploratoryQuestion;
				float beforeValue = exploratoryQuestionsQuantityAux.get(exploratoryQuestion);
    			exploratoryQuestionsQuantityAux.put(exploratoryQuestion, (beforeValue + amount));
			}
			
		} else if (rowSchema != currentSpreadsheet.getRow() && termsSchema == 1) {
			rowSchema = currentSpreadsheet.getRow();
			// nao adiciona countNumberWordsScheme, pois ele ja tem o valor 1
			fields.clear();
			populateExploratoryQuestions();
			fields.put(amount, exploratoryQuestion);
			exploratoryQuestionsQuantityAux.put(exploratoryQuestion, amount);

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
           if (sourceChannel != null && sourceChannel.isOpen())
               sourceChannel.close();
           if (destinationChannel != null && destinationChannel.isOpen())
               destinationChannel.close();           
           source.delete();
       } finally {
           if (sourceChannel != null && sourceChannel.isOpen())
               sourceChannel.close();
           if (destinationChannel != null && destinationChannel.isOpen())
               destinationChannel.close();
      }
  }
  
   /**
	 * This methods returns the nGrams generated for that String. 
	 * @param str
	 * @return an ArrayList that holds the nGrams for the input String
	 */
	protected ArrayList<String> generateNGrams(String str) {
		//logger.debug("str: " + str);
		if ((str == null) || (str.length() == 0))
			return null;
		ArrayList<String> grams = new ArrayList<String>();
		int length = str.length();
		//logger.debug("length: " + length);

		if (length < this.lengthOfNGram) {
			//logger.debug("length < this.lengthOfNGram");
			for (int i = 1; i <= length; ++i) {
				String gram = str.substring(0, length);
				//logger.debug("gram: " + gram);
				if (grams.indexOf(gram) == -1)
					grams.add(gram);
			}//end for
		} else {
			//logger.debug("length >= this.lengthOfNGram");
			for (int i = 0; i < str.length() - lengthOfNGram + 1; i++) {
				String gram = str.substring(i, i + lengthOfNGram);
				//logger.debug("gram: " + gram);
				if (grams.indexOf(gram) == -1)
					grams.add(gram);
			}//end for
		}
		return grams;
	}//end generateNGrams()

	protected int countNumberOfEqualNGrams(ArrayList<String> grams1, ArrayList<String> grams2) {
		int count = 0;
		for (String gram1 : grams1) {
			for (String gram2 : grams2) {
				//logger.debug("gram1: " + gram1);
				//logger.debug("gram2: " + gram2);
				if (gram1.equals(gram2))
					count++;
				//logger.debug("count: " + count);
			}
		}
		return count;
	}//countNumberOfEqualNGrams()
	
	protected float calculateSimilarity(int count, int grams1Size, int grams2Size) {
		float sim = 2.0F * count / (grams1Size + grams2Size);
		return sim;
	}//calculateSimilarity()

	public float match(String string1, String string2) {
		
		if ((string1 == null) || (string2 == null) || (string1.length() == 0) || (string2.length() == 0))
			return 0.0F;

		ArrayList<String> grams1 = generateNGrams(string1.toLowerCase().trim());
		ArrayList<String> grams2 = generateNGrams(string2.toLowerCase().trim());

		/*Count the number of equal NGrams*/
		int count = this.countNumberOfEqualNGrams(grams1, grams2);

		/*Return a similarity (match score)*/
		float sim = this.calculateSimilarity(count, grams1.size(), grams2.size());

		return sim;
	}


}

//Arkadii Redozubov 3187445
package griffth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

//DataManager.java (Handles file read and write operations, manages cache, flashcards, quiz, progress, settings, category, level and auto-filling)
public class DataManager {
	UserInterface inter = Main.inter;
	ApiService serv = new ApiService();
	
// LOAD DATA, Reads the entire data of the given file, returns it as a string
	public String loadData(String fileName) throws IOException {
		File file = new File(fileName);
		StringBuilder builder = null;
//		Uses BufferedReader and StringBuilder to read all the data from the file
			try(BufferedReader reader = new BufferedReader(new FileReader(file))){
				builder = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
//		Convert and return StringBuilder to String format
		return builder.toString();
		
	}
//	SAVE DATA, saves the full word data to cache.json for offline use
//	Skips saving to the cache.json if the word already exists there
	public void saveData(ArrayList<Word>words, String fileName) throws IOException {
		File file = new File(fileName);
		JSONArray jsonArr = new JSONArray();
//		Get the word name from the first entry once
		String wordFind = words.get(0).getWord();
		for(Word word: words) {
		JSONObject json = new JSONObject();
		json.put("word", word.getWord());
		json.put("phonetic", word.getPhonetic());
//		Meaning array for the entry
		JSONArray meaningsArray = new JSONArray();
		ArrayList<Meaning> meanings = word.getMeanings();
//		Iterate through meanings ArrayList
		for(Meaning meaning: meanings) {
			JSONObject meaningObj = new JSONObject();
			meaningObj.put("partOfSpeech", meaning.getPartOfSpeech());
//			Convert each ArrayList field to a JSONArray
			JSONArray definitionArr = new JSONArray();
			for(String definition: meaning.getDefinition()) {
				definitionArr.put(definition);
			}
			meaningObj.put("definition", definitionArr);
			JSONArray synonymsArr = new JSONArray();
			for(String synonym: meaning.getSynonyms()) {
				synonymsArr.put(synonym);
			}
			meaningObj.put("synonyms", synonymsArr);
			JSONArray antonymsArr = new JSONArray();
			for(String antonym: meaning.getAntonyms()) {
				antonymsArr.put(antonym);
			}
			meaningObj.put("antonyms", antonymsArr);
			JSONArray exampleArr = new JSONArray();
			for(String example: meaning.getExample()) {
				exampleArr.put(example);
			}
			meaningObj.put("examples", exampleArr);
			meaningsArray.put(meaningObj);
		}
		json.put("meanings", meaningsArray);	
		jsonArr.put(json);
		}
//		Duplicate check
		JSONArray jsonArr1;
		boolean found = false;
//		If the file does not exist, create a new file
		if(!file.exists()) {
			jsonArr1 = new JSONArray();	
		}
		else {
//			If the file already exist - check if its empty 
			String data = loadData(fileName);
			if(data.isBlank()) {
				jsonArr1 = new JSONArray();	
			}
			else {
//			Iterate through saved words and check word field of the first object of each word
			jsonArr1 = new JSONArray(data);	
			for(Object ob: jsonArr1) {
				JSONArray arr = (JSONArray)ob;
				JSONObject ob1 = arr.getJSONObject(0);
//				If the word already exists in the cache file - set found to true
					if(ob1.has("word") && ob1.getString("word").equals(wordFind)) {
						found = true;
						break;
					}	
			}
			}
		}
//		if the found is false (not in cache.json yet), write to cache file
		if(!found) {
		jsonArr1.put(jsonArr);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
//			toString(2) for readability 
			writer.write(jsonArr1.toString(2));
		}
		}
	}

//	FLASHCARD SAVE DATA, saves a simplified word entry to the flashCards.json for use in flashcards, quiz, random word
//	Saves only necessary data for flashcards, quiz...
	public void flashCardSaveData(Word word, String fileName) throws IOException {
		File file = new File(fileName);
		String wordFind = null;
//		JSONObject with fields needed for flashcards
		JSONObject json = new JSONObject();
		json.put("word", word.getWord());
//		Used for duplicate check 
		wordFind = word.getWord(); 
		json.put("phonetic", word.getPhonetic());
		ArrayList<Meaning> meanings = word.getMeanings();
//		Uses the first definition and part of speech from the first meaning entry 
		json.put("definition", meanings.get(0).getDefinition().get(0));
		json.put("partOfSpeech", meanings.get(0).getPartOfSpeech());
//		Search for non-empty example, break when found
		String example = null;
		for(Meaning meaning: meanings) {
			ArrayList<String>examples = meaning.getExample();
			for(String ex: examples) {
				if(!(ex.isEmpty())){
					example = ex;
					break;
				}
			}
		}
//		If no example found in any meaning, put no example
		json.put("example", example != null ? example: "no example");
		
//		Duplicate check
		JSONArray jsonArr1;
		boolean found = false;
		if(!file.exists()) {
			jsonArr1 = new JSONArray();	
		}
		else {
			String data = loadData(fileName);
			if(data.isBlank()) {
				jsonArr1 = new JSONArray();	
			}
			else {
			jsonArr1 = new JSONArray(data);	
			for(Object ob: jsonArr1) {
				JSONObject ob1 = (JSONObject)ob;
//				Check if the name of the word in the object equals to the word name we are searching for
					if(ob1.getString("word").equals(wordFind)) {
						found = true;
						break;
					}	
			}
			}
		}
//		If the word is not already in the flashcards file, add it
		if(!found) {
		jsonArr1.put(json);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			writer.write(jsonArr1.toString(2));
		}
		}	
	}
	
//	SAVE PROGRESS, saves the results from taken flashcards and quiz to the corresponding progress files (quizProgress, flashCardsProgress)
//	Updates the streak of progress based on the difference between and last competed quiz and flashcard
	public void saveProgress(String fileName, int known, int total, double percentage) throws IOException {
		File file = new File(fileName);
		int tryNumber = 0;
		JSONArray jsonArr;
		JSONArray jsonArrData;
		JSONObject jsonObjDate = null;
//		Get the todays today to compare it with the last attempt data
		LocalDate today = LocalDate.now();
		if(file.exists()) {
//			Load the data from the file
			String data = loadData(fileName);
			jsonArr = new JSONArray(data);
//			Holds the array of past attempts
			jsonArrData = jsonArr.getJSONArray(1);
//			Holds the streak and the date of the last attempt
			jsonObjDate = jsonArr.getJSONObject(0);
//			Get and parse the last stored attempt date to a default LocalDate format for comparison
			LocalDate lastLogin = LocalDate.parse(jsonObjDate.getString("lastLogin"));
//			STREAK, check if the difference between the last saved date and today is 1
			if(lastLogin.equals(today.minusDays(1))){
//				Update the streak by adding a 1
				jsonObjDate.put("streak", (jsonObjDate.getInt("streak"))+1);
//				Update the last login date
				jsonObjDate.put("lastLogin", today.toString());
			}
//			Check if the last saved date if today, keep unchanged
			else if(lastLogin.equals(today)) {
				jsonObjDate = jsonArr.getJSONObject(0);
			}
//			One or more days are missed, the difference is more than 1, reset the string to 1
			else {
				jsonObjDate.put("streak", 1);
				jsonObjDate.put("lastLogin", today.toString());
			}
//			Get the try number of the last saved attempt to increment it
			tryNumber = (jsonArrData.getJSONObject(jsonArrData.length()-1)).getInt("try");
		}
//		First time saving progress, start with fresh array, streak, date
		else {
			jsonArr = new JSONArray();
			jsonArrData = new JSONArray();
			jsonObjDate = new JSONObject();
			jsonObjDate.put("streak", 1);
			jsonObjDate.put("lastLogin", today.toString());
		}
//		Build the attempt record 
		JSONObject obj = new JSONObject();		
		obj.put("try", tryNumber+1);
		obj.put("correct", known);
		obj.put("total", total);
		obj.put("percentage", percentage);
		jsonArrData.put(obj);
		jsonArr.put(0, jsonObjDate);
		jsonArr.put(1, jsonArrData);
//		Write the changes to the progress file
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			writer.write(jsonArr.toString(2));
		}
	}
	
//	SAVE SETTINGS, Saves user preferences to settings.json, if the file is blank or does not exists - fill it with default values
	public void saveSettings(String fileName, String theme, String difficulty, String style, String count, String detail) throws IOException {
		    File file = new File(fileName);
		    JSONArray arr;
//		    Fill the settings.json with default values 
		    if(!file.exists() || loadData(fileName).isBlank()) {
		    	arr = new JSONArray();
				JSONObject obj = new JSONObject();
				obj.put("theme", theme);
				obj.put("difficulty", difficulty);
				obj.put("style", style);
				obj.put("count", count);
				obj.put("detail", detail);
				arr.put(obj);
		    }
//		    If it exists and not blank - load and update settings fields
		    else {
		    	String data = loadData(fileName);
				arr = new JSONArray(data);
				for(int i = 0; i< arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
//					Update each field
					if(obj.has("theme")) {
						obj.put("theme", theme);
						arr.put(i, obj);
					}
					if(obj.has("difficulty")) {
						obj.put("difficulty", difficulty);
						arr.put(i, obj);
					}
					if(obj.has("style")) {
						obj.put("style", style);
						arr.put(i, obj);
					}
					if(obj.has("count")) {
						obj.put("count", count);
						arr.put(i, obj);
					}
					if(obj.has("detail")) {
						obj.put("detail", detail);
						arr.put(i, obj);
					}
				}
		    }
//		    Write the settings to the settings.json
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			writer.write(arr.toString(2));
		}
	}
//	LOAD SETTINGS, Reads settings.json, if it does not exist - create and fill it with default values
	public String[] loadSettings(String fileName, UserInterface inter, Settings set, String difficulty) throws IOException {
		File file = new File(fileName);
//		Declare an array of settings to pass it to Main class for tracking correct values
		String[] settings = new String[5];
		String theme = null;
		String difficulty1 = null;
		String style = null;
		String count = null;
		String detail = null;
		JSONArray arr = null;
		JSONObject obj;
//		Create with default values
		if(!file.exists() || loadData(fileName).isBlank()) {
			arr = new JSONArray();
			obj = new JSONObject();
			obj.put("theme", "Default");
			obj.put("difficulty", "Easy");
			obj.put("style", "Basic");
			obj.put("count", "Quick");
			obj.put("detail", "Brief");
//			Set variables to put in the array of current values 
			theme = "Default";
			difficulty1 = "Easy";
			style = "Basic";
			count = "Quick";
			detail = "Brief";
			arr.put(obj);
//			Write to the file
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
				writer.write(arr.toString(2));
			}
		}
//		Load settings and apply new ones
		else {
			String data = loadData(fileName);
			arr = new JSONArray(data);
			for(Object obj1: arr) {
				obj = (JSONObject)obj1;
				if(obj.has("theme")) {
					theme = obj.getString("theme");
//					Apply color theme in the UserInterface class
					inter.applyTheme(theme);
				}
				if(obj.has("difficulty")) {
					difficulty1 = obj.getString("difficulty");
					set.setQuizString(difficulty1);
				}
				if(obj.has("style")) {
					style = obj.getString("style");
					set.setFlashCardString(style);
				}
				if(obj.has("count")) {
					count = obj.getString("count");
					set.setFlashCardCountString(count);
				}
				if(obj.has("detail")) {
					detail = obj.getString("detail");
					set.setWordDetailString(detail);
				}
			}	
		}
//		Put the settings values into array, matches currentSettings in Main class
		settings[0] = difficulty1;
		settings[1] = theme;
		settings[2] = style;
		settings[3] = count;
		settings[4] = detail;
		return settings;
	}
//	GROUP DATA, return 2D array of group names (row 0), word list (row1+)
	public String[][] groupData(String browseName) {
//		If the chosen group is Category, return an array filled with category words
    if(browseName.equals("Category")) {
    	String[] categoryNames = {"Animals", "Food & Drink", "Emotions", "Travel", "Technology"};
    	String[] animals = {"predator", "nocturnal", "hibernate", "carnivore", "migrate", "amphibian", "domesticate", "camouflage", "scavenger", "omnivore"};
    	String[] foodDrink = {"bitter", "savory", "cuisine", "appetite", "ferment", "garnish", "simmer", "indulge", "recipe", "beverage"};
    	String[] emotions = {"melancholy", "euphoria", "anxious", "content", "frustrated", "serene", "envious", "nostalgic", "optimistic", "empathy"};
    	String[] travel = {"itinerary", "passport", "luggage", "navigate", "destination", "departure", "accommodation", "expedition", "souvenir", "customs"};
    	String[] technology = {"algorithm", "bandwidth", "encrypt", "interface", "protocol", "database", "binary", "network", "software", "digital"};
//        Index 0 - names, 1-5 - word arrays
    	String[][] categoryData = {categoryNames, animals, foodDrink, emotions, travel, technology};
        return categoryData;
    }
//    Otherwise return an array filled with words for different English levels
    else {
    	String[] levelNames = {"Elementary", "Pre-Intermediate", "Intermediate", "Upper-Intermediate", "Advanced", "Proficient"};
    	String[] elementary = {"happy", "friend", "water", "family", "food", "angry", "house", "sleep", "learn", "smile"};
    	String[] preIntermediate = {"travel", "weather", "market", "simple", "garden", "useful", "manage", "polite", "reason", "choice"};
    	String[] intermediate = {"achieve", "benefit", "consider", "develop", "evaluate", "improve", "obtain", "provide", "require", "suggest"};
    	String[] upperIntermediate = {"analyse", "concept", "deduce", "emerge", "fluctuate", "imply", "negotiate", "perceive", "restrict", "sustain"};
    	String[] advanced = {"ambiguous", "contemplate", "elaborate", "fundamental", "inevitable", "meticulous", "pragmatic", "substantial", "vulnerable", "phenomenon"};
    	String[] proficient = {"amalgamate", "circumvent", "disseminate", "ephemeral", "juxtapose", "perfunctory", "recalcitrant", "superfluous", "tenacious", "ubiquitous"};
        String[][] levelData = {levelNames, elementary, preIntermediate, intermediate, upperIntermediate, advanced, proficient};
        return levelData;
    	
    }
   
	}
	
//	FETCH BROWSE WORDS, returns a list of 10 words based on the chosen category, if the file does not exist or blank - fill it with prewritten words in groupData method
//	If the file already exist and not blank - read from it
	public ArrayList<String> fetchBrowseWords(String fileName, String browseName, int categoryIndex) throws IOException{
		ArrayList<String>wordsList = new ArrayList<String>();
		File file = new File(fileName);
		String[][] groupData = groupData(browseName);
		JSONArray arr = null;
		JSONObject group;
		JSONArray words;
		String data;
//		Fill the file with the words from groupData based on the chosen group
		if(!file.exists()) {
			arr = new JSONArray();
			for(int i = 0; i < groupData[0].length; i++) {
				group = new JSONObject();
				words = new JSONArray();
//				Index 0 - group names
				group.put(browseName.toLowerCase(), groupData[0][i]);
				for(int j = 0; j < 10; j++) {
//					Index 1 - words lists
					words.put(groupData[i+1][j]);
				}
				group.put("words", words);
				arr.put(group);	
			}
//			Save generated word lists to file 
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
				writer.write(arr.toString(2));
			}
			}
		else {	
			data = loadData(fileName);
			if(data.isBlank()) {	
				arr = new JSONArray();
				for(int i = 0; i < groupData[0].length; i++) {
					group = new JSONObject();
					words = new JSONArray();
					group.put(browseName.toLowerCase(), groupData[0][i]);
					for(int j = 0; j < 10; j++) {
						words.put(groupData[i+1][j]);
					}
					group.put("words", words);
					arr.put(group);	
				}
				try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
					writer.write(arr.toString(2));
				}
			}
//			File has valid date
			else {
				arr = new JSONArray(data);
			}
		}
//		Extract word list for the requested group index
		group = arr.getJSONObject(categoryIndex);
		words = group.getJSONArray("words");
		for(Object word: words) {
			wordsList.add(word.toString());
		}
		return wordsList;	
	}
//	FETCH RANDOM WORD, returns a randomly selected word in String format from flashCards.json file
//	If the file does not exist or its empty, autoFill it with words in groupData, returns null if autoFill fails
	public String fetchRandomWord() throws IOException {
		Random random = new Random();
		File file = new File("flashCards.json");
		JSONArray arr = null;
		JSONObject obj;
		String data;
		
//		flashCards.json does not exist, attempt to auto fill it, if it fails - return null
		if(!file.exists()) {
			boolean success =  autoFill(random, serv, arr);
			if(!success) return null;
		}
		else {
			data = loadData("flashCards.json");
			if(data.isBlank()) {	
				boolean success =  autoFill(random, serv, arr);
				if(!success) return null;
			}
		}
//		Load the file to pick a random word from it
		data = loadData("flashCards.json");
			
		arr = new JSONArray(data);
//		Pick a random word
		int randomIndex = random.nextInt(arr.length());
		obj = arr.getJSONObject(randomIndex);
		return obj.getString("word");
		}		

//	AUTO FILL, the method fills the flashCards.json with default words when its empty or has not enough words
//	Picks randomly either a category or level group and makes a connection test to spot errors, returns true if successful and false if not
	public boolean autoFill(Random random, ApiService serv, JSONArray jsonArr) throws IOException {
		String randomFile;
		String randomType;
//		Randomly chooses between category and level groups as a source to fill 
		if(random.nextInt(2) == 0) {randomFile = "category.json"; randomType = "Category";}
		else {randomFile = "level.json"; randomType = "Level";}
//		Pick a random group in the chosen file
		ArrayList<String>autoFill = fetchBrowseWords(randomFile, randomType, random.nextInt(5));
//		Get the first word only to test if the API responds normally before fetching the rest
		ArrayList<Word> testWords = serv.fetchWord(autoFill.get(0), false);
		if(testWords ==null) {
			System.out.println(inter.ERROR + "Default words cannot be loaded!" + inter.RESET);
			System.out.println(inter.SECONDARY + "Check your internet connection" + inter.RESET);
			return false;
		}
//		flashCards.json is empty
		if(jsonArr == null) {
		System.out.print(inter.ERROR + "No saved words found!\n" + inter.RESET);
//		Re fetch to get a random group
		autoFill = fetchBrowseWords(randomFile, randomType, random.nextInt(5));
		for(int i = 0; i <autoFill.size(); i++) {
			ArrayList<Word> words = serv.fetchWord(autoFill.get(i), true);
//			Return false if any word fetch fails
			if(words == null) return false;
//			Print Loading to make it visible engaging
			System.out.print(inter.SECONDARY +"\nLoading default words (" +(i+1)+"/10)..."  + inter.RESET);
			flashCardSaveData(words.get(0), "flashCards.json");
		}
		}
//		flashCards.json is not empty, and has some words, fill up to reach 10 in total
		else {
//			Safety check, prevents requesting more words than the group has
			int wordsToFill = Math.min(10 - jsonArr.length(), autoFill.size());
			System.out.print(inter.ERROR + "Not enough saved words!\n" + inter.RESET);
			autoFill = fetchBrowseWords(randomFile, randomType, random.nextInt(5));
			for(int i = 0; i< wordsToFill; i++) {
				ArrayList<Word> words = serv.fetchWord(autoFill.get(i), true);
				if(words == null) return false;
				System.out.print(inter.SECONDARY +"\nLoading default words (" +(jsonArr.length()+i + 1)+"/10)..."  + inter.RESET);
				flashCardSaveData(words.get(0), "flashCards.json");
			}
		}
//		Success message
		System.out.println(inter.SUCCESS + "\nDone!" + inter.RESET);
		return true;
		}



}

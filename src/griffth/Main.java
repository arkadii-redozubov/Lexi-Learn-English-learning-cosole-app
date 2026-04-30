//Arkadii Redozubov 3187445
package griffth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;

//Main.java (Contains the main menu loop, dictionary navigation, helper methods)
public class Main {
//  Declare static instances of the classes used
	public static Settings set = new Settings();
	public static UserInterface inter = new UserInterface();
	public static DataManager manager = new DataManager();
	public static ApiService serv = new ApiService();
	public static Exercises exercise = new Exercises();

//	MAIN method
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
//		Load settings from settings.json into a string array
		String currentSettings[] = new String[5];
		String currentTheme = null, currentDifficulty = null, currentStyle = null, currentCount = null, currentWordDetail = null;
		try {
			currentSettings = manager.loadSettings("settings.json", inter, set, set.getQuizName());
			currentDifficulty = currentSettings[0];
			currentTheme = currentSettings[1];
			currentStyle = currentSettings[2];
			currentCount = currentSettings[3];
			currentWordDetail = currentSettings[4];
			
		} catch (IOException e) {
			e.printStackTrace();
		}

//		Main menu loop, runs until the user wants to exit
		boolean isRunning = true;
//		Counter for the greeting Ascii art
		int runningCount = 0;
		while(isRunning) {
//		Show the greeting ascii art only if the program runs for the first time 
		if(runningCount == 0) {inter.greetingAscii(); runningCount++;};
//		Display the main menu label and options
		inter.showLabel("Main menu");
		inter.showMenu("Main");
//		Get the validated menu choice, call the userChoice method (handles bad inputs) 
		int choice = userChoice(scanner, 1, 6);
//		Check the main menu choice
		switch(choice) {
		case 1 -> {
//			DICTIONARY 
			boolean dictionaryMenu = true;
			while(dictionaryMenu) {
			inter.showLabel("Dictionary");
			inter.showMenu("Dictionary");
			int dicChoice = userChoice(scanner, 1, 5);
			String word;
//			ArrayList of Word objects, returned from the API
			ArrayList<Word> words = null;
//			Check the dictionary menu choice
			switch(dicChoice) {
//			WORD SEARCHING
			case 1 -> {
			inter.showLabel("Word searching");
//			Ask to enter the word until the user enters something
			do {
			System.out.print(inter.SECONDARY + ">> Enter the word: "+ inter.RESET);
			word = scanner.nextLine();
			System.out.println();
			if(word.isBlank()) {
				continue;
			}
			break;
			}while(true);
//			Fetch the word
			try {
//				Prints Searching.. searching while waiting for the response 
				searchingWord(word);
//				Fetch the word data from API, cache if API cannot be reached
				words = serv.fetchWord(word, false);
//				If the word cannot be found - return to the dictionary menu
				if(words==null) {
					continue;
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
//			Call the wordSearch method that prints the words detail options, returns false if the user wants to go back to the main menu
			boolean cotinueRunning = wordSearch(scanner, word, words, "Back to dictionary");
			if(!cotinueRunning) {
				dictionaryMenu = false;
			}
				
		}
//			CATEGORY BROWSING
			case 2 ->{
				boolean categoryOption = true;
				while(categoryOption) {
				inter.showLabel("Browse by Category");
				inter.showMenu("Category");
				int categoryChoice = userChoice(scanner, 1, 7);
				boolean wordOptions = true;
//				Assign the default response options
//				Navigation: backResponse[0] = Back, Main, Continue
//				Return menu: backResponse[1] = Return, Stay
				String[] backResponse = {"Continue", "Stay"};
				while(wordOptions) {
//				Check the category browsing choice
				switch(categoryChoice) {
				case 1 ->{
					inter.showLabel("Animals");
//					Call the categoryWords method that shows the words for the chosen category, returns the backResponse options
					backResponse = categoryWords(scanner, "category.json", "Category", 0, "Back to Animals");	
//					If the back response is not null (the API answered or API failed but the word was found in cache), check if the user wants to return to the menu 
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; categoryOption = false; dictionaryMenu= false;}}
				}
				case 2 ->{
					inter.showLabel("Food & Drink");
					backResponse = categoryWords(scanner, "category.json", "Category", 1, "Back to Food & Drink");	
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; categoryOption = false; dictionaryMenu= false;}}
				}
				case 3 ->{
					inter.showLabel("Emotions");
					backResponse = categoryWords(scanner, "category.json", "Category", 2, "Back to Emotions");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; categoryOption = false; dictionaryMenu= false;}}
				}
				case 4 ->{
					inter.showLabel("Travel");
					backResponse = categoryWords(scanner, "category.json", "Category", 3, "Back to Travel");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; categoryOption = false; dictionaryMenu= false;}}
				}
				case 5 ->{
					inter.showLabel("Technology");
					backResponse = categoryWords(scanner, "category.json", "Category", 4, "Back to Technology");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; categoryOption = false; dictionaryMenu= false;}}
				}
//				Go back to the dictionary menu
				case 6 -> {
					wordOptions = false;
					categoryOption = false;
				}
//				Go back to the main menu 
				case 7 ->{
					wordOptions = false;
					categoryOption = false;
					dictionaryMenu = false;
				}
				}
//				Check navigation signals
				if(backResponse !=null) {
				if(backResponse[0].equals("Back")) {
//					Back to the category options
					wordOptions = false;
				}
				else if(backResponse[0].equals("Main")) {
					wordOptions = false;
					categoryOption = false;
					dictionaryMenu = false;
				}
				}
				}
				}
			}
//			ENGLISH LEVEL BROWSING (Works the same way as category browsing)
			case 3 ->{
				boolean levelOption = true;
				while(levelOption) {
				inter.showLabel("Browse by level");
				inter.showMenu("Level");
				int levelChoice = userChoice(scanner, 1, 8);
				boolean wordOptions = true;
				String[] backResponse = {"Continue", "Stay"};
				while(wordOptions) {
//				Check the english level choice
				switch(levelChoice) {
				case 1 ->{
					inter.showLabel("Elementary (A1)");
					backResponse = categoryWords(scanner, "level.json", "Level", 0, "Back to Elementary");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 2 ->{
					inter.showLabel("Pre-Intermediate (A2)");
					backResponse = categoryWords(scanner, "level.json", "Level", 1, "Back to Pre-Intermediate");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 3 ->{
					inter.showLabel("Intermediate  (B1)");
					backResponse = categoryWords(scanner, "level.json", "Level", 2, "Back to Intermediate");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 4 ->{
					inter.showLabel("Upper-Intermediate  (B2)");
					backResponse = categoryWords(scanner, "level.json", "Level", 3, "Back to Upper-Intermediate");	
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 5 ->{
					inter.showLabel("Advanced  (C1)");
					backResponse = categoryWords(scanner, "level.json", "Level", 4, "Back to Advanced");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 6 -> {
					inter.showLabel("Proficient  (C2)");
					backResponse = categoryWords(scanner,  "level.json", "Level", 5, "Back to Proficient");
					if (backResponse != null) { if(backResponse[1].equals("Return")) {wordOptions = false; levelOption = false; dictionaryMenu= false; }}
				}
				case 7 ->{
					wordOptions = false;
					levelOption = false;
				}
				case 8 ->{
					wordOptions = false;
					levelOption = false;
					dictionaryMenu = false;
				}
				}
				if(backResponse !=null) {
				if(backResponse[0].equals("Back")) {
					wordOptions = false;
				}
				else if(backResponse[0].equals("Main")) {
					wordOptions = false;
					levelOption = false;
					dictionaryMenu = false;
				}
				}
				}
				}
				
			}
//			RANDOM WORD
			case 4 ->{
				inter.showLabel("Random word");
				String randomWord = null;
				try {
//					Calls the fetchRandomWord method that returns the random word from the flashCards file
					randomWord = manager.fetchRandomWord();
					if(randomWord != null) {
//					Print the random word, uses constant color value in the UserInterface class
					System.out.println(inter.SECONDARY + "Random word: " + inter.RESET + randomWord );
					searchingWord(randomWord);
					words = serv.fetchWord(randomWord, false);
//					If API failed and the word is not in cache, go back to the menu
					if(words==null) {
						continue;
					}
					
					boolean cotinueRunning = wordSearch(scanner, randomWord, words, "Back to dictionary" );
					if(!cotinueRunning) {
						dictionaryMenu = false;
					}
					}
			}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
//			Menu return option
			case 5 ->{
				dictionaryMenu = false;
			}
			}
			}
		}

//		FLASHCARDS
		case 2 -> {
			try {
//				flashCards logic in the Exercises class 
				exercise.FlashCards();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		QUIZ
		case 3 -> {
			try {
//				Quiz logic in the Exercises class 
				exercise.Quiz();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		LEARNING PROGRESS
		case 4 -> {
			while(true) {
			inter.showLabel("Learning progress");
			inter.showMenu("Progress");
			int userChoice = userChoice(scanner, 1, 3);
//			Check learning progress choice 
			if(userChoice == 1) {
//				FLASHCARDS PROGRESS
				try {
					exercise.displayProgress("flashCardsProgress.json", "Flash Cards");
					inter.showMenu("BackProgress");
					
					int backChoice = userChoice(scanner,1,2);
					if(backChoice == 2) {
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(userChoice == 2){
//				QUIZ PROGRESS
				try {
					exercise.displayProgress("quizProgress.json", "Quiz");
					inter.showMenu("BackProgress");
					int backChoice = userChoice(scanner,1,2);
					if(backChoice == 2) {
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
//			Return to the main menu
			else {
				break;
			}
			}
			
		}
//		SETTINGS
		case 5 ->{
			boolean isSettings = true;
			while(isSettings) {
			inter.showLabel("Settings");
			inter.showMenu("Settings");
			int settingsChoice = userChoice(scanner, 1, 7);
//			Check settings choice
			switch(settingsChoice) {
//			COLOR THEME, changes primary and secondary colors 
			case 1 ->{
				inter.showLabel("Color theme");
				inter.showMenu("Theme");
//				Show current settings options chosen 
				inter.showCurrentStatus("theme", currentTheme);
				inter.barLabel();
//				Show return options 
				inter.showBackSettingsButtons(5, 6);
				int theme = userChoice(scanner, 1, 6);
				if(theme == 5) {
					break;
				}
				else if(theme == 6) {
					isSettings = false;
				}
				else {
//				Set the color theme in the Settings class 
				set.setTheme(theme);
//				Apply the chosen color theme in the UserInterface class
				inter.applyTheme(set.getThemeName());
//				Get the name of the current theme option to then use it to save in the settings file 
				currentTheme = set.getThemeName();
//				Save all settings in the settings file by calling saveSettings method in the DataManager class
				try {
					manager.saveSettings("settings.json", currentTheme, currentDifficulty, currentStyle, currentCount, currentWordDetail);
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
//			FLASH CARDS STYLE, how much information is shown before revealing the flashcard's definition
			case 2 ->{
				inter.showLabel("Flash cards style");
				inter.showMenu("FlashStyle");
				inter.showCurrentStatus("style", currentStyle);
				inter.barLabel();
				inter.showBackSettingsButtons(4, 5);
				int style = userChoice(scanner, 1,5);
				if(style == 4) {
					break;
				}
				else if(style == 5) {
					isSettings = false;
				}
				else {
				set.setFlashCard(style);
				currentStyle = set.getFlashCardName();
				try {
					manager.saveSettings("settings.json", currentTheme, currentDifficulty, currentStyle, currentCount, currentWordDetail);
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				
			}
//			FLASH CARDS COUNT, how many cards to show  
			case 3 ->{
				inter.showLabel("Flash cards count");
				inter.showMenu("FlashCount");
				inter.showCurrentStatus("count", currentCount);
				inter.barLabel();
				inter.showBackSettingsButtons(4, 5);
				int flashCount = userChoice(scanner, 1, 5);
				if(flashCount == 4) {
					break;
				}
				else if(flashCount == 5) {
					isSettings = false;
				}
				else {
				set.setFlashCardCount(flashCount);
				currentCount = set.getFlashCardCountName();
				try {
					manager.saveSettings("settings.json", currentTheme, currentDifficulty, currentStyle, currentCount, currentWordDetail);
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
//			QUIZ DIFFICULTY, the number of questions and options (more options, questions - harder)
			case 4 ->{
				inter.showLabel("Quiz difficulty");
				inter.showMenu("QuizDifficulty");
				inter.showCurrentStatus("difficulty", currentDifficulty);
				inter.barLabel();
				inter.showBackSettingsButtons(4, 5);
				int difficultyChoice = userChoice(scanner, 1,5);
				if(difficultyChoice == 4) {
					break;
				}
				else if(difficultyChoice == 5) {
					isSettings = false;
				}
				else {
				set.setQuiz(difficultyChoice);
				currentDifficulty = set.getQuizName();
				try {
					manager.saveSettings("settings.json", currentTheme, currentDifficulty, currentStyle, currentCount, currentWordDetail);
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
//			WORD DETAIL, how many API entries to show per word
			case 5 ->{
				inter.showLabel("Word detail");
				inter.showMenu("WordDetail");
				inter.showCurrentStatus("word detail", currentWordDetail);
				inter.barLabel();
				inter.showBackSettingsButtons(4, 5);
				int wordDetailChoice = userChoice(scanner, 1,5);
				if(wordDetailChoice == 4) {
					break;
				}
				else if(wordDetailChoice == 5) {
					isSettings = false;
				}
				else {
					set.setWordDetail(wordDetailChoice);
					currentWordDetail = set.getWordDetailName();
					try {
						manager.saveSettings("settings.json", currentTheme, currentDifficulty, currentStyle, currentCount, currentWordDetail);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			}
//			RESET SETTINGS, deletes the settings.jsons and reloads it with the default values
			case 6 ->{
				try {
//					Create the file object
					File settingsFile = new File("settings.json");
//					Delete the settings file
					settingsFile.delete();
//					Load the settings again - assigns the default values automatically 
					currentSettings = manager.loadSettings("settings.json", inter, set, set.getQuizName());
					currentDifficulty = currentSettings[0];
					currentTheme = currentSettings[1];
					currentStyle = currentSettings[2];
					currentCount = currentSettings[3];
					currentWordDetail = currentSettings[4];
					inter.applyTheme(currentTheme);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(inter.SUCCESS + "\nSettings reset successfully!\n" + inter.RESET);
			
			}
//			Return to the main menu
			case 7 ->{
				isSettings = false;
			}
			}
		}
			
		}
//		Exits the main loop
		case 6 -> {
//			Exit ascii art
			inter.byeAscii();
			isRunning = false;
		}
		}
		}
	}
	
//	WORD SEARCHING, displays word details options
	public static boolean wordSearch(Scanner scanner, String word, ArrayList<Word> words, String returnText) {
		boolean isRunning1 = true;
//		Get the number of API entries to display per word based on the setting
		int entries = set.wordDetail(set.getWordDetailName(), words.size());
//		If the number of the word's entries the user set up is greater than the arr length - make it equal to the arr length
		if(entries > words.size()) {
			if(entries > 1) {
				entries = words.size();
			}
		}
		while(isRunning1) {
			inter.showLabel(word);
			inter.showSearchMenu(returnText);
			int dictinaryChoice = userChoice(scanner, 1, 9);
//			Check the word search choice
			switch(dictinaryChoice) {
//			PHONETIC, shows just the first phonetic, since its shared
			case 1 -> {
				inter.showLabel("Phonetic");
//				Print the words phonetic
				System.out.println(inter.SECONDARY + "Phonetic of \"" + word + "\": "+ inter.RESET + words.get(0).getPhonetic());
			}
//			PART OF SPEECH
			case 2 -> {
				inter.showLabel("Part of speech");
//				Declare an arrayList of part of speeches
				ArrayList<String>partOfSpeech = new ArrayList<String>();
//				Iterate the entries number of times 
				for(int i = 0; i < entries; i++) {
					ArrayList<Meaning> meanings = words.get(i).getMeanings();
//					Iterate through that meaning array 
				for(Meaning meaning: meanings) {
//					Get part of speech
					boolean wordExists = false;
					String partOfSpeechString = meaning.getPartOfSpeech();
//					Check if the part of speech was already printed to not to print it twice
					if(!partOfSpeech.isEmpty()) {
						for(String w: partOfSpeech) {
							if(w.equals(partOfSpeechString)) {
								wordExists = true;
								break;
							}
						}
					}
//					Add the part of speech to the array if it is not already there
					if(!wordExists) {
						partOfSpeech.add(partOfSpeechString);
						System.out.println(inter.SECONDARY + "Part of speech: " + inter.RESET + partOfSpeechString);
					}
				}
				}
			}
//			DEFINITION
			case 3 -> {
				inter.showLabel("Definition");
				int count = 0;
				for(int i = 0; i < entries; i++) {
					ArrayList<Meaning> meanings = words.get(i).getMeanings();
				for(Meaning meaning: meanings) {
//					If the part if speech is not one - add a new line character for better visibility
					if(count != 0) {
						count++;
						System.out.println();
					}
					System.out.println(inter.SECONDARY + "Part of speech: " + inter.RESET + meaning.getPartOfSpeech());
//					Separate the output with the bar
					inter.barLabel();
//					Iterate through definitions and print them 
					for(String l: meaning.getDefinition()) {
						System.out.println(inter.SECONDARY + "Definition: " + inter.RESET +l);
					}
					inter.barLabel();
				}
				}
			}
//			DEFINITION + EXAMPLE
			case 4 -> {
				inter.showLabel("Definition + Example");
				int count = 0;
				for(int i = 0; i < entries; i++) {
					ArrayList<Meaning> meanings = words.get(i).getMeanings();
				for(Meaning meaning: meanings) {
					if(count != 0) {
						System.out.println();
					}
					System.out.println(inter.SECONDARY + "\nPart of speech: " + inter.RESET + meaning.getPartOfSpeech());
					inter.barLabel();
					count++;
//					Iterate through, each definition is pared with the example at the same index
					for(int j =0; j<meaning.getDefinition().size(); j++) {
						System.out.println(inter.SECONDARY + "Definition: " + inter.RESET + meaning.getDefinition().get(j));
						System.out.println(inter.SECONDARY + "Example: " + inter.RESET + meaning.getExample().get(j));
					}
					inter.barLabel();
				}
				}
			}
//			SYNONYMS
			case 5 -> {
				inter.showLabel("Synonyms");
				System.out.print(inter.SECONDARY + "Synonyms: " + inter.RESET);
				int counter = 0;
				for(int i = 0; i < entries; i++) {
				ArrayList<Meaning> meanings = words.get(i).getMeanings();
				ArrayList<String>allSynonyms = new ArrayList<String>();
//				Get synonyms
				for(int j = 0; j< meanings.size(); j++ ) {
					for(int l = 0; l < meanings.get(j).getSynonyms().size(); l++) {
						String syn = meanings.get(j).getSynonyms().get(l).trim();
//						Add to the ArrayList of synonyms only of it has them
						if(!syn.equals("no synonyms")) {
							allSynonyms.add(syn);
						}
					}
				}
//				Only print no synonyms if all entries have no synonyms
				if(allSynonyms.isEmpty()) {
					if(counter<1)
					System.out.print("no synonyms\n");
				}else {
//					Separate the synonyms with the comma, except the last one
					for(int j = 0; j < allSynonyms.size(); j ++) {
						System.out.print(allSynonyms.get(j));
						if((allSynonyms.size()-1) != j) {
							System.out.print(", ");
						}
					}
					System.out.println();
				}
				counter++;
			}
			}
//			ANTONYMS
			case 6 -> {
				inter.showLabel("Antonyms");
				System.out.print(inter.SECONDARY + "\nAntonyms: " + inter.RESET);
				int counter = 0;
				for(int i = 0; i < entries; i++) {
					ArrayList<Meaning> meanings = words.get(i).getMeanings();
				ArrayList<String>allAntonyms = new ArrayList<String>();
				for(int j = 0; j< meanings.size(); j++ ) {
					for(int l = 0; l < meanings.get(j).getAntonyms().size(); l++) {
						String syn = meanings.get(j).getAntonyms().get(l).trim();
						if(!syn.equals("no antonyms")) {
							allAntonyms.add(syn);
						}
					}
				}
				if(allAntonyms.isEmpty()) {
					if(counter<1)
					System.out.print("no antonyms\n");
				}else {
					for(int j = 0; j < allAntonyms.size(); j ++) {
						System.out.print(allAntonyms.get(j));
						if((allAntonyms.size()-1) != j) {
							System.out.print(", ");
						}
					}
					System.out.println();
				}
				counter++;
			}
				inter.barLabel();
			}
//			ALL INFORMATION
			case 7 -> {
				inter.showLabel("All Information");
				int counter = 0;
//				Iterate through the chosen number of entries
				for(int i = 0; i < entries; i++) {
					ArrayList<Meaning> meanings = words.get(i).getMeanings();
//					Print phonetic only if its the first iteration
					if(counter == 0) {
				System.out.println(inter.SECONDARY + "Phonetic of the " + "\"" +  word  + "\": " + inter.RESET + words.get(i).getPhonetic());
				inter.barLabel();
					}
				for(Meaning meaning: meanings) {
//					Get part of speech
					System.out.println(inter.SECONDARY + "\nPart of speech: " + inter.RESET + meaning.getPartOfSpeech());
					inter.barLabel();
//					Iterate and print definitions and examples
					for(int j =0; j<meaning.getDefinition().size(); j++) {
						System.out.println(inter.SECONDARY + "Definition: " + inter.RESET + meaning.getDefinition().get(j));
						System.out.println(inter.SECONDARY + "Example: " + inter.RESET + meaning.getExample().get(j));
					}
					System.out.print(inter.SECONDARY + "\nSynonyms: " + inter.RESET);
//					Iterate and print synonyms
					for(int j = 0; j < meaning.getSynonyms().size(); j++) {
//						Put the comma after the word except the last one, uses ternary operator 
						System.out.print(meaning.getSynonyms().get(j) + ((j != (meaning.getSynonyms().size())-1) ? ", " : ""));
					}
					System.out.println();
					System.out.print(inter.SECONDARY + "\nAntonyms: " + inter.RESET);
//					Iterate and print antonyms
					for(int j = 0; j < meaning.getAntonyms().size(); j++) {
						System.out.print(meaning.getAntonyms().get(j) + ((j != (meaning.getAntonyms().size())-1) ? ", " : ""));
					}
					System.out.println();
					inter.barLabel();
				}
				counter++;
			}
			}
//			Return to the previous category + saves the searched word to the cache and flashCards files
			case 8 ->{
				try {
//					Saves full word data
					manager.saveData(words, "cache.json");
//					Save the simplified version of the entry for flashcards, quiz...
					manager.flashCardSaveData(words.get(0), "flashCards.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isRunning1 = false;
			}
//			Return to the main menu + save the searched word into the cache and flashCards files
			case 9 -> {
				try {
					manager.saveData(words, "cache.json");
					manager.flashCardSaveData(words.get(0), "flashCards.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
			
			
			}
			
			}
		return true;
	}

//	CATEGORY WORDS, displays words for the given category or english level, returns navigation and menu signals as an array (null - API is unreachable)
	public static String[] categoryWords(Scanner scanner, String fileName, String browseName, int categoryIndex, String returnText) {
		ArrayList<String>wordsList = null;
		ArrayList<Word>words = null;
		String[] menuReturnOptions = new String[2];
//		Load the word list from the chosen category
		try {
			wordsList = manager.fetchBrowseWords(fileName, browseName, categoryIndex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Convert ArrayList to the String array
		String[] wordsArr = wordsList.toArray(new String[0]);
		inter.showBrowseChoices(wordsArr);
//		Print return buttons
		inter.showBackCategoryButtons();
		int wordChoice = userChoice(scanner, 1, 12);
		boolean menuReturn = true;
//		Check if the user wants to return
		if(wordChoice == 11) menuReturnOptions[0] = "Back"; 
		else if(wordChoice == 12) {menuReturnOptions[0] = "Main";}
		else {
		menuReturnOptions[0] = "Continue";
//		Get the chosen word
		String word = wordsArr[wordChoice-1];
//		Fetch the chosen word, return null if it cannot be fetched
		try {
			 System.out.println();
			 searchingWord(word);
			 words = serv.fetchWord(word, false);
//			 Return null if API failed and the word is not in cache
			 if(words == null) return null;
			 menuReturn = wordSearch(scanner,word, words, returnText);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		}
//		Return options (the user wants to stay, go back, or return to the main menu) 
		if(!menuReturn) menuReturnOptions[1] = "Return";
		else menuReturnOptions[1] = "Stay";
		return menuReturnOptions;
	}

//	USER CHOICE, Input validation for the integer menu value, inclusively checks between the mix and max values
	public static int userChoice(Scanner scanner, int min, int max) {
		int choiceInt;
//		Ask for the input until the number between min and max entered 
		while(true) {
			System.out.print("> ");
			String choiceString = scanner.nextLine().trim();
//			Regex input validation, checks if the input is digit (at least one or more digits)
			if((choiceString.matches("^[\\d]+$"))) {
				choiceInt = Integer.parseInt(choiceString);
//				If the input passes the decimal validation but is beyond the boundaries, start over again
				if(!(choiceInt >= min && choiceInt <= max)) {
					continue;
				}
				break;
			}
		}
		return choiceInt;	
	}
	
//	SEARCH ANIMATION, Search word imitation
	public static void searchingWord(String word) {
		System.out.print(inter.SECONDARY + "Searching for \"" + word + "\"" + inter.RESET);
//		Prints the dot three times with a 600 ml interval, calls the method sleep
		for(int i = 0; i< 3; i++) {
			System.out.print(inter.SECONDARY + "." + inter.RESET);
			if(i<2) {
			sleep(600);
			}
		}
		System.out.println();	
	}
//	waits the given number of ml
	public static void sleep(int milSeconds) {
		try {
			Thread.sleep(milSeconds);
		}catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	

	
}

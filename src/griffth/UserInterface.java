//Arkadii Redozubov 3187445
package griffth;

//UserInterface.java (Contains all UI elements: menu options, labels, colors)
public class UserInterface {
//	ANSI colors for color theme, PRIMARY and SECONDARY change based on the settings
	public String PRIMARY = "\u001B[0m";
	public String SECONDARY = "\u001B[0m";
	public String SUCCESS = "\u001B[32m";
	public String ERROR = "\u001B[31m";
	public String BOLD = "\u001B[1m";
	public String RESET = "\u001B[0m";
	
//	APPLY THEME, change the PRIMARY and SECONDARY colors based on the chosen color theme name
	public void applyTheme(String theme) {
		switch(theme) {
		case "Ocean" ->{
			PRIMARY = "\u001B[34m";
			SECONDARY = "\u001B[36m";
		}
		case "Forest" ->{
			PRIMARY = "\u001B[32m";   
			SECONDARY = "\u001B[33m";
		}
		case "Sunset" ->{
			PRIMARY = "\u001B[31m";   
			SECONDARY = "\u001B[33m";
		}
		case "Default" ->{
			PRIMARY = "\u001B[0m";   
			SECONDARY = "\u001B[0m";
		}
		}
	}
//	GREETING ASCII, used on the first launch only
	public void greetingAscii() {
		System.out.println(PRIMARY + " ____      ____      __                                       _              \n"
				+ "|_  _|    |_  _|    [  |                                     / |_            \n"
				+ "  \\ \\  /\\  / /.---.  | |  .---.   .--.   _ .--..--.  .---.  `| |-' .--.      \n"
				+ "   \\ \\/  \\/ // /__\\\\ | | / /'`\\]/ .'`\\ \\[ `.-. .-. |/ /__\\\\  | | / .'`\\ \\    \n"
				+ "    \\  /\\  / | \\__., | | | \\__. | \\__. | | | | | | || \\__.,  | |,| \\__. |    \n"
				+ "     \\/  \\/   '.__.'[___]'.___.' '.__.' [___||__||__]'.__.'  \\__/ '.__.'     \n"
				+ SECONDARY +"  _____                     _     _____                                    _ \n"
				+ " |_   _|                   (_)   |_   _|                                  | |\n"
				+ "   | |      .---.  _   __  __      | |      .---.  ,--.   _ .--.  _ .--.  | |\n"
				+ "   | |   _ / /__\\\\[ \\ [  ][  |     | |   _ / /__\\\\`'_\\ : [ `/'`\\][ `.-. | | |\n"
				+ "  _| |__/ || \\__., > '  <  | |    _| |__/ || \\__.,// | |, | |     | | | | |_|\n"
				+ " |________| '.__.'[__]`\\_][___]  |________| '.__.'\\'-;__/[___]   [___||__](_)\n" + RESET);
	}
	
//	GOODBYE ASCII banner on exit 
	public void byeAscii() {
		System.out.println(SECONDARY + "   ______                         __  __                       _ \n"
				+ " .' ___  |                       |  ][  |                     | |\n"
				+ "/ .'   \\_|   .--.    .--.    .--.| |  | |.--.   _   __  .---. | |\n"
				+ "| |   ____ / .'`\\ \\/ .'`\\ \\/ /'`\\' |  | '/'`\\ \\[ \\ [  ]/ /__\\\\| |\n"
				+ "\\ `.___]  || \\__. || \\__. || \\__/  |  |  \\__/ | \\ '/ / | \\__.,|_|\n"
				+ " `._____.'  '.__.'  '.__.'  '.__.;__][__;.__.'[\\_:  /   '.__.'(_)\n"
				+ "                                               \\__.'             " + RESET);
	}
//	MENU DATA, returns the list of menu options based on the given menu name
	public String[] menuData(String menuName) {
		switch(menuName) {
		case "Main" -> {
			 String[] main = {"Dictionary", "Flash Cards", "Quiz Mode", "Learning Progress", "Settings", "Exit"};
			 return main;
		}
		case "Dictionary" ->{
			 String[] dictionary = {"Search a word", "Browse by category", "Browse by english level", "Random word", "Main menu"};
			 return dictionary;
		}
		case "Category" ->{
			 String[] category = {"Animals", "Food & Drink", "Emotions", "Travel", "Technology", "Back to dictionary", "Main menu"};
			 return category;
		}
		case "Level" ->{
			String[] level = {"Elementary         (A1)", "Pre-Intermediate   (A2)", "Intermediate       (B1)", "Upper-Intermediate (B2)",
					"Advanced           (C1)", "Proficient         (C2)", "Back to dictionary", "Main menu"};
			 return level;
		}
		case "Settings" ->{
			 String[] settings = {"Color Theme", "Flash Cards Style", "Flash Cards Count", "Quiz Difficulty", "Word Detail", "Reset to Default", "Main menu"};
			 return settings;
		}
		case "Progress" ->{
			String[] progress = {"Flash Cards Progress", "Quiz Progress", "Main menu"};
			return progress;
		}
		case "Theme" ->{
			String[] theme = {"Ocean (cyan + blue)", "Forest (green + yellow)", "Sunset (red + yellow)", "Default (white)"};
			return theme;
		}
		case "FlashStyle" ->{
			String[] style = {"Short (words only)", "Basic (words + phonetic)", "Detailed (words + phonetic + part of speech"};
			return style;
		}
		case "FlashCount" ->{
			String[] count = {"Quick (5 cards)", "Standard (10 cards)", "Deep dive (all saved words)"};
			return count;
		}
		case "QuizDifficulty" ->{
			String[] difficulty = {"Easy (5 questions, 4 options)", "Medium (10 questions, 5 options)", "Hard (all saved words, 6 options)"};
			return difficulty;
		}
		case "WordDetail" -> {
			String[] wordDetail = {"Brief (1 entry)", "Standard (2 entries)", "Full (everything available)"};
			return wordDetail;
		}
		case "BackProgress" -> {
			String[] progress = {"Back to Learning Progress", "Main menu"};
			return progress;
		}
 		}
		return null;
	}
	
//	SHOW MENU, Displays a numbered menu from menuData method
	public void showMenu(String menuName) {
		String[] menuData = menuData(menuName);
		for(int i = 0; i < menuData.length; i++) {
//			printf statement to for menu options  
			System.out.printf("%s[%d]%s %s\n", PRIMARY, (i+1), RESET, menuData[i]);
		}
		
	}
//	SHOW BROWSE CHOICES, displays 10 words in two columns 
	public void showBrowseChoices(String[] words) {
		for(int i = 0; i < 5; i++) {
//			-15s ensures that the words in the second column start at the same position regardless of the word length
			System.out.printf("%s[%d]%s %-15s %s[%d]%s %s\n",
					PRIMARY, (i+1), RESET, (words[i]), PRIMARY, (i+6), RESET,(words[i+5]));
		}
		System.out.println();
	}
//	Display word detail options
	public void showSearchMenu(String returnText) {
		System.out.println(PRIMARY + "[1] " + RESET + "Phonetic");
		System.out.println(PRIMARY + "[2] " + RESET + "Part of Speech");
		System.out.println(PRIMARY + "[3] " + RESET + "Definition");
		System.out.println(PRIMARY + "[4] " + RESET + "Definition + Example");
		System.out.println(PRIMARY + "[5] " + RESET + "Synonym");
		System.out.println(PRIMARY + "[6] " + RESET + "Antonym");
		System.out.println(PRIMARY + "[7] " + RESET + "All");
		System.out.println(PRIMARY + "[8] " + RESET + returnText);
		System.out.println(PRIMARY + "[9] " + RESET + "Main Menu");
	}
	public void showBackCategoryButtons() {
		System.out.println(PRIMARY + "[11] " + RESET + "Back to categories");
		System.out.println(PRIMARY + "[12] " + RESET + "Main menu");
	}
//	Display return options
	public void showBackSettingsButtons(int backNum, int mainNum) {
		System.out.println(PRIMARY + "[" +backNum+"] " + RESET + "Back to settings");
		System.out.println(PRIMARY + "[" + mainNum +"] " + RESET + "Main menu");
	}
	
//	Know/Don't know buttons for each flascard
	public void showFlashCardOption() {
		System.out.print(PRIMARY +"\n[1] "+ RESET + "I know   ");
		System.out.println(PRIMARY +"[2] " + RESET + "I don't know");
		System.out.println(PRIMARY +"\n===============================\n" + RESET);
	}
	public void showCurrentStatus(String statusName, String status) {
		System.out.println(SECONDARY + "\nCurrent " + statusName + ": "+ RESET + status );
		
	}
	
//	SHOW LABEL, display a centered uppercase label surrounded by borders
	public void showLabel(String labelName) {
		String bar = "===============================";
//		Calculates the number of spaces needed to center the text
		int padding = ((bar.length() - labelName.length())/2);
//		Creates the space string, repeats the space character padding number of times
		String spaces = " ".repeat(padding);
//		printf statement for a label 
		System.out.printf("%s\n%s\n%s%s\n%s\n%s%s\n%s\n%s\n",
				PRIMARY, bar, RESET, 
				SECONDARY, spaces + labelName.toUpperCase(), RESET,
				PRIMARY, bar, RESET);
	}
	
//	FLASH CARD LABEL, shows current card number out of total
	public void flashCardLabel(int answered, int total) {
		System.out.println("\n" + PRIMARY + "===============================" + RESET);
		System.out.println(SECONDARY + "\n       FLASHCARD " + BOLD + answered + "/" + total + RESET);
		System.out.println("\n" + PRIMARY + "===============================\n" + RESET);
	}

// BAR LABEL
	public void barLabel() {
		System.out.println(PRIMARY + "-------------------------------" + RESET);
	}
}

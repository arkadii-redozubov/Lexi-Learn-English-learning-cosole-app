//Arkadii Redozubov 3187445
package griffth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;


//Exercises.java (Contains the flashcard and quiz methods, progress display, reads word data from flashCards.json and saves results)
public class Exercises {
	static Scanner scanner = new Scanner(System.in);
	static Random random = new Random();
	static DataManager manager = new DataManager();
	static UserInterface inter = Main.inter;
	static Settings set = Main.set;
	static ApiService serv = Main.serv;
	
//	FLASHCARDS, displays flashCards using data from flashCards.json
	public void FlashCards() throws IOException {
		File file = new File("flashCards.json");
		JSONArray jsonArr = null;
		String data = null;
//		Ensures the file has enough words to display
		if(!file.exists()) {
			boolean success = manager.autoFill(random, serv, jsonArr);
			if(!success) return;
		}
		else {
			data = manager.loadData("flashCards.json");
			if(data.isBlank()) {	
				boolean success = manager.autoFill(random, serv, jsonArr);
				if(!success) return;
			}
		}
//		Load data + safety check
		data = manager.loadData("flashCards.json");
		if(data == null || data.isBlank()) return; 
		jsonArr = new JSONArray(data);
		int known = 0;
		int length = jsonArr.length();
//		Get the number of cards based on the settings
		int total = set.flashCardCount(set.getFlashCardCountName(), length);
//		If the requested num of cards is greater than the number of saved words - auto fill up to 10 words
		if(total > jsonArr.length()) {
			boolean success = manager.autoFill(random, serv, jsonArr);
			if(!success) return;
			data = manager.loadData("flashCards.json");
			jsonArr = new JSONArray(data);
		}
		
		for(int i = 0; i < total; i++) {
//			Flash cards label with the current card number
			inter.flashCardLabel(i+1, total);
//			Pick a random word from the saved words (ensures that flash cards are in different order every time they are launched)
			int randomIndex = random.nextInt(jsonArr.length());
			JSONObject obj = jsonArr.getJSONObject(randomIndex);
//			Show the word
			System.out.println(inter.SECONDARY + "Word: " + inter.RESET + obj.getString("word"));
//			Show additional information before revealing the definition and example based on the settings
//			1 (word only), 2 (word + phonetic), 3 (word + phonetic + part of speech)
			if(set.getFlashCard() == 2) {
				System.out.println(inter.SECONDARY +"Phonetic: " + inter.RESET + obj.getString("phonetic"));
			}
			else if(set.getFlashCard() == 3) {
				System.out.println(inter.SECONDARY +"Phonetic: " + inter.RESET + obj.getString("phonetic"));
				System.out.println(inter.SECONDARY +"Part of speech: " + inter.RESET + obj.getString("partOfSpeech"));
			}
//			Pause and wait before revealing the answer until the user presses ENTER to continue
			System.out.println(inter.SECONDARY +"\nPress" + inter.BOLD + " ENTER to reveal definition..." + inter.RESET);
			scanner.nextLine();
//			Reveal definition and example of the word
			System.out.println(inter.SECONDARY + "Definition: " + inter.RESET + obj.getString("definition"));
			System.out.println(inter.SECONDARY + "Example: "+ inter.RESET + obj.getString("example"));
			inter.barLabel();
			inter.showFlashCardOption();
//			Ask for self rating, 1 (knows word), 2 (doesn't know)
			int userChoice = Main.userChoice(scanner, 1, 2);
//			Counts known words to show the result at the end
			if(userChoice == 1) 
				known++;
		}
//		FLASHCARDS RESULTS, displays how many cards known, the corresponding score + feedback
		inter.showLabel("Flash cards complete!");
		System.out.println(inter.SECONDARY + "Result: " + inter.RESET +  "You know " + known + "/" + total + " words!");
		double percentageCorrect = ((double)known/total) *100;
		System.out.printf("%sScore:%s %.1f%%\n", inter.SECONDARY, inter.RESET, percentageCorrect);
//		Shows feedback based on the percentage of words known
		resultDisplay(percentageCorrect);
//		Save the results in the progress file (also updates the streak)
		manager.saveProgress("flashCardsProgress.json", known, total, percentageCorrect);
	}
	
//	QUIZ, multiple choice quiz using the flashCards.json, shows the definition and asks to identify the word
	public void Quiz() throws IOException {
		File file = new File("flashCards.json");
		JSONArray jsonArr = null;
		String data;
		if(!file.exists()) {
			boolean success = manager.autoFill(random, serv, jsonArr);
			if(!success) return;
		}
		else {
			data = manager.loadData("flashCards.json");
			if(data.isBlank()) {	
				boolean success = manager.autoFill(random, serv, jsonArr);
				if(!success) return;
				}
			}
		data = manager.loadData("flashCards.json");
		if(data == null || data.isBlank()) return; 
		jsonArr = new JSONArray(data);
		int known = 0;
		int length = jsonArr.length();
		int total = set.quizQuestions(set.getQuizName(), length);
		if(total > jsonArr.length()) {
			boolean success = manager.autoFill(random, serv, jsonArr);
			if(!success) return;
			data = manager.loadData("flashCards.json");
			jsonArr = new JSONArray(data);
		}
		inter.showLabel("Quiz mode");
		for(int i = 0; i < total; i++) {
//			Show question label with the current number
			System.out.println(inter.PRIMARY + "\n------" + inter.RESET + inter.SECONDARY + " Question " + inter.BOLD + (i+1) + "/" + total + inter.RESET + inter.PRIMARY + " ------\n" + inter.RESET);
			System.out.println(inter.SECONDARY + "Which word mathes this definition?" + inter.RESET);
//			Pick a random word from the saved file as a correct answer
			int randomDefinition = random.nextInt(jsonArr.length());
			JSONObject obj = jsonArr.getJSONObject(randomDefinition);
			System.out.println(inter.SECONDARY + "Definition: " + inter.RESET + obj.getString("definition"));
//			Set number of options based on difficulty settings
			int options = set.quizOptions(set.getQuizName());
//			Pick a random position for the correct answer
			int randomCorrectChoice = random.nextInt(1, options+1);
			String[] optionsArr = new String[options];
//			Place the correct answer at the randomly selected position 
			optionsArr[randomCorrectChoice-1] = obj.getString("word");
//			Fill the rest of the options with random words 
			for(int l = 0; l< options; l++) {
				if(optionsArr[l] == null) {
					String wrongWord;
					while(true) {
//					Pick a random word
					int wrongWordIndex = random.nextInt(total);
					wrongWord = jsonArr.getJSONObject(wrongWordIndex).getString("word");
//					Pick again if the random option is the same as the answer
					if(wrongWord.equals(optionsArr[randomCorrectChoice-1])){
						continue;
					}
//					Skip if the word is already used
					boolean alreadyUsed = false;
					for(String option:optionsArr) {
						if(option != null && option.equals(wrongWord)) {
							alreadyUsed = true;
							break;
						}
					}
//					If the word is unique - put it
					if(!alreadyUsed) {
						optionsArr[l] = wrongWord;
					}
//					Try to pick again
					else {
					continue;
					}
					break;
					}
				}
			}
			System.out.println();
//			Display all options numbered from 1 
			for(int j = 0; j<optionsArr.length; j++) {
				System.out.println(inter.PRIMARY + "[" + (j+1) + "] " + inter.RESET + optionsArr[j]);
			}
			int userChoice = Main.userChoice(scanner, 1, options);
//			Check if the chosen option is correct
			if(userChoice == randomCorrectChoice) {
				System.out.println(inter.SUCCESS +"\nCorrect!" + inter.RESET);
//				Show example as extra info
				System.out.println(inter.SECONDARY + "Example: " + inter.RESET + obj.getString("example"));
				known++;
			}
			else {
//				Print the correct answer if the choice is incorrect
				System.out.println(inter.ERROR +"\nWrong!" + inter.RESET + " The correct answer was: " + optionsArr[randomCorrectChoice-1]);
				System.out.println(inter.SECONDARY +"Example: " + inter.RESET + obj.getString("example"));
			}
	
		}
//		RESULTS, Print results after the quiz completion
		inter.showLabel("Quiz complete!");
		System.out.println(inter.SECONDARY + "Result: " + inter.RESET +  "You know " + known + "/" + total + " words!");
		double percentageCorrect = ((double)known/total) *100;
		System.out.printf("%sScore:%s %.1f%%\n", inter.SECONDARY, inter.RESET, percentageCorrect);
		resultDisplay(percentageCorrect);
//		Save the results in the progress file
		manager.saveProgress("quizProgress.json", known, total, percentageCorrect);
	}
	
//	DISPLAY PROGRESS, reads and displays the saved progress about the given file 
	public void displayProgress(String fileName, String exerciseType) throws IOException {
		File file = new File(fileName);
//		Print no progress if the file does not exist 
		if(!file.exists()) {
			System.out.println(inter.ERROR + "You haven't made any progress yet!" + inter.RESET);
		}
		else {
//			Print no progress if the file is blank
			String data = manager.loadData(fileName);
			if(data.isBlank()) {
				System.out.println(inter.ERROR + "You haven't made any progress yet!" + inter.RESET);
			}
			else {
			JSONArray jsonArrFull = new JSONArray(data);
			JSONArray jsonArr = jsonArrFull.getJSONArray(1);
			int attempts = jsonArr.length();
//			Extract the data from the progress file
			double percentageSum = 0;
			double highestPercentage = jsonArr.getJSONObject(0).getDouble("percentage");
			int correctHighest = jsonArr.getJSONObject(0).getInt("correct");
			int totalHighest = jsonArr.getJSONObject(0).getInt("total");
//			Loop through all attempts to find the best one, and calculate the total taken, and the average score
			for(int i = 0; i< jsonArr.length(); i++) {
				double currentPercentage = jsonArr.getJSONObject(i).getDouble("percentage");
				percentageSum+=currentPercentage;
//				Update best score if the current is higher
				if(currentPercentage > highestPercentage) {
					highestPercentage = currentPercentage;
					correctHighest = jsonArr.getJSONObject(i).getInt("correct");
					totalHighest = jsonArr.getJSONObject(i).getInt("total");
				}	
			}
			inter.showLabel(exerciseType + " progress");
			int daysInRow = jsonArrFull.getJSONObject(0).getInt("streak");
//			Display the streak message based on the number of days in a row
			String message;
			if(daysInRow ==1 ) {message = " Good start! Keep ot going!";}
			else if(daysInRow <=5 ) {message = " You are on fire!";}
			else if(daysInRow <=10 ) {message = " Incredible progress!";}
			else {message = " You are unstoppable!";}
//			Display all statistics
			System.out.println(inter.SECONDARY + "Streak: " + inter.RESET + daysInRow + " days in a row!" + message);
			System.out.println(inter.SECONDARY + exerciseType + " taken: " + inter.RESET + attempts);
//			Calculate average and display the current score
			System.out.printf(inter.SECONDARY + "Average score: " + inter.RESET + "%.1f%%\n", (percentageSum/attempts));
			System.out.printf(inter.SECONDARY + "Best Score: " + inter.RESET + "%d/%d (%.1f%%)\n", correctHighest, totalHighest, highestPercentage);
			inter.barLabel();
			}
			}
	}
	
//	RESULT DISLPAY, displays a feedback message based on the percentage given
	public void resultDisplay(double percentageCorrect) {
		if(percentageCorrect ==100) {
			System.out.println(inter.SECONDARY + "Perfect score! Outstanding!" + inter.RESET);
		}
		else if(percentageCorrect >=70) {
			System.out.println(inter.SECONDARY + "Well done! Keep it up!" + inter.RESET);
		}
		else if(percentageCorrect >=50) {
			System.out.println(inter.SECONDARY + "Nice try! Keep practising!" + inter.RESET);
		}
		else {
			System.out.println(inter.SECONDARY + "Keep studying, and you'll get there!" + inter.RESET);
		}
		inter.barLabel();
	}

}

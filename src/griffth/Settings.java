//Arkadii Redozubov 3187445
package griffth;

//Settings.java (Stores and manages all user preferences in settings, each preference is stored as an integer)
public class Settings {
//	Color theme: 1 - Ocean, 2 - Forest, 3 - Sunset, 4 - Default
	private int theme; 
//	Quiz difficulty: 1 - Easy, 2 - Medium, 3 - Hard
	private int quiz;
//	Flashcard style: 1 - Short, 2 - Basic, 3 - Detailed
	private int flashCard;
//	Flashcard number: 1 - Quick(5), 2 - Standard(10), 3 - Deep(all)
	private int flashCardCount;
//	Word detail: 1 - Brief(1), 2 - Standard(2), 3 - Full(all)
	private int wordDetail;
	
//	Constructor, sets all setting preferences to default values
	public Settings() {
		theme = 4;
		quiz = 1;
		flashCard = 2;
		flashCardCount =1;
		wordDetail = 1;
	}
//	COLOR THEME
//	Getter for the current integer theme 
	public int getTheme() {
		return theme;
	}
//	Getter for the current theme converted to the string name for UI, settings file 
	public String getThemeName() {
		if(theme == 1) 
			return "Ocean";
		else if(theme == 2) 
			return "Forest";
		else if(theme == 3) 
			return "Sunset";
		else
			return "Default";	
	}
//	Setter for the current integer theme 
	public void setTheme(int theme) {
		this.theme = theme;
	}
	
//	QUIZ DIFFICULTY
//	Getter for the current integer quiz difficulty 
	public int getQuiz() {
		return quiz;
	}
//	Setter for the current integer quiz difficulty  
	public void setQuiz(int quiz) {
		this.quiz = quiz;
	}
//	Sets quiz difficulty from a string name 
	public void setQuizString(String quizName) {
		if(quizName.equals("Easy"))
			this.quiz = 1;
		else if(quizName.equals("Medium"))
			this.quiz = 2;
		else 
			this.quiz = 3;
	}
//	Getter for the current quiz difficulty converted to the string name for UI, settings file 
	public String getQuizName() {
		if(quiz == 1)
			return "Easy";
		else if(quiz == 2)
			return "Medium";
		else
			return "Hard";
	}
//	Returns the number of options to display per quiz based on the given string 
	public int quizOptions(String quizName) {
		if(quizName.equals("Easy"))
			return 4;
		else if(quizName.equals("Medium"))
			return 5;
		else 
			return 6;
	}
//	Returns the number of quiz questions to display based on the given string 
	public int quizQuestions(String quizName, int length) {
		if(quizName.equals("Easy"))
			return 5;
		else if(quizName.equals("Medium"))
			return 10;
		else 
			return length;
	}
	
//	FLASHCARD STYLE
//	Getter for the current integer flashcard style 
	public int getFlashCard() {
		return flashCard;
	}
//	Setter for the current integer flashcard style 
	public void setFlashCard(int flashCard) {
		this.flashCard = flashCard;
	}
//	Getter for the current flashcard style converted to the string name for UI, settings file 
	public String getFlashCardName() {
		if(flashCard == 1)
			return "Short";
		else if(flashCard == 2)
			return "Basic";
		else
			return "Detailed";
	}
//	Sets flashcard style from a string name
	public void setFlashCardString(String flashCardName) {
		if(flashCardName.equals("Short"))
			this.flashCard = 1;
		else if(flashCardName.equals("Basic"))
			this.flashCard = 2;
		else 
			this.flashCard = 3;
	}
	
//	FLASHCARD COUNT
//	Getter for the current integer flashcard count
	public int getFlashCardCount() {
		return flashCardCount;
	}
//	Setter for the current integer flashcard count
	public void setFlashCardCount(int flashCardCount) {
		this.flashCardCount = flashCardCount;
	}
//	Getter for the current flashcard count converted to the string name for UI, settings file 
	public String getFlashCardCountName() {
		if(flashCardCount == 1)
			return "Quick";
		else if(flashCardCount == 2)
			return "Standard";
		else
			return "Deep dive";
	}
//	Sets flashcard count from a string name
	public void setFlashCardCountString(String flashCardCountName) {
		if(flashCardCountName.equals("Quick"))
			this.flashCardCount = 1;
		else if(flashCardCountName.equals("Standard"))
			this.flashCardCount = 2;
		else 
			this.flashCardCount = 3;
	}
//	Returns the number of flashcards to show 
	public int flashCardCount(String flashCardCountName, int length) {
		if(flashCardCountName.equals("Quick"))
			return 5;
		else if(flashCardCountName.equals("Standard"))
			return 10;
		else 
			return length;
	}
	
	
//	WORD DETAIL 
//	Getter for the current integer word detail
	public int getWordDetail() {
		return wordDetail;
	}
//	Setter for the current integer word detail
	public void setWordDetail(int wordDetail) {
		this.wordDetail = wordDetail;
	}
//	Getter for the current word detail converted to the string name for UI, settings file 
	public String getWordDetailName() {
		if(wordDetail == 1)
			return "Brief";
		else if(wordDetail == 2)
			return "Standard";
		else
			return "Full";
	}
//	Sets word detail from a string name
	public void setWordDetailString(String wordDetailName) {
		if(wordDetailName.equals("Brief"))
			this.wordDetail = 1;
		else if(wordDetailName.equals("Standard"))
			this.wordDetail = 2;
		else 
			this.wordDetail = 3;
	}
//	Returns the number of API entries to display for a searched word
	public int wordDetail(String wordDetailName, int length) {
		if(wordDetailName.equals("Brief"))
			return 1;
		else if(wordDetailName.equals("Standard"))
			return 2;
		else 
			return length;
	}


	

}

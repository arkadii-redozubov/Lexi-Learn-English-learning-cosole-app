//Arkadii Redozubov 3187445
package griffth;

import java.util.ArrayList;

//Meaning.java (Model class used as a single meaning of the dictionary word, each word in the dictionary might have multiple meaning objects
//Stores all data related to that meaning)
public class Meaning {
//	Part of speech is single for each meaning object
	private String partOfSpeech;
//	ArrayLists of definitions, synonyms, antonyms, examples (each meaning might have multiple them)
	private ArrayList<String>definition;
	private ArrayList<String>synonyms;
	private ArrayList<String>antonyms;
	private ArrayList<String>example;
	
//	Constructor, initialises all private fields
//	If ArrayList is passed as a null, creates a new empty ArrayList, checked through a ternary operator
	public Meaning(String partOfSpeech, ArrayList<String>definition, ArrayList<String>synonyms,
			ArrayList<String>antonyms, ArrayList<String>example) {
		this.partOfSpeech = partOfSpeech;
		this.definition = definition != null ? definition : new ArrayList<String>();
		this.synonyms = synonyms != null ? synonyms : new ArrayList<String>();
		this.antonyms = antonyms != null ? antonyms : new ArrayList<String>();
		this.example = example != null ? example : new ArrayList<String>();
	}
	
//	Getters to access private fields 
	public String getPartOfSpeech() {return this.partOfSpeech;}
	public ArrayList<String> getDefinition() {return this.definition;}
	public ArrayList<String> getSynonyms() {return this.synonyms;}
	public ArrayList<String> getAntonyms() {return this.antonyms;}
	public ArrayList<String> getExample() {return this.example;}
}

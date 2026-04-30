//Arkadii Redozubov 3187445
package griffth;

import java.util.ArrayList;

//Word.java (Model class, represents a single dictionary entry for a word)
public class Word {
//	word and phonetic are only used once in the first entry
	private String word;
	private String phonetic;
//	ArrayList of meaning objects, used in all entries
	private ArrayList<Meaning> meanings;
	
//	Full constructor, used for the first API entry, 
//	If ArrayList is passed as a null, creates a new empty ArrayList, checked through a ternary operator
	public Word(String word, String phonetic, ArrayList<Meaning>meanings) {
		this.word = word;
		this.phonetic = phonetic;
		this.meanings = meanings != null ? meanings : new ArrayList<Meaning>();
	}
//	Constructor for additional API entries, contains only extra meanings, do not repeat word and phonetic
	public Word(ArrayList<Meaning>meanings) {
		this.word = "";
		this.phonetic = "";
		this.meanings = meanings != null ? meanings : new ArrayList<Meaning>();
	}
	
//	Getters for accessing private fields 
	public String getWord() {return this.word;}
	public String getPhonetic() {return this.phonetic;}
	public ArrayList<Meaning> getMeanings() {return this.meanings;}

}

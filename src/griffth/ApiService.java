//Arkadii Redozubov 3187445
package griffth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

//ApiService.java (Contains methods for working with the API, fetches info from the cache file if offline)
public class ApiService {
//	Base URL, used for all API requests
	private static final String baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/";
	public static UserInterface inter = Main.inter;
	static DataManager manager = new DataManager();
	
//	FETCH WORD, Tries to fetch from the API first, if it fails - tries to load from cache file
	public ArrayList<Word> fetchWord(String word, boolean autoFill) throws IOException {
//		Get the API's response info 
		String request = getRequest(word, autoFill);
//		Return null if the API unreachable and word not in cache
		if(request==null) {
			return null;
		}
//		If the response data is taken from the cache, fetch the word details from the cache file
		if(request.startsWith("CACHE:")) {
			return fetchWordFromCache(word);
		}
//		Parse the the raw JSON saved in a string into a jSONArray 
		JSONArray jsonArr = new JSONArray(request);
		ArrayList <Word> words = new ArrayList<Word>();
//		EXTRACT PHONETIC, The phonetic is extracted only from the first non-empty entry, since its unchanged across all entries
		String phonetic = null;
		for(Object obj: jsonArr) {
		if(phonetic == null) {
			JSONObject json = (JSONObject)obj;
			JSONArray phoneticArr = json.optJSONArray("phonetics");
			if(!phoneticArr.isEmpty()) {
				for(Object objPhonetic:phoneticArr) {
					phonetic = ((JSONObject)objPhonetic).optString("text");
//					Stop if we find the phonetic
					if(!phonetic.isEmpty()) {
						break;
					}
				}
			}	
			}
		}
		int counter = 0;
		for(Object obj: jsonArr) {
		JSONObject json = (JSONObject)obj;
//		Get the word name only from the first entry
		String word1 = null;
		if(counter < 1) {
		word1 = json.getString("word");
		}
		
//		EXTRACT MEANINGS, each entry might have multiple meanings 
		JSONArray meanings = json.getJSONArray("meanings");
		ArrayList<Meaning>meaningObjArr = new ArrayList<Meaning>();
		for(Object obj1:meanings) {
			JSONObject meaningObj = (JSONObject)obj1;
//			Declare ArrayLists to put and return them in the final ArrayList
			ArrayList<String>definitionsArr = new ArrayList<String>();
			ArrayList<String>synonymsArr = new ArrayList<String>();
			ArrayList<String>antonymsArr = new ArrayList<String>();
			ArrayList<String>exampleArr = new ArrayList<String>();
			String partOfSpeech = meaningObj.getString("partOfSpeech");
			JSONArray synonyms = meaningObj.optJSONArray("synonyms");
			JSONArray antonyms = meaningObj.optJSONArray("antonyms");
//			If no synonyms exist - put no synonyms, prevents null checks
			if(synonyms.isEmpty()) {
				synonymsArr.add("no synonyms");
			}
			else {
				for(Object obj2: synonyms) {
					String synonym =  obj2.toString();
					synonymsArr.add(synonym);
				}
			}
//			If no antonyms exist - put no synonyms, prevents null checks
			if(antonyms.isEmpty()) {
				antonymsArr.add("no antonyms");
			}
			else {
				for(Object obj3: antonyms) {
					String antonym = obj3.toString();
					antonymsArr.add(antonym);	
				}
			}
//			EXTRACT DEFINITIONS
			JSONArray definitionArr = meaningObj.getJSONArray("definitions");
			for(Object obj3: definitionArr) {
				JSONObject definitionObj= (JSONObject)obj3;
//				Put no definition or no example if they are missing
				String definition = definitionObj.optString("definition", "no definition ");
				String example = definitionObj.optString("example", "no example ");
				definitionsArr.add(definition);
				exampleArr.add(example);
			}
//			Build and add the Meaning object
			meaningObjArr.add(new Meaning(partOfSpeech, definitionsArr, synonymsArr,antonymsArr,exampleArr));
		}
//		BUILD WORD OBJECT, first entry gets word and its phonetic, other entries get meanings only
		Word wordObj;
		if(counter<1 && phonetic!=null) {
		wordObj = new Word(word1, phonetic, meaningObjArr);
		counter++;
		}
		else if(counter<1 && phonetic==null) {
//			Phonetic is not available 
			wordObj = new Word(word1, "no phonetic", meaningObjArr);
			counter++;
		}
		else {
//			Additional entries without the word name and phonetic
			wordObj = new Word(meaningObjArr);
		}
//		Add a word object to an ArrayList of word objects
		words.add(wordObj);
	}
		return words;
	}
//	FETCH WORD FROM CACHE, reads cache.json and finds the entry that matches the given word, passes and returns ArrayList of word objects
	public ArrayList<Word> fetchWordFromCache(String word) throws IOException{
		File file = new File("cache.json");
//		Return null if the file does not exist or is empty
		if(!file.exists()) { return null; }
		String data = manager.loadData("cache.json");
		if(data.isBlank()) {return null;};
		JSONArray jsonArr = new JSONArray(data);
		for(Object obj: jsonArr) {
//			Each element is a JSONArray, the element on index 0 has a word name and phonetic
			JSONArray jsonArr1 = (JSONArray)obj;
			JSONObject obj1 = jsonArr1.getJSONObject(0);
			String word1 = obj1.optString("word");
//			Skip entries that don't match the word
			if(!word1.equals(word)) {
				continue;
			}
//			EXTRACT PHONETIC from the first entry
			String phonetic = obj1.getString("phonetic");
			ArrayList <Word> words = new ArrayList<Word>();
			int counter = 0;
			for(Object obj2: jsonArr1) {
				JSONObject obj3 = (JSONObject)obj2;
				JSONArray meaningsArr = obj3.getJSONArray("meanings");
				ArrayList<Meaning>meaningObjArr = new ArrayList<Meaning>();
				for(Object obj4:meaningsArr) {
					JSONObject meaningObj = (JSONObject) obj4;
					ArrayList<String>definitionsArr = new ArrayList<String>();
					ArrayList<String>synonymsArr = new ArrayList<String>();
					ArrayList<String>antonymsArr = new ArrayList<String>();
					ArrayList<String>exampleArr = new ArrayList<String>();
					String partOfSpeech = meaningObj.getString("partOfSpeech");
//					Read synonyms, antonyms.. from cached JSON
					JSONArray synonyms = meaningObj.optJSONArray("synonyms");
					JSONArray antonyms = meaningObj.optJSONArray("antonyms");
					JSONArray definitions = meaningObj.getJSONArray("definition");
					JSONArray examples = meaningObj.getJSONArray("examples");
					for(Object synonym: synonyms) {
						String synonymString =  synonym.toString();
						synonymsArr.add(synonymString);
					}
					for(Object antonym: antonyms) {
						String antonymString = antonym.toString();
						antonymsArr.add(antonymString);	
					}
					for(Object definition: definitions) {
						String definitionString = definition.toString();
						definitionsArr.add(definitionString);
					}
					for(Object example: examples) {
						String exampleString = example.toString();
						exampleArr.add(exampleString);
					}
					meaningObjArr.add(new Meaning(partOfSpeech, definitionsArr, synonymsArr,antonymsArr,exampleArr));
				}
//				First cached entry gets word name and phonetic, rest meanings only
				if(counter == 0) {
					Word wordObj = new Word(word1, phonetic, meaningObjArr);
					words.add(wordObj);
				}
				else {
				Word wordObj = new Word(meaningObjArr);
				words.add(wordObj);
				}
				counter++;
			}
			return words;
		}
//		Return null if it was not found
		return null;
			
		
	}
//	GET REQUEST, HTTP GET request to the API for the given word, returns the raw JSON as a string on success
//	Returns CACHE:word if offline but the word is in cache file, returns null if both API and cache fail
	public String getRequest(String word, boolean autoFill) throws IOException{
		try {
//			Builds the full request URL, uses the base URL + the word
			URL url = new URL(baseUrl + word.toLowerCase().trim());
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
//			Set 5 seconds timeout to prevent infinitive waiting 
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
//			Get the response code
			int responseCode = connection.getResponseCode();
//			Check the response code, If the word was not found in API, return null and print not found
			if(responseCode != 200) {
				System.out.println(inter.ERROR + "Not found!" + inter.RESET);
				return null;
			}
//			Read the response from the API line by line using a BufferedReader into a StringBuilder
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder builder = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
				inputStream.close();
//				Print success message only if autoFill is false (better visibility)
				if(!autoFill) {System.out.println(inter.SUCCESS +  "Word found!" + inter.RESET);}
				return builder.toString();
				
		} catch (MalformedURLException e) {
//			The word contains invalid characters 
			System.out.println(inter.ERROR + "Invalid word format!" + inter.RESET);
			e.printStackTrace();
			return null;
		}
		catch(java.net.UnknownHostException e) {
//			No interned connection
			System.out.println(inter.ERROR + "No internet connection!" + inter.RESET);
			return tryCache(word);
		}
		catch(java.net.SocketTimeoutException e) {
//			API took longer than 5 seconds in TimeOut time 
			System.out.println(inter.ERROR + "Connection timed out!" + inter.RESET);
			return tryCache(word);
		}
		catch(IOException e) {
//			All other connection errors 
			System.out.println(inter.ERROR + "Connection failed" + inter.RESET);
			return tryCache(word);
		}	
	}
//	TRY CACHE, called when the API cannot be reached, checks if the word exists in the cache.json
//	Returns CACHE:word as a signal to fetch the word from the cache file 
	public String tryCache(String word) throws IOException {
		System.out.println(inter.SECONDARY + "Checking cache..." + inter.RESET);
//		Animate the searching text while reading the cache file
		Main.searchingWord(word);
//		Tries to fetch the word from cache
		ArrayList<Word> cached = fetchWordFromCache(word);
//		Return CACHE:word signal
		if(cached!=null) {
			System.out.println(inter.SUCCESS + "Loaded \"" + word +"\" from cache" + inter.RESET);
			return "CACHE:" + word;
		}
//		If its not in cache - return null
		else {
			System.out.println(inter.ERROR + "Word \"" + word + "\" is not in cache!" + inter.RESET);
			System.out.println(inter.SECONDARY + "Tip: Flash Cards and Quiz work offline!");
			return null;
		}
	}

}

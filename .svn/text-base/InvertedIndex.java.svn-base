import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class InvertedIndex {
	// private HashMap<String, HashMap<String, ArrayList<Integer>>>
	// invertedIndex;
	private TreeMap<String, HashMap<String, ArrayList<Integer>>> invertedIndex;
	// key is the file in which the term is found, and first value in the array
	// list is the frequency in that file while the second is the first position
	// it can be found in
	// using the temp data structure to make arranging the adding of frequencies
	// multiple search terms easier.
	private HashMap<String, ArrayList<SearchResult>> searchMap;
	private ArrayList<SearchResult> searchList;
	private ArrayList<String> searchWords;
	private PrintWriter writer = null;
	private Lock searchLock;
	private Lock indexLock;
	private Logger log;
	private WorkQueue threadPool;
	private PrintWriter out;
	
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, HashMap<String, ArrayList<Integer>>>();
		searchMap = new HashMap<String, ArrayList<SearchResult>>();
		searchWords = new ArrayList<String>();
//		this.lock = lock;
		searchLock = new Lock();
		indexLock = new Lock();
		log = Logger.getRootLogger();
	}

	public void sortSearchTerms(String[] lines){
		String s = "";
		for (String term : lines) {
			s = s.concat(term);
			s = s.concat(" ");
		}
		searchWords.add(s);

	}
	
	public void addEntry(String word, String file, int position) {
		indexLock.acquireWriteLock();

		// Goes thru the array and sees if each word is already
		// set into the hash map as a key
		if (invertedIndex.containsKey(word)) {
			// checks if the file name is a key in the nested
			// hash map of the word

			if (invertedIndex.get(word).containsKey(file)) {
				invertedIndex.get(word).get(file).add(position);

			} else {
				/**
				 * if the file name is not yet in the nested hash map for the
				 * word create an array of the word's first position and add
				 * that with the file name as key
				 **/

				invertedIndex.get(word).put(file, new ArrayList<Integer>());
				invertedIndex.get(word).get(file).add(position);

			}
		}
		/*
		 * if the word is not yet a key, create a new entry in the hash map.
		 * Create new hash map to hold the file the word is found and an array
		 * to find the position Then put that in the wordMap hash map as value
		 * and the word as a key.
		 */
		else {
			invertedIndex.put(word, new HashMap<String, ArrayList<Integer>>());
			invertedIndex.get(word).put(file, new ArrayList<Integer>());
			invertedIndex.get(word).get(file).add(position);

		}
		indexLock.releaseWriteLock();

	}
	
	// finds and prints words relevant to the seachTerm given.
	public void findSearches(String[] searchTerms) {
		
		
		HashMap<String, SearchResult> searchResultsList = new HashMap<String, SearchResult>();

		log.debug("Going into search");
		
		// look through the search terms passed in
		for (String term : searchTerms) {
			// iterates through the words in the inverted index to look for
			// matches to the search term
			// submap picks out the range in which the search terms could
			// possibly be in. Range is from search term to the letter after the term's first letter
			// and breaks from searching on the key that no longer starts with the term
			indexLock.acquireReadLock();
			for (String key : invertedIndex.subMap(term,
					nextChar(term.charAt(0)).toString()).keySet()) {
				if (key.startsWith(term.toLowerCase())) {
					for (String fileName : invertedIndex.get(key).keySet()) {
						if(searchResultsList.containsKey(fileName)){
							searchResultsList.get(fileName).setFrequency(searchResultsList.get(fileName).getFrequency()
										+ invertedIndex.get(key)
												.get(fileName).size());
							
								if (searchResultsList.get(fileName).getFirstPos() > invertedIndex
										.get(key).get(fileName).get(0)) {
									searchResultsList.get(fileName).setFirstPos(invertedIndex.get(key)
											.get(fileName).get(0));
								}
						}else{
							indexLock.releaseReadLock();
							searchLock.acquireWriteLock();
							SearchResult sr = new SearchResult(fileName, invertedIndex
									.get(key).get(fileName).size(),
									invertedIndex.get(key).get(fileName)
											.get(0));
							
							searchResultsList.put(fileName, sr);
							searchLock.releaseWriteLock();
						}					
					}

				}else{
					
					break;
				}
			}
			indexLock.releaseReadLock();
		}

		String s = "";
		for (String term : searchTerms) {
			s = s.concat(term);
			s = s.concat(" ");
		}
		addSearchList(searchResultsList.values(), s);
	}
	
	
	
	private void addSearchList(Collection<SearchResult> searchResults, String s){
//		searchMap = new HashMap<String, ArrayList<SearchResult>>();
		searchLock.acquireWriteLock();
		searchList = new ArrayList<SearchResult>();
		searchList.addAll(searchResults);
		Collections.sort(searchList);
		searchMap.put(s, searchList);
		searchLock.releaseWriteLock();
		log.debug("Done searching");
	}
	

	public void printSearches(PrintWriter out) {
		// prints out the results into a the text file
		searchLock.acquireReadLock();
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					"searchresults.txt")));
			if(searchWords.isEmpty()){
				out.println("Enter a search term");
			}else{
				for (String term : searchWords) {
					out.println("<p><strong>" + term + "</strong>");
					if (searchMap.get(term).isEmpty()) {
						out.println("<p> NO MATCHED WORD FOUND!");
					}
					for (SearchResult sr : searchMap.get(term)) {
						out.println("<p> <a href = \"" + sr.getFileFound() + "\">" + sr.getFileFound() + "</a>");
					}
					out.println("<p> -------------------------------------------------");
				}
	
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("Search results file cannot be written. ");
		} finally {
			out.close();
		}
		searchLock.releaseReadLock();
	}

	// getting a char's toString()
	public String castChar(char c) {
		Character newChar = c;
		return newChar.toString();
	}

	// method used to find the char next to the char c passed in
	public Character nextChar(char c) {
		int placeHolder = ((int) c) + 1;
		char newChar = (char) placeHolder;
		return newChar;
	}

	// prints all the values in the inverted index
	public void printResults() {
//		System.out.println("# of threads working: " + threadPool.getPending());
		indexLock.acquireReadLock();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					"invertedindex.txt")));
			for (String key : invertedIndex.keySet()) {
				writer.println(key);
				// prints the file where the word can be found
				for (String fileName : invertedIndex.get(key).keySet()) {
					writer.print("\"" + fileName + "\"");
					// prints the position of the numbers
					for (int i = 0; i < invertedIndex.get(key).get(fileName)
							.size(); i++) {
						writer.print(", "
								+ invertedIndex.get(key).get(fileName).get(i));
					}
					writer.println();
				}

				writer.println();

			}
			writer.flush();
		} catch (IOException e) {
			System.out.println("invertedindex.txt was unsuccessfully written.");
		} finally {
			writer.close();
		}
		System.out.println("Finished writing the file");
		indexLock.releaseReadLock();

	}

}

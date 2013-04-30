import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileParser {
	private InvertedIndex index;
	private Lock lock;
		
	public FileParser(InvertedIndex index, Lock lock){
		this.index = index;
		this.lock = lock;

	}
	
	// Reads in the .txt files passed in from the txtTraverser then enters the
	// values in the inverted index
	public void wordMap(StringBuilder line, String path) {
		HashMap<String, ArrayList<Integer>> subIndex = new HashMap<String, ArrayList<Integer>> ();
//		File file = new File(path);
		BufferedReader reader = null;

			
//				reader = new BufferedReader(new FileReader(file));
				String textLine = line.toString();
//				System.out.println(textLine);
				int position = 0;
				// reads lines in and splits them into an array.
				textLine = textLine.toLowerCase();

				String[] lines = textLine.split("\\s");
				for (int i = 0; i < lines.length; i++) {
					lines[i] = lines[i].replaceAll("\\W", "");

					// Skips empty lines that are read in
					if (lines[i].isEmpty()) {
						continue;
					}
					position += 1;

					addEntry(lines[i], path, position, subIndex);

				}
				

				for(String word : subIndex.keySet()){

					for(Integer count : subIndex.get(word)){
						index.addEntry(word, path, count);
					}

				}

		return;
	}
	
	private void addEntry(String word, String file, int position, HashMap<String, ArrayList<Integer>> subIndex) {
		//log.debug("Adding word " + word);
		
		// Goes thru the array and sees if each word is already
		// set into the hash map as a key
		if (subIndex.containsKey(word)) {
			// checks if the file name is a key in the nested
			// hash map of the word
			subIndex.get(word).add(position);
		
		/*
		 * if the word is not yet a key, create a new entry in the hash map.
		 * Create new hash map to hold the file the word is found and an array
		 * to find the position Then put that in the wordMap hash map as value
		 * and the word as a key.
		 */
		}else {
			subIndex.put(word, new ArrayList<Integer>());
			subIndex.get(word).add(position);
		
		}
	
	}
	
}

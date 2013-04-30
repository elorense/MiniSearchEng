import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class SearchParser {
	private InvertedIndex index;
	private WorkQueue threadPool;
	
	public SearchParser(InvertedIndex index, WorkQueue threadPool){
		this.index = index;
		this.threadPool = threadPool;
	}
	
	
	
	// read in the file that contains the search terms
	public void readSearches(String filePath) {
	
			String textLine = filePath;
			
				// reads lines in and splits them into an array.
			
				textLine = textLine.toLowerCase();

				String[] lines = textLine.split("\\s");
				for (int i = 0; i < lines.length; i++) {
					lines[i] = lines[i].replaceAll("\\W", "");

				}
				// passes in an array of search words to the print search
				// method
				index.sortSearchTerms(lines);
				threadPool.execute(new SearchFinder(lines));
			
	}
	
	private class SearchFinder implements Runnable{
		
		String[] searchTerms;
		
		SearchFinder(String [] searchTerms){
			threadPool.addPending();
			this.searchTerms = searchTerms;
		}
		
		public void run() {
//			HashMap<String, SearchResult> searchResultsList = new HashMap<String, SearchResult>();
			index.findSearches(searchTerms);
			threadPool.subPending();

		}
		
	}
	
}


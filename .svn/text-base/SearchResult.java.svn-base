
public class SearchResult implements Comparable<Object>{

	private int frequency, firstPos;
	private String fileFound;
	
	public SearchResult(String fileFound, int frequency, int firstPos){
		this.frequency = frequency;
		this.fileFound = fileFound;
		this.firstPos = firstPos;
	}
	
	public int compareTo(Object arg0) {
		if(frequency - ((SearchResult) arg0).getFrequency() == 0){
			if(firstPos - ((SearchResult) arg0).getFirstPos() == 0){
				return fileFound.compareTo(((SearchResult) arg0).getFileFound());
			}else{
				return  firstPos - ((SearchResult) arg0).getFirstPos();
			}		
		}else{
			return ((SearchResult) arg0).getFrequency() - frequency;
		}
		
	}


	public String getFileFound() {
		return fileFound;
	}


	public void setFileFound(String fileFound) {
		this.fileFound = fileFound;
	}


	public int getFrequency() {
		return frequency;
	}


	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}


	public int getFirstPos() {
		return firstPos;
	}


	public void setFirstPos(int firstPos) {
		this.firstPos = firstPos;
	}

}

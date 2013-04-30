import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLParser {
	
	private File inputFile, outputFile;
	
	private static final String pregex = "^([a-zA-Z][a-zA-Z0-9+.-]*)://";
	private static final String dregex = "([^/?#]+)";
	private static final String rregex = "(/[^?#]*)?";
	private static final String qregex = "(?:\\?([^#]*))?";
	private static final String fregex = "(?:#(.*))?$";
	

	public HTMLParser(String inputFile, String outputFile){
		this.inputFile = new File(inputFile);
		this.outputFile = new File(outputFile);
	}
	
	public void parseHtml(){

		try {
			String regex =	"(?i)(<a[^>]* href[^=]*=[^(\")]*\")" + "([^\"]*)";
			Pattern patt = Pattern.compile(regex);
			String regex2 = "(?i)(<a)";
			Pattern patt2 = Pattern.compile(regex2);
			
			
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			BufferedReader  reader = new BufferedReader(new FileReader(inputFile));
			String textLine = null;
			Matcher m = null;
			Matcher m2 = null;
			String newline = null;
			while((textLine = reader.readLine()) != null){
				m = patt.matcher(textLine);
				m2 = patt2.matcher(textLine);
				
				int start = 0;
				if(m.find()){
					while(m.find(start)){
						writer.println(m.group(2));
						start = m.end();
						writer.flush();
					}
				}else if(m2.find()){
					while(!m.find()){
						if((newline = reader.readLine()) != null){
							textLine = textLine + newline;
							m = patt.matcher(textLine);
						}else{
							break;
						}
					}
					while(m.find(start)){
						writer.println(m.group(2));
						start = m.end();
						writer.flush();
					}
				}
				

			}
			
			writer.close();
			reader.close();
			System.out.println("Finished writing file");
		} catch (FileNotFoundException e) {
			System.out.println("File cannot be found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File cannot be opened");
		}
	}
}	

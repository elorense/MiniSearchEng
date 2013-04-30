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


public class HTMLTagsStripper {
 
	private File input, output;
	
	public HTMLTagsStripper(String input, String output){
		this.input = new File(input);
		this.output = new File(output);
	}
	
	public void stripTags(){
		String regex = "(?i)(<)([^>]*)(>)([^<]*)";
		String regex2  = "(?i)(<)";
		String regex3 = "(?i)(<)([^>]*)(>)";
		Pattern patt = Pattern.compile(regex);
		Pattern patt2 = Pattern.compile(regex2);
		Pattern patt3 = Pattern.compile(regex3);
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
			BufferedReader  reader = new BufferedReader(new FileReader(input));
			
			String textLine = null;
			String newline = null;
			Matcher m = null;
			Matcher m2 = null;
			Matcher m3 = null;
			
			
			while((textLine = reader.readLine()) != null){
				m = patt.matcher(textLine);
				m2 = patt2.matcher(textLine);
				m3 = patt3.matcher(textLine);
				int start = 0;
				
				if(m.find()){
					if(m.group(2).toLowerCase().contains("script") || m.group(2).toLowerCase().contains("style")){
						while(m3.find()){
							if(m3.find()){
								break;
							}
//							System.out.println(m3.group() + "------------------");
							if((newline = reader.readLine()) != null){
								textLine = textLine + newline;
								m3 = patt.matcher(textLine);
						}
					}
					}else{
						while(m.find(start) ){
							writer.print(m.group(4));
							start = m.end();
						}
						writer.println();
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
							writer.print(m.group(4));
							start = m.end();
						}
						writer.println();
				}else{
					writer.println(textLine);
				}
		}
			
			writer.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

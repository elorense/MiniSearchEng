import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Fetcher{

	Socket socket;
	
	protected static final int PORT = 80;
	protected URLParser url;
	protected String domain;
	protected String resource;
	private ArrayList<String> urlList;
	private WorkQueue threadPool;
	private Lock lock;
	private FileParser fp;
	
	
	public Fetcher(String url, WorkQueue threadPool, InvertedIndex index)
	{
		fp = new FileParser(index, lock);
		lock = new Lock();
		this.threadPool = threadPool;
		this.urlList = new ArrayList<String>();
		urlList.add(url);
		
	}

	protected String craftRequest(URLParser url)
	{
		StringBuffer output = new StringBuffer();
		output.append("GET " + url.resource + " HTTP/1.1\n");
		output.append("Host: " + url.domain + "\n");
		output.append("Connection: close\n");
		output.append("\r\n");
		
		return output.toString();
	}
	
	public void fetch(String newurl, HashMap<String, ArrayList<Integer>> subIndex) 
	{	
		StringBuilder line = new StringBuilder("");
		URLParser url = new URLParser(newurl);
		if (url.resource == null || url.domain == null)
		{
			System.err.printf("There is no domain or resource to fetch.\n");
			return;
		}
		
		Socket socket;
		try {
			socket = new Socket(url.domain, PORT);
		
		
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			
			BufferedReader reader = 
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			String request = craftRequest(url);
			
			writer.println(request);
			writer.flush();
	
			String regex =	"(?i)(<a[^>]* href[^=]*=[^(\")]*\")" + "([^\"]*)" + "([^>]*>)" + "([^<]*)";
			Pattern patt = Pattern.compile(regex);
			String regex2 = "(?i)(<a)";
			Pattern patt2 = Pattern.compile(regex2);
			String regex3 = "(?i)(<)([^>]*)(>)([^<]*)";
			String regex4  = "(?i)(<)";
			String regex5 = "(?i)(<)([^>]*)(>)";
			Pattern patt3 = Pattern.compile(regex3);
			Pattern patt4 = Pattern.compile(regex4);
			Pattern patt5 = Pattern.compile(regex5);
	
			Matcher m3 = null;
			Matcher m4 = null;
			Matcher m5 = null;
			String textLine = null;
			Matcher m = null;
			Matcher m2 = null;
			String newline = null;
			
			while(!(textLine = reader.readLine()).equals("")){
				
			}
			
			while((textLine = reader.readLine()) != null){
				m3 = patt3.matcher(textLine);
				m4 = patt4.matcher(textLine);
				m5 = patt5.matcher(textLine);
				int start = 0;
				
				if(m3.find()){
					if(m3.group(0).toLowerCase().contains("script") || m3.group(0).toLowerCase().contains("style")){
						
						if(textLine.contains("style")){
							while(!textLine.toLowerCase().contains("/style") &&(textLine = reader.readLine()) != null){
								
							}
						}
						if(textLine.contains("script")){
							while(!textLine.toLowerCase().contains("/script") &&(textLine = reader.readLine()) != null){
								
							}
						}
						
					}else if(m3.group(0).toLowerCase().contains("href") && m3.group(0).toLowerCase().contains("<a")){
						String link = m3.group(0).substring(m3.group(0).indexOf("\"") + 1, m3.group(0).indexOf("\"", m3.group(0).indexOf("\"") + 1));
						if(link.startsWith("http:")){
							lock.acquireReadLock();	
							
							if(!urlList.contains(link) && urlList.size() < 30){
								
								lock.releaseReadLock();
								lock.acquireWriteLock();
								urlList.add(link);
								lock.releaseWriteLock();
								threadPool.execute(new ParseWords(link));
							}
						}else{
							URL testURL;
							try {
								testURL = new URL(url.toString());
								URL newUrl = new URL(testURL, link);
							
							
							lock.acquireReadLock();	
							
							if(!urlList.contains(newUrl.toString()) && urlList.size() < 30){
								
								lock.releaseReadLock();
								lock.acquireWriteLock();
								urlList.add(newUrl.toString());
								lock.releaseWriteLock();
								threadPool.execute(new ParseWords(newUrl.toString()));
							}
							} catch (MalformedURLException e) {
							}
	
						}
						line = line.append(m3.group(4) + " ");
						lock.releaseReadLock();

					}
					
					else{
						while(m3.find(start) ){
							if(m3.group(0).toLowerCase().contains("href") && m3.group(0).toLowerCase().contains("<a")){
								String link = m3.group(0).substring(m3.group(0).indexOf("\"") + 1, m3.group(0).indexOf("\"", m3.group(0).indexOf("\"") + 1));
								if(link.startsWith("http:")){
									lock.acquireReadLock();	
									
									if(!urlList.contains(link) && urlList.size() < 30){
										
										lock.releaseReadLock();
										lock.acquireWriteLock();
										urlList.add(link);
										lock.releaseWriteLock();
										threadPool.execute(new ParseWords(link));
									}
								}else{
									URL testURL;
									try {
										testURL = new URL(url.toString());
										URL newUrl = new URL(testURL, link);
									
									
									lock.acquireReadLock();	
									
									if(!urlList.contains(newUrl.toString()) && urlList.size() < 30){
										
										lock.releaseReadLock();
										lock.acquireWriteLock();
										urlList.add(newUrl.toString());
										lock.releaseWriteLock();

										threadPool.execute(new ParseWords(newUrl.toString()));
									}
									} catch (MalformedURLException e) {
									}}
							}
							line = line.append(m3.group(4) + " ");
							lock.releaseReadLock();
							start = m3.end();
						}
						
					}
				}else if(m4.find()){
						while(!m3.find()){
							if((newline = reader.readLine()) != null){
								textLine = textLine + newline;
								m3 = patt3.matcher(textLine);
							}else{
								break;
							}
						}
						while(m3.find(start)){
							if(m3.group(0).toLowerCase().contains("href") && m3.group(0).toLowerCase().contains("<a")){
								String link = m3.group(0).substring(m3.group(0).indexOf("\"") + 1, m3.group(0).indexOf("\"", m3.group(0).indexOf("\"") + 1));
								if(link.startsWith("http:")){
									lock.acquireReadLock();	
									
									if(!urlList.contains(link) && urlList.size() < 30){
										
										lock.releaseReadLock();
										lock.acquireWriteLock();
										urlList.add(link);
										lock.releaseWriteLock();
										
										threadPool.execute(new ParseWords(link));
									}
								}else{
									URL testURL;
									try {
										testURL = new URL(url.toString());
										URL newUrl = new URL(testURL, link);
									
									
									lock.acquireReadLock();	
									
									if(!urlList.contains(newUrl.toString()) && urlList.size() < 30){
										
										lock.releaseReadLock();
										lock.acquireWriteLock();
										urlList.add(newUrl.toString());
										lock.releaseWriteLock();
										
										threadPool.execute(new ParseWords(newUrl.toString()));
									}
									} catch (MalformedURLException e) {
									}}
							}
							line = line.append(m3.group(4) + " ");

//							System.out.println(m3.group(4));
							start = m3.end();
						}
				}else{
					line = line.append(textLine + " ");

				}
				
		}

			fp.wordMap(line, newurl);

			reader.close();
			writer.close();
			socket.close();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	

	
	public void printList(){
		for(String url : urlList){
			System.out.println(url);
		}
	}
	
	private class ParseWords implements Runnable{
		String path;
		
		ParseWords(String path){
			threadPool.addPending();

			
			this.path = path;
		}
		
		public void run() {
			
			try {
				HashMap<String, ArrayList<Integer>> subIndex = new HashMap<String, ArrayList<Integer>>();
				fetch(path, subIndex);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadPool.subPending();

		}
		
		
	}
}


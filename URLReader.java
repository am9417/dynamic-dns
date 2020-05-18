import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import java.util.ArrayList;

/**
 * <p>The point of this class is to simplify the reading pages from the Web in Java projects.</p>
 * 
 * <p>User can generate an instance of this class and use it like a BufferedReader or just simply 
 * use the static methods from the class to fetch the contents neatly.</p>
 * 
 * <p>Made by <a href="mailto:w101297@student.uwasa.fi" target="_blank">Konsta Mäenpänen</a> 
 * <a href="http://uva.fi/~w101297" target="_blank">(http://uva.fi/~w101297)</a>
 * at <a href="http://uva.fi" target="_blank">the University of Vaasa</a> in September 2016. 
 * This package is downloadable at 
 * <a href="http://uva.fi/~w101297/public/URLReader.tar.gz">http://uva.fi/~w101297/public/URLReader.tar.gz</a></p>
 * 
 * @author 		Konsta Mäenpänen 
 * @version		1.0, final version
 * 
 */

public class URLReader extends BufferedReader {
	
	/**
	 * 	<p>Generates a new URLReader instance with the given URL address and UTF-8 encoding.</p>
	 * 
	 * @param url	The URL to be opened
	 * 
	 * @throws MalformedURLException 	if the given URL is not valid
	 * @throws IOException 				if the stream couldn't be opened
	 * 
	 */
	public URLReader(String url) throws MalformedURLException, IOException {	
		
		super(new InputStreamReader((new URL(url)).openConnection().getInputStream(), Charset.forName("UTF-8")));

	}
	
	/**
	 * <p>Generates a new URLReader instance with the given URL address and given character encoding.</p>
	 * 
	 * @param url		The URL to be opened
	 * @param encoding 	Used character encoding
	 * 
	 * @throws MalformedURLException 		if the given URL is not valid
	 * @throws UnsupportedCharsetException 	if the desired charset is not supported
	 * @throws IOException 					if the stream couldn't be opened
	 * 
	 */
	public URLReader(String url, String encoding) throws MalformedURLException, UnsupportedCharsetException, IOException {	
		
		super(new InputStreamReader((new URL(url)).openConnection().getInputStream(), Charset.forName(encoding)));

	}

	/**
	 * <p>Opens the URL address and gets the contents of the page as a list.</p>
	 * 
	 * <p><b>This method uses UTF-8 encoding as default.</b> 
	 * Use {@link URLReader#getLines(String, String)} to specify different character encoding.</p>
	 * 
	 * @param url	The URL to be opened
	 * 
	 * @return		An ArrayList of type String, each element containing the contents of a single row
	 * 
	 * @throws MalformedURLException 	if the given URL is not valid
	 * @throws IOException 				if the stream couldn't be opened or closed
	 * 
	 */
	public static ArrayList<String> getLines(String url) throws MalformedURLException, IOException {
		
		ArrayList<String> lines = new ArrayList<String>();
		URLReader reader = null;
		
		try {
		
			String tempLine = null;
			reader = new URLReader(url);
			
			while((tempLine = reader.readLine()) != null)
				lines.add(tempLine);
				
		} catch(Exception e) {
			
			try {reader.close();} catch(Exception e2) {}
			throw e;
		}
		
		return lines;
		
	}
	
	/**
	 * <p>Opens the URL address and gets the contents of the page as a list with given encoding.</p>
	 * 
	 * @param url		The URL to be opened
	 * @param encoding	Used character encoding
	 * 
	 * @return			An ArrayList of type String, each element containing the contents of a single row
	 * 
	 * @throws MalformedURLException 		if the given URL is not valid
	 * @throws IOException 					if the stream couldn't be opened or closed
	 * @throws UnsupportedCharsetException	if the character encoding was not supported
	 * 
	 */	
	public static ArrayList<String> getLines(String url, String encoding) throws IOException, MalformedURLException, UnsupportedCharsetException {
		
		URLReader reader = null;
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
		
			String tempLine = null;
			reader = new URLReader(url, encoding);
			
			while((tempLine = reader.readLine()) != null)
				lines.add(tempLine);
		
		} catch(Exception e) {
			
			try {reader.close();} catch(Exception e2) {}
			throw e;
		}
		
		return lines;
		
	}

	/**
	 * <p>Opens the URL address and gets a single row from the page.</p>
	 * 
	 * <p><b>Please note that the indexing starts from one (1)</b>. 
	 * Thus the first line wouldn't be 0 and an IndexOutOfBoundException is thrown.</p>
	 * 
	 * <p><b>This method uses UTF-8 encoding as default.</b>
	 * Use {@link URLReader#getLine(String, int, String)} to specify different character encoding.</p>
	 * 
	 * @param url		The URL to be opened
	 * @param line		The number of the row 1 means the first line of the page.
	 * 
	 * @return			The contents of the line as a String
	 * 
	 * @throws MalformedURLException 		if the given URL is not valid
	 * @throws IOException 					if the stream couldn't be opened or closed
	 * @throws IndexOutOfBoundsException	if the desired row number was out of bounds
	 * 
	 */	
	public static String getLine(String url, int line) throws MalformedURLException, IOException, IndexOutOfBoundsException {
		
		ArrayList<String> lines = getLines(url);
		return lines.get(line - 1);
		
	}

	/**
	 * <p>Opens the URL address and gets a single row from the page with the given character encoding.</p>
	 * 
	 * <p><b>Please note that the indexing starts from one (1)</b>. 
	 * Thus the first line wouldn't be 0 and an IndexOutOfBoundException is thrown.</p>
	 * 
	 * @param url		The URL to be opened
	 * @param line		The number of the row 1 means the first line of the page.
	 * @param encoding	Used character encoding
	 * 
	 * @return			The contents of the line as a String
	 * 
	 * @throws MalformedURLException 		if the given URL is not valid
	 * @throws IOException 					if the stream couldn't be opened or closed
	 * @throws UnsupportedCharsetException	if the character encoding was not supported
	 * @throws IndexOutOfBoundsException	if the desired row number was out of bounds
	 * 
	 */	
	public static String getLine(String url, int line, String encoding) throws MalformedURLException, IOException, UnsupportedCharsetException, IndexOutOfBoundsException {
		
		ArrayList<String> lines = getLines(url, encoding);
		return lines.get(line - 1);
		
	}
	
	/**
	 * <p>The method to test the functionality of the URLReader.</p>
	 * 
	 * <p>Run <i><code>java URLReader [address]</code></i> to print all the contents of 
	 * a page in given address with UTF-8 encoding.</p>
	 * 
	 * <p>Run <i><code>java URLReader [address] [line number]</code></i> to print the
	 * desired line from the given address with UTF-8 encoding.</p>
	 * 
	 * @param args			Command line parameters.
	 * 
	 * 						The first parameter is required and it's the URL of 
	 * 						which contents will be printed. The second parameter
	 * 						is optional and it is the line number that will be
	 * 						printed.
	 * 
	 * @throws Exception 	if any exception was caught.
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1) {
			System.out.println("You need to define the URL that will be fetched.");
			System.out.println("Run 'java URLReader <url address> (<line number>)'");
		}
	
		else if(args.length == 1) {
			
			for(String s : getLines(args[0]))
				System.out.println(s);
				
		}
		
		else
			System.out.println(getLine(args[0], Integer.parseInt(args[1])));
		
	}
	
	
}

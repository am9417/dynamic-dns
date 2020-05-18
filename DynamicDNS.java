import java.util.ArrayList;

import java.net.MalformedURLException;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.StringJoiner;

/**
 * This is a class to dynamically change the DNS records if there is a 
 * change in the IP address of the server. Usually there is if you are 
 * hosting on your home server.
 * 
 * In default, this client is made for NameCheap domains but with small
 * changes, it can work with other domain providers too.
 * 
 */
public class DynamicDNS {
	
	private ArrayList<String> hosts;
	private String domain;
	private String password;
	private String url;
	private String currentIp;
	private long updateFrequency;

	private static final String IP_INFO_URL = "http://ipinfo.io/ip";
	private static final String NAMECHEAP_URL = "https://dynamicdns.park-your-domain.com/update?host=[host]&domain=[domain]&password=[password]&ip=[ip]";
	private static final String DEFAULT_FILE_LOCATION = "default.conf";
	private static final long DEFAULT_UPDATE_FREQUENCY = 30*1000;
	
	
	public static void main(String[] args) throws InterruptedException {

		String configFile = (args.length > 0) ? args[0] : DEFAULT_FILE_LOCATION;
		DynamicDNS instance = loadFromFile(configFile);
		
		if(instance == null) {
			System.out.println("!-- Config file was not found or invalid");
			return;
		}
		
		else
			instance.run();

	}

	public DynamicDNS(ArrayList<String> hosts, String url, String domain, String password, long updateFrequency) {
		
		this.hosts = hosts;
		this.domain = domain;
		this.password = password;
		this.currentIp = "";
		this.updateFrequency = (updateFrequency > 0) ? updateFrequency : 1000*30;

	}
	
	private boolean isValid() {
		
		System.out.println(this);
		
		return (this.domain != null && this.password != null && this.hosts.size() > 0 && this.updateFrequency > 0 && this.url != null);
		
	}
	
	public String toString() {
		
		String s = "";
		
		s += "Domain:    \t" + this.domain + "\n";
		s += "Password:  \t" + this.password + "\n";
		s += "Idle time: \t" + this.updateFrequency + "\n";
		s += "Hosts:     \t";
		
		for(String host : this.hosts)
			s += host + " ";
			
		s += "\n";
		s += "Update url \t" + this.url + "\n";
		
		return s;
	}
	
	private static DynamicDNS loadFromFile(String file) {
		
		DynamicDNS temp = new DynamicDNS(new ArrayList<String>(), NAMECHEAP_URL, null, null, DEFAULT_UPDATE_FREQUENCY);
		BufferedReader reader = null;
		
		try {
			
			String line = null;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			
			while((line = reader.readLine()) != null) {
				
				if(line.startsWith("#")) // The # mark is considered as comment annotation.
					continue;
				
				String[] parts = line.split("=");
				
				if(parts.length < 2) // The form is <parameter> = <value>
					continue;
				
				String parameter = parts[0].replaceAll("\\s+","");
				
				StringJoiner sj = new StringJoiner("=");
				for(int i = 1; i < parts.length; i++)
					sj.add(parts[i]);
					
				String value = sj.toString().replaceAll("\\s+","");
				
				if(parameter.equals("host") && !temp.hosts.contains(value))
					temp.hosts.add(value);
					
				else if(parameter.equals("domain"))
					temp.domain = value;
					
				else if(parameter.equals("password") || parameter.equals("key"))
					temp.password = value;
				
				else if(parameter.equals("idletime") || parameter.equals("idle") || parameter.equals("idle_time"))
					temp.updateFrequency = (Long.parseLong(value) > 0) ? (1000 * Long.parseLong(value)) : DEFAULT_UPDATE_FREQUENCY;
					
				else if(parameter.equals("updateurl") || parameter.equals("update_url") || parameter.equals("url"))
					temp.url = value;
				
				
			}
			
			
		} catch(FileNotFoundException e) {
			
			System.out.println("!-- File " + file + " doesn't exist or you don't have rights to read it");
			System.out.println();
			e.printStackTrace();
			
		} catch(IOException e) {
			
			System.out.println("!-- File couldn't be read");
			System.out.println();
			e.printStackTrace();
			
		} catch(NumberFormatException e) {
			
			System.out.println("!-- Invalid number was given to idletime value");
			
			
		} finally {
			
			try {
				reader.close();
			} catch(Exception e2) {}
			
		}
		
		
		return temp.isValid() ? temp : null;
		
	}
	
	public void run() {
		
		while(true) {
			
			try {
				this.updateAllRecords();
				Thread.sleep(this.updateFrequency);
				
			} catch(InterruptedException e) {
				System.out.println("!-- The thread was interrupted");
			}
			
		}
		
	}
	
	private void updateAllRecords() throws InterruptedException {

		String newIp = this.getIp();
		
		if(newIp == null) {
			
			System.out.println("!-- Unable to fetch the IP from " + IP_INFO_URL);
			System.out.println(">> The IP stays unchanged '" + this.currentIp + "'");
			return;
			
		}
		
		if(newIp.equals(this.currentIp))
			return;
			
		System.out.println(">> The IP is changed to '" + newIp + "', updating records now");
		System.out.println();
		
		String refreshUrl = this.url.replaceFirst("\\[domain\\]", this.domain).replaceFirst("\\[password\\]", this.password).replaceFirst("\\[ip\\]", newIp);
		boolean allRecordsUpdated = true;
		
		for(String host : this.hosts) {
			
			if(!this.updateSingleRecord(host, refreshUrl)) {
				
				System.out.println("!-- Failed to change the record " + host + " for " + this.domain);
				allRecordsUpdated = false;
				
			}	
			
			Thread.sleep(5000);
			
		}
		
		if(allRecordsUpdated) {
			
			System.out.println(">> All records for " + this.domain + " were changed successfully.");
			System.out.println(">> Updating current IP to " + newIp);
			this.currentIp = newIp;
			
		}
		
		else
			System.out.println("!-- All the records couldn't be changed. The IP has not been updated");
			
		System.out.println();
		
	}
	
	private boolean updateSingleRecord(String host, String refreshUrl) {
					
		refreshUrl = refreshUrl.replaceFirst("\\[host\\]", host);
		
		try {
					
			String line = URLReader.getLine(refreshUrl, 1);
			String[] parts = line.split("<ErrCount>");
				
			if(parts.length == 2) {
				
				parts = parts[1].split("</ErrCount>");
				
				if(parts.length == 2) {
					
					if(parts[0].toLowerCase().contains("0")) {
						System.out.println(">> Updated the record '" + host + "." + this.domain + "' successfully");
						return true;
					}

						
					else if(parts[1].toLowerCase().contains("1")) {
						System.out.println("!-- Response from '" + refreshUrl + "' contained an error message. Is password/host ok?");
						return false;
					}
					
					else {
						System.out.println("!-- Couldn't parse the response from '" + refreshUrl + "'. Is password/host ok?");
						return false;
					}
						
				}
				
			}
		
		} 
		
		catch(MalformedURLException e) {System.out.println("!-- The update URL '" + refreshUrl + "' is malformed");} 
		catch(Exception e) {System.out.println("!-- Failed to read the response from the update URL '" + refreshUrl + "'");}
		
		System.out.println("!-- Received invalid response from the record updating URL '" + refreshUrl + "'");
		return false;
		
	}
	
	
	private String getIp() {
		
		String ip = null;
		
		try {
			ip = URLReader.getLine(IP_INFO_URL, 1);
		} 
		catch(MalformedURLException e) {System.out.println("!-- IP info URL was malformed");} 
		catch(Exception e) {System.out.println("!-- Failed to read the response from the IP info URL");}
		
		return ip;
		
	}
	
	private boolean isValidIp(String ip) {
			
		String[] parts = ip.split("\\.");
			
		if(parts.length != 4)
			return false;
			
		for(String part : parts) {
			
			int tempInt = Integer.MAX_VALUE;
			
			try {
				tempInt = Integer.parseInt(part);		
						
			} catch(NumberFormatException e) {
				System.out.println("!-- The fetched IP address was not numeric");
				return false;
			}
			
			if(tempInt < 0 || tempInt > 255) {
				System.out.println("!-- A part of the fetched IP address is not in range (0-255)");
				return false;
			}
			
		}

		return true;
		
	}
	
}

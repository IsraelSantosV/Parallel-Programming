/*
 * Developed by: Israel Santos Vieira
 */

package utils;

import java.util.ArrayList;

public class Debug {
	
	public enum LogType { None, BartenderAction, CustomerAction }
	
	private static ArrayList<String> m_BartenderLog_Cache;
	public static ArrayList<String> LogBartender_Cache() {
		if(m_BartenderLog_Cache == null) {
			m_BartenderLog_Cache = new ArrayList<String>();
		}
		
		return m_BartenderLog_Cache;
	}
	
	private static ArrayList<String> m_CustomerLog_Cache;
	public static ArrayList<String> LogCustomer_Cache() {
		if(m_CustomerLog_Cache == null) {
			m_CustomerLog_Cache = new ArrayList<String>();
		}
		
		return m_CustomerLog_Cache;
	}
	
	private static ArrayList<String> m_ControllerLog_Cache;
	public static ArrayList<String> LogController_Cache() {
		if(m_ControllerLog_Cache == null) {
			m_ControllerLog_Cache = new ArrayList<String>();
		}
		
		return m_ControllerLog_Cache;
	}
	
	private static final String BARTENDER_FILE = "Bartender_Log.txt";
	private static final String CUSTOMER_FILE = "Customer_Log.txt";
	private static final String CONTROLLER_FILE = "Controller_Log.txt";
	
	public static void log(String text, LogType targetType) {
		switch (targetType) {
		case BartenderAction: {
			LogBartender_Cache().add(text);
			break;
		}
		case CustomerAction: {
			LogCustomer_Cache().add(text);
			break;
		}
		default:
			break;
		}
		
		LogController_Cache().add(text);
		//System.out.println(prefix + text);
		//System.out.flush();
	}
	
	public static void inlineLog(String text) {
		System.out.print(text);
		System.out.flush();
	}
	
	public static void clearCache() {
		LogBartender_Cache().clear();
		LogCustomer_Cache().clear();
		LogController_Cache().clear();
	}
	
	public static void saveLog() {
		FileManager.saveFile(BARTENDER_FILE, LogBartender_Cache().toArray());
		FileManager.saveFile(CUSTOMER_FILE, LogCustomer_Cache().toArray());
		FileManager.saveFile(CONTROLLER_FILE, LogController_Cache().toArray());
	}

}

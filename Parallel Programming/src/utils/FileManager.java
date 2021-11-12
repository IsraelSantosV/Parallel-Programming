package utils;

import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
	
	public static void saveFile(String path, Object[] data) {
		try (FileWriter file = new FileWriter(path)) {
			for (Object obj : data) {
				try {
					String text = (String) obj;
					file.write(text + "\n");
				}
				catch (Exception e) {
					System.out.println("Error on save data: " + obj + ": " + e);
				}
			}
		} 
		catch (IOException e) {
			System.out.println("Error on save file: " + path);
		}
	}
}

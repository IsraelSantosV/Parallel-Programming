/*
 * Developed by: Israel Santos Vieira
 */

package utils;

import java.util.Random;

public class Randomic {
	
	private static Randomic m_Instance;
	
	public static Randomic getInstance() {
		if(m_Instance == null) {
			m_Instance = new Randomic();
		}
		
		return m_Instance;
	}
	
	private Random m_Random;
	
	private Randomic() {
		m_Random = new Random();
	}
	
	public static boolean getChance(float chance) {
		var rand = getInstance().m_Random;
		return rand.nextFloat() < chance;
	}
	
	public static long getRandomDelay(int min, int max) {
		var rand = getInstance().m_Random;
		var value = rand.nextInt((max - min) + 1) + min;
		return (long)value;
	}

}

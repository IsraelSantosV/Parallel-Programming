/*
 * Developed by: Israel Santos Vieira
 */

package core;
import java.util.Scanner;

import utils.Debug;
import utils.Debug.LogType;

public class Main {
	
	public static void main(String[] args) {
		try {
			String customers = args[0];
			String bartenders = args[1];
			String bartenderCapacity = args[2];
			String rounds = args[3];
			
			int castCustomers = Integer.parseInt(customers);
			int castBartenders = Integer.parseInt(bartenders);
			int castBartenderCapacity = Integer.parseInt(bartenderCapacity);
			int castRounds = Integer.parseInt(rounds);
			
			showOpenMessage();
			var currentPub = new Pub(castCustomers, castBartenders, castBartenderCapacity, castRounds);
			currentPub.open();
		} 
		catch (Exception e) {
			callDefaultInput();
		}
	}
	
	private static void callDefaultInput() {
		var input = new Scanner(System.in);
		showOpenMessage();
		
		Debug.inlineLog("Insira a quantidade de clientes: ");
		var clients = input.nextInt();
		
		Debug.inlineLog("Insira a quantidade de garçons: ");
		var bartenders = input.nextInt();
		
		Debug.inlineLog("Insira quantos clientes o garçom pode atender por vez: ");
		var bartenderCapacity = input.nextInt();
		
		Debug.inlineLog("Insira quantos rounds serão ofertados: ");
		var rounds = input.nextInt();
		input.close();
		
		var currentPub = new Pub(clients, bartenders, bartenderCapacity, rounds);
		currentPub.open();
	}
	
	private static void showOpenMessage() {
		System.out.println("Inicializando a execução, aguarde o término...");
		Debug.log("Abrindo um novo bar...", LogType.None);
	}

}

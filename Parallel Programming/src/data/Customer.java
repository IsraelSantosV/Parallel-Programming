/*
 * Developed by: Israel Santos Vieira
 */

package data;

import core.Pub;
import utils.Debug;
import utils.Randomic;
import utils.Debug.LogType;

public class Customer extends Thread {
	
	public enum CustomerStateMachine {
		PERFORMING_ORDER,
		WAITING_ORDER,
		CONSUMING_REQUEST,
		WAITING_NEXTROUND
	}
	
	private final int MIN_CONSUME_DELAY = 500;
	private final int MAX_CONSUME_DELAY = 1200;
	private final float MAKE_ORDER_CHANCE = 0.6f;
	
	private Pub m_Pub;
	private CustomerStateMachine m_CurrentState;
	private boolean m_WillOrder;
	
	public CustomerStateMachine getCurrentState() { return m_CurrentState; }
	
	public Customer(int id, Pub pub) {
		super();
		m_Pub = pub;
		m_WillOrder = Randomic.getChance(MAKE_ORDER_CHANCE);
		m_CurrentState = CustomerStateMachine.PERFORMING_ORDER;
		start();
	}
	
	//Thread's main method
	public void run() {
		while(m_Pub.isOpen()) {
			try {
				sleep(Pub.Tick);
			} catch (InterruptedException e) {
				return; // Caused by thread.interrupt()
			}
			
			//Execution Line
			switch (m_CurrentState) {
			case PERFORMING_ORDER: { //Action State
				casePerformingOrder();
				break;
			}
			case WAITING_ORDER:{ //Controller State
				caseWaitingOrder();
				break;
			}
			case CONSUMING_REQUEST:{ //Intermediary State
				caseConsumingRequest();
				break;
			}
			case WAITING_NEXTROUND:{ //Controller State
				caseWaitingNextRound();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + m_CurrentState);
			}
		}
	}
	
	private void casePerformingOrder() {
		if(m_WillOrder) {
			var bartender = m_Pub.getValidBartender();
			if(bartender == null) {
				return;
			}
			
			Debug.log("Cliente " + getId() + " fez um pedido para o garçom " + bartender.getId(), LogType.CustomerAction);
			changeCurrentState(CustomerStateMachine.WAITING_ORDER);
			bartender.registerOrder(this);
		}
		else {
			//When the customer does not place an order,
			//change the current state to wait for the round to end
			Debug.log("Cliente " + getId() + " decidiu não fazer um pedido nessa rodada!", LogType.CustomerAction);
			prepareToNextRound();
		}
	}
	
	//Order keep the customer in this state to wait for the waiter to deliver the item
	private void caseWaitingOrder() { } 
	
	private void caseConsumingRequest() {
		//Wait a random delay to consume item
		var delay = Randomic.getRandomDelay(MIN_CONSUME_DELAY, MAX_CONSUME_DELAY);
		Debug.log("Cliente " + getId() + " está consumindo o pedido! Duração: " + ((float)delay / 1000) + "s", LogType.CustomerAction);
		
		try { sleep(delay); }
		catch (InterruptedException e) {
			Debug.log("Cliente foi interrompido enquanto comia!", LogType.CustomerAction);
			e.printStackTrace();
		}
		
		Debug.log("Cliente " + getId() + " terminou de comer, esperando por uma nova rodada!", LogType.CustomerAction);
		prepareToNextRound();
	}
	
	//Keep the customer in this state to wait for the pub to release a new round
	private void caseWaitingNextRound() { }
	
	public void changeCurrentState(CustomerStateMachine state) {
		m_CurrentState = state;
	}
	
	//ACTION METHODS
	
	public void prepareToNextRound() {
		m_WillOrder = Randomic.getChance(MAKE_ORDER_CHANCE);
		changeCurrentState(CustomerStateMachine.WAITING_NEXTROUND);
	}
	
	public void receiveRequest(Bartender bartender) {
		Debug.log("Cliente " + getId() + " recebeu o pedido do garçom " + bartender.getId(), LogType.CustomerAction);
		changeCurrentState(CustomerStateMachine.CONSUMING_REQUEST);
	}
}

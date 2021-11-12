/*
 * Developed by: Israel Santos Vieira
 */

package data;

import java.util.ArrayList;

import core.Pub;
import utils.Debug;
import utils.Debug.LogType;

public class Bartender extends Thread implements Comparable<Bartender> {
	
	public enum BartenderStateMachine {
		RECEIVING_ORDERS,
		REGISTERING_ORDER,
		MAKING_REQUESTS,
		DELIVERING_REQUESTS,
		ENDING_SERVICE
	}
	
	private final long Making_Request_Delay = 1000;
	private final long Delay_To_Return = 800;
	
	private Pub m_Pub;
	private BartenderStateMachine m_CurrentState;
	private ArrayList<Customer> m_Orders;
	
	public BartenderStateMachine getCurrentState() { return m_CurrentState; }
	
	public int getOrdersAmount() { 
		synchronized (m_Orders) {
			return m_Orders.size(); 
		}
	}
	
	public Bartender(Pub myPub) {
		super();
		m_Pub = myPub;
		m_Orders = new ArrayList<Customer>();
		m_CurrentState = BartenderStateMachine.RECEIVING_ORDERS;
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
			
			switch (m_CurrentState) {
			case RECEIVING_ORDERS: {
				caseReceivingOrders();
				break;
			}
			case REGISTERING_ORDER:{
				caseRegisteringOrders();
				break;
			}
			case MAKING_REQUESTS:{
				caseMakingRequests();
				break;
			}
			case DELIVERING_REQUESTS:{
				caseDeliveringRequests();
				break;
			}
			case ENDING_SERVICE:{
				caseEndingService();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + m_CurrentState);
			}
		}
	}
	
	private void caseReceivingOrders() {
		//If there are no customers to order and the number of orders is greater than 0
		if(!m_Pub.hasCustomerOrdered() && getOrdersAmount() > 0) {
			Debug.log(this.getId() + " foi registrar pedidos por falta de clientes com " + getOrdersAmount() + " pedidos!", LogType.BartenderAction);
			changeCurrentState(BartenderStateMachine.MAKING_REQUESTS);
		}
		//If there are no customers to order and the quantity of orders is equal to 0
		else if(!m_Pub.hasCustomerOrdered() && getOrdersAmount() == 0) {
			Debug.log("Garçom " + getId() + " tentando terminar o serviço dessa rodada!", LogType.BartenderAction);
			changeCurrentState(BartenderStateMachine.ENDING_SERVICE);
		}
	}
	
	private void caseRegisteringOrders() {
		//If the number of orders is less than the maximum capacity
		if(getOrdersAmount() < m_Pub.getBartenderCapacity()) {
			try { sleep(Pub.RegisterDuration); } 
			catch (InterruptedException e) { return; }
			
			enableBartender();
		}
		//If the quantity ordered is equal to the maximum capacity or 
		//(there is no customer to order and the quantity of orders is greater than 0)
		else if(getOrdersAmount() == m_Pub.getBartenderCapacity() || (!m_Pub.hasCustomerOrdered() && getOrdersAmount() > 0)) {
			Debug.log("Garçom " + getId() + " foi fazer " + getOrdersAmount() + " pedidos!", LogType.BartenderAction);
			changeCurrentState(BartenderStateMachine.MAKING_REQUESTS);
		}
		else {
			Debug.log("Error on size: " + getOrdersAmount(), LogType.BartenderAction);
		}
	}
	
	private void caseMakingRequests() {
		try {
			sleep(Making_Request_Delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Debug.log("Garçom " + getId() + " terminou de preparar os pedidos!", LogType.BartenderAction);
		changeCurrentState(BartenderStateMachine.DELIVERING_REQUESTS);
	}
	
	private void caseDeliveringRequests() {
		synchronized (m_Orders) {
			//Start with index 0 so that the first customer to be served is the first to consume
			for(int i = 0; i < getOrdersAmount(); i++) {
				var customer = m_Orders.get(i);
				Debug.log("Garçom " + getId() + " entregando o pedido de " + customer.getId(), LogType.BartenderAction);
				customer.receiveRequest(this);
			}
			
			m_Orders.clear();
			try { sleep(Delay_To_Return); } 
			catch (Exception e) { }
			
			Debug.log("Garçom " + getId() + " disponível para novos pedidos!", LogType.BartenderAction);
			enableBartender();
		}
	}
	
	private void caseEndingService() {
		m_Pub.bartenderFinishService(this);
		Debug.log("Garçom " + getId() + " terminou o serviço dessa rodada!", LogType.BartenderAction);
		if(m_Pub.allBartenderFinishService()) {
			//Ensure that only one waiter changes rounds
			Debug.log("Garçom " + getId() + " trocou a rodada atual!", LogType.BartenderAction);
			m_Pub.nextRound();
			
			m_Pub.notifyAllBartenders();
			if(m_Pub.isOpen()) {
				m_Pub.enableAllBartenders();
			}
		}
		//If all bartenders haven't finished the job yet, put the thread to wait
		else {
			synchronized (this) {
				try { this.wait(); }
				catch (Exception e) { }
			}
		}
	}
	
	public void changeCurrentState(BartenderStateMachine state) {
		m_CurrentState = state;
	}
	
	//ACTION METHODS
	
	public void registerOrder(Customer customer) {
		synchronized (m_Orders) {
			m_Orders.add(customer);
			Debug.log("Um novo pedido foi registrado para o garçom " + getId() + ": " + getOrdersAmount() + 
					"/" + m_Pub.getBartenderCapacity(), LogType.BartenderAction);
			
			changeCurrentState(BartenderStateMachine.REGISTERING_ORDER);
		}
	}
	
	public void enableBartender() {
		m_Pub.insertBartender(this);
		changeCurrentState(BartenderStateMachine.RECEIVING_ORDERS);
	}
	
	@Override
	public int compareTo(Bartender o) {
		if(o.getOrdersAmount() < this.getOrdersAmount()) {
			return -1;
		}
		
		if(o.getOrdersAmount() > this.getOrdersAmount()) {
			return 1;
		}
		
		return 0;
	}

}

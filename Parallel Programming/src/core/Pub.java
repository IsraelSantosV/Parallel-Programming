/*
 * Developed by: Israel Santos Vieira
 */

package core;
import data.Bartender;
import data.Customer;
import data.Customer.CustomerStateMachine;
import utils.Debug;
import utils.Debug.LogType;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Pub {
	
	public static long Tick = 800;
	public static long RegisterDuration = 1000;
	private final long NextRoundDelay = 1500;
	
	private int m_CustomerAmount;
	private int m_BartenderAmount;
	private int m_BartenderCapacity;
	private int m_RoundsAmount;
	private boolean m_IsOpen;
	
	private int m_CurrentRound;
	private ArrayList<Customer> m_Customers;
	private ArrayList<Bartender> m_Bartenders;
	private PriorityQueue<Bartender> m_ValidBartenders;
	private ArrayList<Bartender> m_FinishedBartenders;
	
	public int getBartenderCapacity() { return m_BartenderCapacity; }
	
	public Pub(int customerAmount, int bartenderAmount, int bartenderCapacity, int roundsAmount) {
		m_CustomerAmount = customerAmount;
		m_BartenderCapacity = bartenderCapacity;
		m_BartenderAmount = bartenderAmount;
		m_RoundsAmount = roundsAmount;
		m_IsOpen = false;
		m_CurrentRound = 0;
	}
	
	public void open() {
		m_IsOpen = true;
		m_CurrentRound = 1;
		Debug.log("INICIANDO UMA NOVA RODADA: (" + m_CurrentRound + "/" + m_RoundsAmount + ")", LogType.BartenderAction);
		
		m_Bartenders = new ArrayList<Bartender>();
		m_FinishedBartenders = new ArrayList<Bartender>();
		m_Customers = new ArrayList<Customer>();
		m_ValidBartenders = new PriorityQueue<Bartender>();
		
		for (int i = 0; i < m_CustomerAmount; i++) {
			m_Customers.add(new Customer(i, this));
		}
		
		for (int i = 0; i < m_BartenderAmount; i++) {
			var bartender = new Bartender(this);
			m_Bartenders.add(bartender);
			m_ValidBartenders.add(bartender);
		}
		
		for (Customer customer : m_Customers) {
			try { customer.join(); } 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (Bartender bartender : m_Bartenders) {
			try { bartender.join(); }
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Debug.log("O bar fechou, a execução de todas as threads foram finalizadas!", LogType.None);
		Debug.saveLog();
		System.out.println("Execução terminada com sucesso! Os arquivos de log estão presentes na pasta raiz!");
	}
	
	public boolean isOpen() {
		return m_CurrentRound <= m_RoundsAmount && m_IsOpen;
	}
	
	public void nextRound() {
		if(m_CurrentRound < m_RoundsAmount) {
			m_CurrentRound++;
			
			Debug.log("INICIANDO UMA NOVA RODADA: (" + m_CurrentRound + "/" + m_RoundsAmount + ")", LogType.BartenderAction);
			
			synchronized (this) {
				try { this.wait(NextRoundDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			synchronized (m_Customers) {
				for (Customer customer : m_Customers) {
					customer.changeCurrentState(CustomerStateMachine.PERFORMING_ORDER);
				}
			}
			
			m_FinishedBartenders.clear();
		}
		else {
			//Ending
			m_IsOpen = false;
		}
	}
	
	//Gets a valid bartender, being the first on the heap
	public Bartender getValidBartender(){
		synchronized (m_ValidBartenders) {
			if(m_ValidBartenders.size() > 0) {
				return m_ValidBartenders.poll();
			}
			
			return null;
		}
	}
	
	//Inserts a bartender who has already finished his service in the heap
	public void insertBartender(Bartender barterder) {
		synchronized (m_ValidBartenders) {
			if(!m_ValidBartenders.contains(barterder)) {
				m_ValidBartenders.add(barterder);
			}
		}
	}
	
	//Returns true if there is at least one customer who has not yet placed an order
	public boolean hasCustomerOrdered() {
		synchronized (m_Customers) {
			for (Customer customer : m_Customers) {
				if(customer.getCurrentState() == CustomerStateMachine.PERFORMING_ORDER) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	//Returns true if all customers have already placed an order in this round
	public boolean allCustomersPlacedOrders() {
		synchronized (m_Customers) {
			for (Customer customer : m_Customers) {
				if(customer.getCurrentState() != CustomerStateMachine.WAITING_NEXTROUND) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	//Add a bartender to the list of those who have finished the job
	public void bartenderFinishService(Bartender bartender) {
		synchronized (m_FinishedBartenders) {
			if(!m_FinishedBartenders.contains(bartender)) {
				m_FinishedBartenders.add(bartender);
			}
		}
	}
	
	//Returns true if all bartenders have finished serving for this round
	public boolean allBartenderFinishService() {
		synchronized (m_FinishedBartenders) {
			return m_FinishedBartenders.size() >= m_Bartenders.size();
		}
	}
	
	//Changes the current state of all bartenders to initial and places them in the heap
	public void enableAllBartenders() {
		synchronized (m_Bartenders) {
			for (Bartender bartender : m_Bartenders) {
				bartender.enableBartender();
			}
		}
	}
	
	//Releases bartenders who are in the wait state
	public void notifyAllBartenders() {
		synchronized (m_Bartenders) {
			for (Bartender bartender : m_Bartenders) {
				synchronized (bartender) {
					bartender.notify();
				}
			}
		}
	}

}

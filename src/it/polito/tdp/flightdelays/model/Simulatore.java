package it.polito.tdp.flightdelays.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Simulatore {
	
	
	private int k; // numero passeggeri
	private int v; // numero voli
	Random random;
	
	private List<Airport> airports;
	private Queue<Event> queue;
	private FlightDelaysDAO dao;
	private List<Passeggero> passeggeri;
	
	

	public void init(int k, int v, List<Airport> airports, FlightDelaysDAO dao) {
		this.k=k;
		this.v=v;
		this.airports = new LinkedList<>(airports);
		
		queue = new LinkedList<>();
		
		this.dao=dao;
		
		random = new Random();
		
		passeggeri = new ArrayList<>();
		
		for(int i=0; i<k; i++) {
			
			int x = random.nextInt(airports.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
			Passeggero p = new Passeggero(i);
			passeggeri.add(p);
			Airport casualAirport = airports.get(x);
			Flight volo = dao.findFirstFlight(casualAirport.getId(), LocalDateTime.of(2015, 1, 1, 0, 0, 0));
		
			//Event e = new Event(p, casualAirport, 
				//	dao.findFirstFlight(casualAirport, LocalDateTime.of(2015, 1, 1, 0, 0, 0)).getScheduledDepartureDate()); // assegno un passeggero a un aeroporto casuale
		
			if(volo != null) {
				Event e = new Event(p, volo);
				queue.add(e);
			}
		}
		
	}

	
	public void run() {
		
		Event e;
		while((e = this.queue.poll()) != null) {
			processEvent(e);
		}
		
	}


	private void processEvent(Event e) {

		Passeggero passeggero = e.getPasseggero();
		
		if(passeggero.getNumVoli() < v) {
			// se il passeggero non ha raggiunto il numero di voli massimo che può fare
			Flight flight = e.getFlight();
			//System.out.println("CIAO: "+flight.getSource()+ " " + flight.getDestination()+"\n");
			
			if(flight != null) {
				// se ci sono ancora voli in partenza dall aeroporto considerato
				
				String destination = flight.getDestinationAirportId();
				
				// il passeggero fa un altro viaggio a partire dall aeroporto in cui è atterrato (destination, che diventa la partenza) 
				// e in una data successiva a quella di arrivo, che sarà la nuova data di partenza
				Event e1 = new Event(passeggero, dao.findFirstFlight(destination, flight.getArrivalDate()));
				queue.add(e1); // aggiungo l'evento alla coda
				
				// il passeggero viaggia, incremento il numero di voli fatti
				passeggero.setNumVoli();
				
				// calcolo il ritardo accumulato di ciascun passeggero
				int ritardo = flight.getArrivalDelay();
				passeggero.setRitardoAccumulato(ritardo);
			}
		}
	}

	
	public List<Passeggero> getPasseggeri() {
		return this.passeggeri;
	}
}

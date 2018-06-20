package it.polito.tdp.flightdelays.model;

import java.time.LocalDateTime;

public class Event {
	
	private Passeggero passeggero;
	private Airport airport;
	private LocalDateTime partenza;
	private Flight flight;

	public Event(Passeggero p, Flight flight) {
		this.passeggero=p;
		this.flight=flight;
	}
	
	public Event(Passeggero p, Airport airport, LocalDateTime partenza) {
		this.passeggero=p;
		this.airport=airport;
		this.partenza=partenza;
	}

	public Passeggero getPasseggero() {
		return passeggero;
	}

	public void setPasseggero(Passeggero passeggero) {
		this.passeggero = passeggero;
	}

	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public LocalDateTime getPartenza() {
		return partenza;
	}

	public void setPartenza(LocalDateTime partenza) {
		this.partenza = partenza;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}
	
	
	
	

	
	
}

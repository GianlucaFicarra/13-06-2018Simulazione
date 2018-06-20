package it.polito.tdp.flightdelays.model;

import java.time.LocalDateTime;

public class Passeggero {
	
	private int id;
	private int numVoli; // deve arrivare al max a v voli
	private LocalDateTime partenza;
	private int ritardoAccumulato;
	
	public Passeggero(int id) {
		this.id=id;
		this.numVoli = 0;
		ritardoAccumulato = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumVoli() {
		return numVoli;
	}

	public void setNumVoli() {
		this.numVoli++;
	}

	public LocalDateTime getPartenza() {
		return partenza;
	}

	public void setPartenza(LocalDateTime partenza) {
		this.partenza = partenza;
	}

	public int getRitardoAccumulato() {
		return ritardoAccumulato;
	}

	public void setRitardoAccumulato(int ritardoAccumulato) {
		this.ritardoAccumulato += ritardoAccumulato;
	}
	
	
	
	
	
}

package it.polito.tdp.flightdelays.model;

public class Tratta {
	
	private Airport source;
	private Airport destination;
	private double media;
	private double peso;
	private double distanza;
	
	
	public Tratta(Airport source, Airport destination, double media) {
		super();
		this.source = source;
		this.destination = destination;
		this.media = media;
	}
	public Airport getSource() {
		return source;
	}
	public void setSource(Airport source) {
		this.source = source;
	}
	public Airport getDestination() {
		return destination;
	}
	public void setDestination(Airport destination) {
		this.destination = destination;
	}
	public double getMedia() {
		return media;
	}
	public void setMedia(double media) {
		this.media = media;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso() {
		peso = (double)media/((double)distanza);
	}
	public void setDistanza(double distanza) {
		this.distanza =distanza;
	}
	public double getDistanza() {
		return distanza;
	}
	
	

}

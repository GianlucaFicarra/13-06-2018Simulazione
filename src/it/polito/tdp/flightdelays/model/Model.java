package it.polito.tdp.flightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;
import com.javadocmd.simplelatlng.*;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Model {

	private List<Airline> airlines;
	private FlightDelaysDAO dao;
	private List<Airport> airports;
	private List<Flight> flights; // ho solo i voli della linea aerea selezionata
	private AirportIdMap airportMap;
	private Graph<Airport, DefaultWeightedEdge> graph;
	private List<Tratta> tratte;
	
	public Model() {
		dao = new FlightDelaysDAO();
		airportMap = new AirportIdMap();
		airlines = dao.loadAllAirlines();
		airports = dao.loadAllAirports(airportMap);
		tratte = new LinkedList<>();
	}
	
	public List<Airline> getAirlines() {
		return this.airlines;
	}

	public void creaGrafo(Airline airline) {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.graph, this.airports);
		
		flights = dao.loadFlightsWithAirline(airline, airportMap);
		tratte = dao.getRitardoMedioSuTratte(airline, airportMap);

		for(Flight f : flights) {
			Airport source = f.getSource();
			Airport destination = f.getDestination();
			
			if(source != null && destination != null && !source.equals(destination)) {
				
				// peso = mediaRitardi/distanza

				//double mediaRitardi = dao.getRitardoMedioSuTratta(airline, source, destination);
				//tratte.add(new Tratta(source, destination, mediaRitardi));
				double distanza = LatLngTool.distance(new LatLng(source.getLatitude(),
						source.getLongitude()), new LatLng(destination.getLatitude(), destination.getLongitude()), 
						LengthUnit.KILOMETER);
				double media = 0.0;
				for(Tratta t : tratte) {
					if(source.equals(t.getSource()) && destination.equals(t.getDestination())) {
						media = t.getMedia();
						t.setDistanza(distanza);
						t.setPeso();
					}
				}
				double peso = media/distanza;
				
				
				Graphs.addEdge(this.graph, source, destination, peso);
			}
		}
		
		System.out.println("Vertici: "+graph.vertexSet().size());
		System.out.println("Archi: "+graph.edgeSet().size());
	}
	
	
	public List<Tratta> getPeggioriRotte() {
		
		
		Collections.sort(tratte, new Comparator<Tratta>() {

			@Override
			public int compare(Tratta t1, Tratta t2) {
				return Double.compare(t2.getPeso(), t1.getPeso());
			}
			
		});
		
		return tratte.subList(0, 10);
	}
}

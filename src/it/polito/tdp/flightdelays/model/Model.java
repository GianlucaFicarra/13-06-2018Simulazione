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
		//airports = dao.loadAllAirports(airportMap);
		tratte = new LinkedList<>();
	}
	
	public List<Airline> getAirlines() {
		return this.airlines;
	}

	// punto 1 - grafo
	
	public void creaGrafo(Airline airline) {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		airports = dao.getAirportFromAirline(airline, airportMap);
		Graphs.addAllVertices(this.graph, this.airports);
		
//		flights = dao.loadFlightsWithAirline(airline, airportMap);
		tratte = dao.getRitardoMedioSuTratte(airline, airportMap);
		
		for(Tratta t : tratte) {
			Airport source = t.getSource();
			Airport destination = t.getDestination();
			
			if(source != null && destination != null && !source.equals(destination)) {
				double distanza = LatLngTool.distance(new LatLng(source.getLatitude(),
						source.getLongitude()), new LatLng(destination.getLatitude(), destination.getLongitude()), 
						LengthUnit.KILOMETER);
				double peso = t.getMedia()/distanza;
				t.setPeso(peso);
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

	
	// punto 2 - simulatore
	
	public List<Passeggero> simula(int k, int v) {
		Simulatore sim = new Simulatore();
		sim.init(k, v, airports, dao);
		sim.run();
		
		return sim.getPasseggeri();
	}
}

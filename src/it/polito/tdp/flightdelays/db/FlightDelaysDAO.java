package it.polito.tdp.flightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Airport;
import it.polito.tdp.flightdelays.model.AirportIdMap;
import it.polito.tdp.flightdelays.model.Flight;
import it.polito.tdp.flightdelays.model.Tratta;

public class FlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT id, airline from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getString("ID"), rs.getString("airline")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> getAirportFromAirline(Airline airline, AirportIdMap airportMap) {
		
		String sql = "select distinct a.id as id, a.airport as airport, a.city as city, a.state as state, a.country as country, a.latitude as lat, a.longitude as lon from airports as a, flights as f " + 
				"where f.AIRLINE= ? " + 
				"and (f.ORIGIN_AIRPORT_ID = a.ID or f.DESTINATION_AIRPORT_ID = a.ID)";
		
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airline.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("lat"), rs.getDouble("lon"));
				result.add(airportMap.get(airport));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Airport> loadAllAirports(AirportIdMap airportMap) {
		String sql = "SELECT id, airport, city, state, country, latitude, longitude FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"));
				result.add(airportMap.get(airport));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT id, airline, flight_number, origin_airport_id, destination_airport_id, scheduled_dep_date, "
				+ "arrival_date, departure_delay, arrival_delay, air_time, distance FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Flight> loadFlightsWithAirline(Airline airline, AirportIdMap airportMap) {
		String sql = "SELECT id, flight_number, origin_airport_id, destination_airport_id, scheduled_dep_date, "
				+ "arrival_date, departure_delay, arrival_delay, air_time, distance FROM flights WHERE airline = ?";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airline.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), airline.getId(), rs.getInt("flight_number"),
						airportMap.get(rs.getString("origin_airport_id")), airportMap.get(rs.getString("destination_airport_id")),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public double getRitardoMedioSuTratta(Airline airline, Airport source, Airport destination) {
		String sql = "SELECT AVG(ARRIVAL_DELAY) as media " + 
				"from flights " + 
				"where airline= ? " + 
				"and origin_airport_id = ? " + 
				"and destination_airport_id = ?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airline.getId());
			st.setString(2, source.getId());
			st.setString(3, destination.getId());
			ResultSet rs = st.executeQuery();
			double media=0.0;
			if (rs.next()) {
				media = rs.getDouble("media");
			}

			conn.close();
			return media;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	public List<Tratta> getRitardoMedioSuTratte(Airline airline, AirportIdMap airportMap) {
		String sql = "SELECT AVG(ARRIVAL_DELAY) as media, origin_airport_id as o, destination_airport_id as d " + 
				"from flights " + 
				"where airline= ? " + 
				"group by origin_airport_id, destination_airport_id";
		List<Tratta> result = new LinkedList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airline.getId());
			ResultSet rs = st.executeQuery();
			double media=0.0;
			while (rs.next()) {
				media = rs.getDouble("media");
				Airport origin = airportMap.get(rs.getString("o"));
				Airport destination = airportMap.get(rs.getString("d"));
				if(origin != null && destination != null)
					result.add(new Tratta(origin, destination, media));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public LocalDateTime findFirstDate(Airport airport, LocalDateTime data) {
		String sql = "select SCHEDULED_DEP_DATE as partenza " + 
				"from flights where ORIGIN_AIRPORT_ID = ? " + 
				"and SCHEDULED_DEP_DATE > ? " + 
				"order by SCHEDULED_DEP_DATE asc " + 
				"LIMIT 1";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airport.getId());
			st.setString(2, data.toString());
			ResultSet rs = st.executeQuery();
			LocalDateTime partenza = null;
			if (rs.next()) {
				partenza = rs.getTimestamp("partenza").toLocalDateTime();
			}

			conn.close();
			return partenza;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public Flight findFirstFlight(String airportId, LocalDateTime data) {
		String sql = "select * " + 
				"from flights as f, airports as a " + 
				"where f.ORIGIN_AIRPORT_ID = ?" + 
				"and f.SCHEDULED_DEP_DATE > ? " + 
				"and f.DESTINATION_AIRPORT_ID = a.ID " + 
				"order by f.SCHEDULED_DEP_DATE asc " + 
				"LIMIT 1";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airportId);
			st.setString(2, data.toString());
			ResultSet rs = st.executeQuery();
			Flight flight = null;
			if (rs.next()) {
				flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
			}

			conn.close();
			return flight;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
}

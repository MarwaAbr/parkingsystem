package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, int recurring) throws Exception {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		// ParkingService parkingService = new ParkingService(null, null, null);
		Date inHour = ticket.getInTime();
		Date outHour = ticket.getOutTime();

// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = outHour.getTime() - inHour.getTime();
		double hourNbr = duration / 3600000;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {

			if (hourNbr < 0.5) {
				ticket.setPrice(0);
			} else if (recurring >= 2) {
				ticket.setPrice((hourNbr * Fare.CAR_RATE_PER_HOUR) * 0.95);
			}

			else {
				ticket.setPrice(hourNbr * Fare.CAR_RATE_PER_HOUR);
			}
			break;

		}
		case BIKE: {

			if (hourNbr < 0.5) {
				ticket.setPrice(0);
			} else if (recurring >= 2) {
				ticket.setPrice((hourNbr * Fare.BIKE_RATE_PER_HOUR) * 0.95);
			} else {
				ticket.setPrice(hourNbr * Fare.BIKE_RATE_PER_HOUR);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

	}

	public int recurring(Ticket ticket) throws ClassNotFoundException, SQLException {

		Connection con = null;
		DataBaseConfig dataBaseConfig = new DataBaseConfig();

		con = dataBaseConfig.getConnection();

		String sql = "SELECT COUNT(*) AS total FROM ticket WHERE VEHICLE_REG_NUMBER=?";

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setString(1, ticket.getVehicleRegNumber());
		ResultSet rs = statement.executeQuery();
		rs.next();

		return rs.getInt("total");

	}

}
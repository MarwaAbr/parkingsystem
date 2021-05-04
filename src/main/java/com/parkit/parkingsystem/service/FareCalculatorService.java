package com.parkit.parkingsystem.service;

import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, int recurring) throws Exception {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		Date inHour = ticket.getInTime();
		Date outHour = ticket.getOutTime();

		// calculate duration in ms
		double duration = outHour.getTime() - inHour.getTime();
		double hourNbr = duration / 3600000;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			// parking car Free With Less Than 30Min
			if (hourNbr < 0.5) {
				ticket.setPrice(0);

			}
			// 5% discount for recurring users, call "recurring" function
			else if (recurring >= 2) {
				ticket.setPrice((hourNbr * Fare.CAR_RATE_PER_HOUR) * 0.95);
			}

			else {
				ticket.setPrice(hourNbr * Fare.CAR_RATE_PER_HOUR);
			}
			break;

		}
		case BIKE: {
			// parking bike Free With Less Than 30Min
			if (hourNbr < 0.5) {
				ticket.setPrice(0);

			}
			// 5% discount for recurring users, call "recurring" function
			else if (recurring >= 2) {
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

}
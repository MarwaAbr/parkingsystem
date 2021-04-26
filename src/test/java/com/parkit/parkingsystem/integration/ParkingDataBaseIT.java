package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	static Connection con = null;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();

	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		dataBasePrepareService.clearDataBaseEntries();

	}

	@AfterAll
	private static void tearDown() {
		dataBaseTestConfig.closeConnection(con);

	}

	@Test
	public void testParkingACar() throws ClassNotFoundException, SQLException {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability

		con = dataBaseTestConfig.getConnection();

		String sql = "SELECT VEHICLE_REG_NUMBER FROM ticket WHERE ID=?";
		String sql1 = "SELECT AVAILABLE FROM parking WHERE PARKING_NUMBER=?";
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setInt(1, 1);
		ResultSet rs = statement.executeQuery();
		rs.next();
		PreparedStatement statement1 = con.prepareStatement(sql1);
		statement1.setInt(1, 1);
		ResultSet rs1 = statement1.executeQuery();
		rs1.next();

		assertThat(rs.getString(1)).isEqualTo("ABCDEF");
		assertThat(rs1.getInt(1)).isEqualTo(0);

	}

	@Test
	public void testParkingLotExit() throws Exception {

		testParkingACar();

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		con = dataBaseTestConfig.getConnection();

		String sql2 = "SELECT PRICE FROM ticket WHERE ID=?";
		String sql3 = "SELECT OUT_TIME FROM ticket WHERE ID=?";
		PreparedStatement statement2 = con.prepareStatement(sql2);
		statement2.setInt(1, 1);
		ResultSet rs2 = statement2.executeQuery();
		rs2.next();
		PreparedStatement statement3 = con.prepareStatement(sql3);
		statement3.setInt(1, 1);
		ResultSet rs3 = statement3.executeQuery();
		rs3.next();
		assertThat(rs2.getDouble(1)).isEqualTo(ticketDAO.getTicket("ABCDEF").getPrice());
		assertThat(rs3.getTimestamp(1)).isEqualTo(ticketDAO.getTicket("ABCDEF").getOutTime());

	}

}

package utility;

import model.Flight;
import java.time.format.DateTimeFormatter;

public class TicketGenerator {
    public static String generate(Flight flight, String destination) {
        if (flight == null) {
            return "hat-UNKNOWN-DATE";
        }

        String flightNo = flight.getFlightID() != null ? flight.getFlightID().trim() : "";
        
        String destPart = "XYZ";
        if (destination != null && destination.trim().length() >= 3) {
            String cleaned = destination.trim();
            destPart = cleaned.substring(cleaned.length() - 3);
        } else if (destination != null) {
            destPart = destination.trim();
        }

        // Apply time layout parsing
        String datePart = "";
        if (flight.getDepartureDateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmm");
            datePart = flight.getDepartureDateTime().format(formatter);
        }

        return ("hat" + flightNo + destPart + datePart).toLowerCase();
    }
}
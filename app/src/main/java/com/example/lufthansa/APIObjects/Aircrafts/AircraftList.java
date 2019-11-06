package com.example.lufthansa.APIObjects.Aircrafts;

public class AircraftList {
    private static Aircraft[] listOfAircraft = {
            // Airbus
            new Aircraft("318", "Airbus A318"),
            new Aircraft("319", "Airbus A319"),
            new Aircraft("31A", "Airbus A318 (sharklets)"),
            new Aircraft("31B", "Airbus A319 (sharklets)"),
            new Aircraft("31N", "Airbus A319neo"),
            new Aircraft("320", "Airbus A320"),
            new Aircraft("321", "Airbus A321"),
            new Aircraft("32A", "Airbus A320 (sharklets)"),
            new Aircraft("32F", "Airbus A320 Freighter"),
            new Aircraft("32B", "Airbus A321 (sharklets)"),
            new Aircraft("32V", "Airbus A320 (WL)"),
            new Aircraft("32N", "Airbus A320neo"),
            new Aircraft("32Q", "Airbus A321neo"),
            new Aircraft("332", "Airbus A330-200"),
            new Aircraft("333", "Airbus A330-300"),
            new Aircraft("343", "Airbus A340-300"),
            new Aircraft("346", "Airbus A340-600"),
            new Aircraft("351", "Airbus A350-1000"),
            new Aircraft("358", "Airbus A350-800"),
            new Aircraft("359", "Airbus A350-900"),
            new Aircraft("388", "Airbus A380-800"),

            // Boeing
            new Aircraft("738", "Boeing 737-800"),
            new Aircraft("739", "Boeing 737-900"),
            new Aircraft("73G", "Boeing 737-700"),
            new Aircraft("73H", "Boeing 737-800 (winglets)"),
            new Aircraft("73J", "Boeing 737-900 (winglets)"),
            new Aircraft("744", "Boeing 747-400"),
            new Aircraft("74H", "Boeing 747-800"),
            new Aircraft("752", "Boeing 757-200"),
            new Aircraft("753", "Boeing 757-300"),
            new Aircraft("75T", "Boeing 757-300 (winglets)"),
            new Aircraft("75W", "Boeing 757-200 (winglets)"),
            new Aircraft("762", "Boeing 767-200"),
            new Aircraft("763", "Boeing 767-300"),
            new Aircraft("764", "Boeing 767-400"),
            new Aircraft("76W", "Boeing 767-300 (winglets)"),
            new Aircraft("772", "Boeing 777-200"),
            new Aircraft("773", "Boeing 777-300"),
            new Aircraft("777", "Boeing 777-200ER"),
            new Aircraft("779", "Boeing 777-900"),
            new Aircraft("77L", "Boeing 777-200LR"),
            new Aircraft("77W", "Boeing 777-300ER"),
            new Aircraft("788", "Boeing 787-8"),
            new Aircraft("789", "Boeing 787-9"),

            // regional jets
            new Aircraft("CR7", "Canadair Regional Jet 700"),
            new Aircraft("CR9", "Canadair Regional Jet 900"),
            new Aircraft("CRJ", "Canadair Regional Jet CRJ-900LR"),
            new Aircraft("DH4", "De Havilland (Bombardier) DHC-8-400 Dash 8Q"),
            new Aircraft("ER4", "Embraer RJ145"),
    };

    public static String getAirCraftName(String airCraftCode) {
        String name;

        for (int index = 0; index < listOfAircraft.length; index++) {
            if(listOfAircraft[index].getAirCraftCode().equals(airCraftCode))
                return listOfAircraft[index].getAirCraftName();
        }
        return "unknown";
    }
}

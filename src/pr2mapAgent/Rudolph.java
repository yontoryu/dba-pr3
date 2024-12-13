package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Rudolph extends Agent {
    private Map<String, int[]> reindeerLocations = new HashMap<>();
    private int foundReindeerCount = 0;
    private String expectedSecretCode;

    @Override
    protected void setup() {
        // Populate reindeer locations randomly
        String[] reindeerNames = {"Dasher", "Dancer", "Vixen", "Prancer", "Cupid", "Comet", "Blitzen", "Donner"};
        Random random = new Random();

        for (String name : reindeerNames) {
            int[] location = {
                    random.nextInt(10),  // x coordinate
                    random.nextInt(10)   // y coordinate
            };
            reindeerLocations.put(name, location);
        }

        // Register Rudolph in the Directory Facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("rudolph-service");
        sd.setName("Rudolph-Agent");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            System.err.println("Error registering Rudolph agent: " + e.getMessage());
        }

        // Add behavior to handle coordinate requests
        addBehaviour(new HandleCoordinateRequestBehaviour());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            System.err.println("Error deregistering Rudolph agent: " + e.getMessage());
        }
    }

    // Inner class for handling coordinate requests
    private class HandleCoordinateRequestBehaviour extends jade.core.behaviours.CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    // Check secret code for authorization
                    String[] parts = msg.getContent().split(":");
                    String receivedCode = parts[0].trim();
                    String request = parts.length > 1 ? parts[1].trim() : "";

                    ACLMessage reply = msg.createReply();

                    if (receivedCode.equals(expectedSecretCode)) {
                        if (request.equals("VALIDATE")) {
                            // Initial validation request
                            reply.setPerformative(ACLMessage.CONFIRM);
                            reply.setContent("Bro Rudolph confirms communication channel En Plan");
                        } else if (request.equals("GET_LOCATIONS")) {
                            // Send reindeer locations
                            reply.setPerformative(ACLMessage.INFORM);
                            StringBuilder locations = new StringBuilder();
                            for (Map.Entry<String, int[]> entry : reindeerLocations.entrySet()) {
                                if (entry.getValue() != null) {
                                    locations.append(entry.getKey())
                                            .append(":")
                                            .append(entry.getValue()[0])
                                            .append(",")
                                            .append(entry.getValue()[1])
                                            .append(";");
                                }
                            }
                            reply.setContent("Bro Reindeer locations: " + locations.toString() + " En Plan");
                        } else {
                            // Check if a specific reindeer is found
                            String[] locationParts = request.split(":");
                            if (locationParts.length == 3) {
                                String reindeerName = locationParts[1];
                                int[] coordinates = {
                                        Integer.parseInt(locationParts[2].split(",")[0]),
                                        Integer.parseInt(locationParts[2].split(",")[1])
                                };

                                if (reindeerLocations.containsKey(reindeerName)) {
                                    reindeerLocations.remove(reindeerName);
                                    foundReindeerCount++;

                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContent("Bro Reindeer " + reindeerName + " found! Remaining: " +
                                            (8 - foundReindeerCount) + " En Plan");

                                    if (foundReindeerCount == 8) {
                                        reply.setContent("Bro All reindeer found! En Plan");
                                    }
                                }
                            }
                        }
                    } else {
                        // Unauthorized access
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("Bro Invalid secret code En Plan");
                    }

                    send(reply);
                }
            } else {
                block();
            }
        }
    }

    // Method to set the expected secret code
    public void setSecretCode(String code) {
        this.expectedSecretCode = code;
    }
}
package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.*;
import java.util.Map;

public class Rudolph extends Agent {
    private Map<String, int[]> reindeerLocations = new HashMap<>();
    private int foundReindeerCount = 0;
    private String secretCode = "69420";
    private int state = 0;

    @Override
    protected void setup() {
        // Populate reindeer locations randomly
        String[] reindeerNames = {"Dasher", "Dancer", "Vixen", "Prancer", "Cupid", "Comet", "Blitzen", "Donner"};
        Random random = new Random();
        Set<String> usedLocations = new HashSet<>();

        for (String name : reindeerNames) {
            int[] location;

            // Generate a unique location
            do {
                location = new int[]{
                        random.nextInt(10),  // x coordinate
                        random.nextInt(10)   // y coordinate
                };
            } while (!usedLocations.add(Arrays.toString(location))); // Ensure the location is unique

            reindeerLocations.put(name, location);
        }

        // Add behavior to handle coordinate requests
        addBehaviour(new HandlePositionRequestBehaviour(this));
    }

    public boolean understood(String msg) {
        return msg.startsWith("Bro") && msg.endsWith("En Plan");
    }

    public void setCommunicationState(int state) {
        this.state = state;
    }

    public boolean verifyScout(String secretCode) {
        return this.secretCode.equals(secretCode);
    }

    public int getCommunicationState() {
        return state;
    }

    public void startAgent(ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Rudoplh", "pr2mapAgent.Rudolph", null);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to set the expected secret code
    public void setSecretCode(String code) {
        this.expectedSecretCode = code;
    }
}
package pr2mapAgent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.*;
import java.util.Map;

public class Rudolph extends Agent {
    private Map<String, int[]> reindeerLocations;
    private List<String> shuffledKeys;
    private int currentReindeerId = 0;
    private String secretCode;
    private int state;
    private String currentReindeerName;

    @Override
    protected void setup() {
        // Retrieve startup arguments
        Object[] args = getArguments();
        Environment env = (Environment) args[2];
        int width = env.getWidth();
        int height = env.getHeight();

        state = 0;
        secretCode = "69420 xddd";

        reindeerLocations = env.reindeerLocations();

        // randomly shuffle reindeer keys
        shuffledKeys = new ArrayList<>(reindeerLocations.keySet());
        Collections.shuffle(shuffledKeys);

        // Add behavior to handle coordinate requests
        addBehaviour(new HandlePositionRequestBehaviour(this));
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

    public int[] findNextReindeer() {
        if (currentReindeerId >= shuffledKeys.size()) {
            return null;
        }

        currentReindeerName = shuffledKeys.get(currentReindeerId);
        int[] position = reindeerLocations.get(currentReindeerName);
        currentReindeerId++;

        return position;
    }

    public String getCurrentReindeerName() {
        return currentReindeerName;
    }

    public void startAgent(Object[] args, ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Rudolph", "pr2mapAgent.Rudolph", args);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
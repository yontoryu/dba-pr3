package pr2mapAgent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.*;
import java.util.Map;

public class Rudolph extends Agent {
    private Map<String, int[]> reindeerLocations;
    private List<String> shuffledKeys;
    private int currentReindeer = 0;
    private String secretCode;
    private int state;

    @Override
    protected void setup() {
        state = 0;
        secretCode = "69420";
        reindeerLocations = new HashMap<>();

        String[] reindeerNames = {"Dasher", "Dancer", "Vixen", "Prancer", "Cupid", "Comet", "Blitzen", "Donner"};
        Random random = new Random();
        Set<String> usedPositions = new HashSet<>();

        // generate unique positions
        for (String name : reindeerNames) {
            int[] position;
            do {
                position = new int[]{random.nextInt(10), random.nextInt(10)};
            } while (!usedPositions.add(Arrays.toString(position)));
            reindeerLocations.put(name, position);
        }

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
        String nextReindeer = shuffledKeys.get(currentReindeer);
        int[] position = reindeerLocations.get(nextReindeer);
        currentReindeer++;

        return position;
    }

    public void startAgent(ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Rudoplh", "pr2mapAgent.Rudolph", null);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
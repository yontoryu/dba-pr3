package pr2mapAgent;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.Random;

public class Santa extends Agent {
    private Random trustRandom = new Random();
    private String secretCode = "69420 xddd";
    private int state = 0;
    private int[] position;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            position = (int[]) args[1];
        } else {
            System.out.println("No arguments provided.");
        }

        // Add behavior to handle volunteer requests
        addBehaviour(new HandleVolunteerRequestBehaviour(this));
    }

    // Determine if an agent is trustworthy (80% chance)
    public boolean isTrustworthyAgent() {
        return trustRandom.nextDouble() <= 0.8;
    }

    public int getCommunicationState() {
        return state;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setCommunicationState(int state) {
        this.state = state;
    }

    public void setPosition(int[] pos) {
        this.position = pos;
    }

    public int[] getPosition() {
        return position;
    }

    public void startAgent(Object[] args, ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Santa", "pr2mapAgent.Santa", args);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import java.util.Random;

public class Santa extends Agent {
    private Random trustRandom = new Random();
    private String secretCode = "69420";
    private int state = 0;
    private int[] position;

    @Override
    protected void setup() {
        // Register Santa in the Directory Facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("santa-service");
        sd.setName("Santa-Claus-Agent");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            System.err.println("Error registering Santa agent: " + e.getMessage());
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

    public boolean understood(String msg) {
        return msg.startsWith("Rakas Joulupukki") && msg.endsWith("Kiitos");
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            System.err.println("Error deregistering Santa agent: " + e.getMessage());
        }
    }
}
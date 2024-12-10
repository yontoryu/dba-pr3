package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import java.util.Random;

public class SantaClausAgent extends Agent {
    private Random trustRandom = new Random();
    private String secretCode;

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
        addBehaviour(new HandleVolunteerRequestBehaviour());
    }

    // Generate a random secret code for trusted agents
    public String generateSecretCode() {
        return "SECRET_" + System.currentTimeMillis();
    }

    // Determine if an agent is trustworthy (80% chance)
    public boolean isTrustworthyAgent() {
        return trustRandom.nextDouble() <= 0.8;
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            System.err.println("Error deregistering Santa agent: " + e.getMessage());
        }
    }

    // Inner class for handling volunteer requests
    private class HandleVolunteerRequestBehaviour extends jade.core.behaviours.CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    ACLMessage reply = msg.createReply();

                    if (isTrustworthyAgent()) {
                        // Agent is trustworthy
                        secretCode = generateSecretCode();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setContent("Rakas Joulupukki Trustworthy agent accepted. Secret code: " + secretCode + " Kiitos");
                    } else {
                        // Agent is not trustworthy
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent("Rakas Joulupukki Not trustworthy. Mission denied. Kiitos");
                    }

                    send(reply);
                }
            } else {
                block();
            }
        }
    }
}
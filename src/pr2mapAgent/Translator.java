package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Translator extends Agent {
    @Override
    protected void setup() {
        // Add behavior to handle translations
        addBehaviour(new TranslationBehaviour(this));
    }

    public void startAgent(ContainerController mainContainer) {
        try {
            AgentController agent = mainContainer.createNewAgent("Translator", "pr2mapAgent.Translator", null);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String translateMessage(String message) {
        // Simple translation logic
        if (message.startsWith("Bro") && message.endsWith("En Plan")) {
            // Agent to Santa translation
            return message.replace("Bro", "Rakas Joulupukki")
                    .replace("En Plan", "Kiitos");
        } else if (message.startsWith("Hyvää joulua") && message.endsWith("Nähdään pian")) {
            // Santa to Agent translation
            return message.replace("Hyvää joulua", "Bro")
                    .replace("Nähdään pian", "En Plan");
        }
        return message;
    }
}
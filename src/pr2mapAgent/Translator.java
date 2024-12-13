package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;

public class Translator extends Agent {
    @Override
    protected void setup() {

        ServiceDescription sd = new ServiceDescription();
        sd.setType("translator-service");
        sd.setName("Elf-Translator-Agent");

        // Add behavior to handle translations
        addBehaviour(new TranslationBehaviour());
    }

    // Inner class for translation behavior
    private class TranslationBehaviour extends jade.core.behaviours.CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.blockingReceive();

            // Translate messages
            String translatedContent = translateMessage(msg.getContent());

            // Create a new message with translated content
            ACLMessage translatedMsg = new ACLMessage(msg.getPerformative());
            translatedMsg.setContent(translatedContent);
            translatedMsg.setSender(msg.getSender());
            translatedMsg.addReceiver(msg.getSender());

            send(translatedMsg);
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
}
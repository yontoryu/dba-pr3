package pr2mapAgent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;

public class ElfTranslatorAgent extends Agent {
    @Override
    protected void setup() {
        // Register Elf Translator in the Directory Facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("translator-service");
        sd.setName("Elf-Translator-Agent");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            System.err.println("Error registering Elf Translator: " + e.getMessage());
        }

        // Add behavior to handle translations
        addBehaviour(new TranslationBehaviour());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            System.err.println("Error deregistering Elf Translator: " + e.getMessage());
        }
    }

    // Inner class for translation behavior
    private class TranslationBehaviour extends jade.core.behaviours.CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                // Translate messages
                String translatedContent = translateMessage(msg.getContent());

                // Create a new message with translated content
                ACLMessage translatedMsg = new ACLMessage(msg.getPerformative());
                translatedMsg.setContent(translatedContent);
                translatedMsg.setSender(msg.getSender());
                translatedMsg.addReceiver(msg.getSender());

                send(translatedMsg);
            } else {
                block();
            }
        }

        private String translateMessage(String message) {
            // Simple translation logic
            if (message.startsWith("Bro")) {
                // Agent to Santa translation
                return message.replace("Bro", "Rakas Joulupukki")
                        .replace("En Plan", "Kiitos");
            } else if (message.startsWith("Rakas Joulupukki")) {
                // Santa to Agent translation
                return message.replace("Rakas Joulupukki", "Bro")
                        .replace("Kiitos", "En Plan");
            }
            return message;
        }
    }
}
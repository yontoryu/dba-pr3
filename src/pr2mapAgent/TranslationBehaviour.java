package pr2mapAgent;

import jade.lang.acl.ACLMessage;

public class TranslationBehaviour extends jade.core.behaviours.CyclicBehaviour {
    private Translator translator;

    TranslationBehaviour(Translator translator) {
        this.translator = translator;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive();

        if (msg.getPerformative() == ACLMessage.REQUEST) {
            // Translate messages
            String translatedContent = translator.translate(msg.getContent());

            // Create a new message with translated content
            ACLMessage translatedMsg = msg.createReply();
            translatedMsg.setContent(translatedContent);

            myAgent.send(translatedMsg);
        } else {
            System.out.println("Error in the conversation protocol with Translator");
            myAgent.doDelete();
        }
    }
}
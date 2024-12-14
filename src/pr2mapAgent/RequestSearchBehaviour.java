package pr2mapAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RequestSearchBehaviour extends Behaviour {
    Scout scout;

    RequestSearchBehaviour(Scout scout) {
        this.scout = scout;
    }

    @Override
    public void action() {
        ACLMessage msg;

        int state = scout.getCommunicationState();
        System.out.println("msg.getContent()");


        switch (state) {
            case 0 -> {
                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("Bro Santa, can I help you search for your reindeers? En Plan");
                System.out.println("HALLOOOOO TRANSLATOR");
                msg.setSender(myAgent.getAID());
                msg.addReceiver(new AID("Translator", AID.ISLOCALNAME));
                myAgent.send(msg);

                ACLMessage translatedMsg = myAgent.blockingReceive();

                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent(translatedMsg.getContent());
                msg.setSender(myAgent.getAID());
                msg.addReceiver(new AID("Santa", AID.ISLOCALNAME));
                myAgent.send(msg);
                scout.setCommunicationState(1);
                System.out.println(msg.getContent());
            }
            case 1 -> {
                msg = myAgent.blockingReceive();

                if (msg.getPerformative() == ACLMessage.AGREE) {
                    String content = msg.getContent();
                    String secretCode = content.substring(content.indexOf("secret Code: ") + 13, content.indexOf(". Nähdään pian"));

                    scout.setSecretCode(secretCode);

                    msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.setContent("Bro Santa gave me this secret code: " + secretCode + ". En Plan");
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(new AID("Rudolph", AID.ISLOCALNAME));
                    myAgent.send(msg);
                    scout.setCommunicationState(2);
                    System.out.println(msg.getContent());

                } else if (msg.getPerformative() == ACLMessage.REFUSE) {
                    System.out.println("Santa didn't approve");
                    myAgent.doDelete();
                } else {
                    System.out.println("Error in the conversation protocol with Scout in state " + state);
                    myAgent.doDelete();
                }

            }
            case 2 -> {
                scout.isFinalStop();
            }
        }
    }

    @Override
    public boolean done() {
        return scout.finalStopReached();
    }
}

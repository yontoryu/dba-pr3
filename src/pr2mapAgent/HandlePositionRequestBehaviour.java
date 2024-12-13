package pr2mapAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

// Inner class for handling coordinate requests
public class HandlePositionRequestBehaviour extends Behaviour {
    private boolean finished = false;
    private Rudolph rudolph;

    HandlePositionRequestBehaviour(Rudolph rudolph) {
        this.rudolph = rudolph;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive();
        int state = rudolph.getCommunicationState();

        switch (state) {
            case 0 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    String content = msg.getContent();
                    String secretCode = content.substring(content.indexOf("Secret Code: ") + 13, content.indexOf(". En Plan"));

                    if (rudolph.verifyScout(secretCode)) {
                        // Agent is trustworthy
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setContent("Bro Trustworthy agent accepted. I can give you the Reindeer's positions. En Plan");
                    } else {
                        // Agent is not trustworthy
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("Bro You're not trustworthy. En Plan");
                        myAgent.doDelete();
                    }

                    rudolph.setCommunicationState(1);
                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }

            case 1 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    String content = msg.getContent();

                    // Agent is trustworthy
                    reply.setPerformative(ACLMessage.INFORM);


                    reply.setContent("Bro Trustworthy agent accepted. I can give you the Reindeer's positions. En Plan");

                    rudolph.setCommunicationState(1);


                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }
        }
    }


    @Override
    public boolean done() {
        return finished;
    }
}
package pr2mapAgent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleVolunteerRequestBehaviour extends CyclicBehaviour {
    Santa santa;
    Scout scout;

    HandleVolunteerRequestBehaviour(Santa santa) {
        this.santa = santa;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive();
        int state = santa.getCommunicationState();

        switch (state) {
            // Search Request from Search Agent: AGREE (+ Secret Code) / REFUSE
            case 0 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    if (santa.understood(msg.getContent())) {
                        if (santa.isTrustworthyAgent()) {
                            // Agent is trustworthy
                            reply.setPerformative(ACLMessage.AGREE);
                            reply.setContent("Hyvää joulua Trustworthy agent accepted. Secret code: " + santa.getSecretCode() + ". Nähdään pian");
                        } else {
                            // Agent is not trustworthy
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("Hyvää joulua Not trustworthy. Mission denied. Nähdään pian");
                        }

                        santa.setCommunicationState(1);
                    } else {
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        reply.setContent("Hyvää joulua What did you say? Nähdään pian");
                    }

                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }

            // Santa-Position Request from Search Agent
            case 1 -> {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage reply = msg.createReply();

                    if (santa.understood(msg.getContent())) {
                        reply.setPerformative(ACLMessage.INFORM);
                        int[] position = santa.getPosition();
                        reply.setContent("Hyvää joulua I am at position (" + position[0] + ", " + position[1] + "). Nähdään pian");
                    } else {
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        reply.setContent("Hyvää joulua What did you say? Nähdään pian");
                    }

                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }

            case 2 -> {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    ACLMessage reply = msg.createReply();

                    if (santa.understood(msg.getContent())) {
                        reply.setPerformative(ACLMessage.INFORM);

                        int[] position = santa.getPosition();
                        //extract the position of the agent from the msg and check if it is santas position
                        //...

                        reply.setContent("Hyvää joulua HoHoHo! Nähdään pian");
                    } else {
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        reply.setContent("Hyvää joulua What did you say? Nähdään pian");
                    }

                    myAgent.send(reply);

                } else {
                    System.out.println("Error in the conversation protocol in state " + state);
                    myAgent.doDelete();
                }
            }
        }
    }
}

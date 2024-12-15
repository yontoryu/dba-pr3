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

        switch (state) {
            //case 0 is reserved for walking state
            //Search Request to Santa
            case 1 -> {
                String translatedMsg = scout.getTranslation("Bro Santa, can I help you search for your reindeers? En Plan");

                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent(translatedMsg);
                msg.setSender(myAgent.getAID());
                msg.addReceiver(new AID("Santa", AID.ISLOCALNAME));
                myAgent.send(msg);
                scout.setCommunicationState(2);
                System.out.println("TRANSLATED SEARCH REQUEST SENT TO SANTA");
            }
            //Santa's Reply to Search Request + Send received Code to Rudolph
            case 2 -> {
                msg = myAgent.blockingReceive();

                if (msg.getPerformative() == ACLMessage.AGREE) {
                    String content = scout.getTranslation(msg.getContent());

                    System.out.println("AGREE FROM SANTA RECEIVED: " + content);

                    String secretCode = content.substring(content.indexOf("secret code: ") + 13, content.indexOf(". En Plan"));

                    scout.setSecretCode(secretCode);

                    msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.setContent("Bro Santa gave me this secret code: " + secretCode + ". En Plan");
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(new AID("Rudolph", AID.ISLOCALNAME));
                    myAgent.send(msg);
                    scout.setCommunicationState(3);
                    System.out.println("CODE SENT TO RUDOLPH: " + secretCode + ".");
                    System.out.println("test: " + secretCode.equals("69420 xddd"));

                } else if (msg.getPerformative() == ACLMessage.REFUSE) {
                    System.out.println("Santa didn't approve");
                    myAgent.doDelete();
                } else {
                    System.out.println("Error in the conversation protocol with Scout in state " + state);
                    myAgent.doDelete();
                }

            }
            //Rudolph's Reply to establish a connection
            case 3 -> {
                msg = myAgent.blockingReceive();
                boolean isRudolph = "Rudolph".equals(msg.getSender().getLocalName());

                if (msg.getPerformative() == ACLMessage.AGREE && isRudolph) {
                    System.out.println("RUDOLPH APPROVED, POSITION REQUEST SENT TO RUDOLPH");

                    scout.setCommunicationState(4);

                } else if (msg.getPerformative() == ACLMessage.REFUSE && isRudolph) {
                    System.out.println("Rudolph didn't approve");
                    myAgent.doDelete();
                } else if (isRudolph) {
                    System.out.println("Error in the conversation protocol with Scout in state " + state);
                    myAgent.doDelete();
                }
            }
            //Requesting position of next reindeer
            case 4 -> {
                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("Bro can you give me the position of the next reindeer. En Plan");
                msg.setSender(myAgent.getAID());
                msg.addReceiver(new AID("Rudolph", AID.ISLOCALNAME));
                myAgent.send(msg);

                msg = myAgent.blockingReceive();
                String content = msg.getContent();

                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String reindeerPositionStr = content.substring(content.indexOf("position (") + 10, content.indexOf(")"));
                    String[] parts = reindeerPositionStr.split(",\\s*");
                    int[] reindeerPosition = {Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
                    scout.setTargetPos(reindeerPosition);
                    scout.setCommunicationState(0);
                    System.out.println("HEADING TO REINDEER AT POSITION: " + reindeerPosition[0] + ", " + reindeerPosition[1]);


                } else if (msg.getPerformative() == ACLMessage.CANCEL) {
                    System.out.println("FOUND LAST REINDEER, HEADING TO SANTA");
                    scout.finalReindeerReached();
                    findSanta();
                } else {
                    System.out.println("Error in the conversation protocol with Scout in state " + state);
                    myAgent.doDelete();
                }
            }

            case 5 -> {
                msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(scout.getTranslation("Bro Santa, I am here. En Plan"));
                msg.setSender(myAgent.getAID());
                msg.addReceiver(new AID("Santa", AID.ISLOCALNAME));
                myAgent.send(msg);

                msg = myAgent.blockingReceive();

                if (msg.getPerformative() == ACLMessage.INFORM) {
                    System.out.println("FINISHED");
                    myAgent.doDelete();
                }
            }
        }
    }

    private void findSanta() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent(scout.getTranslation("Bro I found everyone. Can you give me your position? En Plan"));
        msg.setSender(myAgent.getAID());
        msg.addReceiver(new AID("Santa", AID.ISLOCALNAME));
        myAgent.send(msg);

        msg = myAgent.blockingReceive();
        String content = scout.getTranslation(msg.getContent());

        if (msg.getPerformative() == ACLMessage.INFORM) {
            String santaPositionStr = content.substring(content.indexOf("position (") + 10, content.indexOf(")"));
            String[] parts = santaPositionStr.split(",\\s*");
            int[] santaPosition = {Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
            scout.setTargetPos(santaPosition);

            scout.setCommunicationState(0);
            System.out.println("HEADING TO SANTA AT POSITION: " + santaPosition[0] + ", " + santaPosition[1]);

        } else {
            System.out.println("Error in the conversation protocol with Scout in state 4 whilst going to Santa");
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        return scout.finalReindeerReached();
    }
}

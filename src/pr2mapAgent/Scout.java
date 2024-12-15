package pr2mapAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scout extends Agent {

    private Environment env;
    private int energy;
    private int[] currentPos;
    private int[] targetPos;
    private int[] santaPos;
    private boolean finalReindeer = false;
    private int state = 1; //commState = 0 --> Walking
    private String secretCode;
    private int pace;

    private final List<PositionListener> listeners = new ArrayList<>(); // Observer list

    public Scout() {
        super();
    }

    @Override
    protected void setup() {
        // Retrieve startup arguments
        Object[] args = getArguments();
        pace = 20;

        if (args != null && args.length > 0) {
            energy = -2;
            santaPos = (int[]) args[1];
            env = (Environment) args[2];
            setCurrentPos(((int[]) args[0]).clone());
//            System.out.println("Agent started with argument: Start - " + currentPos[0] + ", " + currentPos[1] + ", End - " + targetPos[0] + ", " + targetPos[1]);

            PositionListener glm = (PositionListener) args[3];
            addPositionListener(glm);
        } else {
            System.out.println("No arguments provided.");
        }

        //start walking
        addBehaviour(new RequestSearchBehaviour(this));
        addBehaviour(new WalkBehaviour(this, env));
    }

    int getEnergy() {
        return energy;
    }

    int[] getCurrentPos() {
        return currentPos;
    }

    void setCurrentPos(int[] newPos) {
        energy++;
        int[] oldPos = currentPos;
        this.currentPos = newPos;
        notifyPositionListeners(oldPos, newPos);
    }

    int[] getTargetPos() {
        return targetPos;
    }

    boolean targetIsSet() {
        return targetPos != null;
    }

    void setTargetPos(int[] newPos) {
        targetPos = newPos;
    }

    void setSecretCode(String code) {
        secretCode = code;
    }

    boolean stopReached() {
        return Arrays.equals(currentPos, targetPos);
    }

    void setCommunicationState(int state) {
        this.state = state;
    }

    int getCommunicationState() {
        return state;
    }

    boolean finalReindeerReached() {
        return stopReached() && finalReindeer;
    }

    boolean santaReached() {
        return Arrays.equals(currentPos, santaPos);
    }


    void startAgent(Object[] args, ContainerController mainContainer) {
        try {
            // Step 3: Start your agent(s)
            String agentName = "scout"; // Name of your agent
            String agentClass = "pr2mapAgent.Scout"; // Fully qualified name of your agent class

            AgentController agent = mainContainer.createNewAgent(agentName, agentClass, args);
            agent.start(); // Start the agent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPositionListener(PositionListener listener) {
        listeners.add(listener);
    }

    // Entfernen eines Listeners
    public void removePositionListener(PositionListener listener) {
        listeners.remove(listener);
    }

    // Benachrichtigung der Listener
    private void notifyPositionListeners(int[] oldPos, int[] currentPos) {
        for (PositionListener listener : listeners) {
            try {
                Thread.sleep(pace);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.onPositionUpdated(oldPos, currentPos, stopReached(), finalReindeerReached(), energy);
        }
    }

    public String getTranslation(String content) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent(content);
        msg.setSender(getAID());
        msg.addReceiver(new AID("Translator", AID.ISLOCALNAME));
        send(msg);
        System.out.println("TRANSLATION REQUEST SENT TO TRANSLATOR");

        ACLMessage translatedMsg = blockingReceive();

        return translatedMsg.getContent();
    }

    public void resetOnNewTarget() {
        if (santaReached()) {
            setCommunicationState(5);
        } else if (!finalReindeerReached()) {
            addBehaviour(new WalkBehaviour(this, env));
            setCommunicationState(4); //continue requesting the reindeer's positions
        } else if (finalReindeerReached()) {
            addBehaviour(new WalkBehaviour(this, env));
            setCommunicationState(6); //ask for Santa's position
        }
        env.resetHeuristic();
    }
}
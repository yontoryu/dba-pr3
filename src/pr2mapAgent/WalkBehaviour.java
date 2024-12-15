package pr2mapAgent;

import jade.core.behaviours.Behaviour;

import java.util.LinkedList;

public class WalkBehaviour extends Behaviour {

    Scout scout;
    private boolean waitingForStep; // Flag to alternate turns
    int basePenalty;
    int maxRecentVisits;
    LinkedList<Node> lastVisitiedNodes;
    Environment env;
    Node current;
    Node target;
    HeuristicHandler hh;
    private boolean goalReached;
    private boolean setupNewTarget;

    public WalkBehaviour(Scout scout, Environment env) {
        this.goalReached = false;
        this.waitingForStep = false;
        this.basePenalty = 10;
        this.maxRecentVisits = 50;
        this.scout = scout;
        this.env = env;
        this.lastVisitiedNodes = new LinkedList<>();
        this.setupNewTarget = false;
    }

    @Override
    public void action() {
        if (scout.targetIsSet()) {
            if (!setupNewTarget) {
                this.target = new Node(scout.getTargetPos()[0], scout.getTargetPos()[1]);
                this.hh = new HeuristicHandler(target, basePenalty, maxRecentVisits);
                this.current = new Node(scout.getCurrentPos()[0], scout.getCurrentPos()[1]);
                current.setGCost(0);
                current.setHCost(hh.manhattan(current));
                setupNewTarget = true;
            }

            if (!current.equals(target) && !waitingForStep) {
                scout.setCurrentPos(new int[]{current.x, current.y});
                myAgent.addBehaviour(new StepBehaviour(this));
                waitingForStep = true;
            } else if (current.equals(target) && !waitingForStep) {
                scout.setCurrentPos(new int[]{current.x, current.y});
                goalReached = true;
                waitingForStep = true;
            }
        }
    }

    @Override
    public boolean done() {
        //start new WalkBehaviour here, until the Agent has reached Santa
        //...
        if (goalReached) {
            setupNewTarget = false;
            scout.resetOnNewTarget();
        }

        return goalReached;
    }

    public void notifyStepComplete() {
        this.waitingForStep = false;
    }
}

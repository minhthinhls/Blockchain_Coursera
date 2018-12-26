package ConsensusFromTrust;

import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private double p_graph;
    private double p_malicious;
    private double p_tXDistribution;
    private int numRounds;

    private boolean[] followees;
    private Set<Transaction> pendingTransactions;
    private boolean[] blackListed;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_tXDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    @Override
    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
        this.followees = followees;
        this.blackListed = new boolean[followees.length];
    }

    @Override
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
        this.pendingTransactions = pendingTransactions;
    }

    @Override
    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
        Set<Transaction> toSend = new HashSet<>(pendingTransactions);
        pendingTransactions.clear();
        return toSend;
    }

    @Override
    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
        Set<Integer> senders = candidates.stream().map(c -> c.sender).collect(toSet());
        for (int j = 0; j < followees.length; j++) { // Loop for another nodes, which this CompliantNode is following to.
            if (followees[j] && !senders.contains(j)) {
                blackListed[j] = true;
            } // BlackList j followee, if this CompliantNode follow that followee, whom doesn't send Tx to this Compliant Node.
        }
        for (Candidate c : candidates) { // Looping through all candidates
            if (!blackListed[c.sender]) { // Check which candidate is black-listed by Node ID (between 1 and 100)
                pendingTransactions.add(c.tx);
            }
        }

    }

}

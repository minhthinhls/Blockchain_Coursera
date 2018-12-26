package ConsensusFromTrust;

import java.util.Set;
import java.util.HashSet;

public class MaliciousNode implements Node {

    public MaliciousNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    }

    @Override
    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
    }

    @Override
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    }

    @Override
    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
        return new HashSet<Transaction>();
    }

    @Override
    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
    }
}

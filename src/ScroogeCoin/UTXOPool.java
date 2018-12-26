package ScroogeCoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UTXOPool {

    /**
     * The current collection of UTXOs, with each one mapped to its
     * corresponding transaction output
     */
    private HashMap<UTXO, Transaction.Output> UTXO_HashMap;

    /**
     * Creates a new empty UTXOPool
     */
    public UTXOPool() {
        UTXO_HashMap = new HashMap<UTXO, Transaction.Output>();
    }

    /**
     * Creates a new UTXOPool that is a copy of {@code uPool}
     */
    public UTXOPool(UTXOPool uPool) {
        UTXO_HashMap = new HashMap<UTXO, Transaction.Output>(uPool.UTXO_HashMap);
    }

    /**
     * Adds a mapping from UTXO {@code utxo} to transaction output @code{txOut}
     * to the pool
     */
    public void addUTXO(UTXO utxo, Transaction.Output txOut) {
        UTXO_HashMap.put(utxo, txOut);
    }

    /**
     * Removes the UTXO {@code utxo} from the pool
     */
    public void removeUTXO(UTXO utxo) {
        UTXO_HashMap.remove(utxo);
    }

    /**
     * @return the transaction output corresponding to UTXO {@code utxo}, or
     * null if {@code utxo} is not in the pool.
     */
    public Transaction.Output getTxOutput(UTXO utxo) {
        return UTXO_HashMap.get(utxo);
    }

    /**
     * @return true if UTXO {@code utxo} is in the pool and false otherwise
     */
    public boolean contains(UTXO utxo) {
        return UTXO_HashMap.containsKey(utxo);
    }

    /**
     * Returns an {@code ArrayList} of all UTXOs in the pool
     */
    public ArrayList<UTXO> getAllUTXO() {
        Set<UTXO> setUTXO = UTXO_HashMap.keySet();
        ArrayList<UTXO> allUTXO = new ArrayList<UTXO>();
        for (UTXO ut : setUTXO) {
            allUTXO.add(ut);
        }
        return allUTXO;
    }
}

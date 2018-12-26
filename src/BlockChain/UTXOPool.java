package BlockChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UTXOPool {

    /**
     * The current collection of UTXOs, with each one mapped to its
     * corresponding transaction output
     */
    private HashMap<UTXO, Transaction.Output> HashMap;

    /**
     * Creates a new empty UTXOPool
     */
    public UTXOPool() {
        HashMap = new HashMap<UTXO, Transaction.Output>();
    }

    /**
     * Creates a new UTXOPool that is a copy of {@code uPool}
     *
     * @param uPool
     */
    public UTXOPool(UTXOPool uPool) {
        HashMap = new HashMap<UTXO, Transaction.Output>(uPool.HashMap);
    }

    /**
     * Adds a mapping from UTXO {@code utxo} to transaction output @code{txOut}
     * to the pool
     *
     * @param utxo
     * @param txOut
     */
    public void addUTXO(UTXO utxo, Transaction.Output txOut) {
        HashMap.put(utxo, txOut);
    }

    /**
     * Removes the UTXO {@code utxo} from the pool
     *
     * @param utxo
     */
    public void removeUTXO(UTXO utxo) {
        HashMap.remove(utxo);
    }

    /**
     * @param ut
     * @return the transaction output corresponding to UTXO {@code utxo}, or
     * null if {@code utxo} is not in the pool.
     */
    public Transaction.Output getTxOutput(UTXO ut) {
        return HashMap.get(ut);
    }

    /**
     * @param utxo
     * @return true if UTXO {@code utxo} is in the pool and false otherwise
     */
    public boolean contains(UTXO utxo) {
        return HashMap.containsKey(utxo);
    }

    /**
     * Returns an {@code ArrayList} of all UTXOs in the pool
     *
     * @return
     */
    public ArrayList<UTXO> getAllUTXO() {
        Set<UTXO> setUTXO = HashMap.keySet();
        ArrayList<UTXO> allUTXO = new ArrayList<UTXO>();
        for (UTXO ut : setUTXO) {
            allUTXO.add(ut);
        }
        return allUTXO;
    }
}

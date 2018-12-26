package ScroogeCoin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers tx_in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template tx_in the editor.
 */
/**
 *
 * @author Minh Thinh
 */
public class MaxFeeTxHandler {

    private UTXOPool utxoPool;
    private Set<Transaction> pendingTxs;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent
     * transaction outputs) is {@code utxoPool}. This should make a copy of
     * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
     *
     * @param utxoPool
     */
    public MaxFeeTxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
        this.pendingTxs = new HashSet<Transaction>();
    }

    /**
     * @param tx
     * @return true if: (1) all outputs claimed by {@code tx} are tx_in the
     * current UTXO pool, (2) the signatures on each input of {@code tx} are
     * valid, (3) no UTXO is claimed multiple times by {@code tx}, (4) all of
     * {@code tx}s prev_tx_out values are non-negative, and (5) the sum of
     * {@code tx}s input values is greater than or equal to the sum of its
     * prev_tx_out values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        UTXOPool uniqueUtxos = new UTXOPool();
        double previousTxOutSum = 0;
        double currentTxOutSum = 0;

        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input tx_in = tx.getInput(i);
            UTXO utxo = new UTXO(tx_in.prevTxHash, tx_in.outputIndex);
            Transaction.Output prev_tx_out;

            if (!utxoPool.contains(utxo)) {
                pendingTxs.add(tx);
                return false; // (1) all outputs claimed by {@code tx} are tx_in the current UTXO pool.
            } else {
                prev_tx_out = utxoPool.getTxOutput(utxo); // @return the transaction output corresponding to this UTXO in hash map
            }
            if (!Crypto.verifySignature(prev_tx_out.address, tx.getRawDataToSign(i), tx_in.signature)) {
                return false; // (2) the signatures on each input of {@code tx} are valid.
            }
            if (uniqueUtxos.contains(utxo)) {
                return false; // (3) no UTXO is claimed multiple times by {@code tx}.
            } else {
                uniqueUtxos.addUTXO(utxo, prev_tx_out);
                previousTxOutSum += prev_tx_out.value;
            }
        }
        for (Transaction.Output current_tx_out : tx.getOutputs()) {
            if (current_tx_out.value < 0) {
                return false; // (4) all of {@code tx}s prev_tx_out values are non-negative.
            } else {
                currentTxOutSum += current_tx_out.value;
            }
        }
        return previousTxOutSum >= currentTxOutSum;
        //(5) the sum of {@code tx}s input values is greater than or equal to the sum of its prev_tx_out values; and false otherwise.
    }

    private double calcTxFees(Transaction tx) {
        double sumInputs = 0;
        double sumOutputs = 0;

        for (Transaction.Input in : tx.getInputs()) {
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
            if (!utxoPool.contains(utxo) || !isValidTx(tx)) {
                continue;
            }
            Transaction.Output txOutput = utxoPool.getTxOutput(utxo);
            sumInputs += txOutput.value;
        }
        for (Transaction.Output out : tx.getOutputs()) {
            sumOutputs += out.value;
        }
        return sumInputs - sumOutputs;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed
     * transactions, checking each transaction for correctness, returning a
     * mutually valid array of accepted transactions, and updating the current
     * UTXO pool as appropriate.
     *
     * @param possibleTxs
     * @return
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        Set<Transaction> txsSortedByFees = new TreeSet<>((tx1, tx2) -> {
            double tx1Fees = calcTxFees(tx1);
            double tx2Fees = calcTxFees(tx2);
            return Double.valueOf(tx2Fees).compareTo(tx1Fees);
        });
        Collections.addAll(txsSortedByFees, possibleTxs);
        Set<Transaction> acceptedTxs = new HashSet<>();

        for (Transaction tx : txsSortedByFees) {
            if (isValidTx(tx)) {
                try {
                    acceptedTxs.add(tx);
                    for (Transaction.Input tx_in : tx.getInputs()) { // for (int i = 0; i < tx.numInputs(); i++)
                        UTXO utxo = new UTXO(tx_in.prevTxHash, tx_in.outputIndex);
                        utxoPool.removeUTXO(utxo);
                    }
                    for (int i = 0; i < tx.numOutputs(); i++) { // for (Transaction.Output tx_out : tx.getOutputs())
                        Transaction.Output tx_out = tx.getOutput(i);
                        UTXO utxo = new UTXO(tx.getHash(), i);
                        utxoPool.addUTXO(utxo, tx_out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Loop for the remain transactions until it's done !
        while (!pendingTxs.isEmpty()) {
            int beforeLoop = pendingTxs.size();
            for (Transaction tx : pendingTxs) {
                if (isValidTx(tx)) {
                    try {
                        acceptedTxs.add(tx);
                        for (Transaction.Input tx_in : tx.getInputs()) { // for (int i = 0; i < tx.numInputs(); i++)
                            UTXO utxo = new UTXO(tx_in.prevTxHash, tx_in.outputIndex);
                            utxoPool.removeUTXO(utxo);
                        }
                        for (int i = 0; i < tx.numOutputs(); i++) { // for (Transaction.Output tx_out : tx.getOutputs())
                            Transaction.Output tx_out = tx.getOutput(i);
                            UTXO utxo = new UTXO(tx.getHash(), i);
                            utxoPool.addUTXO(utxo, tx_out);
                        }
                        pendingTxs.remove(tx);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (beforeLoop == pendingTxs.size()) {
                break; // Determine whether the size of pendingTx beforeLoop && afterLoop is equal to exit while loop ?
            }
        }
        Transaction[] validTxArray = new Transaction[acceptedTxs.size()];
        return acceptedTxs.toArray(validTxArray);
    }

}

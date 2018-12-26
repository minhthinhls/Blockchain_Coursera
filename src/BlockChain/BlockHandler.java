package BlockChain;

import java.security.PublicKey;

public class BlockHandler {

    private BlockChain blockChain;

    /**
     * assume blockChain has the genesis block
     *
     * @param blockChain
     */
    public BlockHandler(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    /**
     * add {@code block} to the block chain if it is valid.
     *
     * @param block
     * @return true if the block is valid and has been added, false otherwise
     */
    public boolean processBlock(Block block) {
        if (block == null) {
            return false;
        }
        return blockChain.addBlock(block);
    }

    /**
     * create a new {@code block} over the max height {@code block}
     *
     * @param myAddress
     * @return
     */
    public Block createBlock(PublicKey myAddress) {
        Block parent = blockChain.getMaxHeightBlock();
        byte[] parentHash = parent.getHash();
        Block current = new Block(parentHash, myAddress);
        UTXOPool uPool = blockChain.getMaxHeightUTXOPool();
        TransactionPool txPool = blockChain.getTransactionPool();
        TxHandler handler = new TxHandler(uPool);
        Transaction[] txs = txPool.getTransactions().toArray(new Transaction[0]);
        Transaction[] rTxs = handler.handleTxs(txs);

        for (Transaction rTx : rTxs) {
            current.addTransaction(rTx);
        }
        current.finalize();

        if (blockChain.addBlock(current)) {
            return current;
        } else {
            return null;
        }
    }

    /**
     * process a {@code Transaction}
     *
     * @param tx
     */
    public void processTx(Transaction tx) {
        blockChain.addTransaction(tx);
    }
}

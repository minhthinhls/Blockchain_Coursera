package BlockChain;

import java.util.ArrayList;
import java.util.HashMap;

/*  Block Chain should maintain only limited block nodes to satisfy the functions
    You should not have all the blocks added to the block chain in memory 
    as it would cause a memory overflow. */
public class BlockChain {

    private class BlockNode {

        public Block block;
        public BlockNode parent;
        public ArrayList<BlockNode> children;
        public int height;  // The counter for how long the chain is, up to this BlockNode.
        private UTXOPool utxoPool; // Utxo pool for making a new block on top of this block.

        public BlockNode(Block block, BlockNode parent, UTXOPool utxoPool) {
            this.block = block;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.utxoPool = utxoPool;
            if (parent != null) {
                this.height = parent.height + 1;
                BlockNode New_Node = this;
                parent.children.add(New_Node);
            } else {
                this.height = 1;
            }
        }

        public UTXOPool getUTXOPoolCopy() {
            return new UTXOPool(utxoPool);
        }
    }

    public static final int CUT_OFF_AGE = 10;
    private HashMap<ByteArrayWrapper, BlockNode> BlockChain;
    private BlockNode maxHeightNode;
    private TransactionPool txPool;

    /**
     * create an empty block chain with just a genesis block. Assume
     * {@code genesisBlock} is a valid block
     *
     * @param genesisBlock
     */
    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
        BlockChain = new HashMap<>();
        UTXOPool utxoPool = new UTXOPool();
        addCoinbaseToUTXOPool(genesisBlock, utxoPool);
        BlockNode genesisNode = new BlockNode(genesisBlock, null, utxoPool);
        BlockChain.put(wrap(genesisBlock.getHash()), genesisNode);
        this.txPool = new TransactionPool();
        maxHeightNode = genesisNode;
    }

    /**
     * Get the maximum height block
     *
     * @return
     */
    public Block getMaxHeightBlock() {
        // IMPLEMENT THIS
        return maxHeightNode.block;
    }

    /**
     * Get the UTXOPool for mining a new block on top of max height block
     *
     * @return
     */
    public UTXOPool getMaxHeightUTXOPool() {
        // IMPLEMENT THIS
        return maxHeightNode.getUTXOPoolCopy();
    }

    /**
     * Get the transaction pool to mine a new block
     *
     * @return
     */
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
        return txPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all
     * transactions should be valid and block should be at
     * {@code height > (maxHeight - CUT_OFF_AGE)}.
     *
     * <p>
     * For example, you can try creating a new block over the genesis block
     * (block height 2) if the block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot
     * create a new block at height 2.
     *
     * @param block
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // IMPLEMENT THIS
        byte[] prevBlockHash = block.getPrevBlockHash();
        if (prevBlockHash == null) {
            return false;
        }
        BlockNode parentBlockNode = BlockChain.get(wrap(prevBlockHash)); // Get previous node in BlockChain HashMap through prevBlockHash
        if (parentBlockNode == null) {
            return false;
        }
        TxHandler handler = new TxHandler(parentBlockNode.getUTXOPoolCopy());
        Transaction[] txs = block.getTransactions().toArray(new Transaction[block.getTransactions().size()]);
        Transaction[] validTxs = handler.handleTxs(txs);
        if (validTxs.length != txs.length) {
            return false;
        } // Contains at least one invalid transaction.
        int proposedHeight = parentBlockNode.height + 1;
        if (proposedHeight <= maxHeightNode.height - CUT_OFF_AGE) {
            return false;
        } // Cannot fork to a new branch if it's far away from the main branch.
        UTXOPool utxoPool = handler.getUTXOPool();
        addCoinbaseToUTXOPool(block, utxoPool);
        BlockNode node = new BlockNode(block, parentBlockNode, utxoPool);
        BlockChain.put(wrap(block.getHash()), node);
        if (proposedHeight > maxHeightNode.height) {
            maxHeightNode = node;
        } // Update the new node as the on-top of longest-valid branch.
        return true;
    }

    /**
     * Add a transaction to the transaction pool
     *
     * @param tx
     */
    public void addTransaction(Transaction tx) {
        // IMPLEMENT THIS
        txPool.addTransaction(tx);
    }

    /**
     * Add the coin_base transaction into the UTXOPool whenever a block is added
     * to the BlockChain
     *
     * @param block
     * @param utxoPool
     */
    private void addCoinbaseToUTXOPool(Block block, UTXOPool utxoPool) {
        Transaction coinbase = block.getCoinbase();
        for (int i = 0; i < coinbase.numOutputs(); i++) {
            Transaction.Output out = coinbase.getOutput(i);
            UTXO utxo = new UTXO(coinbase.getHash(), i);
            utxoPool.addUTXO(utxo, out);
        }
    }

    /**
     * Wrap an array of byte into an object named ByteArrayWrapper
     *
     * @param byte_array
     * @return
     */
    private static ByteArrayWrapper wrap(byte[] byte_array) {
        return new ByteArrayWrapper(byte_array);
    }

}

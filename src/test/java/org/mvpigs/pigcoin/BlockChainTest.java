package org.mvpigs.pigcoin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class BlockChainTest {

    @Test
    public void constructorTest() {
        BlockChain bc = new BlockChain();
        assertNotNull(bc);
    }

    @Test
    public void addOriginTransaction() {
        BlockChain bc = new BlockChain();
        Transaction transaction = new Transaction();
        transaction = new Transaction("hash_1", "0", "origin", "wallet_1", 20);
        bc.addOrigin(transaction);
        assertEquals(20, bc.getBlockChain().get(0).getPigCoins(), 0);
    }

    @Test
    public void load_wallet_test() {
        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", "origin", "wallet_1", 20);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", "origin", "wallet_2", 10);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_3", "hash_1", "wallet_1", "wallet_2", 10);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_4", "CA_hash_3", "wallet_1", "wallet_1", 10);
        bChain.addOrigin(transaction);

        double[] pigcoins = bChain.loadWallet("wallet_1");
        assertEquals(30, pigcoins[0], 0);
        assertEquals(20, pigcoins[1], 0);
    }

    @Test
    public void load_Input_Transactions_test() {

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", "origin", "wallet_1", 20);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", "origin", "wallet_2", 10);
        bChain.addOrigin(transaction);

        List<Transaction> inputTransactions = bChain.loadInputTransactions("wallet_1");
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 1);
        assertFalse(inputTransactions.contains(transaction));

        inputTransactions = bChain.loadInputTransactions("wallet_2");
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 1);
        assertTrue(inputTransactions.contains(transaction));

        inputTransactions = bChain.loadInputTransactions("wallet_3");
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 0);
        assertFalse(inputTransactions.contains(transaction));
    }

    @Test
    public void is_consumed_coin_valid_test() {
        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", "origin", "wallet_1", 20);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", "origin", "wallet_2", 10);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_3", "hash_1", "wallet_1", "wallet_2", 10);
        bChain.addOrigin(transaction);

        Map<String, Double> consumedCoins = new LinkedHashMap<>();
        consumedCoins.put("hash_1", 10d);
        assertFalse(bChain.isConsumedCoinValid(consumedCoins));
        consumedCoins.clear();
        consumedCoins.put("hash_2", 10d);
        consumedCoins.put("hash_3", 10d);
        assertTrue(bChain.isConsumedCoinValid(consumedCoins));
    }

    @Test
    public void create_transaction_test() {

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", "origin", "wallet_1", 20);
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", "origin", "wallet_2", 10);
        bChain.addOrigin(transaction);

        Map<String, Double> consumedCoins = new LinkedHashMap<>();
        consumedCoins.put("hash_1", 10.2d);
        consumedCoins.put("CA_hash_2", 9.8d);
        assertTrue(bChain.isConsumedCoinValid(consumedCoins));

        int previousBlockChainSize = bChain.getBlockChain().size();
        bChain.createTransaction("wallet_1", "wallet_2", consumedCoins, "signature");
        assertEquals(previousBlockChainSize + consumedCoins.size(), bChain.getBlockChain().size(), 0);
        assertEquals("hash_4", bChain.getBlockChain().get(3).getHash());
        assertEquals(9.8, bChain.getBlockChain().get(3).getPigCoins(), 0);
        bChain.sumarize(3);
    }
}
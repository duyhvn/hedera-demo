package vn.com.chainhaus.hedera;

import com.hedera.hashgraph.sdk.*;
import io.github.cdimascio.dotenv.Dotenv;

public class TransferHBAR {

    public static void main(String[] args) throws Exception {
        transferHbar();

    }

    public static void transferHbar() throws Exception {
        //Grab your Hedera Testnet account ID and private key
        AccountId myAccountId = AccountId.fromString(Dotenv.load().get("MY_ACCOUNT_ID"));
        PrivateKey myPrivateKey = PrivateKey.fromString(Dotenv.load().get("MY_PRIVATE_KEY"));

        //Create your Hedera Testnet client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);
        //-----------------------<enter code below>--------------------------------------

        // Generate a new key pair
        PrivateKey newAccountPrivateKey = PrivateKey.generateED25519();
        PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();

        //Create new account and assign the public key
        TransactionResponse newAccount = new AccountCreateTransaction()
                .setKey(newAccountPublicKey)
                .setInitialBalance(Hbar.fromTinybars(1000))
                .execute(client);

        // Get the new account ID
        AccountId newAccountId = newAccount.getReceipt(client).accountId;

        //Log the account ID
        System.out.println("The new account ID is: " + newAccountId);

        //Check the new account's balance
        AccountBalance accountBalance = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .execute(client);

        System.out.println("The new account balance is: " + accountBalance.hbars);

        // Step 3. Get the account balance
        //Request the cost of the query

        System.out.println("Before transfer cost of this query is: " + new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .getCost(client));

        //Transfer HBAR
        TransactionResponse sendHbar = new TransferTransaction()
                .addHbarTransfer(myAccountId, Hbar.fromTinybars(-1000)) //Sending account
                .addHbarTransfer(newAccountId, Hbar.fromTinybars(1000)) //Receiving account
                .execute(client);

        // Step 2. Verify the transfer transaction reached consensus
        System.out.println("The transfer transaction was: " + sendHbar.getReceipt(client).status);

        // Step 3. Get the account balance
        //Request the cost of the query
        Hbar queryCost = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .getCost(client);

        System.out.println("The cost of this query is: " + queryCost);

    }
}

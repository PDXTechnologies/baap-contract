package life.pdx.daap.sample.simple.caller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverException;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;

public class DaapCaller {
	
	private static final String DST = "daap://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.dapp/sample/simple";
	private static final String HOST = "10.0.0.104";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";

	public static void main(String[] args) throws Exception{
		Properties props = new Properties();
		props.setProperty("privateKey", PRIVATE_KEY);
		props.setProperty("rpcUrl", "http://"+HOST+":8545");
		props.setProperty("containerUrl", "http://"+HOST+":8080");
		BlockChainDriver driver = BlockChainDriverFactory.get(props);
		for (int i = 0 ; i < 10 ; i++) {
			apply(driver);
			Thread.sleep(2000);			
		}
//		query(driver);
	}

	private static void query(BlockChainDriver driver) {
		Transaction tx = new Transaction();
		tx.setBody("txid==12345".getBytes());
		try {
			tx.setDst(new URI(DST));
			List<Transaction> res = driver.query(tx);
			if (res != null && res.size() > 0) {
				Transaction rs = res.get(0);
				System.out.println(new String(rs.getBody()));
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void apply(BlockChainDriver driver) {
		Transaction tx = new Transaction();
		Map<String,byte[]> meta=new HashMap<>();
		meta.put("something", "".getBytes());
		tx.setMeta(meta);
		tx.setBody("apply".getBytes());
		try {
			tx.setDst(new URI(DST));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res;
		try {
			res = driver.apply(tx);
			System.out.println(res);
		} catch (BlockChainDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

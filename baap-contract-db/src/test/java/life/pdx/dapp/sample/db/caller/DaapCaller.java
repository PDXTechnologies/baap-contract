package life.pdx.dapp.sample.db.caller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverException;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;
import biz.pdxtech.daap.bcdriver.util.JacksonUtils;

public class DaapCaller {
	private static final String DST = "daap://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.dapp/sample/db";
	private static final String HOST = "10.0.0.5";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.setProperty("privateKey", PRIVATE_KEY);
		props.setProperty("rpcUrl", "http://" + HOST + ":8545");
		props.setProperty("containerUrl", "http://" + HOST + ":8080");
		BlockChainDriver driver = BlockChainDriverFactory.get(props);
		String txid = apply(driver);
		if (txid.startsWith("0x")) {
			txid = txid.substring(2);
		}
		Thread.sleep(10000);
		query1(driver);
		query2(driver, txid);
	}

	private static void query1(BlockChainDriver driver) {
		Transaction tx = new Transaction();
		HashMap<String, Object> queryMap = new HashMap();
		queryMap.put("type", "1");
		queryMap.put("metaKey", "testkey");
		try {
			tx.setBody(JacksonUtils.toJson(queryMap).getBytes());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

	private static void query2(BlockChainDriver driver, String txid) {
		Transaction tx = new Transaction();
		HashMap<String, Object> queryMap = new HashMap();
		queryMap.put("type", "2");
		queryMap.put("txId", txid);
		try {
			tx.setBody(JacksonUtils.toJson(queryMap).getBytes());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

	private static String apply(BlockChainDriver driver) {
		Transaction tx = new Transaction();
		Map<String, byte[]> meta = new HashMap<>();
		meta.put("testkey", "test".getBytes());
		tx.setMeta(meta);
		tx.setBody("五月九日".getBytes());
		try {
			tx.setDst(new URI(DST));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String txid = null;
		try {
			txid = driver.apply(tx);
			System.out.println(txid);

		} catch (BlockChainDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txid;
	}
}

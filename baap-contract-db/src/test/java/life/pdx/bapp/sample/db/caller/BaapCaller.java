package life.pdx.bapp.sample.db.caller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import biz.pdxtech.baap.api.Constants;
import biz.pdxtech.baap.api.contract.Transaction;
import biz.pdxtech.baap.driver.BlockChainDriver;
import biz.pdxtech.baap.driver.BlockChainDriverException;
import biz.pdxtech.baap.driver.BlockChainDriverFactory;
import life.pdx.bapp.sample.db.util.JacksonUtils;

public class BaapCaller {
	private static String DST = "contract://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.bapp/sample/db";
	//配置节点地址
	private static final String HOST = "121.41.28.145";
	//配置chainid
	private static final String CHAINID = "javabc";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";
	//配置部署合约的用户地址
	private static final String ADDR = "45874a3c0afc2a4d6cc9dea20245350f2981d3ea";

	public static void main(String[] args) throws Exception {
		System.out.println("Usage: java -jar xxx.jar [host] [chainid] [useraddress]");
		Properties properties = new Properties();
		properties.setProperty("baap-private-key", PRIVATE_KEY);
		properties.setProperty("baap-chain-type", Constants.BAAP_CHAIN_TYPE_ETHEREUM);
		String host = HOST;
		String chainid = CHAINID;
		String addr = ADDR;
		if (args != null && args.length>0) {
			host = args[0];
		}
		if (args != null && args.length>1) {
			chainid = args[1];
		}
		properties.setProperty("baap-url", "http://" + host + ":8080/");
		properties.setProperty("baap-chain-id", chainid);
		BlockChainDriver driver = BlockChainDriverFactory.get(properties);
		if (args != null && args.length>2) {
			addr = args[2];
		}
		DST = "contract://"+chainid+"/"+addr+"/pdx.bapp/sample/db";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(new Date());
		System.out.println(time);
		String txid = apply(driver, time);
		Thread.sleep(5000);
		if (txid.startsWith("0x")) {
			txid = txid.substring(2);
		}
		query1(driver, time);
		query2(driver, txid);
	}
	

	private static String apply(BlockChainDriver driver, String time) {
		Transaction tx = new Transaction();
		Map<String, byte[]> meta = new HashMap<>();
		meta.put("testkey "+time, time.getBytes());
		tx.putMeta(meta);
		tx.setBody(("body测试:"+time).getBytes());
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


	private static void query1(BlockChainDriver driver, String time) throws IOException {
		HashMap<String, Object> queryMap = new HashMap();
		queryMap.put("type", "1");
		queryMap.put("metaKey", "testkey "+time);
		InputStream result = driver.query(DST, JacksonUtils.toJson(queryMap).getBytes());
		if (result != null) {
			System.out.println("query by metaKey: "+IOUtils.toString(result, "utf-8"));
		}
		else {
			System.out.println("query by metaKey: return null");
		}
	}

	private static void query2(BlockChainDriver driver, String txid) throws IOException {
		HashMap<String, Object> queryMap = new HashMap();
		queryMap.put("type", "2");
		queryMap.put("txId", txid);
		InputStream result = driver.query(DST, JacksonUtils.toJson(queryMap).getBytes());
		if (result != null) {
			System.out.println("query by tx: "+IOUtils.toString(result, "utf-8"));
		}
		else {
			System.out.println("query by tx: return null");
		}
	}
}

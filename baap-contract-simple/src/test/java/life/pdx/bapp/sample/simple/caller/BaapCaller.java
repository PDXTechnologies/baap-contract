package life.pdx.bapp.sample.simple.caller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.spongycastle.util.encoders.Hex;

import biz.pdxtech.baap.api.Constants;
import biz.pdxtech.baap.api.contract.Transaction;
import biz.pdxtech.baap.driver.BlockChainDriver;
import biz.pdxtech.baap.driver.BlockChainDriverException;
import biz.pdxtech.baap.driver.BlockChainDriverFactory;

public class BaapCaller {

	private static String DST = "contract://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.bapp/sample/simple";
//	private static final String HOST = "121.41.28.145";
	private static final String CHAINID = "javabc";
	private static final String ADDR = "45874a3c0afc2a4d6cc9dea20245350f2981d3ea";
	private static final String HOST = "139.198.11.112";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";

	public static void main(String[] args) throws Exception {
		System.out.println("Usage: java -jar xxx.jar [host] [chainid] [useraddress] [times] [interval]");
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
		DST = "contract://"+chainid+"/"+addr+"/pdx.bapp/sample/simple";
		System.out.println("DST:"+DST);
		int len = 1;
		if (args != null && args.length>3) {
			len = Integer.parseInt(args[3]);
		}
		int interval =  100;
		if (args != null && args.length>4) {
			interval = Integer.parseInt(args[4]);
		}
		for (int i = 0 ; i < len ; i++) {		
			apply(driver);
			Thread.sleep(interval);
			query(driver);
		}
	}

	private static void query(BlockChainDriver driver) throws IOException {
		String qstr = UUID.randomUUID().toString();
		InputStream result = driver.query(DST, qstr.getBytes());
		if (result!= null) {
			System.out.println(IOUtils.toString(result, "utf-8"));
		}
		else {
			System.out.println("return null");
		}
	}

	private static void apply(BlockChainDriver driver) {
		Transaction tx = new Transaction();
		Map<String, byte[]> meta = new HashMap<>();
		meta.put("something", "test".getBytes());
		tx.putMeta(meta);
		tx.setBody("apply 测试".getBytes());
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

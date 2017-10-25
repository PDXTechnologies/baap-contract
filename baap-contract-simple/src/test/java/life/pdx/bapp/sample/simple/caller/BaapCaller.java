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

	private static String DST = "contract://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.dapp/sample/simple";
	private static final String HOST = "10.0.0.7";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";

	public static void main(String[] args) throws Exception {
		System.out.println("Usage: java -jar xxx.jar [host] [times] [useraddress]");
		Properties properties = new Properties();
		properties.setProperty("baap-chain-type", Constants.BAAP_CHAIN_TYPE_ETHEREUM);
		properties.setProperty("baap-chain-id", Constants.BAAP_CHAIN_ID_DEFAULT);
		if (args != null && args.length>0) {
			properties.setProperty("baap-url", "http://" + args[0] + ":8080/");
		}
		else{
			properties.setProperty("baap-url", "http://" + HOST + ":8080/");
		}
		properties.setProperty("baap-private-key", PRIVATE_KEY);
		BlockChainDriver driver = BlockChainDriverFactory.get(properties);
		int len = 1;
		if (args != null && args.length>1) {
			len = Integer.parseInt(args[1]);
		}
		if (args != null && args.length>2) {
			String addr = args[2];
			DST = "contract://default_pdx_chain/"+addr+"/pdx.dapp/sample/simple";
		}
		
		for (int i = 0 ; i < len ; i++) {		
			apply(driver);
			Thread.sleep(2000);
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

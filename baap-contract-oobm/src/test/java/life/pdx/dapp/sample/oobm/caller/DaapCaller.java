package life.pdx.dapp.sample.oobm.caller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import biz.pdxtech.daap.oobm.api.DaapException;
import biz.pdxtech.daap.oobm.api.OOBMClient;

/**
 * Hello world!
 */
public class DaapCaller {

	public static final String DST = "daap://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/biz.pdxtech/sample/oobm";
	private static final String HOST = "10.0.0.5";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";
    public static final String MAC_KEY = "123456";

    
    
    public static Properties genProperties() {
        Properties props = new Properties();
        props.setProperty("type", "ethereum");
        props.setProperty("privateKey", PRIVATE_KEY);
		props.setProperty("rpcUrl", "http://"+HOST+":8545");
		props.setProperty("containerUrl", "http://"+HOST+":8080");
        props.setProperty("dst", DST);
        props.setProperty("macKey", MAC_KEY);
        return props;
    }
    
    public static void main(String[] args) throws DaapException {
        System.out.println("Hello World!");
        Properties properties = genProperties();
        try {
        	OOBMClient oobmClient = OOBMClient.getInstance(properties);
        	//获取智能合约对应的oobm url
        	Map<String, byte[]> urls = oobmClient.execUrls(null);
			InputStream data = new FileInputStream("pom.xml");
			String txId = oobmClient.exec(urls, data);
			System.out.println("oobmId=" + oobmClient.execId());
			System.out.println("txId=" + txId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}

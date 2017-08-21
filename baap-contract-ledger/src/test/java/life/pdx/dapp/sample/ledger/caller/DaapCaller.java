package life.pdx.dapp.sample.ledger.caller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;
import life.pdx.dapp.sample.ledger.util.DappLastState;
import life.pdx.dapp.sample.ledger.util.HexUtil;


public class DaapCaller {
	private static final String DST = "daap://default_pdx_chain/45874a3c0afc2a4d6cc9dea20245350f2981d3ea/pdx.dapp/sample/ledger";
	private static final String HOST = "10.0.0.5";
	private static final String PRIVATE_KEY = "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9";

    public static void main(String[] args) throws Exception{
        /*1 setting props*/
        Properties props = new Properties();
		props.setProperty("privateKey", PRIVATE_KEY);
		props.setProperty("rpcUrl", "http://"+HOST+":8545");
		props.setProperty("containerUrl", "http://"+HOST+":8080");
        
        /*3 generate testKey*/
        System.out.println("**************************************");
        String testKey = LocalDateTime.now().toString();
        System.out.println("remember testKey is : " + testKey);
        System.out.println("**************************************");
        
        /*4 write tx to pdx*/
        BlockChainDriver driver = BlockChainDriverFactory.get(props);
        String secondTxId  = applyForQueryData(driver, testKey);
        
        if (secondTxId.startsWith("0x")) {
        	secondTxId = secondTxId.substring(2);
        }
        
        Thread.sleep(10000);
        queryTx(driver, testKey, secondTxId);
        queryState(driver, secondTxId);
    }

    /**
     * data into blockchain
     * @param driver
     * @param testKey
     */
    private static String applyForQueryData(BlockChainDriver driver, String testKey) {
        assert StringUtils.isNotEmpty(testKey);
        
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setBody(addTestKey("this is a good man", testKey).getBytes());
        HashMap<String, byte[]> map1 = new LinkedHashMap<String, byte[]>();
        map1.put("name", addTestKey("liudehua", testKey).getBytes());
        map1.put("age", addTestKey("45", testKey).getBytes());
        map1.put("sex", addTestKey("man", testKey).getBytes());
        map1.put("job", addTestKey("singer", testKey).getBytes());
        tx1.setMeta(map1);
        transactions.add(tx1);
        
        Transaction tx2 = new Transaction();
        tx2.setBody(addTestKey("this is a famous singer,she sings very well", testKey).getBytes());
        HashMap<String, byte[]> map2 = new LinkedHashMap<String, byte[]>();
        map2.put("name", addTestKey("wangfei", testKey).getBytes());
        map2.put("age", addTestKey("48", testKey).getBytes());
        map2.put("sex", addTestKey("woman", testKey).getBytes());
        map2.put("job", addTestKey("singer", testKey).getBytes());
        tx2.setMeta(map2);
        transactions.add(tx2);
        
        Transaction tx3 = new Transaction();
        tx3.setBody(addTestKey("this is a bad man", testKey).getBytes());
        HashMap<String, byte[]> map3 = new LinkedHashMap<String, byte[]>();
        map3.put("name", addTestKey("wangdachui", testKey).getBytes());
        map3.put("age", addTestKey("21", testKey).getBytes());
        map3.put("sex", addTestKey("man", testKey).getBytes());
        map3.put("isFat", addTestKey("yes", testKey).getBytes());
        tx3.setMeta(map3);
        transactions.add(tx3);
        
        Transaction tx4 = new Transaction();
        tx4.setBody(addTestKey("this is a programmer ", testKey).getBytes());
        HashMap<String, byte[]> map4 = new LinkedHashMap<String, byte[]>();
        map4.put("name", addTestKey("kangxinghao", testKey).getBytes());
        map4.put("age", addTestKey("21", testKey).getBytes());
        map4.put("sex", addTestKey("man", testKey).getBytes());
        map4.put("isTall", addTestKey("yes", testKey).getBytes());
        map4.put("job", addTestKey("engineer", testKey).getBytes());
        tx4.setMeta(map4);
        transactions.add(tx4);
        
        Transaction tx5 = new Transaction();
        tx5.setBody(addTestKey("this is a writer,he has written many books ", testKey).getBytes());
        HashMap<String, byte[]> map5 = new LinkedHashMap<String, byte[]>();
        map5.put("name", addTestKey("bochenlong", testKey).getBytes());
        map5.put("age", addTestKey("25", testKey).getBytes());
        map5.put("sex", addTestKey("man", testKey).getBytes());
        tx5.setMeta(map5);
        transactions.add(tx5);
        
        List<String> list = transactions.stream().map(a -> {
            try {
                a.setDst(new URI(DST));
                return a;
            } catch (Exception e) {
                e.printStackTrace();
                return a;
            }
        }).map(b -> {
            try {
                String res = driver.apply(b);
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }).collect(Collectors.toList()); 
        list.forEach(System.out::println);
        return list.get(1);
    }
    
    private static String addTestKey(String value, String testKey) {
        return new StringBuilder().append(value).append(" ").append(testKey).toString();
    }
    
    
    /**
     * Ledger查询分为两类；两类查询都可以通过设置Transaction.meta属性进行查询或者Transaction.body属性进行查询
     * <p>
     * ### 设置Transaction.meta属性查询
     * 1 查询交易
     * ① 根据交易ID查询；设置meta - ("DaaP-Query-TXID","txidtext".getBytes())
     * ② 根据自定义信息meta查询 设置meta - ("name","nametext") 可设置多个，但认为它们是与的关系
     * <p>
     * 2 查询合约状态
     * ① 根据交易ID查询；设置meta - ("DaaP-Query-STATE-TXID","txidtext".getBytes())；此处查询出来的结果为此次交易执行完的合约状态
     * ② 根据合约查询 设置meta - ("DaaP-Query-STATE-DST","dsttext")
     * <p>
     * 2 设置Transaction.body属性进行查询，这时候你需要传入一个exp表达式，表达式形如："${tx:body[bodytext]}".getBytes())
     * <p>
     * ### 设置Transaction.body属性查询
     * 1 查询交易
     * ① 根据交易ID查询 设置body表达式 "${tx:txid[txidtext]}".getBytes()
     * ② 根据自定义信息meta查询 设置body表达式 "${tx:meta[metak,metav]||meta[metak,metav]}".getBytes() 注意：metav是将原byte[] Hex序列化过的文本
     * 辅助查询条件页码 设置body表达式 "${tx:txid[txidtext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录
     * <p>
     * 2 查询合约状态
     * ① 根据交易Id查询 设置body表达式 "${state:txid[txidtext]}".getBytes()
     * ② 根据合约查询 设置body表达式 "${state:dst[dsttext]}".getBytes()
     * 辅助查询条件页码 设置body表达式 "${state:dst[dsttext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录
     * <p>
     * 查询请注意：
     * 1 如果查询条件中含有txid的条件，则默认只根据txid查询
     * 2 exp表达式中必须以${tx:}/${state:}的形式
     *
     * @param driver
     */
    private static void queryTx(BlockChainDriver driver, String testKey, String txid) {
    	System.out.println("\nqueryTx sample");
        assert StringUtils.isNotEmpty(testKey);
        
        
        /**1 设置Transaction.meta属性查询交易*/
        /** 根据交易ID查询*/
        Transaction tx = new Transaction();
        Map<String, byte[]> map = new LinkedHashMap<>();
        map.put("DaaP-Query-TX-TXID", txid.getBytes());
        tx.setMeta(map);
        queryTx(tx, driver);
        
        /** 根据自定义信息查询*/
        tx = new Transaction();
        map = new LinkedHashMap<>();
        map.put("name", DaapCaller.addTestKey("kangxinghao", testKey).getBytes());
        map.put("age", DaapCaller.addTestKey("22", testKey).getBytes());
        tx.setMeta(map);
        queryTx(tx, driver);
        
        /**2 设置Transaction.body属性进行查询*/
        /** 根据交易ID查询*/
        tx = new Transaction();
        tx.setBody(("${tx:txid["+txid+"]}").getBytes());
        queryTx(tx, driver);
        
        /** 根据自定义信息查询*/
        String exp = ("${tx:meta[name," + HexUtil.toHex(DaapCaller.addTestKey("kangxinghao", testKey).getBytes())
                + "]||meta[age," + HexUtil.toHex(DaapCaller.addTestKey("22", testKey).getBytes()) + "]}");
        tx.setBody(exp.getBytes());
        queryTx(tx, driver);
    }
    
    private static void queryState(BlockChainDriver driver, String txid) {
    	System.out.println("\nqueryState sample");
        /**1 设置Transaction.meta属性查询合约状态*/
        Transaction tx = new Transaction();
        Map<String, byte[]> map = new LinkedHashMap<>();
        map.put("DaaP-Query-STATE-TXID", txid.getBytes());
        tx.setMeta(map);
        queryState(tx, driver);
        /**2 设置Transaction.body属性进行查询*/
        /** 根据交易ID查询*/
        tx = new Transaction();
        tx.setBody(("${state:txid["+txid+"]}").getBytes());
        queryState(tx, driver);
        /** 根据合约地址查询*/
        tx = new Transaction();
        String exp = ("${state:dst[" + DST + "]");
        tx.setBody(exp.getBytes());
        queryState(tx, driver);
    }
    
    private static void queryTx(Transaction tx, BlockChainDriver driver) {
        try {
            tx.setDst(new URI("daap://tender-pdx/pdx.dapp/ledger"));
            List<Transaction> res = driver.query(tx);
            if (res != null && res.size() > 0) {
                res.stream().forEach(a -> System.out.println(new String(a.getBody())));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    private static void queryState(Transaction tx, BlockChainDriver driver) {
        try {
            tx.setDst(new URI("daap://tender-pdx/pdx.dapp/ledger"));
            List<Transaction> res = driver.query(tx);
            if (res != null && res.size() > 0) {
                res.stream().forEach(a -> {
             	    if(a.getBody() != null && a.getBody().length != 0) {
             	    	DappLastState state = null;
						try {
							state = mapper.readValue(a.getBody(), DappLastState.class);
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//             	    	DappLastState state = MessageCodec.toObject(a.getBody(), DappLastState.class);
                         state.getState().stream().forEach(b -> {
                             System.out.println("key: " + b.getKey()+", value: " + new String(b.getValue()));
                         });
                	}
                });
                System.out.println("");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    private static final ObjectMapper mapper ;
    static {
    	mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true); // Important for Token validation.
    }

}

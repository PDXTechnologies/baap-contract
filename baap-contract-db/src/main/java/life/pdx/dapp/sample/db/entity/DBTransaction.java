package life.pdx.dapp.sample.db.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "db_transaction")
//@XmlRootElement
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBTransaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	private String publickKey;
	
	public String getPublicKey() {
		return publickKey;
	}

	public void setPublicKey(String publickKey) {
		this.publickKey = publickKey;
	}
	private String txId;
	private Long createTime;
	

	/**
	 * daap://{tenantId}/{dappId}/{method}), or chain://hex-of-address
	 */
	private String dst;


	/**
	 * OPTIONAL TX signing algo. Default is blockchain's signing algo.
	 */
	private String sigAlgo;



	/**
	 * OPTIONAL timestamp for sign. The first signer should set this item.
	 */
	private long timestamp;



	/**
	 * OPTIONAL extra meta data if needed
	 */
//	Map<String, byte[]> meta;

	/**
	 * TX body
	 */
	private byte[] body;


	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}



	public String getSigAlgo() {
		return sigAlgo;
	}

	public void setSigAlgo(String sigAlgo) {
		this.sigAlgo = sigAlgo;
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}



//	public Map<String, byte[]> getMeta() {
//		return meta;
//	}
//
//	public void setMeta(Map<String, byte[]> meta) {
//		this.meta = meta;
//	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	
	public DBTransaction(){
		this.createTime = System.currentTimeMillis();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}


}

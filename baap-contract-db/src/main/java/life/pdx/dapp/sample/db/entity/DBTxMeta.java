package life.pdx.dapp.sample.db.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "db_txmeta")
//@XmlRootElement
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBTxMeta implements Serializable {
	
	public DBTxMeta(){
		this.createTime = System.currentTimeMillis();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String txId;

	private String metaKey;

	private Long createTime;
	
	public String getMetaKey() {
		return metaKey;
	}

	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}

	private byte[] metaValue;

	public byte[] getMetaValue() {
		return metaValue;
	}

	public void setMetaValue(byte[] metaValue) {
		this.metaValue = metaValue;
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

package life.pdx.dapp.sample.ledger.util;

/**
 * Created by bochenlong on 17-2-13.
 */
public class DappStateWrap {
    private String key;
    private byte[] keyHash;
    private byte[] value;
    private String txId;

    public DappStateWrap() {
    }

    public DappStateWrap(String key, byte[] value, String txId) {
        this.key = key;
        this.value = value;
        this.txId = txId;
    }

    public DappStateWrap(String key, byte[] value, String txId, byte[] keyHash) {
        this.key = key;
        this.value = value;
        this.txId = txId;
        this.keyHash = keyHash;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(byte[] keyHash) {
        this.keyHash = keyHash;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}

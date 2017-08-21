package life.pdx.dapp.sample.oobm.contract.constant;

/**
 * Created by bochenlong on 17-6-7.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum DaapHeader {
    TXID("X-DaaP-TXID"),
    NODEID("X-DaaP-NodeId"),
    DAPPDST("X-DaaP-DST");
    private String content;
    
    DaapHeader(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
}

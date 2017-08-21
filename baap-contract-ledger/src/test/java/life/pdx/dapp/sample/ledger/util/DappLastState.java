package life.pdx.dapp.sample.ledger.util;

import java.util.List;

/**
 * Created by bochenlong on 16-12-21.
 */
public class DappLastState {
    private String dst;
    private List<DappStateWrap> state;
    
    public DappLastState() {
    }
    
    public DappLastState(String dst, List<DappStateWrap> state) {
        this.dst = dst;
        this.state = state;
    }
    
    public String getDst() {
        return dst;
    }
    
    public void setDst(String dst) {
        this.dst = dst;
    }
    
    public List<DappStateWrap> getState() {
        return state;
    }
    
    public void setState(List<DappStateWrap> state) {
        this.state = state;
    }
}

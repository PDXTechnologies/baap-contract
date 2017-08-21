package life.pdx.dapp.sample.oobm.contract.constant;

/**
 * Created by bochenlong on 17-2-20.
 */
public enum StatusEnum {
    SUCCESS("SUCCESS", 200),
    FAIL("FAIL", 400),
    EXCEPTION("EXCEPTION", 500);
    
    String value;
    int code;
    
    StatusEnum(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

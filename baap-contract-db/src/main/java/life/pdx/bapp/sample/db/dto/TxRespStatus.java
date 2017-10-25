package life.pdx.bapp.sample.db.dto;

public enum TxRespStatus {
	INIT("Init"), 
	PENDING("pending"), 
	COMPLETING("completing"), 
	SIGNATURE("signature error"), 
	TIMEOUT("timeout"), 
	EXCEPTION("exception"), 
	DEDUP("de-dup"), 
	SUCCESS("success"), 
	FAILED("failed"),
	WAIT("wait"),
	PARAMERROR("paramerror"),
	USERNOTEXIST("user not exist"),
	BALANCENOTENOUGH(" balance not enough"),
	UNAUTHORIZED("Unauthorized");
	
	private String name;
	
	private TxRespStatus(String name){
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}

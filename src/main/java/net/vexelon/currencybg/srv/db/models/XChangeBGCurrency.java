package net.vexelon.currencybg.srv.db.models;

public class XChangeBGCurrency {

	private String  name                = "";
	private String  symbol              = "";
	private String  walletAddressFormat = "";
	private boolean enableDeposits      = false;
	private boolean enableWithdrawals   = false;
	private String  type                = "";
	private boolean active              = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getWalletAddressFormat() {
		return walletAddressFormat;
	}

	public void setWalletAddressFormat(String walletAddressFormat) {
		this.walletAddressFormat = walletAddressFormat;
	}

	public boolean isEnableDeposits() {
		return enableDeposits;
	}

	public void setEnableDeposits(boolean enableDeposits) {
		this.enableDeposits = enableDeposits;
	}

	public boolean isEnableWithdrawals() {
		return enableWithdrawals;
	}

	public void setEnableWithdrawals(boolean enableWithdrawals) {
		this.enableWithdrawals = enableWithdrawals;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

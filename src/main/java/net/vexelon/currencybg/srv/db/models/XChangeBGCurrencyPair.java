package net.vexelon.currencybg.srv.db.models;

public class XChangeBGCurrencyPair {

	private XChangeBGCurrency baseCurrency  = new XChangeBGCurrency();
	private XChangeBGCurrency quoteCurrency = new XChangeBGCurrency();
	private String            symbol        = "";
	private String            ask           = "";
	private String            bid           = "";
	private String            minBuyAmount  = "";
	private String            maxBuyAmount  = "";
	private String            minSellAmount = "";
	private String            maxSellAmount = "";
	private String            ordering      = "";
	private boolean           enableBuying  = false;
	private boolean           enableSelling = false;
	private boolean           active        = false;

	public XChangeBGCurrency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(XChangeBGCurrency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public XChangeBGCurrency getQuoteCurrency() {
		return quoteCurrency;
	}

	public void setQuoteCurrency(XChangeBGCurrency quoteCurrency) {
		this.quoteCurrency = quoteCurrency;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getMinBuyAmount() {
		return minBuyAmount;
	}

	public void setMinBuyAmount(String minBuyAmount) {
		this.minBuyAmount = minBuyAmount;
	}

	public String getMaxBuyAmount() {
		return maxBuyAmount;
	}

	public void setMaxBuyAmount(String maxBuyAmount) {
		this.maxBuyAmount = maxBuyAmount;
	}

	public String getMinSellAmount() {
		return minSellAmount;
	}

	public void setMinSellAmount(String minSellAmount) {
		this.minSellAmount = minSellAmount;
	}

	public String getMaxSellAmount() {
		return maxSellAmount;
	}

	public void setMaxSellAmount(String maxSellAmount) {
		this.maxSellAmount = maxSellAmount;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public boolean isEnableBuying() {
		return enableBuying;
	}

	public void setEnableBuying(boolean enableBuying) {
		this.enableBuying = enableBuying;
	}

	public boolean isEnableSelling() {
		return enableSelling;
	}

	public void setEnableSelling(boolean enableSelling) {
		this.enableSelling = enableSelling;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

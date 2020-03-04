package com.olympus.qlik.validate.ordreleased;

import java.math.BigDecimal;

public class OrdReleased {
	
	private String agreementNum;
	private BigDecimal netInvoice;
	private String appStatus;
	
	/*********************************************************************************************************************************************************/

	
	public BigDecimal getNetInvoice() {
		return netInvoice;
	}
	public String getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	public void setNetInvoice(BigDecimal netInvoice) {
		this.netInvoice = netInvoice;
	}
	/*********************************************************************************************************************************************************/
	
	
	public String getAgreementNum() {
		return agreementNum;
	}
	public void setAgreementNum(String agreementNum) {
		this.agreementNum = agreementNum;
	}
	 
	/*********************************************************************************************************************************************************/


}

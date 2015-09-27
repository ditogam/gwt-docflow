package com.socarmap.proxy.beans;

import java.io.Serializable;

public class Balance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7208986562553950480L;
	private int cusid;
	private double balance;
	private String balanceType;
	private String loanType;

	public Balance(int cusid, double balance, String balanceType,
			String loanType) {
		this.cusid = cusid;
		this.balance = balance;
		this.balanceType = balanceType;
		this.loanType = loanType;
	}

	public double getBalance() {
		return balance;
	}

	public String getBalanceType() {
		return balanceType;
	}

	public int getCusid() {
		return cusid;
	}

	public String getLoanType() {
		return loanType;
	}

	@Override
	public String toString() {
		return balanceType + " - " + balance + "(" + loanType + ")";
		// return balance + "(" + loanType + ")";
	}
}

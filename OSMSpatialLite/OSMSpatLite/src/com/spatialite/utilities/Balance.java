package com.spatialite.utilities;

public class Balance {
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

	public int getCusid() {
		return cusid;
	}

	public double getBalance() {
		return balance;
	}

	public String getBalanceType() {
		return balanceType;
	}

	public String getLoanType() {
		return loanType;
	}

	@Override
	public String toString() {
		return balanceType + " - " + balance + "(" + loanType + ")";
	}
}

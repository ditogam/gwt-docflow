package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MeterPlombs implements IsSerializable {
	/**
	 * 
	 */
	private int plombid;
	private String plombname;
	private long startdate;
	private String status;
	private String place;

	public String getPlace() {
		return place;
	}

	public int getPlombid() {
		return plombid;
	}

	public String getPlombname() {
		return plombname;
	}

	public long getStartdate() {
		return startdate;
	}

	public String getStatus() {
		return status;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setPlombid(int plombid) {
		this.plombid = plombid;
	}

	public void setPlombname(String plombname) {
		this.plombname = plombname;
	}

	public void setStartdate(long startdate) {
		this.startdate = startdate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static void main(String[] args) {
		String sql = "select chdate,r.zone,c.cityname,p.pcityname,readerdate,readername,username, cstp.custypename,cstp.custypeid\n"
				+ " from readerlist r \n"
				+ " left join customer cus on cus.cusid=r.cusid \n"
				+ " left join streets s on s.streetid=r.streetid \n"
				+ " left join city c on c.cityid=s.cityid \n"
				+ " left join pcity p on p.pcityid=c.pcityid \n"
				+ " left join custype cstp on cstp.custypeid=cus.custypeid \n"
				+ "  where listnum=" + 271155 + " limit 1 \n";

		System.out.println(sql);
	}
}

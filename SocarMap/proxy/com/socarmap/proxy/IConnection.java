package com.socarmap.proxy;

import java.util.ArrayList;
import java.util.Date;

import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.BuildingInfo;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.Customer;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.MakeDBProcess;
import com.socarmap.proxy.beans.MakeDBResponce;
import com.socarmap.proxy.beans.Meter;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.SUserContext;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.proxy.beans.UserContext;
import com.socarmap.proxy.beans.ZXYData;
import com.socarmap.proxy.beans.accident.Case;
import com.socarmap.proxy.beans.accident.Simple_View;
import com.socarmap.proxy.beans.accident.Step;

public interface IConnection {

	public static final int TP_DEMAGE_TYPE = 101;

	public Simple_View createAccidentCase(Case accident_case) throws SocarException;

	public MakeDBProcess createDBMakingProcess(int subregionid, String szones,
			Date lastDownloadedTiles) throws SocarException;

	public String createUniqueIDForFileTransfer() throws SocarException;

	public MakeDBProcess downloadTileDB(int subregionid,
			Date lastDownloadedTiles) throws SocarException;

	public Case getAccidentCase(int id, int user_id) throws SocarException;

	public ArrayList<Simple_View> getAccidents(int user_id, Date start_date,
			Date end_date) throws SocarException;

	public BuildingInfo getBuildingInfo(double x, double y, int srid,
			int buffer, SUserContext context) throws SocarException;

	public Customer getCustomerFull(int cusidid, SUserContext context)
			throws SocarException;

	public ArrayList<CusShort> getCustomers(Long subregion_id, Long zone,
			Long customer_id, boolean with_buildings, Long cus_type_id,
			Long building_id, boolean building_free, SUserContext context)
			throws SocarException;

	public ArrayList<CusShort> getCustomersForDistinctMeter(Long subregion_id,
			Long zone, Long customer_id, SUserContext context) throws SocarException;

	public ArrayList<CusShort> getCustomersForMeter(Long subregion_id,
			Long zone, Long customer_id, SUserContext context) throws SocarException;

	public byte[] getData(String uID) throws SocarException;

	public ArrayList<IDValue> getList(int type, SUserContext context)
			throws SocarException;

	public MakeDBResponce getMakeDBProcessStatus(String sessionID)
			throws SocarException;

	public ArrayList<Simple_View> getMyAccidents(Integer status_id,
			int user_id, Date start_date, Date end_date) throws SocarException;

	public ArrayList<Balance> loadBalance(Long customer_id, SUserContext context)
			throws SocarException;

	public ArrayList<Meter> loadMeters(Long customer_id, Long meter_id,
			SUserContext context) throws SocarException;

	public UserContext loginUser(String user_name, String pwd, int system,
			int language_id) throws SocarException;

	public void ping();

	public void proceedNewBuilding(NewBuilding newBuilding, boolean delete,
			SUserContext context) throws SocarException;

	public Simple_View saveAccidentStep(Step accident_case) throws SocarException;

	public Integer saveDemageDescription(String uID, DemageDescription descr,
			SUserContext context) throws SocarException;

	public void saveMeter(Long meterid, double value, Long cusid, int user_id,
			long modify_time) throws SocarException;

	public void transferFile(String uID, byte[] file_data) throws SocarException;

	public ArrayList<ZXYData> updateBuilding(BuildingUpdate buildingUpdate,
			SUserContext context) throws SocarException;

	public int updateDistinctMeter(int cusid, int user_id, double px,
			double py, int raiid, boolean demage, boolean remove)
			throws SocarException;

}

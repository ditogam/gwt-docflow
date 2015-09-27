package com.isomorphic.datasource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.isomorphic.base.Config;
import com.isomorphic.io.ISCFile;
import com.isomorphic.log.Logger;
import com.isomorphic.rpc.BuiltinRPC;
import com.isomorphic.util.DataTools;
import com.isomorphic.util.IOUtil;

public class SCUploadSaveFileDataSource
  extends BasicDataSource
{
  private static final long serialVersionUID = -4704649403493285140L;
  private static Logger log = new Logger(SCUploadSaveFileDataSource.class.getName());
  
  public DSResponse executeAdd(DSRequest req)
    throws Exception
  {
    Object fileContent = req.getCriteria().get("file");
    String fileContentAsString = null;
    if ((fileContent instanceof String))
    {
      fileContentAsString = (String)fileContent;
    }
    else
    {
      InputStream fileInputStream = (InputStream)req.getCriteria().get("file");
      fileContentAsString = IOUtil.inputStreamToString(fileInputStream);
    }
    String fileName = (String)req.getCriteria().get("file_filename");
    if ((fileName == null) || (fileName.length() == 0))
    {
      DSResponse dsResponse = new DSResponse(this);
      dsResponse.setStatus(DSResponse.STATUS_VALIDATION_ERROR);
      return dsResponse;
    }
    int slashPos = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
    if (slashPos >= 0) {
      fileName = fileName.substring(slashPos + 1);
    }
    int extStartPos = fileName.indexOf('.');
    if (extStartPos >= 0) {
      fileName = fileName.substring(0, extStartPos);
    }
    fileName = fileName + ".bmml";
    
    String filePath = (String)req.getCriteria().get("file_filepath");
    if ((filePath == null) || (filePath.length() == 0)) {
      filePath = "[VBWORKSPACE]";
    }
    String file = filePath + File.separator + fileName;
    
    List enabledBuiltinMethods = config.getList("RPCManager.enabledBuiltinMethods");
    boolean saveFileBuiltinIsEnabled = (enabledBuiltinMethods != null) && ((enabledBuiltinMethods.contains("saveFile")) || (enabledBuiltinMethods.contains("*")));
    if (saveFileBuiltinIsEnabled) {
      BuiltinRPC.saveFile(filePath + File.separator + fileName, fileContentAsString);
    }
    DSResponse dsResponse = new DSResponse(this);
    file = Config.expandPathVariables(file, false);
    if (ISCFile.inContainerIOMode()) {
      file = Config.expandPathVariables(file, true);
    }
    Map data = DataTools.buildMap("fileName", fileName, "fileContent", fileContentAsString, "filePath", saveFileBuiltinIsEnabled ? file : fileName);
    
    dsResponse.setData(data);
    dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
    return dsResponse;
  }
  
  public DSResponse executeCustom(DSRequest req)
    throws Exception
  {
    if ("checkUploadFeature".equals(req.getOperationId()))
    {
      DSResponse dsResponse = new DSResponse(this);
      dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
      return dsResponse;
    }
    return super.executeCustom(req);
  }
  
  public DSResponse executeFetch(DSRequest req)
    throws Exception
  {
    String path = (String)req.getCriteria().get("path");
    File f = new File(path);
    DSResponse dsResponse = new DSResponse(this);
    dsResponse.setData(DataTools.buildMap("lastChangeDate", Long.valueOf(f.lastModified())));
    dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
    return dsResponse;
  }
}

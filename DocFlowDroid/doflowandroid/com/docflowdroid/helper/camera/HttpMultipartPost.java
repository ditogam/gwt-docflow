package com.docflowdroid.helper.camera;

import java.io.File;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.docflowdroid.ActivityHelper;
import com.docflowdroid.helper.DrowableDownloader;
import com.docflowdroid.helper.ICameraResult;
import com.docflowdroid.helper.camera.CustomMultiPartEntity.ProgressListener;

public class HttpMultipartPost extends AsyncTask<HttpResponse, Integer, String> {

	private ProgressDialog pd;
	private long totalSize;
	private String m_userSelectedImagePath;
	private Activity act;
	private ICameraResult cameraResult;
	private Throwable err;
	private String serverResponse;

	public HttpMultipartPost(Activity act, String m_userSelectedImagePath,
			ICameraResult cameraResult) {
		this.act = act;
		this.m_userSelectedImagePath = m_userSelectedImagePath;
		this.cameraResult = cameraResult;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(act);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Uploading Picture...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(HttpResponse... arg0) {

		HttpClient httpClient = null;
		HttpPost httpPost = null;

		try {
			httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();
			httpPost = new HttpPost(DrowableDownloader.getMainURL()
					+ "FileUpload.jsp");

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.addPart("uploaded_file", new FileBody(new File(
					m_userSelectedImagePath)));
			final HttpEntity entity = entityBuilder.build();

			ProgressListener callback = new ProgressListener() {
				@Override
				public void transferred(long num) {
					publishProgress((int) ((num / (float) totalSize) * 100));
				}
			};
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
					entity, callback);
			totalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			String serverResponse = EntityUtils.toString(response.getEntity());
			this.serverResponse = serverResponse;
			return serverResponse;
		}

		catch (Throwable e) {
			err = e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Throwable e2) {
				// TODO: handle exception
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String ui) {
		if (err != null)
			ActivityHelper.showAlert(act, err);
		else {
			try {
				HashMap<Long, String> result = new HashMap<Long, String>();
				JSONArray arr = new JSONArray(serverResponse.trim());
				for (int i = 0; i < arr.length(); i++) {
					JSONObject jsonItem = (JSONObject) arr.get(i);
					Long id = Long.parseLong(jsonItem.get("id").toString());
					String file_name = jsonItem.get("file").toString();
					result.put(id, file_name);
				}
				if (cameraResult != null)
					cameraResult.setResult(result);
			} catch (Throwable e) {
				ActivityHelper.showAlert(act, e);
			}
		}
		pd.dismiss();
	}
}
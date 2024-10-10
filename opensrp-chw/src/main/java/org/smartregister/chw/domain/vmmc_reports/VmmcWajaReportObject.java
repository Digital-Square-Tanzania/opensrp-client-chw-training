package org.smartregister.chw.domain.vmmc_reports;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VmmcWajaReportObject extends ReportObject {


    private Date reportDate;

    public VmmcWajaReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
    }


    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONArray dataArray = new JSONArray();
        List<Map<String, String>> vmmcWajaReport = ReportDao.getVmmcWajaReport(reportDate);

        int i = 0;

        for (Map<String, String> getVmmcWajaReport : vmmcWajaReport) {
            JSONObject reportJsonObject = new JSONObject();
            reportJsonObject.put("id", ++i);

            reportJsonObject.put("referral_date", getVmmcClientDetails(getVmmcWajaReport, "referral_date"));
            reportJsonObject.put("referral_appointment_date", getVmmcClientDetails(getVmmcWajaReport, "referral_appointment_date"));
            reportJsonObject.put("names", getVmmcClientDetails(getVmmcWajaReport, "names"));
            reportJsonObject.put("age", getVmmcClientDetails(getVmmcWajaReport, "age"));
            reportJsonObject.put("referral_status", getVmmcClientDetails(getVmmcWajaReport, "referral_status"));
            dataArray.put(reportJsonObject);
        }


        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("reportData", dataArray);

        return resultJsonObject;
    }

    private String getVmmcClientDetails(Map<String, String> chwWajaClient, String key) {
        String details = chwWajaClient.get(key);
        if (StringUtils.isNotBlank(details)) {
            return details;
        }
        return "-";
    }

}

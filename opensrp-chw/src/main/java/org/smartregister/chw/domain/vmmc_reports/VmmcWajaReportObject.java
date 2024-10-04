package org.smartregister.chw.domain.vmmc_reports;

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
        List<Map<String, String>> getVmmcRegisterList = ReportDao.getVmmcWajaReport(reportDate);

        int i = 0;

        for (Map<String, String> getVmmcRegister : getVmmcRegisterList) {
            JSONObject reportJsonObject = new JSONObject();
            reportJsonObject.put("id", ++i);

            reportJsonObject.put("mc_procedure_date", getVmmcClientDetails(getVmmcRegister, "mc_procedure_date"));
            reportJsonObject.put("vmmc_client_id", getVmmcClientDetails(getVmmcRegister, "vmmc_client_id"));
            reportJsonObject.put("names", getVmmcClientDetails(getVmmcRegister, "names"));
            reportJsonObject.put("age", getVmmcClientDetails(getVmmcRegister, "age"));
            reportJsonObject.put("male_circumcision_method", getVmmcClientDetails(getVmmcRegister, "male_circumcision_method"));
            dataArray.put(reportJsonObject);
        }


        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("reportData", dataArray);

        return resultJsonObject;
    }

    private String getVmmcClientDetails(Map<String, String> chwRegistrationFollowupClient, String key) {
        String details = chwRegistrationFollowupClient.get(key);
        if (StringUtils.isNotBlank(details)) {
            return details;
        }
        return "-";
    }

}

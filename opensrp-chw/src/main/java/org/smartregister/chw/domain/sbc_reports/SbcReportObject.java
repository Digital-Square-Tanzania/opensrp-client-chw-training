package org.smartregister.chw.domain.sbc_reports;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SbcReportObject extends ReportObject {
    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();
    private final String[] indicatorCodes = new String[]{"sbc-1", "sbc-2", "sbc-3"};
    private final String[] indicatorSex = new String[]{"male", "female"};
    private final String[] indicatorAgeGroups = new String[]{"10-14", "15-19", "20-24", "25-29", "30-34",
            "35-39", "40-44", "45-49", "50+"

    };
    private final Date reportDate;

    public SbcReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodesWithAgeGroups(indicatorCodesWithAgeGroups);
    }

    public static int calculateSbcSpecificTotal(HashMap<String, Integer> indicators, String specificKey) {
        int total = 0;

        for (Map.Entry<String, Integer> entry : indicators.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Integer value = entry.getValue();

            if (key.startsWith(specificKey.toLowerCase())) {
                total += value;
            }
        }

        return total;
    }

    public void setIndicatorCodesWithAgeGroups(List<String> indicatorCodesWithAgeGroups) {
        for (String indicatorCode : indicatorCodes) {
            for (String sex : indicatorSex) {
                for (String indicatorKey : indicatorAgeGroups) {
                    indicatorCodesWithAgeGroups.add(indicatorCode + "-" + sex + "-" + indicatorKey);
                }
            }
        }

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        HashMap<String, Integer> indicatorsValues = new HashMap<>();
        JSONObject indicatorDataObject = new JSONObject();
        for (String indicatorCode : indicatorCodesWithAgeGroups) {
            int value = ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate);
            indicatorsValues.put(indicatorCode, value);
            indicatorDataObject.put(indicatorCode, value);
        }

        // Calculate and add total values for "totals"
        for (int i = 1; i < 4; i++) {
            int maleTotal = calculateSbcSpecificTotal(indicatorsValues, "sbc-" + i + "-male");
            int femaleTotal = calculateSbcSpecificTotal(indicatorsValues, "sbc-" + i + "-female");
            indicatorDataObject.put("sbc-" + i + "-male-total", maleTotal);
            indicatorDataObject.put("sbc-" + i + "-female-total", femaleTotal);
            indicatorDataObject.put("sbc-" + i + "-grand-total", maleTotal + femaleTotal);
        }

        int sbc4maleTotal = ReportDao.getReportPerIndicatorCode("sbc-4-male-total", reportDate);
        int sbc4femaleTotal = ReportDao.getReportPerIndicatorCode("sbc-4-female-total", reportDate);
        indicatorDataObject.put("sbc-4-male-total", sbc4maleTotal);
        indicatorDataObject.put("sbc-4-female-total", sbc4femaleTotal);
        indicatorDataObject.put("sbc-4-grand-total", sbc4maleTotal + sbc4femaleTotal);


        return indicatorDataObject;
    }
}
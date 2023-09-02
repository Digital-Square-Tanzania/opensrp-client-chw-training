package org.smartregister.chw.domain;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.sbc_reports.SbcReportObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class SbcReportObjectTest {
    private SbcReportObject sbcReportObject;

    private HashMap<String, Integer> testIndicators;

    private Date reportDate;

    @Before
    public void setUp() throws ParseException {
        MockitoAnnotations.initMocks(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        reportDate = sdf.parse("2023-09-01");
        sbcReportObject = new SbcReportObject(reportDate);

        // Mocking ReportDao.getReportPerIndicatorCode method
        testIndicators = new HashMap<>();
        testIndicators.put("sbc-1-male-10-14", 5);
        testIndicators.put("sbc-1-male-15-19", 10);
        testIndicators.put("sbc-1-female-10-14", 6);
        testIndicators.put("sbc-1-female-15-19", 9);

        for (String indicatorCode : testIndicators.keySet()) {
            try (MockedStatic<ReportDao> reportDao = Mockito.mockStatic(ReportDao.class)) {
                reportDao.when(() -> ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate))
                        .thenReturn(testIndicators.get(indicatorCode));

            }

        }
    }

    @Test
    public void testCalculateSbcSpecificTotal() {
        HashMap<String, Integer> indicators = new HashMap<>();
        indicators.put("sbc-1-male-10-14", 10);
        indicators.put("sbc-1-male-15-19", 20);
        indicators.put("sbc-1-female-10-14", 5);
        indicators.put("sbc-2-male-10-14", 7);
        indicators.put("sbc-2-female-15-19", 9);

        int total = SbcReportObject.calculateSbcSpecificTotal(indicators, "sbc-1-male");
        assertEquals(30, total);

        total = SbcReportObject.calculateSbcSpecificTotal(indicators, "sbc-2-female");
        assertEquals(9, total);
    }

    @Test
    public void testGetIndicatorData() throws JSONException {

        JSONObject indicatorDataObject;
        try (MockedStatic<ReportDao> reportDao = Mockito.mockStatic(ReportDao.class)) {
            for (String indicatorCode : testIndicators.keySet()) {
                reportDao.when(() -> ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate))
                        .thenReturn(testIndicators.get(indicatorCode));

            }
            indicatorDataObject = sbcReportObject.getIndicatorData();
        }


        for (String indicatorCode : testIndicators.keySet()) {
            assertEquals(testIndicators.get(indicatorCode).intValue(), indicatorDataObject.getInt(indicatorCode));
        }

        assertEquals(15, indicatorDataObject.getInt("sbc-1-male-total"));
        assertEquals(15, indicatorDataObject.getInt("sbc-1-female-total"));
    }
}

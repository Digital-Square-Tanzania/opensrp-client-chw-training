package org.smartregister.chw.dao;

import org.smartregister.chw.model.SbcMobilizationSessionModel;
import org.smartregister.chw.model.SbcMonthlySocialMediaReportModel;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.chw.sbc.util.Constants;

import java.util.List;

public class ChwSbcDao extends SbcDao {

    public static List<SbcMobilizationSessionModel> getSbcMobilizationSessions() {
        String sql = "SELECT * FROM " + Constants.TABLES.SBC_MOBILIZATION_SESSIONS;

        DataMap<SbcMobilizationSessionModel> dataMap = cursor -> {
            SbcMobilizationSessionModel sbcMobilizationSessionModel = new SbcMobilizationSessionModel();
            sbcMobilizationSessionModel.setSessionId(cursor.getString(cursor.getColumnIndex("id")));

            String communitySbcActivity = cursor.getString(cursor.getColumnIndex("community_sbc_activity_provided"));
            sbcMobilizationSessionModel.setCommunitySbcActivityType(communitySbcActivity);
            sbcMobilizationSessionModel.setSessionDate(cursor.getString(cursor.getColumnIndex("mobilization_date")));

            return sbcMobilizationSessionModel;
        };

        List<SbcMobilizationSessionModel> res = readData(sql, dataMap);
        if (res == null || res.size() == 0) return null;
        return res;
    }

    public static List<SbcMonthlySocialMediaReportModel> getSbcMonthlySocialMediaReport() {
        String sql = "SELECT * FROM " + Constants.TABLES.SBC_MONTHLY_SOCIAL_MEDIA_REPORT;

        DataMap<SbcMonthlySocialMediaReportModel> dataMap = cursor -> {
            SbcMonthlySocialMediaReportModel sbcMonthlySocialMediaReportModel = new SbcMonthlySocialMediaReportModel();
            sbcMonthlySocialMediaReportModel.setReportId(cursor.getString(cursor.getColumnIndex("id")));

            sbcMonthlySocialMediaReportModel.setReportingMonth(cursor.getString(cursor.getColumnIndex("reporting_month")));
            sbcMonthlySocialMediaReportModel.setOrganizationName(cursor.getString(cursor.getColumnIndex("organization_name")));
            sbcMonthlySocialMediaReportModel.setSocialMediaHivMsgDistribution(cursor.getString(cursor.getColumnIndex("social_media_hiv_msg_distribution")));
            sbcMonthlySocialMediaReportModel.setNumberBeneficiariesReachedFacebook(cursor.getString(cursor.getColumnIndex("number_beneficiaries_reached_facebook")));
            sbcMonthlySocialMediaReportModel.setNumberMessagesPublications(cursor.getString(cursor.getColumnIndex("number_messages_publications")));
            sbcMonthlySocialMediaReportModel.setNumberAiredMessagesBroadcasted(cursor.getString(cursor.getColumnIndex("number_aired_messages_broadcasted")));

            return sbcMonthlySocialMediaReportModel;
        };

        List<SbcMonthlySocialMediaReportModel> res = readData(sql, dataMap);
        if (res == null || res.size() == 0) return null;
        return res;
    }
}

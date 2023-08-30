package org.smartregister.chw.dao;

import org.smartregister.chw.model.SbcMobilizationSessionModel;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.family.util.DBConstants;

import java.util.List;

public class ChwSbcDao extends SbcDao {

    public static List<SbcMobilizationSessionModel> getSbcMobilizationSessions() {
        String sql = "SELECT * FROM " + Constants.TABLES.SBC_MOBILIZATION_SESSIONS;

        DataMap<SbcMobilizationSessionModel> dataMap = cursor -> {
            SbcMobilizationSessionModel sbcMobilizationSessionModel = new SbcMobilizationSessionModel();
            sbcMobilizationSessionModel.setSessionId(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID)));

            String communitySbcActivity = cursor.getString(cursor.getColumnIndex("community_sbc_activity_provided"));
            sbcMobilizationSessionModel.setCommunitySbcActivityType(communitySbcActivity);
            sbcMobilizationSessionModel.setSessionDate(cursor.getString(cursor.getColumnIndex("mobilization_date")));

            return sbcMobilizationSessionModel;
        };

        List<SbcMobilizationSessionModel> res = readData(sql, dataMap);
        if (res == null || res.size() == 0) return null;
        return res;
    }
}

package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.interactor.CoreAncHomeVisitInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.ReferralUtils;

import java.util.Map;

public class AncHomeVisitInteractor extends CoreAncHomeVisitInteractor {

    Context context;
    public AncHomeVisitInteractor(Context context) {
        this.context = context;
        setFlavor(new AncHomeVisitInteractorFlv());
    }

    @Override
    protected void submitVisit(boolean editMode, String memberID, Map<String, BaseAncHomeVisitAction> map, String parentEventType) throws Exception {
        if (map != null && !editMode) {
            BaseAncHomeVisitAction facilitySelectionAction = map.get(context.getString(R.string.home_visit_facility_referral));
            if (facilitySelectionAction != null) {
                String facilitySelectionForm = facilitySelectionAction.getJsonPayload();
                if (facilitySelectionForm != null) {

                    BaseAncHomeVisitAction dangerSignsActions = map.get(context.getString(R.string.anc_home_visit_danger_signs));

                    try {

                        // Here pass the  dangerSignsSelectedObject to the processReferral method and then add the problems to the referralProblems
                        ReferralUtils.processReferral(facilitySelectionForm, memberID, CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS, dangerSignsActions.getJsonPayload());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        super.submitVisit(editMode, memberID, map, parentEventType);
    }

}

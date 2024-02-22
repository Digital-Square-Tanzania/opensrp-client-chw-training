package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

public class ComplimentaryFeedingActionHelper extends HomeVisitActionHelper {
    Alert alert;
    Context context;
    ServiceWrapper serviceWrapper;

    public ComplimentaryFeedingActionHelper(){}

    @Override
    public void onPayloadReceived(String jsonPayload) {
        //Update the form before showing to the user
    }

    @Override
    public String evaluateSubTitle() {
        return "";
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.COMPLETED;
    }
}

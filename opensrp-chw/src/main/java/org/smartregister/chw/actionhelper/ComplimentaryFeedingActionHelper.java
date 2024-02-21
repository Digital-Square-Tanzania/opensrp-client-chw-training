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
    public ComplimentaryFeedingActionHelper(Context context, ServiceWrapper serviceWrapper, Alert alert){
        this.context = context;
        this.serviceWrapper = serviceWrapper;
        this.alert = alert;
    }

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

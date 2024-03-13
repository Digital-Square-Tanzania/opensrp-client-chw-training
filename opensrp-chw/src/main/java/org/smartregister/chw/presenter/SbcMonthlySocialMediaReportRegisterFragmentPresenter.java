package org.smartregister.chw.presenter;

import org.smartregister.chw.sbc.contract.SbcRegisterFragmentContract;
import org.smartregister.chw.sbc.presenter.BaseSbcRegisterFragmentPresenter;
import org.smartregister.chw.sbc.util.Constants;

public class SbcMonthlySocialMediaReportRegisterFragmentPresenter extends BaseSbcRegisterFragmentPresenter {
    public SbcMonthlySocialMediaReportRegisterFragmentPresenter(SbcRegisterFragmentContract.View view, SbcRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.SBC_MONTHLY_SOCIAL_MEDIA_REPORT;
    }

}

package org.smartregister.chw.presenter;

import org.smartregister.chw.fp.contract.BaseFpRegisterFragmentContract;
import org.smartregister.chw.fp.presenter.BaseFpRegisterFragmentPresenter;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

public class FpRegisterFragmentPresenter extends BaseFpRegisterFragmentPresenter {

    public FpRegisterFragmentPresenter(BaseFpRegisterFragmentContract.View view,
                                       BaseFpRegisterFragmentContract.Model model) {
        super(view, model, FamilyPlanningConstants.CONFIGURATION.FP_REGISTRATION_CONFIGURATION);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_family_planning.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }
}

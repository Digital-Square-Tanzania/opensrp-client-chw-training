package org.smartregister.chw.activity;

import org.json.JSONObject;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.activity.CoreCdpRegisterActivity;
import org.smartregister.chw.core.fragment.CoreCdpRegisterFragment;
import org.smartregister.chw.core.fragment.CoreOrdersRegisterFragment;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import androidx.fragment.app.Fragment;

public class CdpRegisterActivity extends CoreCdpRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CoreCdpRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{
                new CoreOrdersRegisterFragment()
        };
    }

    @Override
    public void startOutletForm() {
        JSONObject form = FormUtils.getFormUtils().getFormJson(Constants.FORMS.CDP_OUTLET_REGISTRATION);
        startFormActivity(form);
    }
}

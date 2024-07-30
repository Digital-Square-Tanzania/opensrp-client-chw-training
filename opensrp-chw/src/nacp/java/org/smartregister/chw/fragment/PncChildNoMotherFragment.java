package org.smartregister.chw.fragment;

import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import org.smartregister.chw.model.ChildRegisterFragmentModel;
import org.smartregister.chw.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.chw.provider.ChildRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class PncChildNoMotherFragment extends CoreChildRegisterFragment {


    @Override
    protected String getMainCondition() {
        return "";
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChildRegisterProvider provider = new ChildRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new ChildRegisterFragmentPresenter( this, new ChildRegisterFragmentModel(), null);
    }


}

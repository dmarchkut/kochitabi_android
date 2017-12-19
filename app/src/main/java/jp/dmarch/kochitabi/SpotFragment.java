package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SpotFragment extends Fragment {

    private static final String ARG_PARAM = "page";
    private String mParam;
    //private OnFragmentInteractionListener mListener;

    public SpotFragment() {
    }

    public static SpotFragment newInstance(int page) {
        SpotFragment fragment = new SpotFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        int page = getArguments().getInt(ARG_PARAM, 0);
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        ((TextView)view.findViewById(R.id.textView_spotFragment)).setText("Page" + page);

        return view;
    }

}

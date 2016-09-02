package com.example.xing.androidlearning.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.customview.SideScollBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContactsListFragment extends Fragment {

    final private String TAG = "ContactsListFragment";

    private SideScollBar mSideScollBar;
    private ListView mLvContactsList;
    private List<String> mContactsList;
    private SideScollBar.ContantListAdapter adapter;

    private String[] charactorA_Z = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnImagePickedListener mListener;

    public ContactsListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContactsListFragment newInstance() {
        ContactsListFragment fragment = new ContactsListFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long viewCreateTime = System.currentTimeMillis();
        Log.e(TAG, "onCreateView begin " + viewCreateTime + "");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        mContactsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.listpersons)));

        mLvContactsList = (ListView) root.findViewById(R.id.ch_lv_contacts_list);
        mLvContactsList.setVerticalScrollBarEnabled(false);
//        adapter = new SideScollBar.ContantListAdapter(getContext(), mContactsList, Arrays.asList(charactorA_Z), "*");

        mSideScollBar = (SideScollBar) root.findViewById(R.id.ch_sidescollbar);
        mSideScollBar.setRelativedListView(mLvContactsList).setListViewContantList(mContactsList);
        mSideScollBar.setIndicatorDialog(root.findViewById(R.id.ch_tv_indicator));
//        charactorA_Z[charactorA_Z.length - 1] = "*";
        mSideScollBar.setCharactorA_Z(charactorA_Z);
//        mSideScollBar.setOnSelectedCharChangeListener(new SideScollBar.OnSelectedCharChangeListener() {
//            @Override
//            public void onSelectedCharChange(String selectedChar) {
//                int position = adapter.getPositionForSelection(selectedChar);
//                if (position != -1) {
//                    mLvContactsList.setSelection(position);
//                }
//            }
//        });

//        mLvContactsList.setAdapter(adapter);
        Log.e(TAG, "onCreateView used " + (System.currentTimeMillis() - viewCreateTime) + "");
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onImagePicked(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnImagePickedListener) {
//            mListener = (OnImagePickedListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnImagePickedListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


//    public interface OnImagePickedListener {
//        // TODO: Update argument type and name
//        void onImagePicked(Uri uri);
//    }
}

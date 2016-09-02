package com.example.xing.androidlearning.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xing.androidlearning.Activity.ImageFilterActivity;
import com.example.xing.androidlearning.Activity.ImagePickActivity;
//import com.example.xing.androidlearning.NativeImageLoader;
import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.natives.JniEntry;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnImagePickedListener} interface
 * to handle interaction events.
 * Use the {@link ImagePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePickerFragment extends Fragment {

    private final String TAG = "ImagePickerFragment";
    private static final String ARG_PARAM1 = "parentPath";
    private static final String ARG_PARAM2 = "childRelativePath";

    private String mParentPath;
    private ArrayList<String> mChildRelativePath;

    private OnImagePickedListener mListener;
    private HomeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public ImagePickerFragment() {
        // Required empty public constructor
    }

    public static ImagePickerFragment newInstance(String parentPath, ArrayList<String> childRelativePath) {
        ImagePickerFragment fragment = new ImagePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, parentPath);
        args.putStringArrayList(ARG_PARAM2, childRelativePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParentPath = getArguments().getString(ARG_PARAM1);
            mChildRelativePath = getArguments().getStringArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_picker, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.imagepicker_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
        return root;
    }

    public void onImagePicked(Uri uri) {
        if (mListener != null) {
            mListener.onImagePicked(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        NativeImageLoader.setShouldStopThread(false);
        if (context instanceof OnImagePickedListener) {
            mListener = (OnImagePickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnImagePickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        NativeImageLoader.setShouldStopThread(true);
        mListener = null;
    }


    public interface OnImagePickedListener {
        void onImagePicked(Uri uri);
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getActivity()).inflate(R.layout.item_image_list, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
//            NativeImageLoader.setShouldStopThread(false);
            String path = mParentPath + "/" + mChildRelativePath.get(position);

            Log.d(TAG, "load pic" + position + ": " + path);
//            holder.iv.setImageURI(Uri.parse(Uri.parse();
            holder.iv.setTag(path);
            holder.iv.setBackground(new ColorDrawable(Color.rgb(44, 44, 44)));
            Picasso.with(getActivity()).load(new File(path)).fit().centerCrop().into(holder.iv);
            //利用NativeImageLoader类加载本地图片
//            final Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new Point(360, 420), new NativeImageLoader.NativeImageCallBack() {
//                @Override
//                public void onImageLoader(Bitmap bitmap, String path) {
//                    ImageView mImageView = (ImageView) mRecyclerView.findViewWithTag(path);
//                    Log.d(TAG, "findViewByTag: " + path);
//                    if (bitmap != null && mImageView != null) {
//                        mImageView.setImageBitmap(bitmap);
//                    }
//                }
//            });

//            if (bitmap != null) {
//                holder.iv.setImageBitmap(bitmap);
//            } else {
//                holder.iv.setImageDrawable(new ColorDrawable(Color.rgb(0, 0, 0)));
//            }
            if (getTag() == ImagePickActivity.TAG_ImageFolderFragment) {
                holder.tv.setText((new File(path)).getParentFile().getName());
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = (new File(mChildRelativePath.get(position))).getParentFile().getPath();
                        ArrayList<String> picNameList = ((ImagePickActivity) getActivity()).getmImageListPresenter().getAllPicOfFolder(folder);
                        Log.d(TAG, "add fragmet,path: " + folder + ", 1st pic:" + picNameList.get(0));
                        getActivity().getFragmentManager().beginTransaction().add(R.id.imagepicker_container,
                                ImagePickerFragment.newInstance(folder, picNameList),
                                ImagePickActivity.TAG_ImageFragment)
                                .addToBackStack(ImagePickActivity.TAG_ImageFragment).commit();
                    }
                });
            } else if (getTag() == ImagePickActivity.TAG_ImageFragment) {
                holder.tv.setText((new File(path)).getName());
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "pic selected: \n" + (new File((String) v.getTag())).getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ImageFilterActivity.class);
                        intent.putExtra(ImageFilterActivity.EXTRA_PIC_PATH, (String) v.getTag());
                        startActivity(intent);
//                        JniEntry.processBitmap(((BitmapDrawable) holder.iv.getDrawable()).getBitmap());
//                        holder.iv.invalidate();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mChildRelativePath.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.image_list_item_iv);
                tv = (TextView) view.findViewById(R.id.image_list_item_tv);
            }
        }
    }
}

package com.example.xing.androidlearning.fakewechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.util.CommomUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by james on 2016/7/6.
 */
public class SideScollBar extends View {

    final private String TAG = "SideScollBar";

    //默认使用这些字符，可
    private String[] charactorA_Z = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private View mIndicatorDialog;
    private OnSelectedCharChangeListener onSelectedCharChangeListener;
    private Paint paint = new Paint();

    private int mTextSize;
    private int mMaskColor;
    private int mTextColor;


    private ListView mRelativedListView;
    private ContantListAdapter mContantListAdapter;

    private List<String> mListViewContantList = new ArrayList<>();

    public SideScollBar(Context context) {
        super(context);
    }

    public SideScollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.bottommenubar);
        mTextSize = a.getInt(R.styleable.bottommenubar_textSize, 12);
        mMaskColor = a.getColor(R.styleable.bottommenubar_maskColor, 0xAAC0C0C0);
        mTextColor = a.getColor(R.styleable.bottommenubar_textColor, 0xFF169BD5);
        a.recycle();
        init();
    }

    private void init() {

        Log.d(TAG, "contantList length = " + mListViewContantList.size());
        mContantListAdapter = new ContantListAdapter(getContext(), mListViewContantList, Arrays.asList(charactorA_Z), null);
        if (mRelativedListView != null) {
            mRelativedListView.setAdapter(mContantListAdapter);
        }
        onSelectedCharChangeListener = new OnSelectedCharChangeListener() {
            @Override
            public void onSelectedCharChange(String selectedChar) {
//                Log.d(TAG, "onSelectedCharChange");
                int position = mContantListAdapter.getPositionForSelection(selectedChar);
                if (position != -1) {
                    mRelativedListView.setSelection(position);
                }
            }
        };
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int barHeight = getHeight();
        int barWidth = getWidth();
        int singleCharHeight = barHeight / charactorA_Z.length;

        for (int i = 0; i < charactorA_Z.length; i++) {
            paint.setColor(mTextColor);
            paint.setAntiAlias(true);
            paint.setTextSize(CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_SP, mTextSize));

            float xPos = barWidth / 2 - paint.measureText(charactorA_Z[i]) / 2;
            float yPos = singleCharHeight * i + singleCharHeight;
            canvas.drawText(charactorA_Z[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float yPos = event.getY();
        int choosedIndex = (int) ((yPos / getHeight()) * charactorA_Z.length);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(0x00FFFFFF);
                if (mIndicatorDialog != null) {
                    mIndicatorDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                setBackgroundColor(mMaskColor);
                if (mIndicatorDialog != null) {
                    mIndicatorDialog.setVisibility(View.VISIBLE);
                }
                if (choosedIndex >= 0 && choosedIndex < charactorA_Z.length) {
                    if (onSelectedCharChangeListener != null) {
                        onSelectedCharChangeListener.onSelectedCharChange(charactorA_Z[choosedIndex]);
                    }
                    if (mIndicatorDialog != null) {
                        ((TextView) mIndicatorDialog).setText(charactorA_Z[choosedIndex]);
                        mIndicatorDialog.setVisibility(View.VISIBLE);
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void setIndicatorDialog(View indicatorDialog) {
        this.mIndicatorDialog = indicatorDialog;
        indicatorDialog.setVisibility(INVISIBLE);
    }

    public void setCharactorA_Z(String[] charactorA_Z) {
        this.charactorA_Z = charactorA_Z;
    }

    public void setOnSelectedCharChangeListener(OnSelectedCharChangeListener onSelectedCharChangeListener) {
        this.onSelectedCharChangeListener = onSelectedCharChangeListener;
    }

    public interface OnSelectedCharChangeListener {
        void onSelectedCharChange(String selectedChar);
    }

    public SideScollBar setRelativedListView(ListView relativedListView) {
        this.mRelativedListView = relativedListView;
        mRelativedListView.setAdapter(mContantListAdapter);
        return this;
    }

    public SideScollBar setListViewContantList(List<String> listViewContantList) {
        this.mListViewContantList = listViewContantList;
        Log.d(TAG, "contantList length = " + mListViewContantList.size());
        mContantListAdapter = new ContantListAdapter(getContext(), mListViewContantList, Arrays.asList(charactorA_Z), null);
        if (mRelativedListView != null) {
            mRelativedListView.setAdapter(mContantListAdapter);
        }
//        this.mContantListAdapter.notifyDataSetChanged();
        return this;
    }

    public static class ContantListAdapter extends BaseAdapter {

        final private String Tag = "ContantListAdapter";
        private List<PersonInfo> mPersonInfoList;
        private HashMap<String, Integer> firstCharIndexMap;
        private LayoutInflater layoutInflater;
        private HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        private HashMap<Character, Integer> mCharWeightMap;
        private List<String> mIndexCharList;

        private char othersIndicatorChar;

        private int compareWeight(String s1, String s2) {
            char[] cs1 = s1.toCharArray();
            char[] cs2 = s2.toCharArray();

            int i = cs1.length;
            int j = cs2.length;
            int k = Math.min(i, j);

            int minWeight = mPersonInfoList.size();
            for (int z = 0; z < k; z++) {
                int weight1 = mCharWeightMap.containsKey(cs1[z]) ? mCharWeightMap.get(cs1[z]) : minWeight;
                int weight2 = mCharWeightMap.containsKey(cs2[z]) ? mCharWeightMap.get(cs2[z]) : minWeight;
                if (weight1 != weight2) {
                    return weight1 - weight2;
                }
            }
            return i - j;
        }

        public ContantListAdapter(Context context, List<String> contactsList, List<String> indexCharList, String tagForOthers) {

            mIndexCharList = new ArrayList<String>(indexCharList);
            //初始化中文转拼音的格式
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            //作为分割条的首字母除了indexCharList外还可以有一个其他的字符， 默认#号
            othersIndicatorChar = tagForOthers == null || tagForOthers.isEmpty() ? '#' : tagForOthers.charAt(0);
            if (!mIndexCharList.contains(othersIndicatorChar)) {
                mIndexCharList.add(othersIndicatorChar + "");
            }

            mPersonInfoList = new ArrayList<>();
            long sortBeginTime = System.currentTimeMillis();
            for (int i = 0; i < contactsList.size(); i++) {
                String name = contactsList.get(i);
                String py = getPinYin(name).toUpperCase();
                mPersonInfoList.add(new PersonInfo(name, py));
            }

            mCharWeightMap = new HashMap<>();
            for (int i = 0; i < indexCharList.size(); i++) {
                mCharWeightMap.put(indexCharList.get(i).charAt(0), i);
            }

            Collections.sort(mPersonInfoList, new Comparator<PersonInfo>() {
                @Override
                public int compare(PersonInfo s, PersonInfo t1) {
                    return compareWeight(s.pyName, t1.pyName);
                }
            });
            Log.e(Tag, "sort used " + (System.currentTimeMillis() - sortBeginTime) + " ms");

            firstCharIndexMap = new HashMap<>();
            for (String c : indexCharList) {
                firstCharIndexMap.put(c.toUpperCase(), -1);
            }

            long iterateBeginTime = System.currentTimeMillis();
            //遍历排好序的联系人列表，当上一个首字母和当前首字母不一样时，记录当前联系人在列表中的位置，作为首字母分割条出现的位置
            char lastFirstChar = '?';
            for (int i = 0; i < mPersonInfoList.size(); i++) {
                char firstChar = getFirstChar(mPersonInfoList.get(i).pyName);
                if (lastFirstChar != firstChar) {
                    firstCharIndexMap.put(firstChar + "", i);
                }
                lastFirstChar = firstChar;
            }
            Log.e(Tag, "Iterate used " + (System.currentTimeMillis() - iterateBeginTime) + " ms");


            layoutInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return mPersonInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPersonInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = layoutInflater.inflate(R.layout.item_contact_list_person, null);
            //按首字母来定的那条分割条
            TextView charIndicatorBar = (TextView) item.findViewById(R.id.item_tv_char_indicator_bar);

            View personInfo = item.findViewById(R.id.item_layout_person_info);
//            ((TextView) personInfo.findViewById(R.id.item_tv_person_info_name)).setText(mContactsList.get(position));
            ((TextView) personInfo.findViewById(R.id.item_tv_person_info_name)).setText(mPersonInfoList.get(position).cnName);

            //取得字符串的首字母，再根据首字母在firstCharIndexMap中获得该首字母第一次在contactsList中出现的位置
//            char firstChar = getFirstChar(mContactsList.get(position));
            char firstChar = getFirstChar(mPersonInfoList.get(position).pyName);
            int indicatorIndex = firstCharIndexMap.containsKey(firstChar + "") ? firstCharIndexMap.get(firstChar + "") : -1;
            //如果当前位置是该首字母第一次在contactsList中第一次出现，返回带首字母分割条的View
            if (position == indicatorIndex) {
                charIndicatorBar.setText(firstChar + "");
                return item;
            }
            return personInfo;
        }

        /**
         * 将字符串中的中文转成拼音返回
         *
         * @param inputString 原始字符串
         * @return 原串对应的拼音串
         */
        public String getPinYin(String inputString) {

            char[] input = inputString.trim().toCharArray();
            String output = "";

            try {
                //一个一个字转
                for (char curchar : input) {
                    //如果是中文，转成拼音
                    if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);
                        output += temp[0];
                    } else
                        //如果不是中文，直接追加到末尾
                        output += java.lang.Character.toString(curchar);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            return output;
        }

        /**
         * @param str 字符串（可以有中文）
         * @return 第一个字的拼音的首字母
         */
        public char getFirstChar(String str) {
            char firstChar = str.toUpperCase().charAt(0);
            return ('A' <= firstChar && firstChar <= 'Z') ? firstChar : othersIndicatorChar;
        }

        public int getPositionForSelection(String selection) {
            return firstCharIndexMap.containsKey(selection) ? firstCharIndexMap.get(selection) : -1;
        }
    }

    private static class PersonInfo {
        public String cnName;
        public String pyName;

        public PersonInfo(String cnName, String pyName) {
            this.cnName = cnName;
            this.pyName = pyName;
        }
    }
}

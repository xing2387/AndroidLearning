package com.example.xing.androidlearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xing.androidlearning.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by james on 2016/7/7.
 */
public class ContactsListAdapter extends BaseAdapter {

    final private String Tag = "ContantListAdapter";
    private List<String> mContactsList;
    private HashMap<String, Integer> firstCharIndexMap;
    private LayoutInflater layoutInflater;
    private HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

    private char othersIndicatorChar;

    public ContactsListAdapter(Context context, List<String> contactsList, List<String> indexCharList) {

        //初始化中文转拼音的格式
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        this.mContactsList = contactsList;
        //对联系人列表进行排序
        Collections.sort(mContactsList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int lhs_ascii = getPinYin(lhs).toUpperCase().charAt(0);
                int rhs_ascii = getPinYin(rhs).toUpperCase().charAt(0);
                //如果不是字母，放到最后
                if (lhs_ascii < 65 || lhs_ascii > 90)
                    return 1;
                else if (rhs_ascii < 65 || rhs_ascii > 90)
                    return -1;
                else
                    return getPinYin(lhs).compareTo(getPinYin(rhs));
            }
        });

        //作为分割条的首字母除了26个字母外还可以有一个其他的字符， 默认#号
        othersIndicatorChar = indexCharList.size() > 26 ? indexCharList.get(26).charAt(0) : '#';

        firstCharIndexMap = new HashMap<>();
        for (String c : indexCharList) {
            firstCharIndexMap.put(c.toUpperCase(), -1);
        }
        //遍历排好序的联系人列表，当上一个首字母和当前首字母不一样时，记录当前联系人在列表中的位置，作为首字母分割条出现的位置
        char lastFirstChar = '?';
        for (int i = 0; i < mContactsList.size(); i++) {
            char firstChar = getFirstChar(mContactsList.get(i));
            if (lastFirstChar != firstChar) {
                firstCharIndexMap.put(firstChar + "", i);
            }
            lastFirstChar = firstChar;
        }


        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mContactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactsList.get(position);
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
        ((TextView) personInfo.findViewById(R.id.item_tv_person_info_name)).setText(mContactsList.get(position));

        //取得字符串的首字母，再根据首字母在firstCharIndexMap中获得该首字母第一次在contactsList中出现的位置
        char firstChar = getFirstChar(mContactsList.get(position));
        int indicatorIndex = firstCharIndexMap.containsKey(firstChar+"") ? firstCharIndexMap.get(firstChar + "") : -1;
        //如果当前位置是该首字母第一次在contactsList中第一次出现，返回带首字母分割条的View
        if (position == indicatorIndex) {
            charIndicatorBar.setText(firstChar+"");
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
        char firstChar = getPinYin(str).toUpperCase().charAt(0);
        return ('A' <= firstChar && firstChar <= 'Z') ? firstChar : othersIndicatorChar;
    }

    public int getPositionForSelection(String selection) {
        return firstCharIndexMap.containsKey(selection) ? firstCharIndexMap.get(selection) : -1;
    }
}

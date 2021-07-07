package com.example.music.utils;

import android.text.TextUtils;

import com.example.music.bean.BenDiYuePuBean;
import com.example.music.bean.SPListBean;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SPBeanUtile {
    public static void setSPList(ArrayList<BenDiYuePuBean> list) {
        SPListBean spListBean = new SPListBean(list);
        String json = new Gson().toJson(spListBean);
        PreferenceUtil.getInstance().saveString("tupianqupu", json);
    }

    public static ArrayList<BenDiYuePuBean> getSPList() {
        String json = PreferenceUtil.getInstance().getString("tupianqupu", null);
        if (!TextUtils.isEmpty(json)) {
            SPListBean spListBean = new Gson().fromJson(json, SPListBean.class);
            ArrayList<BenDiYuePuBean> list = spListBean.getList();
            if (list != null) {
                return list;
            } else {
                return null;
            }
        }
        return null;
    }
}

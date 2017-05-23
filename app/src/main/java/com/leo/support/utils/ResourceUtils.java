package com.leo.support.utils;

import com.leo.support.app.BaseApp;

/**
 * Created by leo on 2017/5/22.
 */

public class ResourceUtils {

    /**
     * 根据layout名获取ID
     * @param layoutName
     * @return
     */
    public static int getLayoutId(String layoutName) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(layoutName, "layout", BaseApp.getAppContext().getPackageName());
    }

    public static int getStringId(String stringName) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(stringName, "string", BaseApp.getAppContext().getPackageName());
    }

    public static int getDrawableId(String drawableName) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(drawableName, "drawable", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 根据view名获取ID
     * @param viewName
     * @return
     */
    public static int getId(String viewName) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(viewName, "id", BaseApp.getAppContext().getPackageName());
    }

    public static int getColorId(String colorName) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(colorName, "color", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 获取系统内置参数
     * @param internalResName 内置参数名
     * @return
     */
    public static int getInternalDimenSize(String internalResName) {
        int resourceId = BaseApp.getAppContext().getResources()
                .getIdentifier(internalResName, "dimen", "android");
        if (resourceId > 0) {
            return BaseApp.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}

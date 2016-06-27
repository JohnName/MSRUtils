package msr.msrlibrary.application;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by MSR on 2016/6/27.
 */

public class MSRBaseApplication extends Application {
    private List<Activity> activityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 添加Activity到容器中
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        // XmppConnectionManager.getInstance().disconnect();
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }

        }
    }
}

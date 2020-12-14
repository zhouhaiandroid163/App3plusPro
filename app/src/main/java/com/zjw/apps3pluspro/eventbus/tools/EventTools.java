package com.zjw.apps3pluspro.eventbus.tools;

import org.greenrobot.eventbus.EventBus;

public class EventTools {

    public static void SafeRegisterEventBus(Object o) {
        if (!EventBus.getDefault().isRegistered(o))
            EventBus.getDefault().register(o);
    }

    public static void SafeUnregisterEventBus(Object o) {
        if (EventBus.getDefault().isRegistered(o)) {
            EventBus.getDefault().unregister(o);
        }
    }
}
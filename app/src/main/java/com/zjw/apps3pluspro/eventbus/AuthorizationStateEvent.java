package com.zjw.apps3pluspro.eventbus;

/**
 * Created by android
 * on 2020/6/4.
 */
public class AuthorizationStateEvent {
    public String code;

    public AuthorizationStateEvent(String code) {
        this.code = code;
    }
}

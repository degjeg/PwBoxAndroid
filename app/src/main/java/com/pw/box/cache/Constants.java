package com.pw.box.cache;

import com.pw.box.App;

import java.io.File;

/**
 * 常量
 * Created by danger on 16/9/23.
 */

public class Constants {
    public static final int PACK_TIMEOUT = 5000;
    public static final File keyFile = App.context.getFileStreamPath("key.k");

    /* pref keys ☟☟☟ */
    public static final String PREF_KEY_DATA_VERSION = "data-ver";
    public static final String PREF_KEY_HOST_PROXY = "proxyh";
    public static final String PREF_KEY_PORT_PROXY = "proxyp";
    public static final String PREF_KEY_HOST = "host";
    public static final String PREF_KEY_PORT = "port";
    public static final String PREF_KEY_SHOW_WALKTHOUGH = "walkshowed";

    public static final String PREF_KEY_GOT_HOST_TIME = "get_host_time";
    public static final String PREF_KEY_LAN = "set_lan";
    public static final String PREF_KEY_COUNTRY = "set_country";

    public static final String PREF_KEY_PUBKEY = "pub_key_";
    public static final String PREF_KEY_PUBKEY_V = "pub_key_v";
    public static final String PREF_KEY_PUBKEY_E = "pub_key_e";
    public static final String PREF_KEY_DRAG = "drag_tip";
    public static final String PREF_KEY_SUPPORTUS = "support_us_tip";
    /* pref keys ☝︎☝︎☝︎ */

    /* 服务器配置 ☟☟☟︎ */
    public static final int NET_TIMEOUT = 6000;
    /**
     * pwbox.dengjun.tech
     * 192.168.1.100  home
     * 192.168.5.53   office
     * 192.168.5.250  office
     */
    public static String PROXY_HOST = "pwbox.dengjun.tech";
    public static int PROXY_PORT = 1000;
    /* 服务器配置 ☝︎☝︎☝︎ */
}

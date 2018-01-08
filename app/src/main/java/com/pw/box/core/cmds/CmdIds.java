package com.pw.box.core.cmds;

/**
 * 密码箱的服务器命令码
 * Created by danger on 16/8/27.
 */
public interface CmdIds {
    short GET_HOST = 1;
    short PING = 2;
    short GETKEY = 3;
    short LOGIN_ON_OTHER_DEVICE = 4;
    short CHECK_VER = 5;
    short FEEDBACK = 6;
    short GET_PUBLICK_KEY = 10;
    short GET_COM_KEY = 11;

    // 账户系统相关命令
    short REGISTER = 100;
    short LOGIN = 101;
    short MODIFY_PASSWORD = 102;

    short RETRIEVE_PASSWORD = 110;
    short GET_QUESTION = 111;
    short VERIFY_ANSWER = 112;
    short SET_ANSWER = 113;

    // 数据相关
    short ADD_ITEM = 200;
    short DELETE_ITEM = 201;
    short GET_ITEM_LIST = 202;
    short EDIT_ITEM = 203;

    // 分组相关
    short ADD_GP = 210;
    short DELETE_GP = 211;
    short GET_GPS = 212;
    short EDIT_GP = 213;
    short CHANGE_GP_ORDER = 214;
    short GET_GP_ORDER = 215;
}

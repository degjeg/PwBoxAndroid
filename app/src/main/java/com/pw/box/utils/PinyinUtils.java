package com.pw.box.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;

/**
 */
public class PinyinUtils {
    private static final String[][] emptyStringArray = {};


    /**
     * 获得汉语拼音首字母
     *
     * @param chines 汉字
     * @return
     */
    public static String getAlpha(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    public static String[][] getPinYinArray(String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return emptyStringArray;
        }

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();

        ArrayList<String[]> pinYinList = new ArrayList<>();

        for (int i = 0; i < input.length; i++) {

            try {
                /*if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+"))*/
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                if (temp != null && temp.length > 0 &&
                        !(temp[0] == null || temp[0].trim().isEmpty())) {
                    pinYinList.add(temp);
                    continue;
                }
                // output += java.lang.Character.toString(input[i]);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }


            pinYinList.add(new String[]{Character.toString(input[i])});
        }
        return pinYinList.toArray(new String[pinYinList.size()][]);
    }

    private static int matchsCount(char c, String pinYin, String sub, int pos) {
        int count = 0;

        if (c == sub.charAt(pos)) {
            return 100;
        }
        for (int i = 0; i < pinYin.length() && pos < sub.length(); i++, pos++) {
            if (pinYin.charAt(i) == sub.charAt(pos)) {
                count++;
            } else {
                return count;
            }
        }
        return count;
    }

    public static Range<Integer> matchsPinYin(String str, String sub) {
        if (str == null || sub == null) {
            return null;
        }
        if (sub.length() > 16) sub = sub.substring(0, 16);

        sub = sub.toLowerCase();
        str = str.toLowerCase(); // 搜索不区分大不写
        int index = str.indexOf(sub); // TextUtils.indexOf(str, sub);
        if (index != -1) {
            return new Range<>(index, index + sub.length() - 1);
        }

        String[][] pinYinArray = getPinYinArray(str);


        Range<Integer> range = null;

        for (int i = 0; i <= str.length(); i++) {
            Range<Integer> r = matchAt(str, sub, pinYinArray, i);
            if (r != null) {
                return r;
            }
        }

        //        for (int y = 0; y < total; y++) {
        //            range = null;
        //            pos = 0;
        //            for (int i = 0; i < pinYinArray.length && pos < sub.length(); i++) {
        //                String[] pinYin = pinYinArray[i];
        //
        //                index = y / counts[i + 1] % pinYin.length;
        //                Log.e("]]]]]]", String.format("%02d-%02d-%02d", y, i, index));
        //                String onePinyin = pinYin[index];
        //
        //                int matchCount = matchsCount(str.charAt(i), onePinyin, sub, pos);
        //
        //                if (matchCount > 0) {
        //                    if (matchCount == 100) pos++;
        //                    else pos += matchCount;
        //
        //                    if (range == null) {
        //                        range = new Range<>(i, i);
        //                    } else {
        //                        range.setEnd(i);
        //                    }
        //                    if (pos >= sub.length()) {
        //                        return range;
        //                    }
        //                } else {
        //                    if (range != null) {
        //                        i--;
        //                    }
        //                    range = null;
        //                    pos = 0;
        //                }
        //            }
        //        }

        return null;
    }

    private static Range<Integer> matchAt(String str, String sub, String[][] pinYinArray, int start) {

        int[] counts = new int[sub.length() + 1];
        int total = 1;
        Arrays.fill(counts, 1);

        for (int i = Math.min(str.length() - 1, start + sub.length() - 1); i >= start; i--) {
            total *= pinYinArray[i].length;
            counts[i - start] = total;
        }
        // System.out.println("match At " + start + "," + str + " " + sub + " " + Arrays.toString(counts));
        Range<Integer> range;
        int posInStr;
        for (int y = 0; y < total; y++) {
            range = null;
            posInStr = start;

            for (int posInSub = 0; posInSub < sub.length() && posInStr < str.length(); posInSub++) {
                String[] pinYin = pinYinArray[posInStr];
                int index = y / counts[posInStr - start + 1] % pinYin.length;
                String onePinyin = pinYin[index];
                int matchCount = matchsCount(str.charAt(posInStr), onePinyin, sub, posInSub);

                if (matchCount > 0) {
                    if (range == null) {
                        range = new Range<>(posInStr, posInStr);
                    } else {
                        range.setEnd(posInStr);
                    }

                    if (matchCount == 100) {
                        posInSub++;
                    } else {
                        posInSub += matchCount;
                    }
                    posInStr++;
                    if (posInSub >= sub.length()) {
                        return range;
                    }
                    posInSub--;
                } else {
                    break;
                }
            }
        }

        return null;
    }

    /**
     * 将字符串中的中文转化为拼音,英文字符不变
     *
     * @param inputString 汉字
     * @return
     */
    public static String getPingYin(String inputString) {

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);


        String output = "";
        if (inputString != null && inputString.length() > 0
                && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {
                    if (Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        output += temp[0];
                    } else
                        output += Character.toString(input[i]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            return "*";
        }
        return output;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }


    private static void ttt(String t1, String t2) {
        Range<Integer> m;
        m = matchsPinYin(t1, t2);
        if (m != null) {
            System.out.println(t1.substring(m.start));
        }
        System.out.println(t1 + " " + t2 + "," + m);
    }

    public static void main(String[] arg) {
        String ts[] = {
                "欧了不聊了快乐快乐两路口刘来咯来咯来咯来咯来咯来咯刘经理i部落卡卡KTV拒绝邋里邋遢5句垃圾欧了不聊了快乐快乐两路口刘来咯来咯来咯来咯来咯来咯刘经理i部落卡卡KTV拒绝邋里邋遢5句垃圾欧了不聊了快乐快乐两路口刘来咯来咯来咯来咯来咯来咯刘经理i部落卡卡KTV拒绝邋里邋遢5句垃圾欧了不聊了快乐快乐两路口刘来咯来咯来咯来咯来咯来咯刘经理i部落卡卡KTV拒绝邋里邋遢5句垃圾行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行行",
                "扒①bā 扒开 扒拉②pá 扒手 扒草2. 把①bǎ 把握 把持 把柄②bà 印把 刀把 话把儿3. 蚌①bàng 蛤蚌②bèng 蚌埠4. 薄①báo (口语单用) 纸薄②bó (书面组词) 单薄 稀薄5.堡 ①bǔ 碉堡 堡垒②pū 瓦窑堡 吴堡③bǔ 十里堡6.暴 bào 暴露②pù 地暴十寒7.背 ①bèi 脊背 背静②bēi 背包 背枪8.奔 ①bēn 奔跑 奔波②bèn 投奔9. 臂 ①bì 手臂 臂膀②bei 胳臂10.辟 ①bì 复辟②pì 开辟11.扁 ①biǎn 扁担②piān 扁舟12.辟 ①biàn 方便 便利13.骠 ①biāo 黄骠马14.屏 ②bǐng 屏息 屏气15.剥 ①bō (书面组词)剥削(xuē)②bāo (口语单用) 剥皮16.泊 ①bó 淡泊 停泊②bǎi 大伯子(夫兄)17.伯 ①bó 老伯 伯父②bǎi 大伯子(夫兄)18.簸 ①bǒ 颠簸②bò 簸箕19.膊 ①bó 赤膊②bo 胳膊20.卜 ①bo 萝卜②bǔ 9.膊 ①bó 赤膊②bo 胳膊20.卜 ①bo 萝卜②bǔ 占卜Ｃ部1. 藏 ①cáng 矿藏②zàng 宝藏2. 差 ①chā (书面组词)偏差 差错②chà (口语单用)差点儿 ②shā 刹车4. 禅 ①chán 禅师②shàn 禅让 封禅5. 颤 ①chàn 颤动 颤抖②zhàn 颤栗 打颤6. 场 ①chǎng 场合 冷场②cháng 场院 一场(雨)③chaág 排场7. 嘲 ①cháo 嘲讽 嘲笑②zhāo 嘲哳(zhāo zhā)...银行123",
                "扒①bā 扒开 扒拉②pá 扒手 扒草2. 把①bǎ 把握 把持 把柄②bà 印把 刀把 话把儿3. 蚌①bàng 蛤蚌②bèng 蚌埠4. 薄①báo (口语单用) 纸薄②bó (书面组词) 单薄 稀薄5.堡 ①bǔ 碉堡 堡垒②pū 瓦窑堡 吴堡③bǔ 十里堡6.暴 bào 暴露②pù 地暴十寒7.背 ①bèi 脊背 背静②bēi 背包 背枪8.奔 ①bēn 奔跑 奔波②bèn 投奔9. 臂 ①bì 手臂 臂膀②bei 胳臂10.辟 ①bì 复辟②pì 开辟11.扁 ①biǎn 扁担②piān 扁舟12.辟 ①biàn 方便 便利13.骠 ①biāo 黄骠马14.屏 ②bǐng 屏息 屏气15.剥 ①bō (书面组词)剥削(xuē)②bāo (口语单用) 剥皮16.泊 ①bó 淡泊 停泊②bǎi 大伯子(夫兄)17.伯 ①bó 老伯 伯父②bǎi 大伯子(夫兄)18.簸 ①bǒ 颠簸②bò 簸箕19.膊 ①bó 赤膊②bo 胳膊20.卜 ①bo 萝卜②bǔ 9.膊 ①bó 赤膊②bo 胳膊20.卜 ①bo 萝卜②bǔ 占卜Ｃ部1. 藏 ①cáng 矿藏②zàng 宝藏2. 差 ①chā (书面组词)偏差 差错②chà (口语单用)差点儿 ②shā 刹车4. 禅 ①chán 禅师②shàn 禅让 封禅5. 颤 ①chàn 颤动 颤抖②zhàn 颤栗 打颤6. 场 ①chǎng 场合 冷场②cháng 场院 一场(雨)③chaág 排场7. 嘲 ①cháo 嘲讽 嘲笑②zhāo 嘲哳(zhāo zhā)...朝红长少行先新星新行"
        };

        for (String t : ts) {

            ttt(t, "qq");
            ttt(t, "yh1");
            ttt(t, "h12");
            ttt(t, "yih");
            ttt(t, "yinh");
            ttt(t, "yx");
            ttt(t, "x1");
            ttt(t, "yh123");

            ttt(t, "xin");
            ttt(t, "xxx");
            ttt(t, "xinxinxinxin");
            ttt(t, "xinxing");
            ttt(t, "xinhang");


        }
    }
}

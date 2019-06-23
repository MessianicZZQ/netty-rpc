package cn.mldn.util;



import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputUtil {
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));
    private InputUtil () {}

    public static String getString (String prompt) {
        boolean flag = true; // 控制是否获取到键盘输入
        String str = null;
        while (flag) {
            System.out.println(prompt);
            try {
                str = KEYBOARD_INPUT.readLine();
                if (StringUtils.isEmpty(str)) {
                    System.out.println("键盘输入不能为空");
                } else {
                    flag = false;
                }
            } catch (IOException e) {
                System.out.println("键盘输入报错，报错信息：" + e);
            }
        }
        return str;
    }
}

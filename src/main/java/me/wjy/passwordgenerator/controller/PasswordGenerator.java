package me.wjy.passwordgenerator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * 随机密码生成器
 *
 * @author 王金义
 * @date 2021/9/1
 */
@RestController
public class PasswordGenerator {
    /**
     * 相似字符字典
     * 规则参考 http://zhongguosou.com/shopping/password_generate.aspx
     */
    private final HashSet<Character> excludeSet = new HashSet<>(8);

    PasswordGenerator() {
        // 将相似字母字典放入一个 Set 中
        // 60: <, 62: >, 105: i, 108: l, 111: o, 73: I, 48: 0, 49: 1
        Character[] similarChars = {60, 62, 105, 108, 111, 73, 48, 49};
        Collections.addAll(excludeSet, similarChars);
    }

    /**
     * 生成随机密码
     *
     * @param length    长度, 默认为 16
     * @param hasSymbol 是否包含特殊符号, 默认否
     * @return 生成的密码
     */
    @GetMapping("generate")
    public String passwordGenerator(
            @RequestParam(value = "length", defaultValue = "16", required = false) Integer length
            , @RequestParam(value = "hasSymbol", defaultValue = "false", required = false) Boolean hasSymbol) {
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            try {
                char temp = getARandomWord(hasSymbol);
                // 如果生成了相似字符字典中的字符, 则重新生成
                while (excludeSet.contains(temp)) {
                    temp = getARandomWord(hasSymbol);
                }
                password[i] = temp;

            } catch (RuntimeException e) {
                return e.getMessage();
            }
        }
        return String.valueOf(password);
    }

    /**
     * 随机生成一个字符
     *
     * @param hasSymbol 是否生成特殊符号
     * @return 生成的字符
     */
    private char getARandomWord(boolean hasSymbol) {
        Random random = new Random();
        if (hasSymbol) {
            // 标准 ASCII 码表 https://baike.baidu.com/item/ASCII/309296#3
            // 33: !, 126: ~
            // 127 = 126 - 1, random.nextInt(int i) 生成的随机数是 [0,i)
            return (char) (33 + random.nextInt(127 - 33));
        } else {
            /*
                随机生成 0~2 之间的数
                0: 数字, 1: 大写字母, 2: 小写字母
            */
            int i = random.nextInt(3);
            int startPoint;
            int endPoint;
            switch (i) {
                case 0:
                    // 48: 0
                    // 57: 0
                    startPoint = 48;
                    endPoint = 58;
                    break;
                case 1:
                    // 65: A
                    // 90: Z
                    startPoint = 65;
                    endPoint = 91;
                    break;
                case 2:
                    // 97: a
                    // 122: z
                    startPoint = 97;
                    endPoint = 123;
                    break;
                default:
                    // 如果产生了非 0~2 的数则抛出异常
                    throw new RuntimeException("系统随机数错误");
            }
            return (char) (startPoint + random.nextInt(endPoint - startPoint));
        }
    }

}

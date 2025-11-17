package com.example.Initial.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomString {
    private static final Random rnd = new Random();

    /**
     * 랜덤으로 문자열을 생성합니다.
     *
     * @param length 생성할 문자열의 길이입니다.
     * @param types  어떤 종류의 문자열을 생성할지를 결정합니다. 입력하지 않으면 숫자만 생성합니다.
     * @return 랜덤으로 생성된 문자열입니다.
     */
    public String generate(int length, RandomStringType... types) {
        if (types.length == 0) types = new RandomStringType[]{RandomStringType.NUMBER};
        StringBuilder strBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int rIndex = rnd.nextInt(types.length);
            RandomStringType type = types[rIndex];
            switch (type) {
                case LOWERCASE:
                    strBuffer.append((char) ((rnd.nextInt(26)) + 97));
                    break;
                case UPPERCASE:
                    strBuffer.append((char) ((rnd.nextInt(26)) + 65));
                    break;
                case NUMBER:
                    strBuffer.append((rnd.nextInt(10)));
                    break;
                case SPECIAL:
                    strBuffer.append((char) ((rnd.nextInt(5)) + 33));
                    break;
            }
        }
        return strBuffer.toString();
    }

    /**
     * 랜덤으로 문자열을 생성할 때, 자리수를 결정합니다.
     * 기본적으로 2자리로 시작하며, 현재 자리수 내에서 숫자가 50%이상 사용되면 자리수가 증가합니다.
     * 예를들어, itemCount가 50 이상이면 3자리가 되고, itemCount가 500 이상이면 4자리가 됩니다.
     */
    public int pickRandomStringLength(int itemCount) {
        int length = 2;
        while (itemCount > 50) {
            itemCount /= 10;
            length++;
        }
        return length;
    }

    public enum RandomStringType {
        LOWERCASE, UPPERCASE, NUMBER, SPECIAL
    }
}

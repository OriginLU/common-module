package com.zeroone.digtial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {


    public static void main(String[] args) {

        System.out.println(getSecondNumber(new String[]{"12a", "bc", "-123", "12", "0", "a5"}));
    }



    public static String getSecondNumber(String[] numbers){

        List<String> negativeNumber = new ArrayList<>();
        List<String> positiveNumber = new ArrayList<>();
        Arrays.stream(numbers).forEach(number -> {
            char firstChar = number.charAt(0);
            if (!(firstChar == 45 || (firstChar >= 48 && firstChar <= 57))){
                return;
            }
            if (number.length() == 1){
                if (firstChar == 45) {
                    negativeNumber.add(number);
                } else {
                    positiveNumber.add(number);
                }
            }

            int numberCount = 0;
            for (int i = 1; i < number.length(); i++) {
                char c = number.charAt(i);
                if (!(c >= 48 && c <= 57)){
                  return;
                }
                numberCount ++;
            }
            if (numberCount == 0){
                return;
            }

            if (firstChar == 45) {
                negativeNumber.add(number);
            } else {
                positiveNumber.add(number);
            }
        });
        if ((positiveNumber.size() + negativeNumber.size()) < 2){
            return "";
        }

        negativeNumber.sort((number, number1) -> {
            if (number.equals(number1)){
                return 0;
            }
            if (number.length() == number1.length()){
                for (int i = 1; i < number.length(); i++) {
                    char c = number.charAt(i);
                    char c1 = number1.charAt(i);
                    if (c < c1){
                        return -1;
                    }else if (c > c1){
                        return 1;
                    }
                }
            }
            if (number.length() > number1.length()) {
                return -1;
            }else {
                return 1;
            }
        });

        positiveNumber.sort((number, number1) -> {

            if (number.equals(number1)){
                return 0;
            }
            if (number.length() == number1.length()){
                for (int i = 1; i < number.length(); i++) {
                    char c = number.charAt(i);
                    char c1 = number1.charAt(i);
                    if (c < c1){
                        return 1;
                    }else if (c > c1){
                        return -1;
                    }
                }
            }
            if (number.length() > number1.length()) {
                return 1;
            }else {
                return -1;
            }
        });

        negativeNumber.addAll(positiveNumber);

        return negativeNumber.get(1);
    }
}

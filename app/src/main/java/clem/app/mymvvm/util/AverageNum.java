package clem.app.mymvvm.util;

import java.text.DecimalFormat;

public class AverageNum {

    public static void main(String[] args) {
        averageANum(13, 3);
    }

    public static int[] averageANum(double num, int groupCount){

        DecimalFormat df = new DecimalFormat("#.0");
        String average_str = df.format(num/groupCount);
        String average_str_oneDecimal = average_str.substring(0, average_str.indexOf('.') + 2);
        int[] two_parts = splitADoubleNumByDot(Double.parseDouble(average_str_oneDecimal));
        int inteter_part = two_parts[0];
        int decimal_part = two_parts[1];
        if(decimal_part > 5){
            inteter_part++;
        }

        int[] arr = new int[groupCount];
        for(int i = 0; i < groupCount - 1; i++){
            arr[i] = inteter_part;
        }
        arr[groupCount - 1] = (int) (num - inteter_part*(groupCount - 1));

        return arr;
//        for (int value : arr) {
//            System.out.println(value);
//        }

    }

    public static int[] splitADoubleNumByDot(double num){
        String str = Double.toString(num);
        String[] two_parts = str.split("\\.");
        int part1 = Integer.parseInt(two_parts[0]);
        int part2 = Integer.parseInt(two_parts[1]);
        return new int[]{part1, part2};
    }
}

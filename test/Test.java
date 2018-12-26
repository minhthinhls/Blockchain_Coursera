/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Minh Thinh
 */
public class Test {

    public static void main(String[] args) {
        int[][] arr = new int[3][3];//3 row and 3 column  
        arr[0][0] = 1;
        arr[0][1] = 2;
        arr[0][2] = 3;
        arr[1][0] = 4;
        arr[1][1] = 5;
        arr[1][2] = 6;
        arr[2][0] = 7;
        arr[2][1] = 8;
        arr[2][2] = 9;

        boolean[] a = new boolean[2];
        if(!a[0]){
            System.out.print(!a[0]);
        }
        System.out.print(arr[1][0] + "\n");
    }
}

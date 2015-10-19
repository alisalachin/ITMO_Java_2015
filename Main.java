package com.company;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        //   System.out.println(args[0].toString()); //toys
        //   System.out.println(args[1].toString()); //colors

        Map<String, Double> ToysPrice = new HashMap<String, Double>();
        Set<String> ToysInFile;
        Map<String, Integer> ToysCount = new HashMap<String, Integer>();
        Map<String, Integer> Colors = new HashMap<String, Integer>();
        Map<String, Double> ToysWithColors = new HashMap<String, Double>();
        File[] fList;
        File files = new File("C://Users/iao/IdeaProjects/lab1/src/com/company/" + args[0].toString());
        fList = files.listFiles();
        File file2 = new File("C://Users/iao/IdeaProjects/lab1/src/com/company/" + args[1].toString() + "/color.txt");
        File file3 = new File("C://Users/iao/IdeaProjects/lab1/src/com/company/result.txt");


        if (!files.exists()) {
            System.out.println("Files don't exists!");
        } else {
            try {
                Scanner sc;
                Integer price;
                if (fList==null) {
                    System.out.println("Empty");
                    return;
                }
                for (int i = 0; i < fList.length; i++) {
                    ToysInFile = new HashSet<String>();
                    sc = new Scanner(fList[i]);
                    while (sc.hasNext()) {
                        ArrayList<String> line = new ArrayList<String>();
                        for (String s : sc.nextLine().split(" ")) {
                            line.add(s);
                        }
                        if (line.size() == 2) {
                            try {
                                price = new Integer(line.get(1));
                            } catch (NumberFormatException e) {
                                continue;
                            }
                            String toyName=line.get(0);
                            if (!ToysInFile.contains(toyName)) {
                                ToysInFile.add(toyName);
                                if (ToysPrice.containsKey(toyName)) {
                                    ToysCount.put(toyName, ToysCount.get(toyName) + 1);
                                    ToysPrice.put(toyName, ToysPrice.get(toyName) + price.doubleValue());
                                } else {
                                    ToysCount.put(toyName, 1);
                                    ToysPrice.put(toyName, price.doubleValue());
                                }
                            }
                        }
                    }
                    sc.close();
                }
                if (ToysPrice.size()==0) {
                    System.out.println("Empty");
                    return;
                }
                for (Map.Entry<String, Double> entry : ToysPrice.entrySet()) {
                    entry.setValue(entry.getValue() / ToysCount.get(entry.getKey()));
                }

                if (!file2.exists()) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMinimumFractionDigits(0);
                    PrintWriter fout = new PrintWriter(file3);
                    for (Map.Entry entry : ToysPrice.entrySet()) {
                        fout.println(entry.getKey() + " "
                                + nf.format(entry.getValue()));
                    }
                    fout.close();
                } else {

                    Scanner sc2 = new Scanner(file2);
                    Integer num;
                    while (sc2.hasNext()) {
                        ArrayList<String> line = new ArrayList<String>();
                        for (String s : sc2.nextLine().split(" ")) {
                            line.add(s);
                        }
                        if (line.size() == 2) {
                            try {
                                price = new Integer(line.get(1));
                            } catch (NumberFormatException e) {
                                continue;
                            }
                            Colors.put(line.get(0), price);
                        }
                    }
                    sc2.close();

                    for (Map.Entry<String, Double> entry : ToysPrice.entrySet()) {
                        for (Map.Entry<String, Integer> entry2 : Colors.entrySet()) {
                            ToysWithColors.put(entry.getKey() + "_" + entry2.getKey(), entry.getValue() + (double) entry2.getValue());
                        }
                    }
                    if (ToysWithColors.size() == 0) {
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(0);
                        PrintWriter fout = new PrintWriter(file3);
                        for (Map.Entry entry : ToysPrice.entrySet()) {
                            fout.println(entry.getKey() + " "
                                    + nf.format(entry.getValue()));
                        }
                        fout.close();

                    } else {
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(0);
                        PrintWriter fout = new PrintWriter(file3);
                        for (Map.Entry entry : ToysWithColors.entrySet()) {
                            fout.println(entry.getKey() + " "
                                    + nf.format(entry.getValue()));
                        }
                        fout.close();
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}

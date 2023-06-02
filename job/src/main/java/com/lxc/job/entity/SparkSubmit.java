package com.lxc.job.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SparkSubmit {

    public void submit(Integer userId){

        Process proc;
        try {
            String commandText = String.format("'spark-submit --class com.lxc.TextClassification --master local[2] file://G/CCZU/bishe/dd/Text.jar \\\"%d\\\"'", userId);

            String[] args = new String[] { "python", "G:/CCZU/bishe/dd/jar.py", commandText};
            proc=Runtime.getRuntime().exec(args);
            proc.waitFor();
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void submit(){

        Process proc;
        try {
            String commandAls = "'spark-submit --class com.lxc.ALSSpark --master local[2] file://G/CCZU/bishe/dd/ALSmodel.jar";
            String[] args = new String[] { "python", "G:/CCZU/bishe/dd/jar.py", commandAls};

            proc=Runtime.getRuntime().exec(args);
            proc.waitFor();
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

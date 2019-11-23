package org.seckill.util.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成项目结构的工具类
 *
 * @author heng
 * @date 2018/08/20
 *
 */
public class GenerateProjectTreeUtil {
    private static List<String> regExpList = new ArrayList<>();
    private static List<String> regDirExpList = new ArrayList<>();

    public static void main(String[] args) {
        File file = new File("项目根目录");
        // 需要忽略的文件类型
        regExpList.add(".*iml");
        regExpList.add(".*jar");
        regExpList.add(".*class");
        regExpList.add(".*gitignore");
        regExpList.add(".project");
        regExpList.add(".classpath");

        // 需要忽略的目录
        regDirExpList.add("target");
        regDirExpList.add(".git");
        regDirExpList.add(".gitignore");
        regDirExpList.add(".idea");
        regDirExpList.add("layui");
        regDirExpList.add(".settings");
        print(file, file.isDirectory(), 0);
    }

    /**
     * 递归方式输出项目结构
     *
     * @param file   当前目录或文件
     * @param isDir  是否是目录
     * @param deepth 当前文件或目录的深度，根目录定义为0
     */
    public static void print(File file, boolean isDir, int deepth) {
        String name = file.getName();
        if (isDir) {
            for (int i = 0; i < regDirExpList.size(); i++) {
                if (name.matches(regDirExpList.get(i))) {
                    break;
                } else {
                    if (i == regDirExpList.size() - 1) {
                        printByDeepth(deepth);
                        System.out.println(name);
                        for (File f : file.listFiles()) {
                            print(f, f.isDirectory(), deepth + 1);
                        }
                    }
                    continue;
                }
            }
        } else {
            for (int i = 0; i < regExpList.size(); i++) {
                if (name.matches(regExpList.get(i))) {
                    break;
                } else {
                    if (i == regExpList.size() - 1) {
                        printByDeepth(deepth);
                        System.out.println(name);
                    }
                    continue;
                }
            }
        }
    }

    /**
     * 根据文件目录深度动态打印前置字符
     *
     * @param deepth
     */
    private static void printByDeepth(int deepth) {
        for (int j = 0; j < deepth; j++) {
            if (j == deepth - 1) {
                System.out.print("|--");
            } else {
                System.out.print("|   ");
            }
        }
    }
}

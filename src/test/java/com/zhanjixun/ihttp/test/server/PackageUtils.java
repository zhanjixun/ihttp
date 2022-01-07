package com.zhanjixun.ihttp.test.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhanjixun
 * @date 2021-04-14 17:13:48
 */
public class PackageUtils {

    public static List<Class<?>> listType(String basePackage) {
        try {
            URL resource = PackageUtils.class.getClassLoader().getResource(basePackage.replace(".", "/"));
            if ("file".equals(resource.getProtocol())) {
                File rootFile = new File(PackageUtils.class.getResource("/").toURI());
                List<Class<?>> list = new ArrayList<>();
                for (File file : listFile(new File(resource.toURI()))) {
                    list.add(Class.forName(file.getAbsolutePath().substring(rootFile.getAbsolutePath().length() + 1)
                            .replace(File.separator, ".")
                            .replace(".class", "")));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<File> listFile(File baseFile) {
        List<File> result = new ArrayList<>();
        LinkedList<File> dir = new LinkedList<>();
        dir.add(baseFile);
        while (!dir.isEmpty()) {
            for (File file : dir.removeFirst().listFiles()) {
                if (file.isDirectory()) {
                    dir.add(file);
                }
                if (file.isFile() && file.getName().endsWith(".class") && !file.getName().contains("$")) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}



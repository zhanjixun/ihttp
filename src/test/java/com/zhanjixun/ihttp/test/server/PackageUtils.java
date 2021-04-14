package com.zhanjixun.ihttp.test.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
        List<File> list = new ArrayList<>();
        for (File file : baseFile.listFiles()) {
            if (file.isDirectory()) {
                list.addAll(listFile(file));
            }
            if (file.isFile() && file.getName().endsWith(".class") && !file.getName().contains("$")) {
                list.add(file);
            }
        }
        return list;
    }
}



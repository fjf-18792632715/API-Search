package org.example.utils;

import org.example.model.DocInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 步骤一：从本地的api目录，遍历静态html文件
 * 每一个html文件需要构建一个正文索引（docinfo）本地某个文件
 *
 * 正文索引信息List<DocInfo>
 * DocInfo（id，title，content，url）
 */
public class Parser {

    // api目录
    public static final String API_PATH = "E:\\IDEA2020\\Java-Search\\jdk-8u261-docs-all\\docs\\api";
    // 构建的本地文件的正拍索引
    public static final String RAW_DATA = "E:\\IDEA2020\\Java-Search\\jdk-8u261-docs-all\\docs\\raw_data.txt";
    // 官方api文档的根路径
    public static final String API_BASE_PATH = "https://docs.oracle.com/javase/8/docs/api";

    public static void main(String[] args) throws IOException {
        // api本地路径下所有的html文件找到
        List<File> htmls = listHtml(new File(API_PATH));
        List<DocInfo> list = new ArrayList<>();
        // 输出流
        FileWriter fw = new FileWriter(RAW_DATA);
        BufferedWriter bw = new BufferedWriter(fw);

        for (File html : htmls) {
            DocInfo doc = parseHtml(html);

            // 输出格式为：title + '\3' + url + '\3' + content
            bw.append(doc.getTitle()).append(String.valueOf('\3'));
            bw.append(doc.getUrl()).append(String.valueOf('\3'));
            bw.append(doc.getContent()).append("\n");

            bw.flush();
        }
        bw.close();
        fw.close();

    }

    // 将html文件转化为DocInfo对象
    private static DocInfo parseHtml(File html) {
        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(html.getName().substring(0, html.getName().length() - 5));
        docInfo.setUrl(API_BASE_PATH + html.getAbsolutePath().substring(API_PATH.length()));
        docInfo.setContent(parseContent(html));
        return docInfo;
    }
    // 将html文件的内容部分提取
    public static String parseContent(File html) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(html);
            boolean isContext = false;
            int i;
            while ((i = fr.read()) != -1) {
                char c = (char) i;
                if (isContext) {
                    if (c == '<') {
                        isContext = false;
                    } else if (c == '\n' || c == '\r') {
                        sb.append(" ");
                    } else {
                        sb.append(c);
                    }
                } else if (c == '>'){
                    isContext = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString().trim();
    }

    // 递归遍历子文件
    private static List<File> listHtml(File dir) {
        File[] children = dir.listFiles();
        List<File> list = new ArrayList<>();

        for (File child : children) {
            if (child.isDirectory()) { // 子文件夹：递归调用文件夹内的html文件
                list.addAll(listHtml(child));
            } else if (child.getName().endsWith(".html")){ // html文件
                list.add(child);
            }
        }

        return list;
    }

}

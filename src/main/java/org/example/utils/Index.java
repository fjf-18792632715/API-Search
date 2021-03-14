package org.example.utils;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.example.model.DocInfo;
import org.example.model.Weight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.*;

/**
 * 构建索引：
 *  正排索引：从本地文件数据中读取到java内存（蕾西数据库保存的数据）
 *  倒排索引：构建Map<String, List<信息>>（类似数据库的hash索引）
 *  Map键：关键词（Ansj分词来做）
 *  Map值-信息：
 *      （1）DocInfo对象引用或是DocInfo的id
 *      （2）权重（标题对应的关键词数量*10+正文对应的关键词数量*1）---自己决定权重
 *      （3）关键词（是否需要，待定）
 */

public class Index {

    // 正排索引
    public static final List<DocInfo> FORWARD_INDEX = new ArrayList<>();
    // 倒排索引
    public static final Map<String, List<Weight>> INVERTED_INDEX = new HashMap<>();

    // 构建正排索引
    public static void buildForwardIndex(){
        try {
            FileReader fr = new FileReader(Parser.RAW_DATA);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            int id = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) continue;
                String[] parts = line.split("\3");
                DocInfo docInfo = new DocInfo();
                docInfo.setId(++id);
                docInfo.setTitle(parts[0]);
                docInfo.setUrl(parts[1]);
                docInfo.setContent(parts[2]);
                FORWARD_INDEX.add(docInfo);
//                if (id == 5) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // 直接抛出异常，再构建阶段就发现异常
        }
    }

    // 构建倒排索引: 从 java内从中的正排索引获取文档信息来构造
    public static void buildInvertedIndex(){
        Map<String, Weight> temp = new HashMap<>();
        for (DocInfo docInfo : FORWARD_INDEX) {
            // 一个doc，分别对标题和正文分词，每一个分词生成一个weigh对象，需要计算权重

            // 计算title权重
            String title = docInfo.getTitle();
            List<Term> terms = ToAnalysis.parse(title).getTerms(); // 获取title分词结果
            for (Term term : terms) {
                if (temp.containsKey(term.getName())) {
                    temp.get(term.getName()).setWeight(temp.get(term.getName()).getWeight() + 10);
                } else {
                    Weight we = new Weight();
                    we.setWeight(10);
                    we.setDocInfo(docInfo);
                    we.setKeyword(term.getName());
                    temp.put(term.getName(), we);
                }
            }

            // 计算context权重
            String content = docInfo.getContent();
            terms = ToAnalysis.parse(content).getTerms(); // 获取title分词结果
            for (Term term : terms){
                if (temp.containsKey(term.getName())) {
                    temp.get(term.getName()).setWeight(temp.get(term.getName()).getWeight() + 1);
                } else {
                    Weight we = new Weight();
                    we.setWeight(1);
                    we.setDocInfo(docInfo);
                    we.setKeyword(term.getName());
                    temp.put(term.getName(), we);
                }
            }

            // 将该docInfo的关键词注入到总的关键词map
            for (String key : temp.keySet()) {
                if (INVERTED_INDEX.containsKey(key)) {
                    INVERTED_INDEX.get(key).add(temp.get(key));
                } else {
                    List<Weight> list = new ArrayList<>();
                    list.add(temp.get(key));
                    INVERTED_INDEX.put(key, list);
                }
            }

            // 注入完成后，释放temp内容
            temp.clear();

        }
    }
    public static void main(String[] args) {
//        System.out.println(FORWARD_INDEX);
        Index.buildForwardIndex();
////        System.out.println("==========================================");
//        for (DocInfo forwardIndex : FORWARD_INDEX) {
//            System.out.println(" id = " + forwardIndex.getId() + " url = " + forwardIndex.getUrl());
//        }

        Index.buildInvertedIndex();
        int index = 0;
        for (String key : INVERTED_INDEX.keySet()) {
            System.out.print("key = " + key + " 、 Weight = ");
            for (Weight we : INVERTED_INDEX.get(key)) {
                System.out.print("   weight = " + we.getWeight() + " id = " + we.getDocInfo().getId());
            }
            System.out.println();
        }
    }
}

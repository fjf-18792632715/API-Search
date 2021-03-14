package org.example.model;

/**
 * 倒排索引Map<String, List<Weight>>中，关键词对应的信息
 */
public class Weight {

    private DocInfo docInfo; // 文档
    private String keyword; // 关键词
    private int weight; // 权重

    @Override
    public String toString() {
        return "Weight{" +
                "docInfo=" + docInfo +
                ", keyword='" + keyword + '\'' +
                ", weight=" + weight +
                '}';
    }

    public DocInfo getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(DocInfo docInfo) {
        this.docInfo = docInfo;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Weight() {
    }

    public Weight(DocInfo docInfo, String keyword, int weight) {
        this.docInfo = docInfo;
        this.keyword = keyword;
        this.weight = weight;
    }
}

package org.example.model;

/**
 * 每一个html文件对应一个文档对象
 */
public class DocInfo {
    private Integer id; // 类似数据库的id主键，识别不同的文档
    private String title; // 标题：每个文档的文件名
    private String content; // 文档正文：将html的内容部分取出
    private String  url; // Oracle 官网上的api文档下的html的url

    public DocInfo() {
    }

    public DocInfo(Integer id, String title, String content, String url) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
    }

    @Override
    public String toString() {
        return "DocInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

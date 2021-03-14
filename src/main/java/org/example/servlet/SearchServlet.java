package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.example.model.Result;
import org.example.model.Weight;
import org.example.utils.Index;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// 根据前端请求路径定义后端服务路径
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进入SearchServlet - doGet");
        resp.setContentType("application/json");// ajax请求，响应json格式
        // 构造返回给前端的内容，使用对象，在使用jackson序列化为json字符串
        Map<String, Object> map = new HashMap<>();
        // 解析请求的数据
        String query = req.getParameter("query");
        // 每个文档转换为一个Result（可能出现多个分词对应一个文档，需要进行文档合并）
        Map<Integer, Result> resultMap = new HashMap<>();
        // 初始权重为100，随着排序依次减半
        int w = 100;
        try {
            // 根据搜索内容处理搜索业务
            //根据搜索内容处理搜索业务
            if(query==null||query.trim().length()==0){
                map.put("ok",false);
                map.put("msg","搜索内容为空");
            }else{
                // 1. 根据搜索内容，进行分词，遍历每个分词
                List<Term> terms = ToAnalysis.parse(query).getTerms();
                for (Term term : terms) {
                    String word = term.getName(); // 单个分词
                    List<Weight> weightList = Index.INVERTED_INDEX.get(word); // 获取到单个分词的 Weight 集合
                    // 2. 每个分词，在倒牌中查找对应的文档（一个分词对一个多个文档）
                    for (Weight weight : weightList) {
                        final Integer id = weight.getDocInfo().getId();
                        // 文档合并
                        if (resultMap.containsKey(id)) {
                            // 存在id
                            resultMap.get(id).setWeight(resultMap.get(id).getWeight() + w);
                        } else {
                            // 不存在id
                            final Result temp = new Result(id, weight.getWeight(),
                                                            weight.getDocInfo().getTitle(),
                                                            weight.getDocInfo().getUrl(),
                                                            weight.getDocInfo().getContent().substring(0, 100) + "......");
                            resultMap.put(id, temp);
                        }
                    }
                    /**
                     * Weight:
                     *     private DocInfo docInfo; // 文档
                     *     private String keyword; // 关键词
                     *     private int weight; // 权重
                     *
                     * Result:
                     *     private Integer id; // 作为键
                     *     private int weight;
                     *     private String title;
                     *     private String url;
                     *     private String desc;
                     */

                    // 每过一个关键字，w减半
                    w >>= 1;
                }
                // 4. 文档合并后，根据权重对List<Result>进行排序
                List<Result> resultList = new ArrayList<>(resultMap.values());
                resultList.sort((o1, o2) -> o2.getWeight() - o1.getWeight());

                map.put("data", resultList);
                // 如果成功
                map.put("ok", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 失败
            map.put("ok", false);
        }

        // 获取输出流
        PrintWriter pw = resp.getWriter();
        // 将map序列化为json对象，然后通过ajax传递给前端
        pw.print(new ObjectMapper().writeValueAsString(map));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

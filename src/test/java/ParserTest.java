import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.example.utils.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.TreeMap;

public class ParserTest {

    @Test
    public void testParseContext(){
        File html = new File("E:\\IDEA2020\\Java-Search\\jdk-8u261-docs-all\\docs\\api\\index.html");
        String content = Parser.parseContent(html);
        System.out.println(content);
    }

    public void test(){
        File html = new File("E:\\IDEA2020\\Java-Search\\jdk-8u261-docs-all\\docs\\api\\index.html");

    }

    @Test
    public void testAnalysis(){
        String string = "小明毕业于清华大学计算机专业，擅长使用计算机控制挖掘机专业的炒菜";
        Result parse = ToAnalysis.parse(string);
//        System.out.println(parse.toString());
        List<Term> terms = parse.getTerms();
        System.out.println(terms);
        System.out.println("================");
        for (Term term : terms) {
            System.out.print(term.getName() + " / ");
        }
    }

}

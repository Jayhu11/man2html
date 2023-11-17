import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefUtil {


    private final Pattern localRefRe1 = Pattern.compile("(?<=<i>)([A-Za-z0-9-]*?)</i>\\((\\d+)\\)");
    private final Pattern localRefRe2 = Pattern.compile("(?<=<b>)([A-Za-z0-9-]*?)</b>\\((\\d+)\\)");
    private final Pattern localRefRe3 = Pattern.compile("([A-Za-z0-9-]*?)\\((\\d+)\\)");

    private final Pattern globalRefRe = Pattern.compile("(https?://|ftp://|file:///)([A-Z0-9-~]+\\.?/?)+", Pattern.CASE_INSENSITIVE);
    private final Pattern mailtoRefRe = Pattern.compile("([a-z0-9_.-]+)@([\\da-z.-]+)\\.([a-z.]{2,6})", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        RefUtil processor = new RefUtil();
        String testLine = "ttt(2)Some text with <i>localRef1</i>(123) and <b>localRef2</b>(456) and an email test@example.com";
        System.out.println(processor.localRefSelection(testLine));
        System.out.println(processor.globalRefSelection(testLine));

        String inputFilePath = "path/to/your/input/file.txt";
        String outputFilePath = "path/to/your/output/file.txt";

        try {
            processor.processDocument(inputFilePath, outputFilePath);
            System.out.println("Document processing completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replaceWithPattern(String input, Pattern pattern, String replacement) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // 正则替换
    public String localRefSelection(String line) {
//      line = replaceWithPattern(line, localRefRe1, "<a href=\"$1#$2\">$1</a></i>($2)");
//      line = replaceWithPattern(line, localRefRe2, "<a href=\"$1#$2\">$1</a></b>($2)");
//      转换成a标签，$1表示M，即链接到的说明文档的标题，$2表示章节。 TODO 改写成前端更好取值的方式
        String finalRef = "<a href=\"$1#$2\">$1</a>($2)";
        // TODO 转换成后端接口调用
        String finalRef2 = "<a href=\"https://www.runoob.com/getPage?M=$1&X=$2\">$1</a>($2)";
        return replaceWithPattern(line, localRefRe3, finalRef);
    }

    public String globalRefSelection(String line) {
        line = replaceWithPattern(line, globalRefRe, "<a href=\"$0\">$0</a>");
        return replaceWithPattern(line, mailtoRefRe, "<a href=\"mailto:$0\">$0</a>");
    }

    // 直接从文件读取。
    public void processDocument(String inputFilePath, String outputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = processLine(line);
                writer.write(processedLine);
                writer.newLine();
            }
        }
    }

    //TODO 根据传入的POJO进行替换。
    //TODO 多线程？

    private String processLine(String line) {
        return localRefSelection(line);
    }
}


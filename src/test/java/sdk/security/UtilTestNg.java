/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * @author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package sdk.security;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.comm.table.SmsReceive;
import com.github.jspxnet.comm.utils.FormatParsing;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.io.ReadPdfTextFile;
import com.github.jspxnet.io.ReadWordTextFile;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.route.RouteSession;
import com.github.jspxnet.network.util.PacketUtil;
import com.github.jspxnet.scriptmark.util.ScriptConverter;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.security.symmetry.SymmetryEncryptFactory;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.upload.multipart.DateRandomNamePolicy;
import com.github.jspxnet.upload.multipart.DefaultFileRenamePolicy;
import com.github.jspxnet.upload.multipart.FileRenamePolicy;
import com.github.jspxnet.upload.multipart.JspxNetFileRenamePolicy;
import com.github.jspxnet.util.QRCodeUtil;
import com.github.jspxnet.util.StringList;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.util.XMLFormat;
import com.github.jspxnet.utils.*;
import org.apache.poi.hwpf.converter.WordToTextConverter;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class UtilTestNg {
    @BeforeClass
    public static void init() {
        System.out.println("------------开始测试");
    }

    @Test
    public static void stringMap() {
        String str = "ccc=gfd\r\nbbb=gfdsldkfg";
        StringMap smp = new StringMap(str);
        Assert.assertEquals(smp.get("ccc"), "gfd");
        Assert.assertEquals(smp.get("bbb"), "gfdsldkfg");
    }


    @Test
    public static void securityStringMap() throws Exception {

        String file = "d:/temp.txt";
        String str = "ccc=gfd\r\nbbb=gfdsldkfg\r\ncccx=中文达萨里房价";
        StringMap smp = new StringMap(str);
        smp.setSecurity(true);
        System.out.println("-------------------smp=\r\n" + smp.toString());
        boolean save = smp.save(file);
        System.out.println("-------------------save=" + save);

        Assert.assertEquals(smp.get("ccc"), "gfd");
        Assert.assertEquals(smp.get("bbb"), "gfdsldkfg");
    }

    @Test
    public static void getNumber() throws Exception {
        String actionName = "张三 13599998888 000000";
        ;
        long str = StringUtil.toLong(Pattern.compile("[^0-9]").matcher(actionName).replaceAll(""));
        Assert.assertEquals(13599998888000000L, str);
        System.out.println("-------str=" + str);
        String actionName2 = "r-13599998888 000000";
        ;
        long str2 = StringUtil.toLong(Pattern.compile("[^0-9]").matcher(actionName2).replaceAll(""));
        Assert.assertEquals(13599998888000000L, str2);

        System.out.println("-------str=" + str);
    }

    @Test
    public static void interiorly() throws Exception {
        String ips = "127.0.0.1;192.168.0.*";
        System.out.println("-------str=" + IpUtil.interiorly(ips, "127.0.0.1"));
    }


    @Test
    public static void ClassMethodReturnType() {
        String aaa = "app_version_fld";
        System.out.println(StringUtil.underlineToCamel(aaa));
        Assert.assertEquals(StringUtil.camelToUnderline(StringUtil.underlineToCamel(aaa)), aaa);
    }


    @Test
    public static void StringMapTest() throws Exception {


        String str = "ccc:gfd\r\nbbb:gfdsldkfg\r\ncccx=中文达萨里房价";
        StringMap<String, String> smp = new StringMap(str);
        smp.setString(str);
        Assert.assertEquals(smp.get("ccc"), "gfd");
        Assert.assertEquals(smp.get("bbb"), "gfdsldkfg");
    }


    @Test
    public static void stringToMap() {
        String str = "ccc=gfd\r\nbbb=gfdsldkfg\r\n";
        Map map = StringUtil.toMap(str, "=", "\r\n");
        Assert.assertEquals(map.containsKey("ccc"), true);
        Assert.assertEquals(map.containsKey("bbb"), true);
    }

    @Test
    public static void deleteNotes() {
        String xx = "1<!--A-->2<!--\r\n3\r\n//-->4<!--<5>//-->6<!--\r\nB\t-->7<!-- <C -->8<!--[9]//-->10";
        String end = "12<!--\r\n3\r\n//-->4<!--<5>//-->678<!--[9]//-->10";
        Assert.assertEquals(HtmlUtil.deleteNotes(xx), end);
    }

    @Test
    public static void zipAndUn() throws IOException {
        String xx = "中文，中国[^\\\\?#]*)?(\\\\?[^#]*)?(#.*)?$";
        byte[] bb = ZipUtil.zip(xx.getBytes("UTF-8"));
        Assert.assertEquals(xx, new String(ZipUtil.unZip(bb), "UTF-8"));
    }


    @Test
    public static void testZip() throws Exception {
        String text = "";
        byte[] bytes = ZipUtil.zip(text.getBytes("UTF-8"));
        String text2 = new String(ZipUtil.unZip(bytes), "UTF-8");
        Assert.assertEquals(text, text2);
        text = "解决了基于TCP";
        bytes = ZipUtil.zip(text.getBytes("UTF-8"));
        text2 = new String(ZipUtil.unZip(bytes), "UTF-8");
        Assert.assertEquals(text, text2);

    }


    @Test
    public static void testSHA256() throws Exception {
        Assert.assertEquals(EncryptUtil.getSha256("123456"), "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
    }

    @Test
    public static void spPacketFile() throws Exception {
        Assert.assertEquals(EncryptUtil.getMd5("luming123"), "35a4e5b9da212c65a8161d07e3b8757e");
    }

    @Test
    public static void escape() throws Exception {
        String temp = StringUtil.escape("机场");
        Assert.assertEquals("机场", StringUtil.unescape(temp));
    }

    @Test
    public static void subtract() throws Exception {
        String fullCert = "A,B,c";
        String haveCert = "B,c";
        String lackCert = ArrayUtil.toString(ArrayUtil.subtract(StringUtil.split(fullCert, ","), StringUtil.split(haveCert, ",")), ",");
        Assert.assertEquals("A", lackCert);
    }

    @Test
    public static void getAttachCaption() throws Exception {

        String wh = "XNRW20120295\n" +
                "29=中国民用航空局发现问题汇总单（XNRW20120295）.pdf\n";

        Assert.assertEquals(StringUtil.getAttachCaption(wh), "XNRW20120295");

        wh = "黔监发 2012 177\n" +
                "368=黔监发明电[2012]177号：关于兴义机场安全审计整改跟踪检查情况的报告.pdf\n";
        Assert.assertEquals(StringUtil.getAttachCaption(wh), "黔监发 2012 177");

        wh = "368=黔监发明电[2012]177号：关于兴义机场安全审计整改跟踪检查情况的报告.pdf\r\n黔监发 2012 177\n";
        Assert.assertEquals(StringUtil.getAttachCaption(wh), "黔监发 2012 177");

    }


    @Test
    public static void deleteAtt() throws Exception {

        String html = "xxxxe<td class='xl181' bgcolor=\"#D9D9D9\" align=center valign=middle width=168 style='width:126pt;font-size:11.0pt;color:#002060;font-weight:700;text-decoration:'>文号</td>休闲";
        String str = HtmlUtil.deleteAttribute(html);
        Assert.assertEquals(str, "xxxxe<td>文号</td>休闲");
    }


    @Test
    public static void arrayExpression() throws Exception {
        int[] array = new int[]{2, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43, 50};
        Assert.assertEquals(ArrayUtil.getArrayExpression(array), "2-7;9-10;20;30;40-43;50");
        array = new int[]{2, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43};
        Assert.assertEquals(ArrayUtil.getArrayExpression(array), "2-7;9-10;20;30;40-43");

        array = new int[]{1, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43};
        Assert.assertEquals(ArrayUtil.getArrayExpression(array), "1;3-7;9-10;20;30;40-43");

        array = new int[]{1, 3, 5, 40, 41, 42, 43};
        Assert.assertEquals(ArrayUtil.getArrayExpression(array), "1;3;5;40-43");

        array = new int[]{};
        Assert.assertEquals(ArrayUtil.getArrayExpression(array), "");
    }

    @Test
    public static void expressionArray() throws Exception {
        int[] array = new int[]{2, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43, 50};
        Assert.assertEquals(StringUtil.expressionArray("2-7;9-10;20;30;40-43;50"), array);

        array = new int[]{2, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43};
        Assert.assertEquals(StringUtil.expressionArray("2-7;9-10;20;30;40-43"), array);

        array = new int[]{1, 3, 4, 5, 6, 7, 9, 10, 20, 30, 40, 41, 42, 43};
        Assert.assertEquals(StringUtil.expressionArray("1;3-7;9-10;20;30;40-43"), array);

        array = new int[]{1, 3, 5, 40, 41, 42, 43};
        Assert.assertEquals(StringUtil.expressionArray("1;3;5;40-43"), array);

    }

    @Test
    public static void testMarkdown() throws Exception {
        String text = "## 欢迎使用 Markdown for java in jspx.net##\n" +
                "\n" +
                "**Markdown** 是 java 平台上一个功能完善的 Markdown 编辑器。\n" +
                "\n" +
                "### 专为 Markdown 打造 ###\n" +
                "\n" +
                "提供了语法高亮和方便的快捷键功能，给您最好的 Markdown 编写体验。\n" +
                "\n" +
                "来试一下：\n" +
                "\n" +
                "- **粗体** (`Ctrl+B`) and *斜体* (`Ctrl+I`)\n" +
                "- 引用 (`Ctrl+Q`)\n" +
                "- 代码块 (`Ctrl+K`)\n" +
                "- 标题 1, 2, 3 (`Ctrl+1`, `Ctrl+2`, `Ctrl+3`)\n" +
                "- 列表 (`Ctrl+U` and `Ctrl+Shift+O`)\n" +
                "\n" +
                "### 实时预览，所见即所得 ###\n" +
                "\n" +
                "无需猜测您的 [语法](http://markdownpad.com) 是否正确；每当您敲击键盘，实时预览功能都会立刻准确呈现出文档的显示效果。\n" +
                "\n" +
                "### 自由定制 ###\n" +
                " \n" +
                "100% 可自定义的字体、配色、布局和样式，让您可以将 MarkdownPad 配置的得心应手。\n" +
                "\n" +
                "### 为高级用户而设计的稳定的 Markdown 编辑器 ###\n" +
                " \n" +
                " JMarkdown 支持多种 Markdown 解析引擎，包括 标准 Markdown 、 Markdown 扩展 (包括表格支持) 以及 GitHub 风格 Markdown 。\n" +
                " \n" +
                " 有了标签式多文档界面、PDF 导出、内置的图片上传工具、会话管理、拼写检查、自动保存、语法高亮以及内置的 CSS 管理器，您可以随心所欲地使用 MarkdownPad。\n" +
                "## Markdown plus tables ##\n" +
                "\n" +
                "# Simple tables\n" +
                "\n" +
                "Table alignement:\n" +
                "\n" +
                "* * *\n" +
                "Table alignement:\n" +
                "| Default   | Right     |  Center   |     Left  |\n" +
                "| --------- |:--------- |:---------:| ---------:|\n" +
                "| Long Cell | Long Cell | Long Cell | Long Cell |\n" +
                "| Cell      | Cell      |   Cell    |     Cell  |\n" +
                "HTML:\n" +
                "```html \n" +
                "    <h1>HTML code</h1>\n" +
                "    <p class=\"some\">This is an example</p>\n" +
                "```\n" +
                "Python:\n" +
                "```Python\n" +
                "    def func():\n" +
                "      for i in [1, 2, 3]:\n" +
                "        print \"%s\" % i\n" +
                "```\r\n" +
                "~~strike-through~~\n" +
                "This is [an example](http://example.com/ \"Title\") inline link.\r\n" +
                "![Alt text](/path/to/img.jpg)\r\n" +
                "[foo]: http://example.com/  \"Optional Title Here\" \n" +
                "this is=[foo]";
        String html = ScriptMarkUtil.getMarkdownHtml(text);
        //System.out.println("------------html:"+html);

        String endStr = "<h2 id=\"HuanYingShiYongMarkdownforjavainjspxnet\">欢迎使用 Markdown for java in jspx.net</h2>\n" +
                "\n" +
                "<p><strong>Markdown</strong> 是 java 平台上一个功能完善的 Markdown 编辑器。</p>\n" +
                "\n" +
                "<h3 id=\"ZhuanWeiMarkdownDaZao\">专为 Markdown 打造</h3>\n" +
                "\n" +
                "<p>提供了语法高亮和方便的快捷键功能，给您最好的 Markdown 编写体验。</p>\n" +
                "\n" +
                "<p>来试一下：</p>\n" +
                "\n" +
                "<ul>\n" +
                "<li><strong>粗体</strong> (<code>Ctrl+B</code>) and <em>斜体</em> (<code>Ctrl+I</code>)</li>\n" +
                "<li>引用 (<code>Ctrl+Q</code>)</li>\n" +
                "<li>代码块 (<code>Ctrl+K</code>)</li>\n" +
                "<li>标题 1, 2, 3 (<code>Ctrl+1</code>, <code>Ctrl+2</code>, <code>Ctrl+3</code>)</li>\n" +
                "<li>列表 (<code>Ctrl+U</code> and <code>Ctrl+Shift+O</code>)</li>\n" +
                "</ul>\n" +
                "\n" +
                "<h3 id=\"ShiShiYuLan,SuoJianJiSuoDe\">实时预览，所见即所得</h3>\n" +
                "\n" +
                "<p>无需猜测您的 <a href=\"http://markdownpad.com\">语法</a> 是否正确；每当您敲击键盘，实时预览功能都会立刻准确呈现出文档的显示效果。</p>\n" +
                "\n" +
                "<h3 id=\"ZiYouDingZhi\">自由定制</h3>\n" +
                "\n" +
                "<p>100% 可自定义的字体、配色、布局和样式，让您可以将 MarkdownPad 配置的得心应手。</p>\n" +
                "\n" +
                "<h3 id=\"WeiGaoJiYongHuErSheJiDeWenDingDeMarkdownBianJiQi\">为高级用户而设计的稳定的 Markdown 编辑器</h3>\n" +
                "\n" +
                "<p>JMarkdown 支持多种 Markdown 解析引擎，包括 标准 Markdown 、 Markdown 扩展 (包括表格支持) 以及 GitHub 风格 Markdown 。</p>\n" +
                "\n" +
                "<p>有了标签式多文档界面、PDF 导出、内置的图片上传工具、会话管理、拼写检查、自动保存、语法高亮以及内置的 CSS 管理器，您可以随心所欲地使用 MarkdownPad。</p>\n" +
                "\n" +
                "<h2 id=\"Markdownplustables\">Markdown plus tables</h2>\n" +
                "\n" +
                "<h1 id=\"Simpletables\">Simple tables</h1>\n" +
                "\n" +
                "<p>Table alignement:</p>\n" +
                "\n" +
                "<hr />\n" +
                "\n" +
                "<p>Table alignement:</p>\n" +
                "\n" +
                "<table>\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th style=\"text-align:left;\"> Default   </th>\n" +
                "<th style=\"text-align:left;\"> Right     </th>\n" +
                "<th style=\"text-align:left;\">  Center   </th>\n" +
                "<th style=\"text-align:left;\">     Left  </th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "\n" +
                "<tbody>\n" +
                "<tr><td style=\"text-align:left;\"><p>Long Cell </p></td><td style=\"text-align:left;\"><p>Long Cell </p></td><td style=\"text-align:left;\"><p>Long Cell </p></td><td style=\"text-align:left;\"><p>Long Cell </p></td></tr>\n" +
                "<tr><td style=\"text-align:left;\"><p>Cell      </p></td><td style=\"text-align:left;\"><p>Cell      </p></td><td style=\"text-align:left;\"><p>Cell    </p></td><td style=\"text-align:left;\"><p>Cell  </p></td></tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "<pre class=\"brush:html\">\n" +
                "    &lt;h1&gt;HTML code&lt;/h1&gt;\n" +
                "    &lt;p class=\"some\"&gt;This is an example&lt;/p&gt;\n" +
                "</pre>\n" +
                "\n" +
                "<p>Python:</p>\n" +
                "\n" +
                "<pre class=\"brush:Python\">\n" +
                "    def func():\n" +
                "      for i in [1, 2, 3]:\n" +
                "        print \"%s\" % i\n" +
                "</pre>\n" +
                "\n" +
                "<p><del>strike-through</del>\n" +
                "This is <a href=\"http://example.com/\" title=\"Title\">an example</a> inline link.\n" +
                "<img src=\"/path/to/img.jpg\" alt=\"Alt text\" title=\"\" />\n" +
                "this is=<a href=\"http://example.com/\" title=\"Optional Title Here\">foo</a></p>";
        Assert.assertEquals(html, endStr);
    }


    @Test
    public static void testWikiCode() throws Exception {
        String text = "== Heading Example ==\n" +
                "=== SubHeading 1 ===\n" +
                "==== List Example ====\n" +
                "* List 1\n" +
                "* List 2\n" +
                "* List 3\n" +
                "** List 3.1\n" +
                "** List 3.2\n" +
                "*** List 3.2.1\n" +
                "*** List 3.2.2\n" +
                "==== Number List Example ====\n" +
                "# Number 1\n" +
                "# Number 2\n" +
                "# Number 3\n" +
                "## Number 3.1\n" +
                "### Number 3.1.1\n" +
                "==== Indent Example ====\n" +
                "No Indent!\n" +
                ": Indent 1\n" +
                ":: Indent 2\n" +
                "::: Indent 3\n" +
                "==== List ====\n" +
                ";Item 1 : Description 1\n" +
                ";Item 2 : Description 2\n" +
                ";Item 3 : Description 3\n" +
                "=== SubHeading 2 ===\n" +
                "==== Link Example ====\n" +
                "*[[Link 1]]\n" +
                "*[[Link 2]]\n" +
                "*[[首页]]\n" +
                "*[[#Link|页面锚点]]\n" +
                "*[http://www.google.com Google]\n" +
                "*[mailto:someone@example.com mailto]\n" +
                "*[[media:testaio.txt]]\n" +
                "\n" +
                "==== Font Example ====\n" +
                "*''斜体\n" +
                "*'''黑体\n" +
                "*'''''斜体加黑体\n" +
                "*''''左右各一个单引号''''\n" +
                "\n" +
                "=== SubHeading 3 ===\n" +
                "==== 用线分割文本 ====\n" +
                "This is the first line.\n" +
                "----\n" +
                "This is the second line.\n" +
                "==== Text Example ====\n" +
                "<pre>\n" +
                "abcdefg [[main]]\n" +
                "haha\n" +
                "haha\n" +
                "</pre>\n" +
                "==== HTML Example ====\n" +
                "<center>Center Align</center>\n" +
                "==== Table Example ====\n" +
                "{| class=\"wikitable\" border=\"1\"\n" +
                "|-\n" +
                "! header 1\n" +
                "! header 2\n" +
                "! header 3\n" +
                "|-\n" +
                "| row 1, cell 1\n" +
                "| row 1, cell 2\n" +
                "| row 1, cell 3\n" +
                "|-\n" +
                "| row 2, cell 1\n" +
                "| row 2, cell 2\n" +
                "| row 2, cell 3\n" +
                "|}";
        String html = ScriptMarkUtil.getMarkdownHtml(text);
        System.out.println("------------html:" + html);

        //String endStr ="";
        //Assert.assertEquals(html,endStr);
    }


    @Test
    public static void testQuest() throws Exception {

        String str = "select count(*)>=5 from site_webcont where nodeId='招投动态' and createDate>='${beginDateTime.getTime().toDateString(\"yyyy-MM-dd hh:mm:ss\")}'";


        Assert.assertEquals(StringUtil.quote(str, true), "\"select count(*)>=5 from site_webcont where nodeId='招投动态' and createDate>='${beginDateTime.getTime().toDateString(\\\"yyyy-MM-dd hh:mm:ss\\\")}'\"");

        Assert.assertEquals(StringUtil.quote(str, false), "'select count(*)>=5 from site_webcont where nodeId=\\'招投动态\\' and createDate>=\\'${beginDateTime.getTime().toDateString(\"yyyy-MM-dd hh:mm:ss\")}\\''");

    }

    @Test
    public static void tesLoadClass() throws Exception {


        //dalvik.system.DexClassLoader.getSystemClassLoader();
        Class<?> class1 = Class.forName("dalvik.system.DexClassLoader");

        Method callMethod = class1.getMethod("getSystemClassLoader");
        //Method callMethod=class1.getDeclaredMethod("getSystemClassLoader",null);

        System.out.println("----------callMethod=" + callMethod);

        System.out.println(callMethod.invoke(null));


        System.out.println("----------callMethod end=" + ClassUtil.callStaticMethod(class1, "getSystemClassLoader"));
    }


    // @Test
    public static void cutImage() throws Exception {
        File file = new File("e:\\temp\\testaio.jpg");


        File fileTo = new File("e:\\temp\\test2.jpg");
        boolean cutYes = ImageUtil.cut(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", 20, 10, 400, 400);
        System.out.println(cutYes);
    }

    //@Test
    public static void createThumbnail() throws Exception {
        File file = new File("e:\\temp\\testaio.jpg");
        File fileTo = new File("e:\\temp\\test3.jpg");
        boolean cutYes = ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", 400, 400);
        System.out.println(cutYes);
    }

    // @Test
    public static void gray() throws Exception {
        File file = new File("e:\\temp\\testaio.jpg");
        File fileTo = new File("e:\\temp\\test4.jpg");
        boolean cutYes = ImageUtil.gray(new FileInputStream(file), new FileOutputStream(fileTo), "jpg");
        System.out.println(cutYes);
    }

    private static float contrast = 0.9f; // default value;
    private static float brightness = 0.9f; // default value;

    //@Test
    public static void filter() throws Exception {
        File file = new File("e:\\temp\\testaio.jpg");
        File fileTo = new File("e:\\temp\\test5.jpg");
        boolean cutYes = ImageUtil.filter(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", contrast, brightness);
        System.out.println(cutYes);
    }

    // @Test
    public static void createThumbnail2() throws Exception {

        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2014\\jpg\\WeiBiaoTi-14.jpg");
        File fileTo = new File("D:\\website\\webapps\\root\\jcms\\upload\\2014\\jpg\\testaio.jpg");
        boolean cutYes = ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", 300, 100);
        System.out.println(cutYes);
    }

    //  @Test
    public static void rotate() throws Exception {

        File file = new File("e:\\tmp\\WeiYunXiangCe-ShiYongZhiYin01.jpg");
        File fileTo = new File("e:\\tmp\\1.jpg");
        boolean cutYes = ImageUtil.rotate(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", 360 - 90);
        System.out.println(cutYes);
    }

    @Test
    public static void testStringMap() throws Exception {
        String history = "=\n" +
                "2=WeiYunXiangCe-ShiYongZhiYin01_jpg.tmp\n" +
                "3=WeiYunXiangCe-ShiYongZhiYin02_jpg.tmp\n" +
                "4=WeiYunXiangCe-ShiYongZhiYin03_jpg1.tmp\n" +
                "5=WeiYunXiangCe-ShiYongZhiYin04_jpg.tmp\n" +
                "6=WeiYunXiangCe-ShiYongZhiYin05_jpg.tmp\n" +
                "7=WeiYunXiangCe-ShiYongZhiYin06_jpg1.tmp\n" +
                "8=WeiYunXiangCe-ShiYongZhiYin07_jpg.tmp\n" +
                "9=WeiYunXiangCe-ShiYongZhiYin08_jpg.tmp\n" +
                "10=WeiYunXiangCe-ShiYongZhiYin09_jpg.tmp\n" +
                "11=WeiYunXiangCe-ShiYongZhiYin010_jpg.tmp\n" +
                "12=WeiYunXiangCe-ShiYongZhiYin011_jpg.tmp\n" +
                "13=WeiYunXiangCe-ShiYongZhiYin012_jpg.tmp\n";


        StringMap map = new StringMap();
        map.setKeySplit("=");
        map.setLineSplit("\n");
        map.setString(history);
        map.put(NumberUtil.toString(map.size()), "testxxx");


        System.out.println("-----------------1-removeLast=" + map.removeLast());
        System.out.println("-----------------2-removeLast=" + map.removeLast());

    }

    @Test
    public static void testStringList() throws Exception {
        String history =
                "2=WeiYunXiangCe-ShiYongZhiYin01_jpg.tmp\n" +
                        "3=WeiYunXiangCe-ShiYongZhiYin02_jpg.tmp\n" +
                        "4=WeiYunXiangCe-ShiYongZhiYin03_jpg1.tmp\n" +
                        "5=WeiYunXiangCe-ShiYongZhiYin04_jpg.tmp\n" +
                        "6=WeiYunXiangCe-ShiYongZhiYin05_jpg.tmp\n" +
                        "7=WeiYunXiangCe-ShiYongZhiYin06_jpg1.tmp\n" +
                        "8=WeiYunXiangCe-ShiYongZhiYin07_jpg.tmp\n" +
                        "9=WeiYunXiangCe-ShiYongZhiYin08_jpg.tmp\n" +
                        "10=WeiYunXiangCe-ShiYongZhiYin09_jpg.tmp\n" +
                        "11=WeiYunXiangCe-ShiYongZhiYin010_jpg.tmp\n" +
                        "12=WeiYunXiangCe-ShiYongZhiYin011_jpg.tmp\n" +
                        "13=WeiYunXiangCe-ShiYongZhiYin012_jpg.tmp\n";


        StringList list = new StringList();
        list.setString(history);
        list.removeFirst();
        list.removeLast();

        System.out.println("-----list.toString()=" + list.toString());
        for (String key : list.toArray()) {
            System.out.println("-----key=" + key);
            list.remove(key);

        }

    }


    @Test
    public static void testMap() {
        StringMap<String, String> map = new StringMap<String, String>();
        map.setKeySplit("=");
        map.setLineSplit("\r\n");
//        map.setString("11=aaa");
        System.out.println(map.size());
        map.put("" + (map.size() + 1), "AAA");
        map.put("" + (map.size() + 1), "BBB");


        String fileName = map.get("" + (map.size()));
        System.out.println(map.size() + "=" + fileName);
        map.remove("" + (map.size()));
        String fileName2 = map.get("" + (map.size()));
        System.out.println(map.size() + "=" + fileName2);

    }


    //  @Test
    public static void testThumbnail() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2014\\jpg\\8.jpg");
        File fileTo = new File("D:\\website\\webapps\\root\\jcms\\upload\\2014\\jpg\\8_800x0.jpg");
        boolean cutYes = ImageUtil.scale(new FileInputStream(file), new FileOutputStream(fileTo), "jpg", 0.8f);
        System.out.println(cutYes);

        //  File fileTo2= new File("e:\\tmp\\test6.jpg");
        //   BufferedImage image = ImageIO.read(new FileInputStream(file));


        //   ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(fileTo2), "jpg", (int)(image.getWidth()*0.8),(int)(image.getHeight()*0.8f));
        //  System.out.println(cutYes);
    }


    @Test
    public static void testScale() throws Exception {
        float scaleValue = 120;
        if (scaleValue > 5) scaleValue = scaleValue / 100;
        System.out.println(scaleValue);
    }

    @Test
    public static void testLanguage_zh() throws Exception {
        String lang = FileUtil.getNamePart("c:\\sss\\language_zh.properties");
        System.out.println("lang------------" + lang);
        System.out.println("lang2------------" + lang.contains("_"));
        if (lang.contains("_")) lang = StringUtil.substringAfter(lang, "_");
        System.out.println("lang end------------" + lang);
    }

    @Test
    public static void testStringDeleteLine() throws Exception {
        String xx = "13=立刻就随大流富士康的.jpg\n" +
                "5=风光得很过分.jpg\n" +
                "11=未标题-16.jpg\n" +
                "6=未标题-2.jpg\n" +
                "7=风光得很过分.jpg\r\n" +
                "2=ks_04.gif\n" +
                "4=u=2336664911,883551863&amp;fm=11&amp;gp=0.jpg\r\n" +
                "9=未标题-11.jpg\n" +
                "10=5555.jpg\n" +
                "8=未标题-6.jpg\n" +
                "3=pro-24.jpg\n" +
                "12=未标题-18.jpg\n" +
                "9=未标题-11.jpg";


        System.out.println(StringUtil.deleteLine(xx, 2));
    }

    @Test
    public static void replaceLine() throws Exception {
        String xx = "13=立刻就随大流富士康的.jpg\n" +
                "5=风光得很过分.jpg\n" +
                "11=未标题-16.jpg\n" +
                "6=未标题-2.jpg\n" +
                "7=风光得很过分.jpg\r\n" +
                "2=ks_04.gif\n" +
                "4=u=2336664911,883551863&amp;fm=11&amp;gp=0.jpg\r\n" +
                "9=未标题-11.jpg\n" +
                "10=5555.jpg\n" +
                "8=未标题-6.jpg\n" +
                "3=pro-24.jpg\n" +
                "12=未标题-18.jpg\n" +
                "9=未标题-11.jpg";


        System.out.println(StringUtil.replaceLine(xx, 6, "aaa=sdfasdfasd"));
    }

    @Test
    public static void getRootNamespace() throws Exception {

        System.out.println("lang------------" + TXWebUtil.getRootNamespace("/hdoc/mootools/request_Html.html"));
    }


    @Test
    public static void testSystemUtil() throws Exception {

        SystemUtil.encode = "GBK";

//mysql

        // System.out.println("---------" +SystemUtil.shell("ipconfig"));
        //System.out.println("---------" +SystemUtil.nirCMD("initshutdown reboot"));

        ////System.out.println("---------" +SystemUtil.nirCMD("net start MySQL"));
//
        // System.out.println("---------" +SystemUtil.cmd("cmd.exe/c cat e:\\tmp\\SimpleVerifier.txt"));


    }

    @Test
    public static void nirCMD() throws Exception {
        System.out.println(SystemUtil.saveScreensHot("d:\\temp\\shot.png"));

    }


    public static String utcToTimeZoneDate(long date, TimeZone timeZone, DateFormat format) {
        Date dateTemp = new Date(date);
        format.setTimeZone(timeZone);
        return format.format(dateTemp);
    }

    @Test
    public void availableCharsets() throws Exception {
        long last = FileUtil.getLastModified("e:\\zzz\\dtmp\\了多  少.卡   飞了电视剧撒.jsp");
        System.out.println(last);    //1429145133000

        final Calendar cal = Calendar.getInstance();
        System.out.println("---------cal.get(java.util.Calendar.ZONE_OFFSET);=" + cal.get(Calendar.ZONE_OFFSET));
        cal.setTimeInMillis(last);
        cal.add(Calendar.MILLISECOND, -cal.get(Calendar.ZONE_OFFSET));

        System.out.println((last - cal.get(Calendar.ZONE_OFFSET)) + "---------cal.getTimeInMillis()=" + cal.getTimeInMillis());
        Date date = new Date(cal.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.ENGLISH);
        String dateStr = dateFormat.format(date);
        System.out.println(dateStr);


        String strDate = (dateStr);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddhhmmss");
        Date dateStr2 = dateFormat2.parse(strDate);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(dateStr2.getTime());
        cal2.add(Calendar.MILLISECOND, cal.get(Calendar.ZONE_OFFSET));
        Date date2 = new Date(cal2.getTimeInMillis());
        System.out.println(date2.getTime());
    }


    @Test
    public static void getTopDomain() throws Exception {
        String url = "http://www.jspx.net:8080/jcms/htdoc/pageview.jhtml?id=11";

        Assert.assertEquals(URLUtil.getDomain(url), "www.jspx.net");
        Assert.assertEquals(URLUtil.getTopDomain(url), "jspx.net");

        url = "http://www.jspx.net";
        Assert.assertEquals(URLUtil.getDomain(url), "www.jspx.net");
        Assert.assertEquals(URLUtil.getTopDomain(url), "jspx.net");


        url = "http://192.168.0.200/jcms/htdoc/pageview.jhtml?id=11";
        Assert.assertEquals(URLUtil.getDomain(url), URLUtil.getTopDomain(url));

        url = "http://192.168.0.200:886/jcms/htdoc/pageview.jhtml?id=11";
        Assert.assertEquals(URLUtil.getDomain(url), URLUtil.getTopDomain(url));

        url = "http://192.168.0.200:886";
        Assert.assertEquals(URLUtil.getDomain(url), URLUtil.getTopDomain(url));

        url = "http://192.168.0.200:8888";
        Assert.assertEquals(URLUtil.getDomain(url), URLUtil.getTopDomain(url));

        url = "http://jspx.net";
        Assert.assertEquals(URLUtil.getDomain(url), URLUtil.getTopDomain(url));

        url = "http://www.gzcom.gov.cn";
        Assert.assertEquals("gzcom.gov.cn", URLUtil.getTopDomain(url));

        url = "http://hall.gzcom.gov.cn";
        Assert.assertEquals("gzcom.gov.cn", URLUtil.getTopDomain(url));

        url = "www.gzcom.gov.cn";
        Assert.assertEquals("gzcom.gov.cn", URLUtil.getTopDomain(url));

        url = "hall.gzcom.gov.cn";
        Assert.assertEquals("gzcom.gov.cn", URLUtil.getTopDomain(url));

        url = "http://www.testaio.com:8080/";
        Assert.assertEquals("testaio.com", URLUtil.getTopDomain(url));


    }

    @Test
    public static void allowServerName() throws Exception {
        String allowServerName = "(.*).gzcom.gov.cn|(.*).test.com";
        Assert.assertEquals("www.gzcom.gov.cn".matches(allowServerName), true);
        Assert.assertEquals("12.testaio.com".matches(allowServerName), false);
    }

    @Test
    public static void getUTFTxt() throws Exception {
        String txt = "\\\\u914D\\\\u7F6E\\\\u6587\\\\u4EF6\\\\u521D\\\\u59CB\\\\u5316\\\\u5931\\\\u8D25";
        txt = StringUtil.replace(txt, "\\\\", "\\");
        txt = StringUtil.UTFToString(txt);
        System.out.println(txt);
    }


    @Test
    public static void moveToTypeDir() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2015\\sadfsd.jpg");
        JspxNetFileRenamePolicy fileRenamePolicy = new JspxNetFileRenamePolicy();
        System.out.println(FileUtil.moveToTypeDir(file, fileRenamePolicy, false));
    }

    @Test
    public static void moveToTypeDirDefaultFileRenamePolicy() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2015\\sadfsd.jpg");
        FileRenamePolicy fileRenamePolicy = new DefaultFileRenamePolicy();
        System.out.println(FileUtil.moveToTypeDir(file, fileRenamePolicy, false));
    }

    @Test
    public static void moveToTypeDirDateRandomNamePolicy() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2015\\15080821585660.jpg");
        FileRenamePolicy fileRenamePolicy = new DateRandomNamePolicy();
        System.out.println(FileUtil.moveToTypeDir(file, fileRenamePolicy, false));
    }

    @Test
    public static void cleanLink() throws Exception {

        String html =
                "  <ul>\n" +
                        "      <li>HtmlCleaner JAR:" +
                        "      <a href=\"http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13.zip/download\">htmlcleaner-2.13.zip<a href=\"http\">xxxx</a><img src=\"/i/eg_tulip.jpg\"  alt=\"上海鲜花港\" /></li>\n" +
                        "      <li>HtmlCleaner GUI: <a href=\"http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-gui.zip/download\">htmlcleaner-2.13-gui.zip</a></li>\n" +
                        "      <li>Project source code: <a href=\"https://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-src.zip/download\">htmlcleaner-2.13-src.zip</a></li>\n" +
                        "      <img src=\"/i/eg_tulip.jpg\"  alt=\"上海鲜花港 - 郁金香\" />" +
                        "  </ul>";
        ;

        String txt = HtmlUtil.getSafeFilter(html);
        txt = HtmlUtil.linkToMarkdown(HtmlUtil.getSafeFilter(txt));

        //   System.out.println(txt);

        String out = "  <ul>\n" +
                "      <li>HtmlCleaner JAR:      [htmlcleaner-2.13.zip](http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13.zip/download)[xxxx](http)[![Alt \"上海鲜花港\"](/i/eg_tulip.jpg)](http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13.zip/download)</li>\n" +
                "      <li>HtmlCleaner GUI: [htmlcleaner-2.13-gui.zip](http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-gui.zip/download)</li>\n" +
                "      <li>Project source code: [htmlcleaner-2.13-src.zip](https://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-src.zip/download)</li>\n" +
                "      <li>![Alt \"上海鲜花港 - 郁金香\"](/i/eg_tulip.jpg)  </li></ul>";

        Assert.assertEquals(txt, out);


    }


    @Test
    public static void cleanHTML() throws Exception {


        String html =
                "  <ul>\n" +
                        "      <li>HtmlCleaner JAR:" +
                        " <a href=\"http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13.zip/download\">htmlcleaner-2.13.zip<a href=\"http\">xxxx</a><img src=\"/i/eg_tulip.jpg\"  alt=\"上海鲜花港\" /></a></li>\n" +
                        "      <li>HtmlCleaner GUI: <a href=\"http://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-gui.zip/download\">htmlcleaner-2.13-gui.zip</a></li>\n" +
                        "      <li>Project source code: <a href=\"https://sourceforge.net/projects/htmlcleaner/files/htmlcleaner/htmlcleaner%20v2.13/htmlcleaner-2.13-src.zip/download\">htmlcleaner-2.13-src.zip</a></li>\n" +
                        "<img src=\"/i/eg_tulip.jpg\"  alt=\"上海鲜花港 - 郁金香\" />" +
                        "  </ul>\n";
        ;

        HtmlCleaner hmlCleaner = new HtmlCleaner();

        CleanerProperties props = hmlCleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setOmitHtmlEnvelope(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(true);
        props.setUseCdataForScriptAndStyle(false); //是否使用 <![CDATA[
        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setIgnoreQuestAndExclam(false);
        props.setNamespacesAware(false);
        props.setOmitUnknownTags(false);
        // props.setPruneTags("body");

        TagNode node = hmlCleaner.clean(html);


        System.out.println(new PrettyHtmlSerializer(props).getAsString(node.findElementByName("body", false)));
    }


    @Test
    public static void splitLength() throws Exception {
        String xx = "栈解决程序的运行问题，即程序如何执行，或者说如何处理数据；堆解决的是数据存储的问题，即数据怎么放、放在哪儿。\n" +
                "在Java中一个线程就会相应有一个线程栈与之对应，这点很容易理解，因为不同的线程执行逻辑有所不同，因此需要一个独立的线程栈。而堆则是所有线程共享的。栈因为是运行单位，因此里面存储的信息都是跟当前线程（或程序）相关信息的。包括局部变量、程序运行状态、方法返回值等等；而堆只负责存储对象信息。";

        String[] strings = StringUtil.split(xx, 20);
        for (String tmp : strings) {
            System.out.println(tmp.length() + ":" + tmp);
        }
    }

    @Test
    public static void smsUTF2Zh() throws Exception {
        String xx = "00320030003100355E740038670881F3003100326708FF0C6BCF67086210529F81EA62636EE1767E5143FF0C5F5367088FD86B3E91D1989D96F65934FF08514389D25206FF0963096210529F6B2165707FFB500D595652B1FF0C6B2167085E95524D595652B18FD481F38FD86B3E501F8BB05361FF1B7D2F8BA16210529F00346B21FF0C53EF";
        String txt = StringUtil.mobileUTFToString(xx);
        String txt2 = StringUtil.toMobileUTFString(txt);
        Assert.assertEquals(xx, txt2);

    }

    @Test
    public static void toUTFString() throws Exception {
        String xx = "程度的支持中文输出,则最好使 Java文件使用test";
        String txt = StringUtil.toMobileUTFString(xx);
        System.out.println(txt);
        //7A0B5EA67684652F63014E2D65878F9351FA002C52196700597D4F7F0020004A00610076006165874EF64F7F75280074006500730074
        //7A0B5EA67684652F63014E2D65878F9351FA002C52196700597D4F7F0020004A00610076006165874EF64F7F75280074006500730074
    }

    @Test
    public static void getMessageInList() throws Exception {

        String txt = "AT+CMGL=\"ALL\"\n" +
                "+CMGL: 1,\"REC READ\",\"0031003200350032003000310033003500310031003900390033003600360035\",\"\",\"15/09/02,12:50:20+32\"\n" +
                "246452A0516500316EF400438BD55242FF0C6DF753003002000A246552A05165003100306EF400448BD55242FF08FF0C653E68C06D4BFF1A53D600396EF484B8998F6C344E8E79BB5FC37BA14E2DFF0C5206522B52A0516591526837548C6807683700316EF4FF0C52A000316EF400438BD55242FF0C52A00031006D004C00448BD55242FF0C\n" +
                "\n" +
                "+CMGL: 2,\"REC READ\",\"0031003200350032003000310033003500310031003900390033003600360035\",\"\",\"15/09/02,12:50:20+32\"\n" +
                "52A076D6540E6DF75300FF0C89C25BDF989C827253CD5E94\n" +
                "\n" +
                "+CMGL: 3,\"REC READ\",\"0031003200350032003000310033003500310031003900390033003600360035\",\"\",\"15/09/02,12:50:20+32\"\n" +
                "68C06D4B6B659AA4FF1A000A246053D679BB5FC37BA14E0053EAFF0C52A0516500326EF400418BD552423002000A2461541179BB5FC37BA14E2D52A051655F856D4B9152683700316EF43002000A24626DF753003002000A246352A0516500326EF400428BD55242FF0C514552066DF75300FF0C4F7F6EB66DB25B8C5168892A82723002000A\n" +
                "\n" +
                "OK";

        List<SmsReceive> list = FormatParsing.getMessageInList(txt);
        //list = FormatParsing.getJoinMessage(list);
        for (SmsReceive smsMessageIn : list) {
            System.out.println("SmsMessageIn----------begin");

            System.out.println(ObjectUtil.getJson(smsMessageIn));
            System.out.println("----------end");
        }
    }

    @Test
    public static void getMessageIn() throws Exception {

        String txt = "AT+CMGR=2\n" +
                "+CMGR: \"REC READ\",\"002B0038003600310038003900380035003100370039003800380035\",\"\",\"15/08/24,11:39:22+32\"\n" +
                "989D591683B78D600031003000305143501F8BB053615237536191D1595652B1FF0800320030003100365E74003167088FD48FD8FF09FF0C53C24E0E65B95F0F53CA6D3B52A87EC652198BE24EA4884C4FE1752853615B987F5162165FAE4FE1670D52A153F73002005B4EA4901A94F6884C4FE175285361005D\n";

        SmsReceive messageIn = FormatParsing.getMessageIn(txt);
        System.out.println("SmsMessageIn----------begin");
        System.out.println(ObjectUtil.getJson(messageIn));
        System.out.println("----------end");
    }

    @Test
    public static void getTxt() {
        String txt = "00320030003100355E744EE56765FF0C9ED44E1C7ECF6D4E5F0053D1533A79EF678190E87F72843D5B9E5B895168751F4EA768C067E55DE54F5CFF0C51715F005C554E13987968C067E500386B21FF0C6DF151654F014E1A0031003800306237FF0C898676D673878FBE0031003000300025FF0C639267E5969060A30031003200396761300252075B9E505A52305168";
        String txt1 = StringUtil.mobileUTFToString(txt);
        System.out.println(txt1);
        System.out.println(txt1.length());
    }


    @Test
    public static void getRingCode() throws Exception {
        String txt = "RING\n" +
                "\n" +
                "+CLIP: \"13984415037\",161,\"\",,\"\",0";
        String phone = FormatParsing.getRingCode(txt);
        System.out.println("------------" + phone);

    }

    @Test
    public static void getIPForLong() throws Exception {
        long xx = IpUtil.toLong("192.168.0.200");
        String ip = IpUtil.getIPForLong(xx);
        Assert.assertEquals("192.168.0.200", ip);


        String testIp = "58.16.7.248";
        xx = IpUtil.toLong(testIp);
        ip = IpUtil.getIPForLong(xx);
        Assert.assertEquals(testIp, ip);
    }


    @Test
    public static void sortFile() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\load\\default\\");
        File[] xx = file.listFiles();
        //sort(File[] src, String mark, boolean rule)
        FileUtil.sort(xx, FileUtil.sortName, true);
        for (File f : xx) {
            System.out.println("----------f.getName()=" + f.getName());
        }

    }


    @Test
    public static void sortToolsFile() throws Exception {
        File file = new File("e:\\xxx\\");
        List<File> files = FileUtil.getFileList(file, new String[]{"xml"}, true);
        int i = 0;
        for (File f : files) {
            AbstractRead abstractRead = new AutoReadTextFile();
            abstractRead.setEncode(Environment.defaultEncode);
            abstractRead.setFile(f);
            String text = abstractRead.getContent();
            String documentSerial = StringUtil.trim(StringUtil.substringBetween(text, "<documentSerial>", "</documentSerial>"));

            File nFile = new File(f.getParent(), documentSerial + f.getName());
            if (f.renameTo(nFile)) {
                i++;
                System.out.println(i + "=" + f.getName());
            }
            System.out.println("----------files=" + files.size() + " i=" + i);
        }

    }

    @Test
    public static void escapeHTML() throws Exception {
        String html = "<li>\n" +
                "<span class=\"grid-r f-aid\">2007-06-28</span>\n" +
                "<a href=\"/question/28789969.html?fr=qrl&index=1&qbl=topic_question_1&word=java%20html%D7%AA%D2%E5\" log=\"pos:exttitle,topic:1,index:2\" target=\"_blank\" data-qid=\"28789969\">\n" +
                "java和html的区别\n" +
                "<i class=\"i-evaluate mb-5\"></i><span class=\"ml-5 f-red f-14\" title=\"回答获得19个赞同\">19</span>\n" +
                "</a>\n" +
                "</li>";
        String tmp = HtmlUtil.escapeEncoderHTML(html);
        System.out.println(tmp);
        String oldHtml = HtmlUtil.escapeDecodeHtml(tmp);
        System.out.println("---------------------------");
        System.out.println(oldHtml);
        Assert.assertEquals(html, oldHtml);

    }


    @Test
    public static void toJavaScriptString() throws Exception {

        String html = "<li>\n" +
                "<span class=\"grid-r f-aid\">2007-06-28</span>\n" +
                "<a href=\"/question/28789969.html?fr=qrl&index=1&qbl=topic_question_1&word=java%20html%D7%AA%D2%E5\" log=\"pos:exttitle,topic:1,index:2\" target=\"_blank\" data-qid=\"28789969\">\n" +
                "java和html的区别\n" +
                "<i class=\"i-evaluate mb-5\"></i><span class=\"ml-5 f-red f-14\" title=\"回答获得19个赞同\">19</span>\n" +
                "</a>\n" +
                "</li>";

        System.out.println("1---------------------------");
        System.out.println(StringUtil.toJavaScriptQuote(html));

        System.out.println("2---------------------------");
        System.out.println(StringUtil.toJavaScriptString(html));
        //Assert.assertEquals(html, oldHtml);

    }

    @Test
    public static void freeMarkerVar() throws Exception {

        String html = "<li>\n" +
                "${xx}<span class=\"grid-r f-aid\">2007-06-28</span>\n" +
                "${id}A${ids}${AA}${BB}\n" +
                "java和html的区别\n" +
                "<i class=\"i-evaluate mb-5\"></i><span class=\"ml-5 f-red f-14\" title=\"回答获得19个赞同\">${CC}${ids}</span>\n" +
                "</a>\n" +
                "</li>";

        String[] aas = StringUtil.getFreeMarkerVar(html);
        System.out.println("1---------------------------" + ArrayUtil.toString(aas, "|"));
    }

    @Test
    public static void isMobileBrowser() throws Exception {

        String html = "Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; SCH-P709 Build/JDQ39) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025488 Mobile Safari/533.1 MicroMessenger/6.3.8.56_re6b2553.680 NetType/WIFI Language/zh_CN";
        boolean bo = RequestUtil.isMobileBrowser(html);
        System.out.println("1---------------------------" + bo);
    }

    @Test
    public static void getSystem() throws Exception {

        String html = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";
        String bo = RequestUtil.getSystem(html);
        System.out.println("1---------------------------" + bo);
    }

    @Test
    public static void getStartDateTime() throws Exception {

        int day = 2;
        Date startDate = DateUtil.getStartDateTime(DateUtil.addDate(-(day - 1)));

        System.out.println("1---------------------------" + DateUtil.toString(startDate, DateUtil.ST_FORMAT));
    }

    @Test
    public static void getDateMinute() throws Exception {

        Date date = DateUtil.getDateMinute(DateUtil.addDate(-1, new Date()));


        System.out.println("1---------------------------" + DateUtil.toString(date, DateUtil.FULL_ST_FORMAT));
    }


    public static void testAttribute() throws Exception {


    }

/*
    public static void testGetHtml() throws Exception {

        String html = ReadFileUtil.readToString(new File("D:\\Noname1.txt"));

        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setOmitHtmlEnvelope(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(true);
        props.setUseCdataForScriptAndStyle(false);
        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setNamespacesAware(false);
        TagNode node = cleaner.clean(html);   //不能有body
        TagNode[] tagNodes = node.getAllElements(true);
        for (TagNode tNode:tagNodes)
        {
            if (tNode!=null&&"l_list clearfix".equalsIgnoreCase(tNode.getAttributeByName("class")))
            {

                String tt = new SimpleXmlSerializer(props).getAsString(tNode.getChildTags()[0]);
                System.out.println("1---------------------------" + tt);
            }
        }
    }
 */


    @Test
    public static void testMapValue() throws Exception {
        String key = "jspx.jcms.table.Matter_list_tagsLIKE'DDDD%'_nodeId_jcms4_namespace_192.168.0.200_auditingType_1_recycleType_0_issueDate>_MonJan1800:00:00CST2016_o_sortDatedesc_L_0-6_l_true";
        HashMap hashMap = new HashMap<String, Object>();
        hashMap.put(key, "A10");
        Assert.assertEquals("A10", hashMap.get(key));

    }

    @Test
    public static void deleteHtml() throws Exception {
        String key =
                "丹索亚刺梨干酒紫阳果酒750ml\n" +
                        "\t\t\t\t\t\t\t\t\t\t<span class=\"cf60\">丹索亚刺梨干酒紫阳果酒750ml</span>";
        Assert.assertEquals("丹索亚刺梨干酒紫阳果酒750ml\n" +
                "\t\t\t\t\t\t\t\t\t\t丹索亚刺梨干酒紫阳果酒750ml", HtmlUtil.deleteHtml(key));

    }

    @Test
    public static void getNamespace() throws Exception {

        Assert.assertEquals(TXWebUtil.getNamespace("jcms/" + "htdoc/"), "jcms/htdoc");
    }

    @Test
    public static void ScriptConverter() throws Exception {
        Assert.assertEquals("Er.GongNengShuoMing", ScriptConverter.deleteHtml(ScriptConverter.getPinYin("二.功能说明"), 40, ""));
    }

    @Test
    public static void attributes() throws Exception {

        char escapeVariable = '\\';
        char beginTag = '<';
        char endTag = '>';
        String source = "<#include file=\"/share/sys/globaltopmenu.ftl\" showHead=\"false\" />";

        boolean begin = false;
        int in = 0;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < source.length()) {
            char c = source.charAt(i);
            if (c == escapeVariable) {
                sb.append(source.charAt(i + 1));
                i = i + 2;
                continue;
            }
            if (c == beginTag) in++;
            if (c == endTag) in--;
            if (begin && in > 0) {
                sb.append(c);
            }
            if (c == ' ') {
                begin = true;
            }
            if (in == 0 || i >= source.length()) {
                break;
            }
            i++;
        }
        String result = "";
        if (sb.length() > 2 && sb.toString().endsWith("/")) {
            result = sb.substring(0, sb.length() - 1).trim();
        } else result = sb.toString().trim();
        System.out.println("---------------------------" + result);

    }

    @Test
    public static void testMobileImageFilter() throws Exception {
        String html = "<img src=\"/jcms/upload/demo/03.jpg\" />作的总称。[filter;filterate] 通过特殊装置将流体提纯净化的过程，过滤的方式很多，使用的物系也很广泛，固-液、固-气、" +
                " <li><a style=\"\" href=\"pageview.jhtml?id=371\" target=\"_blank\" title=\"测试数据,二次元标题一定比较长才好测试，后边为标题糕点名店 sort=77\">\n" +
                "\t\t\t\t\t  <img src=\"/jcms/upload/demo/02.jpg\" />\n" +
                "<img src=\"/jcms/upload/demo/02.jpg\" />\n" +
                "<img src=\"http://www.xx.com/jcms/upload/demo/02.jpg\" />\n" +
                "\t\t\t\t\t  <span>测试数据,二次元标题..</span></a></li>大颗粒、小颗粒都很常见。过滤是在推动力或者其他外力作用下悬浮液（或含固体颗粒发热气体）中的液体（或气体）透过介质，固体颗粒及";
        html = HtmlUtil.getMobileImageFilter(html, "xmx.com");
        System.out.println("---------------------------\r\n" + html);
    }


    @Test
    public static void test() throws Exception {
        String xTmp = "[20150905_123725:upload/2016/jpg/330_800_0.jpg]";

        System.out.println("---------------------------\r\n" + StringUtil.getElementName(xTmp));
    }


    @Test
    public static void testXMLFormat() throws Exception {

        String xx = "<list>  \n" +
                "  <Contacts>  \n" +
                "    <id>1</id>  \n" +
                "    <name>11</name>  \n" +
                "    <email>111</email>  \n" +
                "    <phone>1111</phone>  \n" +
                "  </Contacts>  <Contacts>  \n" +
                "    <id>2</id>  \n" +
                "    <name>22</name>  \n" +
                "    <email>222</email>  \n" +
                "    <phone>2222</phone>  \n" +
                "  </Contacts><Contacts>  \n" +
                "    <id>3</id>  \n" +
                "    <name>33</name>  \n" +
                "    <email>333</email>  \n" +
                "    <phone>3333</phone>  \n" +
                "  </Contacts>  \n" +
                "</list> ";
        XMLFormat format = new XMLFormat();

        System.out.println(format.getFormatClass() + "---------------------------\r\n" + format.format(xx));

    }

    @Test
    public static void testGetFile() throws Exception {

        System.out.println("1---------------------------\r\n" + FileUtil.getMobileFileName(""));

    }


    @Test
    public static void testFileEQ() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\juweb\\upload\\2016\\jpg\\T2GEueXXX66624658_s.jpg");
        File file2 = new File("D:/website/webapps/root/juweb/upload/2016/jpg/T2GEueXXX66624658_s.jpg");

        System.out.println("1---------------------------" + file.equals(file2));
        System.out.println("2---------------------------" + (file == file2));
        System.out.println("3---------------------------" + (file.compareTo(file2)));
    }

    @Test
    public static void testRenamePolicy() throws Exception {
        JspxNetFileRenamePolicy renamePolicy = new JspxNetFileRenamePolicy();
        File file = new File("C:\\Users\\chenyuan\\Pictures\\2013040400_617f2f2a849b3d0bc1480OjSdOueiMHs.jpg");
        File file2 = renamePolicy.rename(file);
        System.out.println("1---------------------------" + file2.getAbsolutePath());
        System.out.println("1---------------------------" + file.equals(file2));
        System.out.println("2---------------------------" + (file == file2));
        System.out.println("3---------------------------" + (file.compareTo(file2)));
    }

    @Test
    public static void testUploadedFile() throws Exception {
        File outFile = new File("D:\\website\\webapps\\root\\juweb\\upload\\2016\\xxx.jpg");
        File fileDir = new File("D:\\website\\webapps\\root\\juweb\\upload\\2016\\chunk-206-dsc0001020\\");
        System.out.println("---------------------------" + FileUtil.mergeFiles(outFile, fileDir.listFiles()));
    }

    @Test
    public static void testUploadedFileMd5() throws Exception {
        File outFile = new File("D:\\website\\webapps\\root\\jcms\\upload\\2016\\jpg\\o1apnqr9uh1or61pbk1kr2atp1mb017.jpg");
        File fileDir = new File("D:\\website\\webapps\\root\\jcms\\upload\\2016\\jpg\\aaaa.jpg");

        System.out.println("---------------------------" + FileUtil.getHash(outFile, "MD5"));
        System.out.println("---------------------------" + FileUtil.getHash(fileDir, "MD5"));
    }

    @Test
    public static void testFileMd5() throws Exception {
        File file = new File("D:\\website\\webapps\\root\\jcms\\upload\\2016\\zip\\voldemort.zip");
        System.out.println("---------------------------" + FileUtil.getHash(file, "MD5"));

    }


    @Test
    public static void getSafeFilter() throws Exception {
        String html = "<p>将修复下边,错误的html代码。</p>\n" +
                "<table width=\"100%\" border=\"0\">\n" +
                "  <tr>\n" +
                "    <td>1</td>\n" +
                "    <td>2\n" +
                "    <td>3</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>4</td>\n" +
                "   <td>5</td>\n" +
                "    <td>6</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>7</td>\n" +
                "    <td>8\n" +
                "    <td>9\n" +
                "  </tr>\n" +
                "</table>";
        System.out.println("---------------------------" + HtmlUtil.getSafeFilter(html));

    }

    @Test
    public static void probeContentType() throws Exception {

        //  String  txt = java.nio.file.Files.probeContentType(java.nio.file.Paths.get("e:\\20150622_155508.jpg"));
        //   System.out.println("---------------------------" + txt);

    }

    @Test
    public static void testTimeMillis() throws Exception {

        long currentTime = System.currentTimeMillis();

        Date date = new Date(currentTime);

        System.out.println("---------------------------date=" + DateUtil.toString(date, DateUtil.ST_FORMAT));
        long timeMillis = System.currentTimeMillis() - DateUtil.DAY;
        Date dateA = new Date(timeMillis);
        System.out.println("1 day---------------------------date=" + DateUtil.toString(dateA, DateUtil.ST_FORMAT));

    }


    @Test
    public static void testMapKeySort() throws Exception {

        Map<String, String> map = new HashMap<String, String>();

        map.put("KFC", "kfc");
        map.put("WNBA", "wnba");
        map.put("NBA", "nba");
        map.put("CBA", "cba");
        map.put("41A", "cba");
        map.put("1A", "cba");
        map.put("5A", "cba");
        map.put("3A", "cba");
        map.put("4A", "cba");

        //map = MapUtil.sortByKey(map);
        StringMap<String, String> valueMap = new StringMap<String, String>();
        valueMap.setMap(map);

        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

    }

    @Test
    public static void testStringMapKeySort() {

        StringMap<String, String> map = new StringMap<String, String>();

        map.put("KFC", "kfc");
        map.put("WNBA", "wnba");
        map.put("NBA", "nba");
        map.put("CBA", "cba");
        map.put("41A", "cba");
        map.put("1A", "cba");
        map.put("5A", "cba");
        map.put("3A", "cba");
        map.put("22", "");
        map.put("4A", "cba");

        map.sortByKey(true);
        for (String key : map.keySet()) {
            System.out.println(key + "------------------" + map.get(key));
        }
    }

    @Test
    public static void testStringMapKeySortFalse() throws Exception {

        String str = "userIntegral=0\n" +
                "isGuest=true\n" +
                "method=buy\n" +
                "accountName=游客\n" +
                "userMoney=0.0\n" +
                "optional={\"paySign\": \"threadUBB\",\"currency\": \"money\",\"sort\": 1,\"tid\": 2}\n" +
                "sort=1\n" +
                "title=2014年中国出资成立丝路基金\n" +
                "toUserId=1000\n" +
                "tid=2\n" +
                "accountId=0\n" +
                "receivePayUrl=http://192.168.0.200:8080/jbbs/receivepay.jhtml\n" +
                "money=5.0\n" +
                "paySign=threadUBB\n" +
                "namespace=jspx.jbbs.table.SpeakThread\n" +
                "toUserName=chenyuan\n" +
                "organizeId=10000\n" +
                "signType=RSA\n" +
                "sign=\n" +
                "payData=\n" +
                "currency=money\n" +
                "describe=2014年中国出资成立丝路基金\n" +
                "payReturnUrl=http://192.168.0.200:8080/jbbs/payreturn.jhtml\n" +
                "tradeId=2\n" +
                "timeMillis=1498379249406";

        str = StringUtil.replace(str, "\n", "\r\n");
        StringMap payMap = new StringMap();
        payMap.setKeySplit(StringUtil.EQUAL);
        payMap.setLineSplit(StringUtil.CRLF);
        payMap.setString(str);
        payMap.sortByKey(true);
        String strA = payMap.toString();
        //  System.out.println("------------------=" + payMap.toString());
        int x = payMap.size();

        String strB = payMap.toString();
        //   System.out.println(x+"------------------=" + payMap.toString());
        Assert.assertEquals(strA, strB);
    }

    @Test
    public static void testSubArray() throws Exception {

        byte[] txt = "123456789abcdefghijklmn".getBytes(Environment.defaultEncode);
        byte[] out = ArrayUtil.subArray(txt, 2, 8);
        System.out.println(new String(out, Environment.defaultEncode));
    }

    @Test
    public static void testTimeMillisFormat() throws Exception {

        String txt = DateUtil.getTimeMillisFormat(DateUtil.HOUR + DateUtil.MINUTE * 2 + DateUtil.SECOND * 5, Environment.defaultLanguage);
        System.out.println(txt);
    }


    @Test
    public static void testSplitBytes() throws Exception {

        byte[] data = "所以通常加密中并不是直接使用RSA 来对所有的信息进行加密， 最常见的情况是随机产123456个对称加密的密钥只所以分页不仅仅是因为在一个页面显示那么多内容不太好,更因为12345678对于数据库大数据量的查询.所以没有什么储存查询所有记录END".getBytes(Environment.defaultEncode);
        int totalLen = data.length;
        int blockLength = 117;
        int totalPage = (totalLen - 1) / blockLength + 1;
        System.out.println(new String(data, Environment.defaultEncode));
        System.out.println("------------totalPage:" + totalPage);
        int pos = blockLength;
        for (int i = 0; i < totalPage; i++) {
            if (totalLen - i * blockLength < blockLength) pos = totalLen - (i * blockLength);
            System.out.println((i * blockLength) + "------------pos:" + pos);
            byte[] block = new byte[pos];
            System.arraycopy(data, i * blockLength, block, 0, pos);
            System.out.println(new String(block, Environment.defaultEncode));
        }
    }

    @Test
    public static void testSplitJoin() throws Exception {
        byte split = '&';
        byte[] data = "所以通常加密中并不是直接使用RSA来对所有的信息进行加密， 最常见的情况是随机产1234&56个对称加密的密钥只所&以分页不仅仅是因为在一个页面显示那么多内容不太好,更因为1234&5678对于数据库大数据量的查询.所以没有什么储存查询所有记录END".getBytes(Environment.defaultEncode);


        System.out.println("------------indexOf:" + ArrayUtil.indexOf(data, split));

        int totalLen = data.length;
        int i = 0;
        while (i != -1) {
            int pos = ArrayUtil.indexOf(data, split, i);
            if (pos == -1) pos = totalLen;
            int blockLength = pos - i;
            byte[] block = new byte[blockLength];
            System.arraycopy(data, i, block, 0, blockLength);

            System.out.println(i + "------------------" + pos + "--------------totalLen=" + totalLen);
            System.out.println(new String(block));

            i = pos + 1;
            if (i > totalLen) break;
        }

    }

    @Test
    public static void testLength() throws Exception {
        String code = "QKxqb9nVMRlUaZVRzn7JIC5v1eWBwlJ5sd9mgC2x06QH2/3/1vzvxUj4GGIRek6Vixleg+AX2E+q\n" +
                "VG0D0d/CcRNtMdgi0uB7QU1SZvUJV6OMp5EvD18dvOFS88C05GZgdr24MXuH0RiZ4vIGBssarpzW\n" +
                "cbyyZFdaSwewrWFWlGU=";
        System.out.println("------------length:" + code.getBytes().length);

    }

    @Test
    public static void testUrl() throws Exception {
        String url = "/\\S+[^.].jhtml";
        String requestURI = "/jbbs/ueditorcontroller.js.jhtml";

        boolean out = !requestURI.equals("*") && requestURI.matches(url);
        //  String  txt = java.nio.file.Files.probeContentType(java.nio.file.Paths.get("e:\\20150622_155508.jpg"));
        System.out.println("---------------------------" + out);
    }

    @Test
    public static void testmatchesUrl() throws Exception {
        String url = "\\S[^\\.\\w].jhtml";
        String requestURI = "/jbbs/ueditorcontroller.js.jhtml";

        boolean out = !requestURI.equals("*") && requestURI.matches(url);
        //  String  txt = java.nio.file.Files.probeContentType(java.nio.file.Paths.get("e:\\20150622_155508.jpg"));
        System.out.println("---------------------------" + out);
    }


    @Test
    public static void testHostURL() throws Exception {
        String host = URLUtil.getHostURL("http://192.168.0.200:8080/manage/main.jhtml");
        Assert.assertEquals("http://192.168.0.200:8080", host);

        String host2 = URLUtil.getHostURL("http://jspx.net/manage/main.jhtml");
        Assert.assertEquals("http://jspx.net", host2);

    }

    @Test
    public static void testPacketUtil() throws Exception {

        String str = "http://192.168.0.200:8080/manage/main.jhtml";
        System.out.println(EncryptUtil.getMd5(str));

        System.out.println(PacketUtil.getHash(str));


    }

    @Test
    public static void testPacketUtil2() throws Exception {
        String key = "19x936y6577a882x";
        String txt = "今天给大家带来的这一款就是宝沃BX5,2017款25TGDI自动四驱尊贵型,下面我们就来看看这位车主在日常用车过程中,都遇到了哪些满意的地方和哪些不满意的地方。同级别关注度排名第77名，高于同级别车综合评分4.88%2309184302";
        Character exp = 'K';
        String sendTxt = PacketUtil.getEncodePacket(txt, exp, PacketUtil.Charset_UTF_8, PacketUtil.ZIP, SymmetryEncryptFactory.AES, key);

        System.out.println("发送的数据:" + sendTxt);

        String data = PacketUtil.getDecodePacket(sendTxt, key);

        System.out.println(data);

        Character expOut = PacketUtil.getPacketExtended(sendTxt);
        Assert.assertEquals(txt, data);

        Assert.assertEquals(exp, expOut);

    }

    @Test
    public static void testBase64() throws Exception {
        String txt = "QzAoY29tLmdpdGh1Yi5qc3B4bmV0LnJwYy5tb2RlbC5Jb2NSZXNwb25zZZMJcmVxdWVzdElkBWVycm9yBnJlc3VsdGBOTjAwY29tLmdpdGh1Yi5qc3B4bmV0LnJwYy5IZWxsb1NlcnZpY2VJbXBsQDRiN2ZkMTdl";

        System.out.println("发送的数据:" + ObjectUtil.getUnSerializable(EncryptUtil.getBase64Decode(txt)));


    }


    @Test
    public static void testPacketUtil3() {
        String json = "{\"cmd\": \"requestKey\",\"publicKey\": \"30819f300d06092a864886f70d010101050003818d003081890281810094a99b6de6b85c68dc5aa9247357e131353f1fe3690dd6bfaf0ef1f10e1736617c3a49b1554585bb7d318af30ac9cd7b9384ca35699cd328414531c68a8bf6d3eee395b6bd99c856879864f1e58a2b5a0810b5989fdabcdd9e5c124ff6713d280a78e5094ab93c5a4df557f6835fa92760e66f9a39401f1fdc3768d2ae8cb7130203010001\"}";
        String sendTxt = PacketUtil.getEncodePacket(json, ' ', PacketUtil.Charset_UTF_8, PacketUtil.ZIP, SymmetryEncryptFactory.Encrypt_NONE, "");
        try {
            String sendTxtDes = PacketUtil.getDecodePacket(sendTxt, "12342424");
            System.out.println(sendTxtDes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sendTxt);

        //  ClientReceiveDispatcher clientReceiveDispatcher = ClientReceiveDispatcher.getInstance();
        //  clientReceiveDispatcher.setJSONObject(new JSONObject(json));
    }

    @Test
    public static void testPacketUtil4() throws Exception {
        String json = "{\n" +
                "    \"suc\": 0,\n" +
                "    \"msg\": \"验证错误\",\n" +
                "    \"resCmd\": \"requestKey\",\n" +
                "    \"cmd\": \"response\"\n" +
                "}";
        String sendTxt = PacketUtil.getEncodePacket(json);
        System.out.println(sendTxt);
        String sendTxtDes = PacketUtil.getDecodePacket(sendTxt, "123424246574567445634564564");

        Assert.assertEquals(json, sendTxtDes);
//100ee49 H4sIAAAAAAAAAKvmUgACpeLSZCUrBQMdCC+3OB3IU3q5qufF+saXU2a+WL9eCSpVlFrsnJsCki1KLSxNLS7xTq2EySXDJIoL8vOKU5W4agG8jWZWXgAAAA==
//110a0e0098vqny0myRBCjxzcV4T3riW/O63V2Hj/bIIJ3u4k2DZDT3argetCCx0wfOgDDRXyN5aWN0Rnq9M4qvmVRH1EJsI0f9dWgBAUGWPq30nZyVTCV0ghDMBaE+bedDd/vFpK
//
        String xx = PacketUtil.getDecodePacket("010a0e0098vqny0myRBCjxzcV4T3riW/O63V2Hj/bIIJ3u4k2DZDT3argetCCx0wfOgDDRXyN5aWN0Rnq9M4qvmVRH1EJsI0f9dWgBAUGWPq30nZyVTCV0ghDMBaE+bedDd/vFpK", "");
        System.out.println(xx);
    }

    @Test
    public static void testTestSecretKey() throws Exception {
        String txt = "{\n" +
                "    \"suc\": 1,\n" +
                "    \"cipherIv\": \"1e8785c850e74886c9bc14572eadb421c34897ae7f6cf5f93173fe06358e86f7422fe3dc73987c157775c7d8e996b8213dd45003ce9367fd9e875a42ea55fd3f7efc713fc45d8ec80cb4e2baad7d016bdc67ce5eae9a6a1748ddce111b83f57db53fb5436c1995474e39ec5d0865a39bd4e0b3beb033416e551587f02c337e00\",\n" +
                "    \"secretKey\": \"1160d0bfbfe65b1b874a393762a991903edd9d7f90a8579b5160045d81e4d9f2bf70e14c0d4cc19cbad6133e64225cefa636a3be3a007e28e6dd582729c14faa7743794906cfab2b14c23ae035f4955e12a040871926c776dfd866fdb24beea0b8e3bff5b3a29a0562c7277bc9f32aa02e44f3fdf9439632da5030431b1bde69\",\n" +
                "    \"resCmd\": \"requestKey\",\n" +
                "    \"sign\": \"2b6b5705d1c9dbfafe12f3c62bedd7bd042e7cd92342d3ae3d96a8f0e5e2fa02b3a9960886ee88fc1dc6ae9b3cd0d981acc0621646ce3ed553546997d26d81aa11df9da964e7850fa29519d9c44710ce4514aecd69a28d5068b51f5b88ee39f52c64f1b6057e1ffbdcc201298b89caa658073245b714b7465857fdc4f9f9d8ec\",\n" +
                "    \"cmd\": \"secretKey\",\n" +
                "    \"signAlgorithm\": \"MD5withRSA\"\n" +
                "}";


        /*
{
    "suc": 1,
    "cipherIv": "1e8785c850e74886c9bc14572eadb421c34897ae7f6cf5f93173fe06358e86f7422fe3dc73987c157775c7d8e996b8213dd45003ce9367fd9e875a42ea55fd3f7efc713fc45d8ec80cb4e2baad7d016bdc67ce5eae9a6a1748ddce111b83f57db53fb5436c1995474e39ec5d0865a39bd4e0b3beb033416e551587f02c337e00",
    "secretKey": "1160d0bfbfe65b1b874a393762a991903edd9d7f90a8579b5160045d81e4d9f2bf70e14c0d4cc19cbad6133e64225cefa636a3be3a007e28e6dd582729c14faa7743794906cfab2b14c23ae035f4955e12a040871926c776dfd866fdb24beea0b8e3bff5b3a29a0562c7277bc9f32aa02e44f3fdf9439632da5030431b1bde69",
    "resCmd": "requestKey",
    "sign": "2b6b5705d1c9dbfafe12f3c62bedd7bd042e7cd92342d3ae3d96a8f0e5e2fa02b3a9960886ee88fc1dc6ae9b3cd0d981acc0621646ce3ed553546997d26d81aa11df9da964e7850fa29519d9c44710ce4514aecd69a28d5068b51f5b88ee39f52c64f1b6057e1ffbdcc201298b89caa658073245b714b7465857fdc4f9f9d8ec",
    "cmd": "secretKey",
    "signAlgorithm": "MD5withRSA"
}

        String data = "1160d0bfbfe65b1b874a393762a991903edd9d7f90a8579b5160045d81e4d9f2bf70e14c0d4cc19cbad6133e64225cefa636a3be3a007e28e6dd582729c14faa7743794906cfab2b14c23ae035f4955e12a040871926c776dfd866fdb24beea0b8e3bff5b3a29a0562c7277bc9f32aa02e44f3fdf9439632da5030431b1bde69";
        String sign = "2b6b5705d1c9dbfafe12f3c62bedd7bd042e7cd92342d3ae3d96a8f0e5e2fa02b3a9960886ee88fc1dc6ae9b3cd0d981acc0621646ce3ed553546997d26d81aa11df9da964e7850fa29519d9c44710ce4514aecd69a28d5068b51f5b88ee39f52c64f1b6057e1ffbdcc201298b89caa658073245b714b7465857fdc4f9f9d8ec";
        RSAEncrypt encrypt = new RSAEncrypt();
        boolean b = encrypt.verify(data, jspx.jpoker.netty.client.cmd.receive.SecretKey.publicKey,sign);
        byte[] dataX = encrypt.decryptByPublicKey(EncryptUtil.hexToByte(data),EncryptUtil.hexToByte(jspx.jpoker.netty.client.cmd.receive.SecretKey.publicKey));
        System.out.println(b);
        System.out.println(new String(dataX,"UTF-8"));
         */
    }

/*
    @Test
    public static void  testsecretKey() throws Exception {
        SecretKey key =new SecretKey();
        System.out.println("-------------key=" + key);
        SecretKey obj = (SecretKey)SendCommandFactory.createCommand(SecretKey.cmd);
        System.out.println("-------------obj=" + obj);
    }
 */

    /*
    {
    "suc": 1,
    "cipherIv": "1e8785c850e74886c9bc14572eadb421c34897ae7f6cf5f93173fe06358e86f7422fe3dc73987c157775c7d8e996b8213dd45003ce9367fd9e875a42ea55fd3f7efc713fc45d8ec80cb4e2baad7d016bdc67ce5eae9a6a1748ddce111b83f57db53fb5436c1995474e39ec5d0865a39bd4e0b3beb033416e551587f02c337e00",
    "secretKey": "4b01baf249655b00696442dcf69ebecf9446a91fe66ab8662cb8817215b5a8f407650bf4924a4ff34929f1dc95a9944d7005d636b706cb66c4050092a6c84899b257b69bd53920b2229f316b7771a04b7447aa5a6f9c6b26b4f581a74d259d5dae10a5c8af93748e79cbc899fcf4b8b8e07cadc0b48a49a5474424da110e18fb",
    "resCmd": "requestKey",
    "sign": "4792cb062c86051eb814bc32b477c5931c98fc6e10bd59e0c95c7d2469a50c837eb9b5c6ce6cf94dfc9d27df802b48c6da1ef7fb4d8ba75370d0afe202d19c542036eadbe7d661ba43c5061711f3f24dcadf2f767aba3673d377c28a8200e128de4a4362a1cb5ff85b4f4e345ada4b212afaa593290b323d4cc2f227fbea07e5",
    "cmd": "secretKey",
    "signAlgorithm": "MD5withRSA"
   }
     */

    @Test
    public static void getMobileFileName() {
        String html = "http://www.gywlfz.com/jcms/upload/2019/jpg/FangZhouQianQiWuYeGuanLiHeTong01.jpg";
        System.out.println("-----------html=" + FileUtil.getMobileFileName(html));
    }

    @Test
    public static void make16() {

        for (int i = 0; i < 1000; i++) {
            long pid = RandomUtil.getRandomLong(RandomUtil.getRandomInt(1, 14));
            long id = RandomUtil.getRandomLong(RandomUtil.getRandomInt(1, 14));
            if (id <= 0) continue;
            String str = QRCodeUtil.createCode(pid, id);
            System.out.println(id + "----createQRCode-------pid=" + pid);
            System.out.println(str + "----2-------verify=" + (QRCodeUtil.verifyCode(str, pid, id)));
            Assert.assertEquals((QRCodeUtil.verifyCode(str, pid, id) && !StringUtil.isNull(str)), true);
        }
    }

    @Test
    public static void make1() {

        for (int i = 0; i < 1000; i++) {
            long id = RandomUtil.getRandomLong(RandomUtil.getRandomInt(1, 14));
            if (id <= 0) continue;
            String str = QRCodeUtil.createCode(id);
            System.out.println(id + "----createQRCode-------str=" + str);
            System.out.println(str + "----createQRCode-------verify=" + (QRCodeUtil.verifyCode(str, id)));
            Assert.assertEquals(QRCodeUtil.verifyCode(str, id), true);
        }
    }

    @Test
    public static void createQRCode() throws Exception {

        long id = RandomUtil.getRandomLong(RandomUtil.getRandomInt(1, 14));
        String str = QRCodeUtil.createCode(id);
        System.out.println(id + "----createQRCode-------str=" + str);
        QRCodeUtil.encode(str, "d://temp/txt.jpg");
        String inCode = QRCodeUtil.decode("d://temp/txt.jpg");
        System.out.println(id + "----createQRCode-------inCode=" + inCode);
        Assert.assertEquals(inCode, str);

    }

    @Test
    public static void getHostName() throws NoSuchMethodException {
        String url = "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9046915371243383544%22%7D&n_type=0&p_from=1";
        String hostName = URLUtil.getTopDomain(url);
        Assert.assertEquals(hostName, "baidu.com");
    }


    @Test
    public static void getSubdomainPrefix() {
        String url = "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9046915371243383544%22%7D&n_type=0&p_from=1";
        String sub = URLUtil.getSubdomainPrefix(url);

        Assert.assertEquals(sub, "mbd");
    }

    @Test
    public static void getSubdomainPrefix2() {
        String url = "123456789.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9046915371243383544%22%7D&n_type=0&p_from=1";
        String sub = URLUtil.getSubdomainPrefix(url);
        System.out.println("----sub-------=" + sub);
        Assert.assertEquals(sub, "123456789");
    }

    @Test
    public static void makeTest() {


        String[] arr1 = {"1", "2", "3", "4", "5"};
        int[] arr2 = {1, 2, 3, 4, 5};
        Object[] arr3 = new Object[10];
        Float[] arr4 = new Float[]{(float) 1, (float) 2, (float) 3, (float) 4.3, (float) 5.2};
        String notArr = "not array";

        //判断是否为数组
        System.out.println(arr1.getClass().isArray());
        System.out.println(arr2.getClass().isArray());
        System.out.println(arr3.getClass().isArray());
        System.out.println(arr4.getClass().isArray());
        System.out.println(notArr.getClass().isArray());
        //获取数组元素类型名称
    }

    @Test
    public static void testIsIp() {
        String ipStr = "127.0.0.1";
        Assert.assertEquals(IpUtil.isIpv4(ipStr), true);
        ipStr = "192.168.0.14";
        Assert.assertEquals(IpUtil.isIpv4(ipStr), true);
        ipStr = "255.255.255.255";
        Assert.assertEquals(IpUtil.isIpv4(ipStr), true);
        ipStr = "0.0.0.0";
        Assert.assertEquals(IpUtil.isIpv4(ipStr), true);
    }

    //6421519----createQRCode-------pid=816409804
    //        1-------------code=77BCDAFF8C6
//

    @Test
    public static void testIsIp6() {
        String ipStr = "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789";
        Assert.assertEquals(IpUtil.isIpv6(ipStr), true);
        ipStr = "2000:0000:0000:0000:0001:2345:6789:abcd";
        Assert.assertEquals(IpUtil.isIpv6(ipStr), true);
        ipStr = "2000:0:0:0:0:0:0:1";
        Assert.assertEquals(IpUtil.isIpv6(ipStr), true);
        ipStr = "::0:0:0:0:0:0:1";
        Assert.assertEquals(IpUtil.isIpv6(ipStr), true);
        ipStr = "2000:0:0:0:0::";
        Assert.assertEquals(IpUtil.isIpv6(ipStr), true);
    }

    @Test
    public static void getMd5() {
        String str = "a:1:{i:0;a:18:{s:7:\"cart_id\";i:1394;s:12:\"created_time\";i:1542268806;s:16:\"image_default_id\";s:85:\"http://www.modoopark.com/images/7f/f3/4b/a9008716ca7bf1315a2b678f72ab53d6314722d2.jpg\";s:10:\"is_checked\";s:1:\"1\";s:7:\"item_id\";i:22;s:13:\"modified_time\";i:1542268814;s:9:\"obj_ident\";s:7:\"item_46\";s:8:\"obj_type\";s:4:\"item\";s:10:\"package_id\";N;s:6:\"params\";N;s:8:\"quantity\";d:1;s:18:\"selected_promotion\";s:1:\"0\";s:7:\"shop_id\";i:2;s:6:\"sku_id\";i:46;s:5:\"title\";s:90:\"心缘堂琉璃禅意迷你茶宠摆件茶席配件装饰品趣味创意工艺品小和尚\";s:7:\"type_id\";s:1:\"0\";s:7:\"user_id\";i:14;s:10:\"user_ident\";s:32:\"aab3238922bcc25a6f606eb525ffdc56\";}}";
        System.out.println(EncryptUtil.getMd5(str));
    }


    @Test
    public static void getScanFile() throws IOException {
        String findPath = "Upload*.*";
        String path = "D:\\website\\webapps\\root\\WEB-INF\\classes";
        File[] fileArray = FileUtil.getPatternFiles(path, findPath);
        for (File f : fileArray) {
            System.out.println(f.getAbsolutePath());
        }

    }

    @Test
    public static void getLoadFile() throws IOException {

        String findPath = "Upload*.*";
        String path = "D:\\website\\webapps\\root\\WEB-INF\\classes";
        File[] fileArray = FileUtil.getPatternFiles(path, findPath);
        for (File f : fileArray) {
            System.out.println(f.getAbsolutePath());
        }
    }

    @Test
    public static void arrayIsNull() {

        String[] xx = new String[0];
        String[] one = StringUtil.split(null, ";");
        System.out.println("--------------xx=" + xx.length);
        System.out.println("--------------ArrayUtil.isEmpty(xx)=" + ArrayUtil.isEmpty(xx));
        System.out.println("--------------ArrayUtil.isEmpty(one)=" + ArrayUtil.isEmpty(one));
    }

    @Test
    public static void testIsBase64() {
        String str = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJSpm23muFxo3FqpJHNX4TE1Px_jaQ3Wv68O8fEOFzZhfDpJsVVFhbt9MYrzCsnNe5OEyjVpnNMoQUUxxoqL9tPu45W2vZnIVoeYZPHliitaCBC1mJ_avN2eXBJP9nE9KAp45QlKuTxaTfVX9oNfqSdg5m-aOUAfH9w3aNKujLcTAgMBAAECgYAi-nUzuqGOPK38Nuf4q6i0p06e9ib1tp2LVvzeNu6HQRC1tjkfUyhQIPjTNLRJHywAXbImNx9LH6Gb4kZQuvXsM-eyxG105Mnh87oiVlsQyinNk2ycBvGqKitb2-_TwU0gtCXtrDjyd3M1w18YpS1NS7mQX9Ad0wNsRCuvHv6RwQJBAMg5nnyk7fdQe5ZVDMiELr-y8rHzhYWbcVSIBYzTvQqz3KhrGog7mlK7wOF_BRdFYAOT9uxUSypfthzbLouFnRsCQQC-EvpHksWtlRvrSHCPfHiymDFigS-GHGyOiNfY7B9LE4XqZdjxQ65f7myqtwt1x64-3DLLxPzfesPk5aR9pUVpAkAlpT-5K8FaOJWkBEWtBb1Mabbb4m9-WWWLgS-Z9M2cT4jzWX_ZkWtIluiC_UDVyUNuKMnKUWb4hppU_pjXvr11AkB21cqgoQTyR71S1tF5BCs6Dakimv4pbO_6FVhOCSJvf99D8zU1ckk9NQW_nf2OP6-TIXyopZor5sc-_sxeucuxAkBDCeEvs5Tml10_xQuZb1cz8CpxoC5nkcrPYLU3UglwtzSI8l5Ga8Y1qS-ruK2W1qKCi4ybpKqzsXSiz4yIibTa";
        boolean isBase = EncryptUtil.isBase64(str);
        Assert.assertEquals(isBase, true);

    }

    @Test
    public static void getPatternPath() throws IOException {
        String findPath = "LicenseService.class";
        String path = "D:\\website\\webapps\\root\\WEB-INF\\classes/jspx/jembed/";
        File[] fileArray = FileUtil.getPatternFiles(path, findPath);
        for (File f : fileArray) {
            System.out.println(f.getAbsolutePath());
        }
    }

    final static List list = new CopyOnWriteArrayList();

    @Test(threadPoolSize = 10, invocationCount = 3)
    public void testParam() {


        while (list.size() < 1000000) {
            String guid = RandomUtil.getRandomGUID(8);
            if (list.contains(guid)) {
                System.out.println("---------发生重复了------------------guid=" + guid);
                Assert.assertEquals(false, true);
                break;
            } else {
                list.add(guid);
                System.out.println(list.size() + "   guid= " + guid);
            }
        }

    }

    @Test
    public void ObjtoString() {

        System.out.println(ObjectUtil.toString(1));
        System.out.println(ObjectUtil.toString(new int[]{1, 2, 3, 4, 5}));
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        System.out.println(ObjectUtil.toString(list));

    }

    @Test
    public void testIsMail() {


        System.out.println(ValidUtil.isMail("432423@erew.com"));

    }

    @Test
    public void testSocketAddress() {
        InetSocketAddress socketAddress = new InetSocketAddress("xxx/127.0.0.1", 6775);
        System.out.println(IpUtil.getIp(socketAddress));
        System.out.println(socketAddress.toString());

    }

    @Test
    public void testRouteSession() {
        RouteSession routeSession = new RouteSession();
        //routeSession.setSocketAddress(new InetSocketAddress("xxx/127.0.0.3", 6775));
        routeSession.setOnline(1);
        JSONObject json = new JSONObject(routeSession);
        System.out.println(json.toString());

    }

    @Test
    public void testRouteSession2() {
        List<InetSocketAddress> list = new ArrayList<>();
        list.add(new InetSocketAddress("xxx/127.0.0.3", 6775));
        list.add(new InetSocketAddress("xxx/127.0.0.1", 6777));

        System.out.println(ObjectUtil.toString(list));

    }

    @Test
    public void testRouteSession3() {
        List<Date> list = new ArrayList<>();
        list.add(new Date());
        list.add(new Date());
        System.out.println(ObjectUtil.toString(list));

    }

    @Test
    public void testRead() throws Exception {

        String fileName = "f:\\住房.docx";

        String txt = WordToTextConverter.getText(new File(fileName));
        System.out.println(txt);
        AbstractRead abstractRead = new ReadWordTextFile();
        abstractRead.setFile(fileName);

        System.out.println(abstractRead.getContent());

    }

    @Test
    public void testRead2() throws Exception {

        String fileName = "f:\\住房.pdf";
        AbstractRead abstractRead = new ReadPdfTextFile();
        abstractRead.setFile(fileName);
        System.out.println(abstractRead.getContent());
    }

    @Test
    public void testCopyList() throws Exception {


        String fileName = "f:\\住房.pdf";
        AbstractRead abstractRead = new ReadPdfTextFile();
        abstractRead.setFile(fileName);
        System.out.println(abstractRead.getContent());
    }


    @Test
    public void testRead3() throws Exception {

        System.out.println(new JSONObject(RocResponse.success(new MemberRole())));
    }

    @Test
    public void testRead4() throws Exception {


        System.out.println(new JSONObject(RocResponse.success(new MemberRole())));
    }

    @AfterClass
    public void afterExit() {
        System.out.println("------------结束:");
    }


}
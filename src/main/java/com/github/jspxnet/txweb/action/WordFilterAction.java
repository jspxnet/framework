package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.impl.DFAFilterImpl;
import com.github.jspxnet.txweb.table.BlockedWord;
import com.github.jspxnet.txweb.view.WordFilterView;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by yuan on 2015/6/16 0016.
 */
@HttpMethod(caption = "屏蔽关键词")
public class WordFilterAction extends WordFilterView {

    private String encode = Environment.defaultEncode;

    public WordFilterAction() {

    }

    @Param(caption = "编码")
    public void setEncode(String encode) {
        this.encode = encode;
    }

    //这里将会清空所有数据
    @Operate(caption = "导入文件")
    public void importFile(@Param(caption = "文件路径",required = true,max = 250) String[] fileName) throws Exception {

        DFAFilterImpl dfaFilter = (DFAFilterImpl) filter;
        int row = 0;

        if (dfaFilter.deleteAll()) {
            for (String f : fileName) {
                if (!FileUtil.isFileExist(f)) {
                    continue;
                }
                AbstractRead abstractRead = new AutoReadTextFile();
                abstractRead.setFile(f);
                abstractRead.setEncode(encode);
                String txt = abstractRead.getContent();
                if (StringUtil.isNull(txt)) {
                    continue;
                }
                row = row + dfaFilter.importWord(txt);
            }
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }
    }

    //不清空数据，累加方式
    @Operate(caption = "导入提交")
    public void importWord(@Param(caption = "文件路径",required = true,message = "数据不允许为空") String content) throws Exception {
        DFAFilterImpl dfaFilter = (DFAFilterImpl) filter;
        int row = dfaFilter.checkImportWord(content);
        if (row >= 0) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }
    }

    //不清空数据，累加方式
    @Operate(caption = "清空")
    public void delete() throws Exception {
        DFAFilterImpl dfaFilter = (DFAFilterImpl) filter;
        if (dfaFilter.deleteAll()) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            DFAFilterImpl dfaFilter = (DFAFilterImpl) filter;
            dfaFilter.evict(BlockedWord.class);
        }
        return super.execute();
    }
}

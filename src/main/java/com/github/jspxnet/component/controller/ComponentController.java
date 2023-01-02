package com.github.jspxnet.component.controller;

import com.github.jspxnet.component.ComponentEnv;
import com.github.jspxnet.component.zhex.phrase.Phrase;
import com.github.jspxnet.component.zhex.phrase.PhraseDictionary;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
<<<<<<< HEAD
import org.xml.sax.SAXException;
=======
>>>>>>> dev
import java.io.IOException;
import java.util.List;

@HttpMethod(caption = "sping方式", namespace = ComponentEnv.namespace)
@Bean(bind = ComponentController.class, namespace = ComponentEnv.namespace)
public class ComponentController extends ActionSupport {


    @Operate(caption = "成语字典")
    public List<Phrase> getPhraseList(@Param String find) throws IOException {
        return PhraseDictionary.getPhraseList(find);
    }
}

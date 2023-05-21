package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.SearchSchemeParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.SearchScheme;
import com.github.jspxnet.txweb.view.SearchSchemeView;
import com.github.jspxnet.utils.BeanUtil;

@HttpMethod(caption = "搜索条件")
public class SearchSchemeAction extends SearchSchemeView {

    public RocResponse<?> save(@Param(caption = "保存",required = true) SearchSchemeParam param)
    {
        if (isGuest())
        {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        IUserSession userSession = getUserSession();
        SearchScheme searchStore = BeanUtil.copy(param, SearchScheme.class);
        searchStore.setIp(getRemoteAddr());
        searchStore.setPutUid(userSession.getUid());
        searchStore.setPutName(userSession.getName());
        int x = 0;
        try {
            x = genericDAO.save(searchStore);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.saveFailure));
        }
        return RocResponse.success(x).setMessage(language.getLang(LanguageRes.saveSuccess));
    }

    public RocResponse<?> update(@Param(caption = "编辑",required = true) SearchSchemeParam param)
    {
        if (isGuest())
        {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        IUserSession userSession = getUserSession();
        SearchScheme searchScheme = BeanUtil.copy(param, SearchScheme.class);
        searchScheme.setId(param.getId());
        searchScheme.setIp(getRemoteAddr());
        searchScheme.setPutUid(userSession.getUid());
        searchScheme.setPutName(userSession.getName());
        int x = 0;
        try {
            x = genericDAO.update(searchScheme);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.updateFailure));
        }
        return RocResponse.success(x).setMessage(language.getLang(LanguageRes.updateSuccess));
    }

    public RocResponse<?> delete(@Param(caption = "删除",required = true) SearchSchemeParam param)
    {
        if (isGuest())
        {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        SearchScheme searchScheme = BeanUtil.copy(param, SearchScheme.class);
        int x;
        try {
            x = genericDAO.delete(searchScheme);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.deleteFailure));
        }
        return RocResponse.success(x).setMessage(language.getLang(LanguageRes.deleteSuccess));
    }
}

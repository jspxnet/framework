package com.github.jspxnet.txweb.util;

import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.TipStatusEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.txweb.model.dto.TipDto;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * web方式的提示暂存,解决前后端分离后，异步提示的问题
 * 前段只需要通过 messageId 就可以得到信息
 */
public final class TipUtil {
    public static final String PREFIX = "TIP_";
    private TipUtil() {

    }

    /**
     * 避免缓存key 冲突
     * @param id 消息id
     * @return 返回缓存KEY
     */
    public static String getKey(String id)
    {
        if (!id.startsWith(PREFIX))
        {
            return PREFIX + id;
        }
        return id;
    }

    public static TipDto createTipFactory(String messageId, String message,TipStatusEnumType statusEnum,float percent)
    {
        TipDto dto = new TipDto();
        //放入原ID
        dto.setId(messageId);
        if (statusEnum==null)
        {
            dto.setStatus(TipStatusEnumType.RUNING.getValue());
        } else
        {
            dto.setStatus(statusEnum.getValue());
        }
        dto.setSuccess(YesNoEnumType.YES.getValue());
        dto.setPercent(percent);
        dto.setMessage(message);
        return dto;
    }

    /**
     * 放入单个提示信息
     * @param messageId 消息id
     * @param dto 提示信息
     */
    public static void pubSingleTip(String messageId, TipDto dto)
    {
        if (StringUtil.isNull(messageId))
        {
            return;
        }
        String key = getKey(messageId);
        JSCacheManager.put(TipDto.class,key,dto);
    }

    /**
     * 放入到
     * @param messageId 消息id
     * @param dto  提示信息
     */
    public static void putTipList(String messageId, TipDto dto)
    {
        if (StringUtil.isNull(messageId) || dto==null)
        {
            return;
        }
        String key = getKey(messageId);
        List<TipDto> list = null;
        Object obj = JSCacheManager.get(TipDto.class,key);
        if (obj==null)
        {
            list = new ArrayList<>();
        }
        if (obj instanceof TipDto)
        {
            list = new ArrayList<>();
            list.add((TipDto)obj);
        }
        if (obj instanceof Collection)
        {
            list = (List<TipDto>)obj;
        }
        if (list==null)
        {
            list = new ArrayList<>();
        }
        dto.setSort(list.size()+1);
        list.add(dto);
        JSCacheManager.put(TipDto.class,key,list);
    }

    /**
     *
     * @param messageId  消息id
     * @return  得到内存对象，不区分类型
     */
    public static Object getMessage(String messageId) {
        String key = getKey(messageId);
        return JSCacheManager.get(TipDto.class,key);
    }

    /**
     *
     * @param messageId  消息id
     * @return 返回单个提示
     */
    public static TipDto getSingleTip(String messageId) {
        Object obj = getMessage(messageId);
        if (obj==null)
        {
            TipDto dto = new TipDto();
            dto.setId(messageId);
            dto.setStatus(TipStatusEnumType.UNKNOWN.getValue());
            dto.setSuccess(YesNoEnumType.NO.getValue());
            dto.setMessage(TipStatusEnumType.UNKNOWN.getName());
            return dto;
        }
        if (obj instanceof Collection)
        {
            List<TipDto> list = (List<TipDto>)obj;
            if (!ObjectUtil.isEmpty(list))
            {
                //返回最后一个
                return list.get(list.size()-1);
            }
        }
        return (TipDto)obj;
    }

    /**
     *
     * @param messageId 消息id
     * @return  返回列表
     */
    public static List<TipDto> getTipList(String messageId) {
        Object obj = getMessage(messageId);
        if (obj==null)
        {
            TipDto dto = new TipDto();
            dto.setId(messageId);
            dto.setStatus(TipStatusEnumType.UNKNOWN.getValue());
            dto.setSuccess(YesNoEnumType.NO.getValue());
            dto.setMessage(TipStatusEnumType.UNKNOWN.getName());
            List<TipDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }
        if (obj instanceof TipDto)
        {
            List<TipDto> list = new ArrayList<>();
            list.add((TipDto)obj);
            return list;
        }
        return  (List<TipDto>)obj;
    }
}

package com.github.jspxnet.txweb.devcenter.service;

import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.model.dto.SchedulerRegisterDto;
import com.github.jspxnet.txweb.result.RocResponse;
import java.util.Collection;
import java.util.List;

public interface SchedulerTaskService {
    /**
     * 注册
     * @param dto 注册
     */
    void register(SchedulerRegisterDto dto);
    /**
     *
     * @return 得到注册列表
     */
    Collection<SchedulerRegisterDto> getRegisterList();

    /**
     *
     * @return 刷新所有
     * @throws Exception 异常
     */
    RocResponse<?> refreshAll() throws Exception;

    RocResponse<?> forceRun(String id, String[] guids) throws Exception;

    RocResponse<?> start(String id, String[] guids) throws Exception;


    RocResponse<?> stop(String id, String[] guids) throws Exception;

    RocResponse<?> runOne(String id, String[] guids) throws Exception;

    RocResponse<?>  updatePattern(String id, String guid, String pattern) throws Exception;

    /**
     *
     * @param id 服务器id
     * @param find 查询条件
     * @param currentPage 翻页
     * @param count  条数
     * @return 返回结果
     * @throws Exception 异常
     */
    RocResponse<List<SchedulerDto>> getSchedulerList(String id, String find, Integer currentPage, Integer count) throws Exception;

    /**
     *
     * @param id 注册服务id
     * @return 注册信息
     */
    SchedulerRegisterDto getSchedulerRegisterDto(String id);

    List<SchedulerDto> getTaskList(String find, Integer currentPage, Integer count);
}

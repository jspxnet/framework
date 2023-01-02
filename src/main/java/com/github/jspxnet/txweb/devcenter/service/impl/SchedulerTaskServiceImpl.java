package com.github.jspxnet.txweb.devcenter.service.impl;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.devcenter.service.SchedulerTaskService;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.model.dto.SchedulerRegisterDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.SchedulerTaskLog;
<<<<<<< HEAD
import com.github.jspxnet.util.TypeReference;
=======
import com.google.gson.reflect.TypeToken;

>>>>>>> dev
import java.util.*;

@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class SchedulerTaskServiceImpl implements SchedulerTaskService {

    //注册列表
    private final static Map<String,SchedulerRegisterDto> REGISTER_MAP = new HashMap<>();

    @Ref
    protected GenericDAO genericDAO;
    /**
     * 注册
     * @param dto 注册
     */
    @Override
    public void register(SchedulerRegisterDto dto)
    {
        if (!REGISTER_MAP.containsKey(dto.getId()))
        {
            REGISTER_MAP.put(dto.getId(),dto);
        }
    }


    /**
     *
     * @return 得到注册列表
     */
    @Override
    public Collection<SchedulerRegisterDto> getRegisterList()
    {
        return REGISTER_MAP.values();
    }

    /**
     * 刷新所有
     * @throws Exception  异常
     */
    @Override
    public void refreshAll() throws Exception {

        for (SchedulerRegisterDto dto:REGISTER_MAP.values())
        {
            String myUrl = dto.getSchedulerRegisterApi() + "/refresh.jwc";
            HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
            String actionResult = null;
            try {
                JSONObject json = new JSONObject();
                json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
                json.put("id",dto.getId());
                actionResult = httpClient.post(json);
            } catch (Exception e) {
                e.printStackTrace();
                SchedulerTaskLog log = new SchedulerTaskLog();
                log.setActionResult(actionResult);
                log.setUrl(myUrl);
                log.setErrorInfo(e.getMessage());
                log.setTitle("refreshAll");
                genericDAO.save(log);
            }
        }
    }

    /**
     * @param id 服务id
     * @param guids  任务guid
     * @throws Exception 异常
     */
    @Override
    public void forceRun(String id, String[] guids) throws Exception {
        if (id==null)
        {
            return ;
        }
        SchedulerRegisterDto dto = getSchedulerRegisterDto(id);
        if (dto==null)
        {
            return;
        }


        String myUrl = dto.getSchedulerRegisterApi() + "/forcerun.jwc";
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            JSONObject json = new JSONObject();
            json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
            json.put("id",id);
            json.put("guids",guids);
            actionResult = httpClient.post(json);
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("forceRun");
            genericDAO.save(log);
        }
    }

    /**
     * @param id 服务id
     * @param guids 任务guid
     * @throws Exception 异常
     */
    @Override
    public void start(String id, String[] guids) throws Exception {
        if (id==null)
        {
            return ;
        }
        SchedulerRegisterDto dto = getSchedulerRegisterDto(id);
        if (dto==null)
        {
            return;
        }

        String myUrl = dto.getSchedulerRegisterApi()  + "/start.jwc";
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            JSONObject json = new JSONObject();
            json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
            json.put("id",id);
            json.put("guids",guids);
            actionResult = httpClient.post(json);


                /*JSONObject json = new JSONObject(actionResult);
                RocResponse<Integer> response = json.parseObject(new TypeReference<RocResponse<Integer>>(){});
                if (response.getData()==0)
                {
                    continue;
                }*/
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("start");
            genericDAO.save(log);
        }
    }


    /**
     *
     * @param guids 任务guid
     * @throws Exception 异常
     */
    @Override
    public void stop(String id, String[] guids) throws Exception {
        if (id==null)
        {
            return ;
        }
        SchedulerRegisterDto dto = getSchedulerRegisterDto(id);
        if (dto==null)
        {
            return;
        }
        String myUrl = dto.getSchedulerRegisterApi()  + "/stop.jwc";
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            JSONObject json = new JSONObject();
            json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
            json.put("id",id);
            json.put("guids",guids);
            actionResult = httpClient.post(json);
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("stop");
            genericDAO.save(log);
        }
    }


    /**
     *
     * @param guids 任务guids
     * @throws Exception  异常
     */
    @Override
    public void runOne(String id, String[] guids) throws Exception
    {

        if (id==null)
        {
            return ;
        }
        SchedulerRegisterDto dto = getSchedulerRegisterDto(id);
        if (dto==null)
        {
            return;
        }
        String myUrl = dto.getSchedulerRegisterApi()  + "/runone.jwc";
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            JSONObject json = new JSONObject();
            json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
            json.put("id",id);
            json.put("guids",guids);
            actionResult = httpClient.post(json);
                /*JSONObject json = new JSONObject(actionResult);
                RocResponse<Integer> response = json.parseObject(new TypeReference<RocResponse<Integer>>(){});
                if (response.getData()==0)
                {
                    continue;
                }*/
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("runOne");
            genericDAO.save(log);
        }
    }




    /**
     *
     * @param id 服务id
     * @param guid 任务id
     * @param pattern  corn表达式
     * @throws Exception  异常
     */
    @Override
    public void updatePattern(String id, String guid, String pattern) throws Exception
    {

        if (id==null)
        {
            return ;
        }
        SchedulerRegisterDto dto = getSchedulerRegisterDto(id);
        if (dto==null)
        {
            return;
        }
        String myUrl = dto.getSchedulerRegisterApi()  + "/updatepattern.jwc";
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            JSONObject json = new JSONObject();
            json.put("schedulerRegisterToken",dto.getSchedulerRegisterToken());
            json.put("id",id);
            json.put("guid",guid);
            json.put("pattern",pattern);
            actionResult = httpClient.post(json);
                /*JSONObject json = new JSONObject(actionResult);
                RocResponse<Integer> response = json.parseObject(new TypeReference<RocResponse<Integer>>(){});
                if (response.getData()==0)
                {
                    continue;
                }*/
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("updatepattern");
            genericDAO.save(log);
        }
    }

    /**
     *
     * @param id 服务器id
     * @param find 查询条件
     * @param currentPage 翻页
     * @param count  条数
     * @return 返回结果
     * @throws Exception 异常
     */
    @Override
    public RocResponse<List<SchedulerDto>> getSchedulerList(String id, String find, Integer currentPage, Integer count) throws Exception
    {
        SchedulerRegisterDto registerDto = getSchedulerRegisterDto(id);
        if (registerDto==null)
        {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
        String myUrl = registerDto.getSchedulerRegisterApi()  + "/list/register.jwc";
        JSONObject param = new JSONObject();

        param.put("schedulerRegisterToken",registerDto.getSchedulerRegisterToken());
        param.put("find",find);
        param.put("currentPage",currentPage);
        param.put("count",count);

        HttpClient httpClient = HttpClientFactory.createRocHttpClient(myUrl);
        String actionResult = null;
        try {
            actionResult = httpClient.post(param);
            JSONObject json = new JSONObject(actionResult);
<<<<<<< HEAD
            return json.parseObject(new TypeReference<RocResponse<List<SchedulerDto>>>(){});
=======
            return json.parseObject(new TypeToken<RocResponse<List<SchedulerDto>>>(){});
>>>>>>> dev
        } catch (Exception e) {
            e.printStackTrace();
            SchedulerTaskLog log = new SchedulerTaskLog();
            log.setActionResult(actionResult);
            log.setUrl(myUrl);
            log.setErrorInfo(e.getMessage());
            log.setTitle("getSchedulerList");
            genericDAO.save(log);
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
    }

    /**
     *
     * @param id 注册服务id
     * @return 注册信息
     */
    @Override
    public SchedulerRegisterDto getSchedulerRegisterDto(String id)
    {
        if (id==null)
        {
            return null;
        }
        return REGISTER_MAP.get(id);
    }

}

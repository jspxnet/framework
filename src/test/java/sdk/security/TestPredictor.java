package sdk.security;

import com.github.jspxnet.utils.DateUtil;
import it.sauronsoftware.cron4j.Predictor;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2020/9/11 0:06
 * @description: jspbox
 **/
public class TestPredictor {

    @Test
    public static void testPredictor()
    {
        //表达式  开始时间
        Predictor predictor = new Predictor("* * * * *",new Date());

        for (int i=0;i<10;i++)
        {
            System.out.println("------------------" + DateUtil.toString(predictor.nextMatchingDate(),DateUtil.FULL_ST_FORMAT));
           // Thread.sleep(DateUtil.SECOND * 20);

        }


    }

}

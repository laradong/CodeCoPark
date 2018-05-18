package com.tencent.cd.pipeline.manager.service;

import com.tencent.cd.pipeline.manager.util.ConfigUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import runner.catagory.Middle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Category({Middle.class})
public class OperateServiceTest {
    private OperateService service = new OperateService();

    @Test
    public void testBatchOperate() {
        String skey = ConfigUtils.getValue("opt.skey");
        String operator = "lara";
        boolean isSync = true;
        String ips = "10.123.12.97";
        int timeout = 3000;
        String cmdContent = "date";

        Method batchOperate;
        try {
            batchOperate = OperateService.class.getDeclaredMethod("batchOperate",
                    String.class, String.class, boolean.class, String.class, int.class, String.class);
            batchOperate.setAccessible(true);
            String output = (String) batchOperate.invoke(service, skey, operator, isSync, ips, timeout, cmdContent);
            System.out.println(output);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

}

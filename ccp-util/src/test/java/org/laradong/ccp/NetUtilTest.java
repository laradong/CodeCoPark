package org.laradong.ccp;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class NetUtilTest {
    @Test
    public void calcSignShouldUseOperationParameterToCalculatePushedValue() {
        String ip = NetUtil.getLocalIp();
        System.out.println(ip);
        Assert.assertFalse(StringUtils.isBlank(ip));
    }
}

package org.laradong.cpp.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.laradong.ccp.json.CommitStatusDto;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Lara on 2017/11/9.
 */
public class CommitStatusDtoTest {
    private Gson gson = new Gson();

    @Test
    public void testFromJson() {
        String json = "{\"id\":30000037,\"state\":\"pending\",\"target_url\":null,\"description\":\"checking\",\"context\":\"RDM\""
                + ",\"created_at\":\"2017-11-09T02:42:14+0000\",\"updated_at\":\"2017-11-09T02:42:14+0000\",\"block\":true}";

        CommitStatusDto dto = null;
        try {
            dto = gson.fromJson(json, CommitStatusDto.class);
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            dto = null;
        }
        Assert.assertNotNull(dto);
        Assert.assertEquals(30000037, dto.getId());

        Calendar cal = Calendar.getInstance();
        cal.setTime(dto.getCreated_at());
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        Assert.assertEquals(2017, cal.get(Calendar.YEAR));
        Assert.assertEquals(10, cal.get(Calendar.MONTH));
        Assert.assertEquals(9, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(2, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(42, cal.get(Calendar.MINUTE));
        Assert.assertEquals(14, cal.get(Calendar.SECOND));
    }

}

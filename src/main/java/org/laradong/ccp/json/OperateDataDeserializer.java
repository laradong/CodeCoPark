package com.tencent.cd.pipeline.manager.dto.operate;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * OperateData的自定义反序列化类.
 *
 * <p>
 * 使用方法：描述
 * </p>
 *
 * <p>
 * 注意事项：描述
 * </p>
 *
 * @author laradong
 * @version 2017年5月9日 下午8:19:07
 * @since JDK1.8
 */
public class OperateDataDeserializer implements JsonDeserializer<OperateData> {

    private String getStringValue(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            return jsonObject.get(fieldName).getAsString();
        }
        return null;
    }

    private int getIntValue(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            return jsonObject.get(fieldName).getAsInt();
        }
        return 0;
    }

    private long getLongValue(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            return jsonObject.get(fieldName).getAsLong();
        }
        return 0L;
    }

    @Override
    public OperateData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        OperateData operateData = new OperateData();
        operateData.setTask_id(getStringValue(jsonObject, "task_id"));
        operateData.setInner_ip(getStringValue(jsonObject, "inner_ip"));
        operateData.setExc_status(getStringValue(jsonObject, "exc_status"));
        operateData.setExc_result(getStringValue(jsonObject, "exc_result"));
        operateData.setExc_msg(getStringValue(jsonObject, "exc_msg"));
        operateData.setStart_time(getStringValue(jsonObject, "start_time"));
        operateData.setEnd_time(getStringValue(jsonObject, "end_time"));
        operateData.setData(getStringValue(jsonObject, "data"));
        operateData.setCmd_channel(getStringValue(jsonObject, "cmd_channel"));
        operateData.setF_work_server(getStringValue(jsonObject, "f_work_server"));
        operateData.setTask_queue(getIntValue(jsonObject, "task_queue"));
        operateData.setRet_code(getIntValue(jsonObject, "ret_code"));
        operateData.setUse_time(getLongValue(jsonObject, "use_time"));

        // 特殊处理
        if (jsonObject.has("err_msg")) {
            JsonElement errMsg = jsonObject.get("err_msg");
            if (errMsg.isJsonObject()) {
                operateData.setErr_msg(errMsg.toString());
            } else {
                Gson gson = new Gson();
                operateData.setErr_msg(gson.toJson(errMsg));
            }
        }
        return operateData;
    }

}

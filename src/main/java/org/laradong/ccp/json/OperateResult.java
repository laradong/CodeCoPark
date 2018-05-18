package com.tencent.cd.pipeline.manager.dto.operate;

import java.util.List;

/**
 * 请求返回结果.
 *
 * @author laradong
 * @version 2017年3月17日 下午2:37:46
 * @since JDK1.8
 */
@SuppressWarnings({"MemberName", "ParameterName"})
public class OperateResult<T> {
    private int ret_code;// 返回值，200成功,其它参见返回码说明
    private int sub_code;
    private int record_cnt;
    private String err_msg; // 附加信息，如出错信息等
    private String call_chain;// 调用信息
    private List<T> data;// 具体返回内容

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public int getSub_code() {
        return sub_code;
    }

    public void setSub_code(int sub_code) {
        this.sub_code = sub_code;
    }

    public int getRecord_cnt() {
        return record_cnt;
    }

    public void setRecord_cnt(int record_cnt) {
        this.record_cnt = record_cnt;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getCall_chain() {
        return call_chain;
    }

    public void setCall_chain(String call_chain) {
        this.call_chain = call_chain;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


}

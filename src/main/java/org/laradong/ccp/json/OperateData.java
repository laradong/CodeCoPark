package com.tencent.cd.pipeline.manager.dto.operate;

/**
 * 请求返回结果的具体内容，具体到每个执行机器.
 *
 * @author laradong
 * @version 2017年3月17日 下午2:42:24
 * @since JDK1.8
 */
@SuppressWarnings({"MemberName", "ParameterName"})
public class OperateData {
    private int task_queue;
    private String task_id = ""; // 任务ID，异步请求时返回。
    private String inner_ip = ""; // 内网IP列表
    private String exc_status = ""; // 执行状态
    private String exc_result = ""; // 执行结果
    private String exc_msg = ""; // 执行返回消息
    private String err_msg; // 错误信息;
    private int ret_code;
    private String start_time;
    private String end_time;
    private long use_time;
    private String data;
    private String cmd_channel;
    private String f_work_server;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getInner_ip() {
        return inner_ip;
    }

    public void setInner_ip(String inner_ip) {
        this.inner_ip = inner_ip;
    }

    public String getExc_status() {
        return exc_status;
    }

    public void setExc_status(String exc_status) {
        this.exc_status = exc_status;
    }

    public String getExc_result() {
        return exc_result;
    }

    public void setExc_result(String exc_result) {
        this.exc_result = exc_result;
    }

    public String getExc_msg() {
        return exc_msg;
    }

    public void setExc_msg(String exc_msg) {
        this.exc_msg = exc_msg;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public long getUse_time() {
        return use_time;
    }

    public void setUse_time(long use_time) {
        this.use_time = use_time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public int getTask_queue() {
        return task_queue;
    }

    public void setTask_queue(int task_queue) {
        this.task_queue = task_queue;
    }

    public String getCmd_channel() {
        return cmd_channel;
    }

    public void setCmd_channel(String cmd_channel) {
        this.cmd_channel = cmd_channel;
    }

    public String getF_work_server() {
        return f_work_server;
    }

    public void setF_work_server(String f_work_server) {
        this.f_work_server = f_work_server;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

}

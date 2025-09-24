package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 任务进度实体类
 * 
 * @author system
 * @date 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("task_process")
public class TaskProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进程ID
     */
    @TableId(value = "process_id", type = IdType.AUTO)
    @JsonProperty("processId")
    private Integer processId;

    /**
     * 步骤名称
     */
    @TableField("step_name")
    @JsonProperty("stepName")
    private String stepName;

    /**
     * 步骤状态
     */
    @TableField("step_status")
    @JsonProperty("stepStatus")
    private String stepStatus;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 错误日志
     */
    @TableField("error_log")
    @JsonProperty("errorLog")
    private String errorLog;

    /**
     * 任务ID
     */
    @TableField("task_id")
    @JsonProperty("taskId")
    private Integer taskId;

    /**
     * 步骤状态枚举
     */
    public enum StepStatus {
        PENDING("待执行"),
        RUNNING("进行中"),
        COMPLETED("已完成"),
        FAILED("失败");

        private final String description;

        StepStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static StepStatus fromString(String status) {
            for (StepStatus s : StepStatus.values()) {
                if (s.description.equals(status)) {
                    return s;
                }
            }
            return PENDING;
        }
    }
}

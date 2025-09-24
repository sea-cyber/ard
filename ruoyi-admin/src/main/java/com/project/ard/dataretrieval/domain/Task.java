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
import java.time.OffsetDateTime;

/**
 * 任务实体类
 * 
 * @author system
 * @date 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（主键）
     */
    @TableId(value = "task_id", type = IdType.AUTO)
    @JsonProperty("taskId")
    private Integer taskId;

    /**
     * 任务描述
     */
    @TableField("task_description")
    @JsonProperty("taskDescription")
    private String taskDescription;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime createTime;

    /**
     * 任务状态（1-待执行，2-执行中，3-已完成，4-失败）
     */
    @TableField("status")
    @JsonProperty("status")
    private Integer status;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime endTime;

    /**
     * 运行时长
     */
    @TableField("run_duration")
    @JsonProperty("runDuration")
    private String runDuration;

    /**
     * 创建用户ID
     */
    @TableField("create_user")
    @JsonProperty("createUser")
    private Integer createUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime updateTime;

    /**
     * 进程ID
     */
    @TableField("process_id")
    @JsonProperty("processId")
    private Integer processId;

    /**
     * 任务类型（cube-立方体构建，timeseries-时序分析，convert-格式转换）
     */
    @TableField("task_type")
    @JsonProperty("taskType")
    private String taskType;

    // 分页相关字段（不映射到数据库）
    @TableField(exist = false)
    @JsonProperty("pageNum")
    private Long pageNum;

    @TableField(exist = false)
    @JsonProperty("pageSize")
    private Long pageSize;

    // 查询条件字段（不映射到数据库）
    @TableField(exist = false)
    @JsonProperty("queryTimeRange")
    private TimeRange queryTimeRange;

    // 内部类定义
    @Data
    public static class TimeRange {
        @JsonProperty("beginTime")
        private String beginTime;
        
        @JsonProperty("endTime")
        private String endTime;
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING(1, "待执行"),
        RUNNING(2, "执行中"),
        COMPLETED(3, "已完成"),
        FAILED(4, "失败");

        private final Integer code;
        private final String description;

        TaskStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TaskStatus fromCode(Integer code) {
            for (TaskStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        CUBE("cube", "立方体构建"),
        TIMESERIES("timeseries", "时序分析"),
        CONVERT("convert", "格式转换");

        private final String code;
        private final String description;

        TaskType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TaskType fromCode(String code) {
            for (TaskType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
}

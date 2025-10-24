package com.project.ard.dataretrieval.dto;

/**
 * 选中的数据信息
 */
public class SelectedData {
    
    /**
     * 数据ID
     */
    private String id;
    
    /**
     * 数据名称
     */
    private String name;
    
    /**
     * 数据来源
     */
    private String source;
    
    // 构造函数
    public SelectedData() {}
    
    public SelectedData(String id, String name, String source) {
        this.id = id;
        this.name = name;
        this.source = source;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
}


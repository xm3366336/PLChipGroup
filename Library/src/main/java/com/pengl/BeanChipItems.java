package com.pengl;

public class BeanChipItems {
    private String label;
    private Object data;

    public BeanChipItems(String label, Object data) {
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

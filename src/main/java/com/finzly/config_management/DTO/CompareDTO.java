package com.finzly.config_management.DTO;

import java.util.List;
import java.util.Map;

public class CompareDTO {
    private List<Map<String, Object>> result1;
    private List<Map<String, Object>> result2;

    public List<Map<String, Object>> getResult1() {
        return result1;
    }

    public void setResult1(List<Map<String, Object>> result1) {
        this.result1 = result1;
    }

    public List<Map<String, Object>> getResult2() {
        return result2;
    }

    public void setResult2(List<Map<String, Object>> result2) {
        this.result2 = result2;
    }



    public CompareDTO(List<Map<String, Object>> result1, List<Map<String, Object>> result2) {
        this.result1 = result1;
        this.result2 = result2;
    }
}

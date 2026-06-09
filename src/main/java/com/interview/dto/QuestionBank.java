package com.interview.dto;

import java.util.List;
import java.util.Map;

public class QuestionBank {

    private Map<String,
            Map<String,
                    List<QuestionItem>>> skills;

    public Map<String, Map<String, List<QuestionItem>>> getSkills() {
        return skills;
    }

    public void setSkills(
            Map<String, Map<String, List<QuestionItem>>> skills) {
        this.skills = skills;
    }
}
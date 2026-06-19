package com.elearning.backend.service;

import com.elearning.backend.dto.QuestionDTO;
import com.elearning.backend.dto.TestDetailDTO;
import com.elearning.backend.model.Question;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.Test;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestMapper {

    /**
     * Converts a Test entity into a TestDetailDTO
     * (SAFE for frontend – no circular refs, no sensitive data)
     */
    public TestDetailDTO toDto(Test entity) {

        TestDetailDTO dto = new TestDetailDTO();

        /* ===============================
           Core Test fields
        =============================== */
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDate(entity.getDate());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setStandard(entity.getStandard());
        dto.setReviewEnabled(entity.isReviewEnabled());

        /* ===============================
           Teacher info (flattened)
        =============================== */
        Teacher teacher = entity.getTeacher();
        if (teacher != null) {
            dto.setTeacherId(teacher.getId());
            dto.setTeacherName(teacher.getName());
            dto.setTeacherSubject(teacher.getSubject());
        }

        /* ===============================
           Questions (optional, safe)
        =============================== */
        List<Question> questions = entity.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            List<QuestionDTO> questionDtos = questions.stream()
                    .map(QuestionDTO::new) // constructor-based mapping
                    .collect(Collectors.toList());

            dto.setQuestions(questionDtos);
        }

        return dto;
    }
}

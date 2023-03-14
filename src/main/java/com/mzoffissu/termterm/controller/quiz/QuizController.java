package com.mzoffissu.termterm.controller.quiz;

import com.mzoffissu.termterm.dto.quiz.QuizDailyDto;
import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.response.success.QuizSuccessType;
import com.mzoffissu.termterm.service.quiz.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class QuizController {
    private final QuizService quizService;
    @GetMapping("/quiz/daily-quiz")
    public ResponseEntity getDailyQuiz(){
        List<QuizDailyDto> quizDailyDtoList = quizService.getDailyQuiz();
        return new ResponseEntity<>(DefaultResponse.create(QuizSuccessType.QUIZ_DAILY_SUCCESS, quizDailyDtoList), QuizSuccessType.QUIZ_DAILY_SUCCESS.getHttpStatus());
    }
}

package com.mzoffissu.termterm.service.quiz;

import com.mzoffissu.termterm.domain.term.Term;
import com.mzoffissu.termterm.dto.quiz.QuizDailyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mzoffissu.termterm.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    public List<QuizDailyDto> getDailyQuiz() {
        List<QuizDailyDto> result = new ArrayList<>();

        for(int i=0; i<5; i++) {
            List<Term> answerSheet = quizRepository.findRandomBy();
            List<String> options = new ArrayList<>();
            for(int j=0; j<3; j++) {
                options.add(answerSheet.get(j).getName().split(" :: ")[0]);
            }

            Random random = new Random();
            int randN = random.nextInt(3) + 1;
            long answerId = answerSheet.get(randN-1).getId();
            String description = answerSheet.get(randN-1).getDescription();

            QuizDailyDto quiz = QuizDailyDto.builder()
                    .quiz(options)
                    .answerId((long) randN)
                    .termId(answerId).description(description).build();
            result.add(i, quiz);
        }
        return result;
    }
}

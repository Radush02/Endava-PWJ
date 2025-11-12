package com.example.endavapwj.listeners;

import com.example.endavapwj.DTOs.JudgeResultMessage;
import com.example.endavapwj.repositories.SubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.example.endavapwj.util.JudgeQueueConfig;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JudgeResultListener {

    private final SubmissionRepository submissions;

    @RabbitListener(queues = JudgeQueueConfig.RESULT)
    @Transactional
    public void handleJudgeResult(JudgeResultMessage msg) {
        submissions.findById(msg.getSubmissionId()).ifPresent(sub -> {
            sub.setVerdict(msg.getVerdict());
            sub.setMaxTimeMs(msg.getMaxTimeMs());
            sub.setMaxMemoryKb(msg.getMaxMemoryKb());
            sub.setFinishedAt(Instant.now());
            submissions.save(sub);
        });
    }
}

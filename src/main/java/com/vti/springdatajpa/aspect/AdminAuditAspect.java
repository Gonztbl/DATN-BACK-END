package com.vti.springdatajpa.aspect;

import com.vti.springdatajpa.entity.AdminAction;
import com.vti.springdatajpa.repository.AdminActionRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminAuditAspect {

    private final AdminActionRepository adminActionRepository;

    @AfterReturning(pointcut = "execution(* com.vti.springdatajpa.controller.Admin*.*(..)) && (@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping))", returning = "result")
    public void logAdminAction(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            // Assume adminId = 1 or get from SecurityContext
            Integer adminId = 1; 

            AdminAction action = new AdminAction();
            action.setAdminId(adminId);
            action.setActionType(methodName);
            
            String targetType = className.replace("Admin", "").replace("Controller", "");
            action.setTargetType(targetType);
            
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // Try to extract ID if it's the first argument (common pattern in REST)
                if (args[0] instanceof Integer) {
                    action.setTargetId((Integer) args[0]);
                } else if (args[0] instanceof String) {
                    try {
                        action.setTargetId(Integer.parseInt((String) args[0]));
                    } catch (NumberFormatException ignored) {}
                }
                action.setMetadata("Args: " + Arrays.toString(args));
            } else {
                action.setMetadata("Executed " + methodName + " successfully.");
            }
            
            action.setCreatedAt(LocalDateTime.now());
            adminActionRepository.save(action);
        } catch (Exception e) {
            System.err.println("Error logging admin action: " + e.getMessage());
        }
    }
}

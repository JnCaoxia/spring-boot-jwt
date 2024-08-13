package murraco.controller.flowable;

import murraco.dto.TaskDTO;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/manager")
    public ResponseEntity<List<TaskDTO>> getManagerTasks(@RequestParam String manager) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(manager).list();

        return ResponseEntity.ok(tasks.stream()
                .map(task -> new TaskDTO(task.getId(), task.getName(), task.getAssignee()))
                .collect(Collectors.toList()));
    }


    @PostMapping("/approve")
    public ResponseEntity<String> approveTask(@RequestParam String taskId, @RequestParam boolean approved) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        taskService.complete(taskId, variables);
        ProcessEngines.getDefaultProcessEngine();

        return ResponseEntity.ok("Task " + taskId + " completed.");
    }
}

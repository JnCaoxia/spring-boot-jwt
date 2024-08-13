package murraco.controller.flowable;

import com.google.common.collect.Maps;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动 员工提交请假申请
 */
@RestController
@RequestMapping("/leave")
public class LeaveRequestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/start")
    public ResponseEntity<String> startLeaveRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", request.get("employee"));
        variables.put("manager", request.get("manager"));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveRequest", variables);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        taskService.complete(tasks.get(0).getId(), Maps.newHashMap());
        return ResponseEntity.ok("Process started with ID: " + processInstance.getId() + "task ID:" + tasks.get(0).getId());
    }
}

package murraco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDTO {
    private String id;
    private String name;
    private String assignee;

}

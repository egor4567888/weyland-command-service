package com.example.weyland.dto;

import com.example.weyland.model.Priority;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandRequest {
    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private Priority priority;

    @NotBlank
    @Size(max = 100)
    private String author;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?Z?$", message = "Invalid ISO 8601 time format")
    private String time;


}

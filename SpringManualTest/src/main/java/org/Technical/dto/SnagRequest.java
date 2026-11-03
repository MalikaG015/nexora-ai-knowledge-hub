
package org.Technical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class SnagRequest {

    @NotBlank(message = "Snag description must not be empty")
    @Size(
            min = 10,
            max = 1000,
            message = "Snag description must be between 10 and 1000 characters"
    )

    private String description;

    public SnagRequest() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.itmo2022_final_project.exceptions;

public class ErrorMessage {
        private String message;
        public ErrorMessage(String message) {
            this.message = message;
        }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

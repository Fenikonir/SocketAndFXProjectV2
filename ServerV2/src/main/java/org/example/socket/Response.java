package org.example.socket;

public record Response(boolean isCodeGame, String user, String password, int code) {
}

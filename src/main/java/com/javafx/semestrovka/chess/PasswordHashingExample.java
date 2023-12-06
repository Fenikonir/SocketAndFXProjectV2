//package com.javafx.semestrovka.chess;
//
//import org.mindrot.jbcrypt.BCrypt;
//
//public class PasswordHashingExample {
//
//    public static String hashPassword(String password) {
//        String salt = BCrypt.gensalt();
//        return BCrypt.hashpw(password, salt);
//    }
//
//    public static boolean checkPassword(String candidatePassword, String hashedPassword) {
//        return BCrypt.checkpw(candidatePassword, hashedPassword);
//    }
//}

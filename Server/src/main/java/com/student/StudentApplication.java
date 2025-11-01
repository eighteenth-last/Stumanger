package com.student;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.student.mapper")
public class StudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
        System.out.println("(✿◠‿◠) 学生管理系统启动成功  ٩(●̮̃•)۶   \n" +
                "\033[36m   ______\n"+
                "  / __/ /___ ____ _  ___ ____  ___ ____ ____     \n"+
                " _\\ \\/ __/ // /  ' \\/ _ `/ _ \\/ _ `/ -_) __/ \n"+
                "/___/\\__/\\_,_/_/_/_/\\_,_/_//_/\\_, /\\__/_/   \n"+
                "                             /___/\033[0m");
    }

}






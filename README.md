# 📅 Schedule Management App

> 일정을 관리할 수 있는 앱의 API 서버 개발
 일정을 등록, 조회, 수정, 삭제할 수 있는 RESTful API 기능 제공
> 


## 🏡 Project Overview

- 사용자가 할 일을 관리할 수 있도록 합니다.
- Spring Boot, JDBC 기반 구현
- 예상지 못한 입력이나 패턴을 방지하기 위해 유효성 검증 기능(@Valid) 적용
- 페이지네이션 적용


## ⚙️ Tech Stack

| Type | Tech |
| --- | --- |
| Framework | Spring Boot 3.4.3 |
| DB | MySQL |
| Build Tool | Gradle |
| Language | Java 17 |
| Validation | Jakarta Validation API |


## 📂 Project Structure
```java
C:.
└─scheduleapp
    │  ScheduleAppApplication.java
    │  structure.txt
    │
    ├─common
    │  ├─exception
    │  │      BadRequestException.java
    │  │      CustomException.java
    │  │      GlobalExceptionHandler.java
    │  │      ResponseStatusSetterAdvice.java
    │  │
    │  └─response
    │      │  ApiResponseDto.java
    │      │
    │      └─enums
    │              ErrorCode.java
    │              SuccessCode.java
    │
    ├─schedule
    │  ├─controller
    │  │      ScheduleController.java
    │  │
    │  ├─dto
    │  │  ├─request
    │  │  │      PageRequestDto.java
    │  │  │      ScheduleCreateDto.java
    │  │  │      ScheduleDeleteDto.java
    │  │  │      ScheduleUpdateDto.java
    │  │  │
    │  │  └─response
    │  │          PageResponseDto.java
    │  │          ScheduleDetailDto.java
    │  │          ScheduleDto.java
    │  │          ScheduleListDto.java
    │  │
    │  ├─entity
    │  │      Schedule.java
    │  │
    │  ├─repository
    │  │      JdbcTemplateScheduleRepository.java
    │  │      ScheduleRepository.java
    │  │
    │  └─service
    │          ScheduleService.java
    │          ScheduleServiceImpl.java
    │
    └─users
        ├─controller
        │      UsersController.java
        │
        ├─dto
        │  ├─request
        │  └─response
        ├─entity
        │      Users.java
        │
        ├─repository
        │      JdbcTemplateUserRepository.java
        │      UsersRepository.java
        │
        └─service
                UserServiceImpl.java
                UsersService.java
```

## 🌐 API Endpoints

[API 명세서](https://www.notion.so/API-1bb61a650f078021962bcffaf6b10d21?pvs=21)

**각 API가 수행하는 기능**

| Method | URL | Description |
| --- | --- | --- |
| `POST` | `/schedules` | 일정 등록 |
| `GET` | `/schedules` | 일정 목록 조회 (email+id 기본) |
| `GET` | `/schedules/{id}` | 상세 정보 조회 |
| `PUT` | `/schedules/{id}` | 정보 수정 |
| `DELETE` | `/schedules/{id}` | 정보 삭제 |



## 📊 ERD

![image](https://github.com/user-attachments/assets/50db3935-66e5-419e-af24-85dd16198f41)

# MiniDelivery     
자율주행 자동차를 활용한 배달 서비스<br><br><br>


## FE part
Customer APP
<br><br>


## Project Overview
![Project Overview](https://github.com/user-attachments/assets/6ae3f8b4-4460-4c45-b75a-b1926ffb1574)      



## 주요 적용 기술
- Kotlin을 이용한 소비자 및 업체측 배달 서비스 어플 제작<br><br>
- Springboot 프레임워크를 활용해 만든 서버로 데이터 전달 및 저장<br><br>
- JPA(Java Persistence API)를 이용하여 객체지향적 데이터베이스 작업 수행<br><br>
- Raspberry 4B를 활용한 센서 데이터 처리 및 차량 모터 제어<br><br>
- Raspberry picamera 모듈을 이용한 실시간 송출 화면 제작<br><br>
- 스레드를 활욜한 멀티태스킹 및 CPU 자원관리<br><br>
- opencv, opencvdnn을 이용한 객체 인식 및 주행 경로 추적<br><br>
<br><br><br>

## 참여 학생의 주요 이력
### 웹공학트랙 최은서 <br>
  JavaScript, Python 프로그래밍 기술보유, 웹 프론트 취업 희망

### 모바일소프트웨어트랙 도건우 <br>
  Kotlin/Java/Dart 및 Flutter, Springboot 기술 보유. 백엔드 업체 취업 희망 

### 빅데이터트랙 이종범 <br>
  Python, Java 프로그래밍 기술보유, 컴퓨터 비젼 계열 취업, 대학원 진학 희망 

### 모바일소프트웨어 김소룡 <br>
  데이터베이스 및 안드로이드 프로그래밍 기술 보유, 안드로이드 개발 업체 취업 희망

<br><br><br>


# For Study
#### lateinit (Late Initialization)
: non-null 타입의 프로퍼티를 초기화를 나중에 할 수 있도록 해주는 키워드

목적 <br>
클래스의 프로퍼티를 선언 시점에 초기화하지 않고, 나중에 초기화할 수 있게 함
주로 의존성 주입이나 설정이 필요한 객체에 사용됨
<br><br>

사용 조건 <br>
var (변경 가능한 변수)에만 사용 가능
non-null 타입에만 사용 가능
프리미티브 타입(Int, Boolean 등)에는 사용할 수 없음
<br><br>

장점 <br>
null 체크를 하지 않아도 됨
생성자에서 모든 프로퍼티를 초기화할 필요가 없음



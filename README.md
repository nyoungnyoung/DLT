# <img src="./upload/DBLT.png" width=50px; hegith=50px;/> "언제와? Don't be Late Together" 소개

> [ 약속관리 모바일 앱 서비스 ]  
> **“모임을 쉽고 간편하게~ 현재 친구 위치를 확인하고 재미있는 지각비 정산까지지!”**

<br />

- 약속 시간에 늦으면서 지금 가고 있다고 거짓말 한 친구가 있다.
- 모임 장소나 시간을 계속 다시 물어보는 친구가 짜증난 적이 있다.
- 지각비를 정하고 정산하고 싶지만 말하기 무안한 적이 있다.

### 이런분들을 위해 "언제와? Don't be Late Together" 서비스를 소개합니다!!

✔ 지각한 사람과 기다리는 사람 모두가 즐길 수 있는 약속 관리 애플리케이션 약속을 친구와 공유하며 효율적으로 관리할 수 있도록 합니다.  
✔ 약속 당일 도착지와 참여자들의 GPS 정보를 공유하여  지각자의 거짓말을 예방할 수 있습니다.  
✔ 지각비를 회수하는 메타버스 게임을 통해 먼저 도착한 사람이 지루한 대기시간을 즐겁게 보낼 수 있도록 합니다.  

<br /><br />

# Contents

1. [개발기간](#개발기간) <br/>
2. [팀원 및 담당파트](#팀원-및-담당파트) <br/>
3. [주요기능](#주요기능) <br/>
4. [서비스 화면](#서비스-화면) <br/>
5. [실행방법](#실행방법) <br/>
6. [기술스택 및 협업툴](#기술스택-및-협업툴) <br/>
7. [시스템 아키텍처](#시스템-아키텍처) <br/>
8. [ERD](#ERD) <br/>
9. [일정](#일정) <br/>
10. [API 명세서](#API-명세서) <br/>
11. [협업 툴](#협업-툴) <br/>

<br /><br />

## 📅개발기간

2023.04.10 ~ 2023.05.19 (40일)

<br /><br />

## 💻팀원 및 담당파트  


**김부경(팀장/Backend)**

- EC2, Docker, Jenkins를 활용한 CI/CD 구축
- MySQL, Nginx 서버 구축
- 약속 생성, 수정, 삭제, 조회 기능 개발 
- 약속 참가자 등록 및 수정 기능 개발
- 웹소켓을 활용한 실시간 위치 공유 및 실시간 도착 로직 구현
- 시간에 따른 약속 상태 관리 및 상세 페이지 구현
- 약속 후기 사진 업로드 및 사진 댓글 CRUD
- 게임에서 획득한 지각비 결과 반영 및 지각비 정산 로직 구현
- 스케줄러를 활용한 약속 시간 전/후, 정산 성공 여부 FCM push 알림 구현
- FCM 등록 토큰 관리 및 토큰을 활용한 정산 요청 FCM push 알림 구현
- 기록 페이지 월별, 날짜별 사진 내역 구현
- 마이페이지 회원 정보, 지각율, 평균 도착 시간, 지각비 누적 정산금 구현

**이채은(팀원/Backend)**

- Spring Security와 JWT를 활용한 카카오 로그인 및 회원 관련 기능
- 회원정보 수정 기능
- 닉네임 검색
- 상점 내 아이템 구매 및 캐릭터 착장 변경 
- 게임을 위한 지각비 및 약속 상태 관리
- 친구 신청/수락·거절/삭제 기능
- 지갑 내역 및 충전·인출
- 클라우드를 활용한 정적자원 관리


**박소현(팀원/Mobile)**  

- 카카오 로그인 구현
- 약속 상태에 따른 약속 상세 페이지 구현
- 사진 촬영 기능 구현
- 기록 탭 사진 달력 구현
- 기록 탭 리뷰페이지 구현
- 기록 탭 댓글 기능 구현
- 기록 탭 상세페이지 구현
- 지갑 탭 게임 내 재화 충전/인출 기능 구현
- 마이페이지 메인화면 구현
- FCM을 활용한 정산 알림 구현

**윤선영(팀원/Mobile)**

- Kotlin/Android Studio 활용하여 안드로이드 앱 구현
- REST API 사용하여 백서버와 통신
- Navigation, DataBinding, ViewModel, LiveData 등 Jetpack 라이브러리 활용하여 메인/약속 생성 페이지/하단Nav바 구현
- 메인페이지
  - 주간 달력에서 날짜 선택 시 해당 날짜의 약속 목록 출력
  - 멀티 리사이클러뷰 활용하여 약속의 상태(지난 약속/30분 전/게임중/미래 약속)에 따라 목록 디자인 변경
- 약속 생성 페이지
  - ViewModel 사용하여 약속 정보 저장 후 서버에 생성 요청
  - CalendarView, DatePicker 활용하여 일정 선택
  - 카카오맵 API 활용하여 장소 선택
  - Dialog, 리사이클러뷰 활용하여 참여자 추가 모달 창 구현
- 디테일 페이지 내 사용자 실시간 위치 표시
  - 글로벌 웹소켓 선언하여 앱 전체에서 사용할 수 있도록 함
  - 휴대폰의 위치정보 받아온 후 약속 시간 30분 전부터 해당 약속의 웹소켓에 사용자 정보와 실시간 위치정보 1초에 1번 전송
  - 메시지를 받을 때마다 사용자정보(닉네임, 카카오톡 프로필 사진) 활용하여 카카오맵 내에 사용자 위치 마커 표시

**이주찬(팀원/Unity)**

- 게임 메타버스 구현
- 플레이어 컨트롤러 구현
- 플레이어 및 동물 애니메이션 설정
- 3인칭 궤도 카메라 및 시선 이동 구현
- 에셋 씬 내 콜라이더 수정
- 포톤 서버를 이용한 멀티 플레이 구현
- 플레이어 캐릭터 및 동물 오브젝트 생성 및 동기화
- 동물 움직임, 체력바 구현
- 동물 사망시 동전 나오는 효과 구현
- PvP, PvE 타격 구현
- 배경음 및 효과음 추가
- 시간에 따른 태양 각, 가로등 불빛 점멸 구현
- 시간에 따라 달라지는 skybox 구현
- 안드로이드 스튜디오와 데이터 교환, API 를 활용한 백엔드와 데이터 통신
- 안드로이드 스튜디오에 유니티 프로젝트 연동

**장예은(팀원/Unity)**

- 메타버스에 사용할 에셋 조사
- Plastic SCM을 사용하여 Unity 프로젝트 형상관리리
- 캐릭터를 관리할 수 있는 인벤토리 시스템 개발
- 캐릭터 파츠 교체 및 결제 기능 개발
- 캐릭터를 따라다니는 3인칭 카메라 구현
- 유니티에서 사용할 모달창 GUI 제작
- 게임 화면에서 전체 남은 돈 프로그래스바로 구현
- 안드로이드에서 호출하는 곳에 따라 원하는 유니티 씬을 로딩하는 기능 구현
- 게임, 상점 입장 시 캐릭터 상태 로드
- 마이페이지 레이아웃 구성
- 협업 도구로 피그잼과 노션 관리
- 안드로이드 스튜디오에서 데이터 수신, API 를 활용한 백엔드와 데이터 송수신
- 안드로이드 스튜디오에 유니티 프로젝트 연동

<br />
<br />

## 📑주요기능

#### 1. 약속 생성

약속 이름, 날짜, 시간, 장소, 참여자, 지각비 정보를 입력하여 약속을 생성합니다.

#### 2. 약속 조회

주별로 현재 사용자에게 계획된 약속 목록과 함께 약속 정보를 조회합니다.
약속 30분 전에 지도가 활성화 되며 참여자들의 위치를 확인합니다.
약속 시간이 지났는데 도착하지 않은 참여자가 있다면 게임 입장 버튼이 활성화됩니다.

#### 3. 약속 상세 조회

약속에 대한 상세 정보를 확인합니다.
약속 30분 전에 지도가 활성화 되며 참여자들의 위치를 확인합니다.
약속 시간이 지났는데 도착하지 않은 참여자가 있다면 게임 입장 버튼이 활성화됩니다.
약속이 종료되고 나면 사진을 남겨 기록합니다.

#### 4. 갤러리 보기

월별로 사용자가 모임을 가졌던 기록을 확인합니다.
모임 후기 정보를 확인합니다. 대표 사진과 참여자들의 댓글을 조회합니다.
내가 제시간에 도착했는지 지각했는지 상세정보와 게임을 통해 얻거나 잃은 지각비 정보를 확인합니다.

#### 5. 내 지갑

충전하여 두고 약속이 종료되면 정산하기 요청으로 지각비를 요청합니다.
충전 및 인출이 가능합니다.

#### 6. 마이페이지

나의 기본 정보(프로필사진, 닉네임, 지각률, 평균도착시간, 지각비)를 확인합니다.
캐릭터 정보를 조회/수정합니다.

### 7. 캐릭터(약속이) 관리

현재 내 캐릭터 정보를 조회합니다.
아이템을 구매하고 캐릭터에 적용합니다.

### 8. 게임

게임을 통해 지각비를 정산합니다.
몬스터(지각이)를 공격하여 동전을 획득합니다.

<br />
<br />

## 📱서비스 화면

### 접근권한 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_01_access.jpg" width=150px />
</div>

▲ 애플리케이션 사용을 위한 접근 권한 안내 페이지

<br/>

### 로그인 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_02_login.jpg" width=150px />
</div>

▲ 애플리케이션 사용을 위한 소셜로그인(카카오톡)

<br/>

### 회원 정보 입력 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_03_nickname.jpg" width=150px/>
</div>

▲ 서비스에 사용할 닉네임을 입력하는 페이지

<br/>

### 메인 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_04_main_noplan.jpg" width=150px/>
  <img src="./upload/DBLT_04_main_plan.jpg" width=150px/>
  <img src="./upload/DBLT_04_main.gif" width=150px/>
</div>

▲ 해당 날짜에 약속 리스트를 확인할 수 있는 메인 페이지

<br/>

### 기록 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_05_gallery.jpg" width=150px/>
  <img src="./upload/DBLT_05_gallery_noimage.jpg" width=150px/>
  <img src="./upload/DBLT_05_gallery_image.jpg" width=150px/>
  <img src="./upload/DBLT_05_gallery.gif" width=150px/>
</div>

▲ 월별로 약속을 사진, 후기와 함께 상세 정보를 확인할 수 있는 페이지

<br/>

### 지갑 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_06_wallet.jpg" width=150px/>
  <img src="./upload/DBLT_06_wallet_charge.jpg" width=150px/>
  <img src="./upload/DBLT_06_wallet_charge_input.jpg" width=150px/>
  <img src="./upload/DBLT_06_wallet_charge_complete.jpg" width=150px/>
</div>
<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_06_wallet_withdrawal.jpg" width=150px/>
  <img src="./upload/DBLT_06_wallet_withdrawal_input.jpg" width=150px/>
  <img src="./upload/DBLT_06_wallet_withdrawal_complete.jpg" width=150px/>
</div>

▲ 지각비 정산을 위해 충전, 인출을 할 수 있는 페이지

<br/>

### 마이 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_07_mypage.jpg" width=150px/>
</div>

<br/>

### 캐릭터 관리 페이지

<div style="display : flex; flex-direction : row; gap:10px;">
  <img src="./upload/DBLT_08_store.jpg" width=150px/>
  <img src="./upload/DBLT_08_store_purchase.jpg" width=150px/>
  <img src="./upload/DBLT_08_store_select.jpg" width=150px/>
  <img src="./upload/DBLT_08_store.gif" width=150px/>
</div>

▲ 약속이(캐릭터)의 색, 장갑, 꼬리 등 파츠를 착용하고 구매할 수 있는 페이지


<br />
<br />

## 📄실행방법

- 포팅 메뉴얼 참조

<br />
<br />

## 🛠기술스택 및 협업툴

### Mobile

<strong>Language</strong> | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />

<strong>Library</strong> | <img src="https://img.shields.io/badge/Jetpack-3DDC84?style=flat-square&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/Glide-4285F4?style=flat-square&logo=Glide&logoColor=white"/>

<strong>Build Tool</strong> | <img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white" /> <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white" />

<br />

### Unity

<strong>Language</strong> | <img src="https://img.shields.io/badge/Csharp-3178C6?style=flat-square&logo=csharp&logoColor=white" />

<strong>GameEngine</strong> | <img src="https://img.shields.io/badge/Unity-000000?style=flat-square&logo=Unity&logoColor=white" /> 

<strong>Library</strong> | <img src="https://img.shields.io/badge/Photon-DB7093?style=flat-square&logo=Photon&logoColor=black"/>

<br />

### Backend

<strong>Language</strong> | <img src="https://img.shields.io/badge/Java-5382a1?style=flat-square&logo=Java&logoColor=white" />

<strong>Framework</strong> | <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white" />

<strong>Library</strong> | <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white" /> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=Swagger&logoColor=black" /> <img src="https://img.shields.io/badge/WebSocket-010101?style=flat-square&logo=Web-Socket&logoColor=black" />

<strong>Build Tool</strong> | <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white" />

<strong>DB</strong> | <img src="https://img.shields.io/badge/MySql-4479A1?style=flat-square&logo=MySql&logoColor=white" /> <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=Firebase&logoColor=white" />

<br />

### Infra

<strong>Deploy</strong> | <img src="https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=Nginx&logoColor=white" /> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white" /> <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=Jenkins&logoColor=white" /> <img src="https://img.shields.io/badge/Aws-232F3E?style=flat-square&logo=amazonaws&logoColor=white" /> <img src="https://img.shields.io/badge/NaverCloud-03C75A?style=flat-square&logo=naver&logoColor=white" />

<br />

### Tool

<br />

<strong>Storage</strong> | <img src="https://img.shields.io/badge/Git-F05032?style=flat-square&logo=Git&logoColor=white"/> <img src="https://img.shields.io/badge/PlasticSCM-5A0FC8?style=flat-square&logo=Plastic-SCM&logoColor=white" />

<strong>Design</strong> | <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=Figma&logoColor=white"/> <img src="https://img.shields.io/badge/FigJam-F24E1E?style=flat-square&logo=Figma&logoColor=white"/>

<strong>Record</strong> | <img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Mattermost-0058CC?style=flat-square&logo=Mattermost&logoColor=white"/>

<strong>Scheduler</strong> | <img src="https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=Jira&logoColor=white" />

<br />
<br />

## 🖇시스템 아키텍처

![DBLT_Architecture](./upload/DBLT_Architecture.png)

<br />
<br />

## 🗃ERD

![DBLT_ERD](./upload/DBLT_ERD.png)

<br />
<br />

## 일정

![DBLT_Schedule](./upload/DBLT_Schedule1.png)
![DBLT_Schedule](./upload/DBLT_Schedule2.png)

<br />
<br />

## 🔃API 명세서

![DBLT_API](./upload/DBLT_API.png)

<br />
<br />

## ⚙협업 툴

[Notion - 프로젝트 관리](https://www.notion.so/chaeni2/DOPAMINES-0199800abb084fbeac10255de73c5a2a?pvs=4) <br/>
[Figjam - 기획 설계](https://www.figma.com/file/2UrpqX5zd1o6TKWe2IakK3/D209---DOPAMINES?type=whiteboard&node-id=0-1&t=4UlhNyH7HAesrXA0-0) <br/>
[Figma - 와이어프레임](https://www.figma.com/file/MtACFy9cjXa80l4AP2fKNQ/Wireframe?type=design&node-id=300-38733&t=lwWItDU8bVskoWiq-0)

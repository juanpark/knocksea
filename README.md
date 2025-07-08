# 🐟 멋쟁이사자처럼 2차 프로젝트 - KnockSea

## 프로젝트 소개

낚시를 좋아하는 사람들을 위한 온라인 커뮤니티 플랫폼입니다. 사용자는 지역별 낚시 장소를 포함한 다양한 정보를 자유롭게 공유할 수 있으며, 소통 게시판을 통해 다른 낚시인과 교류할 수 있습니다. 
<br><br>

## 조원 소개
| <img src="https://avatars.githubusercontent.com/u/49888727?v=4" width="120px"> | <img src="https://avatars.githubusercontent.com/u/6318827?v=4" width="120px"> | <img src="https://avatars.githubusercontent.com/u/155379997?v=4" width="120px"> | <img src="https://avatars.githubusercontent.com/u/62700196?v=4" width="120px"> | <img src="https://avatars.githubusercontent.com/u/132995507?v=4" width="120px"> |<img src="https://avatars.githubusercontent.com/u/201712860?v=4" width="120px">|
|:-------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|:--------------------------------------:|
|[**정현해**](https://github.com/gusgo200)|[**박정환**](https://github.com/juanpark)|[**김현민**](https://github.com/rlagusalsb)|[**김희연**]([https://github.com/Happy-Lotus](https://github.com/RE4LN4ME))|                     [**황승미**](https://github.com/Seungmi97)                     | [**문정환**](https://github.com/takonism) |
|`게시판 답변 및 댓글 구현`|`DevOps 환경 구축 및 모니터링`|`게시판 게시글 및 검색 구현`|`JWT 이용한 이메일&카카오 로그인 및 지도 화면 구현`|`게시글 카테고리&태그 및 상태 표시 구현`|`게시글 좋아요/싫어요 구현`|

<br><br>

## 기술 스택
<img src="https://img.shields.io/badge/JAVA-437291?style=for-the-badge&logo=OpenJDK&logoColor=white"/><img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"/><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"/><img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/><br>
<img src="https://img.shields.io/badge/Apache Maven-C71A36?style=for-the-badge&logo=ApacheMaven&logoColor=white"/><img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white"/><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"/><img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white"/><img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white"/><img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"/><img src="https://img.shields.io/badge/amazon aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <br>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/><img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white"/><br>
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=Thymeleaf&logoColor=white"/> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white"/> <br>
<img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white"/><img src="https://img.shields.io/badge/Intellij IDEA-000000?style=for-the-badge&logo=IntellijIDEA&logoColor=white"/> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=GitHub&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Google Maps API-4285F4?style=for-the-badge&logo=GoogleMaps&logoColor=white"/><img src="https://img.shields.io/badge/KAKAO-FFCD00?style=for-the-badge&logo=KAKAO&logoColor=white"/><br>


<br><br>

## 아키텍처
![image](https://github.com/user-attachments/assets/b002d2e8-be34-4b93-91dc-750a0bdfa54b)


## 기능
### 1. 게시판 게시글 작성 및 검색 

### 🖊️ 게시글 CRUD <br>
- 낚시에 대한 질문을 게시글로 작성합니다
- 게시글의 수정, 삭제는 게시글 작성자만 할 수 있습니다.
- 인증에 성공하면 "로그인 성공" 알림과 함께 메인 화면으로 이동합니다.

### 🔍  게시판 키워드 검색 및 페이징 <br>
- 게시글 제목과 내용에 포함된 키워드를 기준으로 검색할 수 있으며, 해당 키워드는 하이라이팅되어 표시됩니다.
- 검색 결과는 페이징 처리되어, 많은 게시글도 효율적으로 탐색할 수 있습니다.
- 사용자가 페이지를 이동할 때마다 해당 페이지에 필요한 게시글만 서버에서 불러와 성능을 최적화합니다.

### 📊 게시판 조회수 및 글 정렬 <br>
- 게시글 클릭 시 조회수가 자동으로 증가합니다.
- 최신순, 조회수순으로 게시글을 정렬할 수 있습니다.
- 정렬은 서버에서 처리되며, 선택한 기준에 따라 실시간으로 게시글 목록이 갱신됩니다.

---

### 2. 게시판 답변 및 댓글

### 💬 게시판 답변 및 댓글 <br>
- 게시글에 댓글과 대댓글을 작성해 사용자들과 소통할 수 있습니다.
- 게시글의 작성자는 댓글을 채택해 질문에 대한 답을 해결할 수 있습니다.
- 채택된 답변은" ✅ 체택됨"으로 표시되며 게시글의 상태는 "완료"로 표시됩니다.
- 게시글에 작성된 댓글의 수와 현재 상태를 직관적으로 확인할 수 있습니다.


---

### 3. 게시글 카테고리&태그 및 상태 표시
### 🏷  게시글 태그
- 게시글에 여러 개의 태그를 지정할 수 있습니다.


### 📂 게시글 카테고리
- 각 게시글은 1개의 카테고리를 지정할 수 있으며, 카테고리별 게시글 조회가 가능합니다.


### 📌 게시글 상태
- 게시글은 작성 후 상태가 아래와 같이 표시됩니다:
   - 대기(WAITING): 기본 상태 (회색 배지)
   - 완료(COMPLETED): 댓글이 1개 이상 달리면 자동 전환 (파란 배지)
   - 채택(ADOPTED): 작성자가 댓글을 채택하면 전환 (초록 배지)
- 상태에 따라 배지 색상이 다르게 표시되어 한눈에 현재 상태를 파악할 수 있습니다.

---

### 4. 게시글 좋아요/싫어요 

### 👍 게시글 좋아요/싫어요
- 한 사용자는 하나의 게시글에 좋아요 또는 싫어요 중 하나만 선택할 수 있습니다.
- 이미 선택된 반응을 다시 클릭하면 해당 반응이 취소됩니다.
- 반대 반응을 클릭하면 기존 반응은 취소되고 새로운 반응으로 변경됩니다.

---

### 5. JWT 이용한 이메일&카카오 로그인
### 📧 이메일 로그인
- Spring Security 기반으로 사용자 인증을 처리합니다.
- 로그인 시 사용자 인증 정보로 Access Token / Refresh Token 생성합니다.
  - Access Token: 클라이언트에서 요청 시 Authorization 헤더에 Bearer 방식으로 전달
  - Refresh Token: 서버 DB에 저장 및 만료 시 토큰 재발급 요청 처리
- JWT 유효성 검사는 JwtAuthenticationFilter에서 처리합니다. 
-인증된 사용자 정보는 SecurityContextHolder에 저장되어 전역 인증 처리 가능

### 🟡 카카오 로그인

- 카카오 OAuth 2.0 인가 코드를 통해 Access Token 발급 (RestTemplate 또는 WebClient 사용)받습니다.  
- 발급받은 카카오 Access Token으로 사용자 프로필 요청 (카카오 API)합니다.
- 이메일 기준으로 기존 회원 여부 판단합니다.
- 이후 JWT 토큰 발급 방식은 이메일 로그인과 동일하게 적용합니다.

---


### 6. 지도 API + 공공데이터 활용한 시각적인 정보 제공
### 🗺️ 지도 기반 낚시 장소 시각화
- 공공데이터 포털의 지역별 낚시터 데이터(엑셀 형식)를 수집하여 Python 스크립트로 병합합니다.
  - 낚시터 데이터는 해당 사이트 데이터를 활용하였습니다. 
  - [지방행정
    인허가 데이터개방 - 생활밀착데이터/낚시터정보](https://www.localdata.go.kr/lif/lifeCtacDataView.do?opnEtcSvcId=12_04_10_E)
- 병합된 .csv 파일을 MongoDB에 저장 (pymongo 라이브러리 사용)합니다. 
- MongoDB에 저장된 낚시터의 위도/경도 좌표 데이터를 조회합니다.
- 조회된 위치 데이터를 Google Maps JavaScript API와 연동하여 지도에 시각적으로 표현합니다.
- 각 낚시터는 빨간 마커로 지도 위에 표시되어 한눈에 위치 파악할 수 있습니다.

---

### 7. DevOps 환경 구축
### 🚀 CI/CD
- GitHub부터 AWS EC2까지 연결되는 CI/CD 파이프라인 구축하였습니다.
- 빌드 및 배포 과정을 자동화하여 개발자는 코드 구현에만 집중할 수 있는 효율적인 개발 환경을 조성합니다.
- 프로젝트 배포 서버 및 Spring 애플리케이션 모니터링 환경 구축하였습니다
- 서버 및 애플리케이션의 상태를 직관적으로 파악할 수 있어, 장애를 사전에 예방하고 성능을 지속적으로 최적화할 수 있는 기반 마련하였습니다.

<br>
<br>
<br>

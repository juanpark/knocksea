# 🐟 Knock Sea | 멋사 벡엔드 자바 15기 회고팀2

이 문서는 Knock Sea 프로젝트를 위한 Git 및 GitHub 사용 가이드입니다.  
브랜치 기반 워크플로우를 통해 안정적인 협업과 코드 관리를 목표로 합니다.

---

### 📌 1. GitHub 저장소 클론하기 (최초 1회만)

#### ✅ 방법
- macOS: 터미널
- Windows: Git Bash 또는 GitHub CLI 추천

```bash
cd ~/Dev  # 원하는 폴더로 이동
git clone https://github.com/juanpark/knocksea.git
# 또는 SSH 사용 시
git clone git@github.com:juanpark/knocksea.git
```

⸻

## 📌 2. main에서 직접 작업하지 마세요!

❌ 금지된 방법:
```bash
git add .
git commit -m "수정함"
git push origin main   # 🚨 금지
```
👉 main은 항상 안정적인 상태를 유지해야 합니다.

⸻

## 📌 3. 새로운 브랜치에서 작업하기

### 브랜치 생성
```bash
git switch -c feature/작업이름
```

### 브랜치 관련 명령어
```bash
git branch            # 모든 브랜치 목록
git switch 브랜치명    # 브랜치 이동
git switch -           # 이전 브랜치로 이동
```

### 브랜치 이름 예시
```bash
feature/add-login
feature/improve-ui
bugfix/fix-login-error
```

⸻

## 📌 4. 변경 사항 저장 (커밋하기)

### 변경된 파일 확인
```bash
git status
```

### 스테이징
```bash
git add .
```

### 커밋
```bash
git commit -m "[Feature] 로그인 기능 추가"
```

✅ 커밋 메시지 예시:  
- `[Fix] 로그인 오류 수정`  
- `[Feature] 판매글 업로드 기능 추가`  
- `[Chores] 폴더 구조 정리`

⸻

## 📌 5. 원격 저장소로 푸시하기

```bash
git push origin feature/작업이름
```


⸻

## 📌 6. Pull Request (PR) 생성하기

1. GitHub 저장소 → Pull Requests 클릭
2. "New Pull Request" 클릭
3. base: main / compare: feature/작업이름 설정
4. 작업 설명 작성, 팀원에게 리뷰 요청
5. 승인 후 Merge

⸻

## 📌 7. 머지 후 브랜치 삭제하기

### 로컬 브랜치 삭제
```bash
git branch -d feature/작업이름
```

### 원격 브랜치 삭제
```bash
git push origin --delete feature/작업이름
```


⸻

## 📌 8. 새로운 작업을 시작할 때 항상 develop 최신화!

```bash
git switch develop
git pull origin main
git switch -c feature/new-task
```


⸻

# 🚀 Git 협업 워크플로우 요약

| 단계 | 명령어 | 설명 |
|:----|:----|:----|
| 저장소 클론 | `git clone` | 로컬에 저장소 복제 |
| 새 브랜치 생성 | `git switch -c feature/작업이름` | 새 작업 브랜치 생성 |
| 변경사항 확인 | `git status` | 수정 파일 확인 |
| 변경사항 추가 | `git add .` | 스테이징 |
| 변경사항 커밋 | `git commit -m "메시지"` | 변경 저장 |
| 원격 저장소 푸시 | `git push origin feature/작업이름` | GitHub 업로드 |
| PR 생성 및 병합 | GitHub에서 PR 생성 후 Merge |
| 브랜치 삭제 | `git branch -d`, `git push origin --delete` | 병합 완료 후 삭제 |
| 새로운 작업 시작 전 최신화 | `git pull origin develop` | 항상 최신 develop 기준 |


⸻

# 🔥 Git 협업 시 주의할 점

✅ main 브랜치 직접 수정 금지  
✅ 작업 전 항상 최신 develop pull  
✅ 의미 있는 커밋 메시지 작성 (`[Feature]`, `[Fix]`, `[Chores]` 등)  
✅ 브랜치 → PR → Merge 순서로 협업  
✅ 병합 완료 후 브랜치 삭제

⸻

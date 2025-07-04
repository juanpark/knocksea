const postId = document.getElementById("postId").value;
const postOwnerId = document.getElementById("postOwnerId").value;
const accessToken = localStorage.getItem("accessToken");
const currentUserId = document.getElementById("currentUserId") ? document.getElementById("currentUserId").value : null;

// 댓글 새로고침 함수 분리
function loadComments() {
  fetch(`/api/comments/post/${postId}`)
    .then(res => res.json())
    .then(data => renderComments(data));
//    .then(data => {
//          console.log("댓글 데이터:", data);
//          renderComments(data);
//    });
}

// 페이지 최초 진입 시 한 번만 호출
window.addEventListener("DOMContentLoaded", () => {
  loadComments();
});

// 댓글 렌더링
function renderComments(comments) {
  const container = document.getElementById("comments-container");
  container.innerHTML = "";

  comments.forEach(comment => {
    const commentEl = buildCommentElement(comment);
    container.appendChild(commentEl);
  });
//    console.log("renderComments 호출!", comments);
//      const container = document.getElementById("comments-container");
//      if (!container) {
//        console.error("comments-container div가 없습니다!");
//        return;
//      }
//      container.innerHTML = "";
//      comments.forEach(comment => {
//        console.log("댓글 데이터:", comment);
//        const commentEl = buildCommentElement(comment);
//        container.appendChild(commentEl);
//      });
}

// 댓글 DOM 구성
function buildCommentElement(comment, isChild = false) {
  const div = document.createElement("div");
  div.style.marginLeft = isChild ? "30px" : "0";
  div.style.borderBottom = "1px solid #ccc";
  div.style.padding = "10px 0";

  const info = document.createElement("p");
  info.innerHTML = `<strong>${comment.nickname}</strong> | ${comment.createAt}`;
  div.appendChild(info);

  const content = document.createElement("p");
  content.textContent = comment.content;
  div.appendChild(content);

  console.log(
    "currentUserId:", currentUserId,
    "comment.userId:", comment.userId,
    "같나?", Number(currentUserId) === Number(comment.userId)
  );

  // 수정/삭제 버튼 (내 댓글)
  if (currentUserId && Number(currentUserId) === Number(comment.userId)) {
    const editBtn = document.createElement("button");
    editBtn.textContent = "수정";
    editBtn.onclick = () => {
      const newContent = prompt("수정할 내용을 입력하세요", comment.content);
      if (newContent) updateComment(comment.commentId, newContent);
    };
    div.appendChild(editBtn);

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "삭제";
    deleteBtn.onclick = () => deleteComment(comment.commentId);
    div.appendChild(deleteBtn);
  }

  // 채택 버튼 (게시글 작성자만 & 채택 안된 댓글만)
  if (Number(postOwnerId) === Number(currentUserId) && !comment.answer) {
    const adoptBtn = document.createElement("button");
    adoptBtn.textContent = "채택";
    adoptBtn.onclick = () => adoptComment(comment.commentId);
    div.appendChild(adoptBtn);
  }

  // 대댓글 버튼
  const replyBtn = document.createElement("button");
  replyBtn.textContent = "답글";
  replyBtn.onclick = () => {
    const replyContent = prompt("대댓글을 입력하세요");
    if (replyContent) submitComment(replyContent, comment.commentId);
  };
  div.appendChild(replyBtn);

  // 채택 표시
  if (comment.answer) {
    const adopted = document.createElement("span");
    adopted.textContent = " ✅ 채택됨";
    adopted.style.color = "green";
    div.appendChild(adopted);
  }

  // 자식 댓글 재귀 호출
  if (comment.children && comment.children.length > 0) {
    comment.children.forEach(child => {
      const childEl = buildCommentElement(child, true);
      div.appendChild(childEl);
    });
  }

  return div;
}

// 댓글 작성 (함수명 충돌 방지 submitComment로)
function submitComment(content = null, parentId = null) {
  const textarea = document.getElementById("newCommentContent");
  const commentContent = content || textarea.value.trim();

  if (!commentContent) return alert("댓글 내용을 입력하세요");

  fetch("/api/comments", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + accessToken,
    },
    body: JSON.stringify({
      content: commentContent,
      postId: postId,
      userId: currentUserId,
      parentId: parentId,
    }),
  })
    .then(res => {
      if (!res.ok) throw new Error("작성 실패: " + res.status);
      return res.text().then(text => {
        return text ? JSON.parse(text) : {};
      });
    })
    .then(() => {
      textarea.value = "";
      loadComments();
    })
    .catch((err) => alert("댓글 작성 실패: " + err.message));
}

// 댓글 수정
function updateComment(commentId, newContent) {
  fetch(`/api/comments/${commentId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + accessToken,
    },
    body: JSON.stringify({ content: newContent }),
  })
    .then(res => {
      if (!res.ok) throw new Error("수정 실패");
      loadComments();
    })
    .catch(() => alert("댓글 수정 실패"));
}

// 댓글 삭제
function deleteComment(commentId) {
  if (!confirm("댓글을 삭제할까요?")) return;

  fetch(`/api/comments/${commentId}`, {
    method: "DELETE",
    headers: {
      Authorization: "Bearer " + accessToken,
    },
  })
    .then(res => {
      if (!res.ok) throw new Error("삭제 실패");
      loadComments();
    })
    .catch(() => alert("댓글 삭제 실패"));
}

// 댓글 채택
function adoptComment(commentId) {
  if (!confirm("이 댓글을 채택하시겠습니까?")) return;

  fetch(`/api/comments/${commentId}/adopt`, {
    method: "POST",
    headers: {
      Authorization: "Bearer " + accessToken,
    },
  })
    .then(res => {
      if (!res.ok) throw new Error("채택 실패");
      loadComments();
    })
    .catch(() => alert("댓글 채택 실패"));
}
